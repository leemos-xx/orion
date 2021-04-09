package leemos.orion.remoting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leemos.orion.Constants;
import leemos.orion.commons.NamedThreadFactory;
import leemos.orion.commons.StringManager;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.lifecycle.LifecycleSupport;
import leemos.orion.remoting.utils.ExecutedTask;
import leemos.orion.remoting.utils.NotCompletedException;
import leemos.orion.remoting.utils.NotExecutedException;

/**
 * 默认的连接管器实现类
 *
 *
 * @author lihao
 * @date 2020年8月19日
 * @version 1.0
 */
public class DefaultConnectionManager extends LifecycleSupport
        implements ConnectionManager {

    private static final Logger logger = LoggerFactory
            .getLogger(DefaultConnectionManager.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private SelectStrategy<Connection> selectStrategy;

    /**
     * 用于异步创建连接池
     */
    private ThreadPoolExecutor asyncCreateConnectionExecutor;

    /**
     * 记录连接池的集合
     */
    private ConcurrentHashMap<String, ExecutedTask<ConnectionPool>> createConnectionPoolTasks = new ConcurrentHashMap<String, ExecutedTask<ConnectionPool>>();

    private ConnectionFactory connectionFactory;

    public DefaultConnectionManager(ConnectionFactory connectionFactory,
            SelectStrategy<Connection> selectStrategy) {
        this.connectionFactory = connectionFactory;
        this.selectStrategy = selectStrategy;
    }

    /*
     * @see leemos.orion.ConnectionManager#add(leemos.orion.Connection)
     */
    @Override
    public void add(Connection connection) {
        String poolKey = connection.getPoolKey();
        ConnectionPool pool = null;
        try {
            // 获取或者创建连接池
            pool = this.getConnectionPoolAndCreateIfAbsent(poolKey,
                    new ConnectionPoolCallable());
        } catch (Exception e) {
            logger.error(sm.getString("defaultConnectionManager.add.emptyPool"), e);
        }

        if (pool != null) {
            pool.add(connection);
        } else {
            logger.error(sm.getString("defaultConnectionManager.add.emptyPool"));
        }
    }

    /*
     * @see leemos.orion.remoting.ConnectionManager#get(java.lang.String)
     */
    @Override
    public Connection get(String poolKey) {
        ConnectionPool pool = getConnectionPool(createConnectionPoolTasks.get(poolKey));
        return pool == null ? null : pool.get();
    }

    /*
     * @see leemos.orion.remoting.ConnectionManager#getAll(java.lang.String)
     */
    @Override
    public List<Connection> getAll(String poolKey) {
        ConnectionPool pool = getConnectionPool(createConnectionPoolTasks.get(poolKey));
        return pool == null ? new ArrayList<Connection>() : pool.getAll();
    }

    /*
     * @see
     * leemos.orion.remoting.ConnectionManager#remove(leemos.orion.remoting.
     * Connection)
     */
    @Override
    public void remove(Connection connection) {
        String poolKey = connection.getPoolKey();
        ConnectionPool pool = getConnectionPool(createConnectionPoolTasks.get(poolKey));
        if (pool == null) {
            connection.close();
        } else {
            pool.removeAndTryClose(connection);
            if (pool.isEmpty()) {
                removeTask(poolKey);
            }
        }
    }

    /*
     * @see leemos.orion.remoting.ConnectionManager#removeAll()
     */
    @Override
    public void removeAll() {
        Iterator<String> iter = this.createConnectionPoolTasks.keySet().iterator();
        while (iter.hasNext()) {
            String poolKey = iter.next();
            removeTask(poolKey);
            iter.remove();
        }
    }

    /*
     * @see
     * leemos.orion.remoting.ConnectionManager#check(leemos.orion.remoting.
     * Connection)
     */
    @Override
    public boolean check(Connection connection) {
        if (connection == null) {
            return false;
        }
        if (!connection.isFine()) {
            this.remove(connection);
            return false;
        }
        if (!connection.getChannel().isWritable()) {
            return false;
        }

        return true;
    }

    /*
     * @see leemos.orion.remoting.ConnectionManager#count(java.lang.String)
     */
    @Override
    public int count(String poolKey) {
        ConnectionPool pool = getConnectionPool(createConnectionPoolTasks.get(poolKey));
        if (pool != null) {
            return pool.size();
        } else {
            return 0;
        }
    }

    /*
     * @see
     * leemos.orion.remoting.ConnectionManager#getAndCreateIfAbsent(leemos.
     * orion.remoting.Url)
     */
    @Override
    public Connection getAndCreateIfAbsent(Url url)
            throws InterruptedException, RemotingException {
        ConnectionPool pool = getConnectionPoolAndCreateIfAbsent(url.getUniqueKey(),
                new ConnectionPoolCallable(url));
        if (null != pool) {
            return pool.get();
        } else {
            logger.error(sm.getString(
                    "defaultConnectionManager.getAndCreateIfAbsent.emptyPool"));
            return null;
        }
    }

    /*
     * @see leemos.orion.Scannable#scan()
     * 
     * FIXME not called yet
     */
    @Override
    public void scan() {
        if (null != this.createConnectionPoolTasks
                && !this.createConnectionPoolTasks.isEmpty()) {
            Iterator<String> iter = this.createConnectionPoolTasks.keySet().iterator();
            while (iter.hasNext()) {
                String poolKey = iter.next();
                ExecutedTask<ConnectionPool> task = this.createConnectionPoolTasks
                        .get(poolKey);
                if (!task.isDone()) {
                    continue;
                }

                ConnectionPool pool = this.getConnectionPool(task);
                if (null != pool) {
                    pool.scan();
                    if (pool.isEmpty()) {
                        if ((System.currentTimeMillis() - pool
                                .getLastAccessTimestamp()) > Constants.DEFAULT_EXPIRE_TIME) {
                            iter.remove();
                        }
                    }
                }
            }
        }
    }

    /*
     * @see leemos.orion.LifecycleSupport#initializeInternal()
     */
    @Override
    protected void initializeInternal() throws LifecycleException {
        this.connectionFactory.initialize();
    }

    /*
     * @see leemos.orion.LifecycleSupport#startInternal()
     */
    @Override
    protected void startInternal() throws LifecycleException {
        this.asyncCreateConnectionExecutor = new ThreadPoolExecutor(1, 10, 30,
                TimeUnit.MICROSECONDS, new ArrayBlockingQueue<Runnable>(1000),
                new NamedThreadFactory("connection-warmup-executor", true));
    }

    /*
     * @see leemos.orion.LifecycleSupport#stopInternal()
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        if (asyncCreateConnectionExecutor != null) {
            asyncCreateConnectionExecutor.shutdown();
        }

        if (null == this.createConnectionPoolTasks
                || this.createConnectionPoolTasks.isEmpty()) {
            return;
        }

        Iterator<String> iter = this.createConnectionPoolTasks.keySet().iterator();
        while (iter.hasNext()) {
            String poolKey = iter.next();
            this.removeTask(poolKey);
            iter.remove();
        }
    }

    @Override
    public int getLevel() {
        return 1;
    }

    /**
     * 获取连接池，如果不存在则创建
     *
     * @param poolKey
     * @param callable
     * @return
     * @throws InterruptedException
     * @throws RemotingException
     */
    private ConnectionPool getConnectionPoolAndCreateIfAbsent(String poolKey,
            ConnectionPoolCallable callable)
            throws InterruptedException, RemotingException {
        ExecutedTask<ConnectionPool> task;
        ConnectionPool pool = null;

        // 重试次数
        int retry = Constants.DEFAULT_RETRY_TIMES;

        for (int i = 0; i < retry && pool == null; ++i) {
            task = createConnectionPoolTasks.get(poolKey);
            // 如果任务不存在，说明连接池未创建，则尝试创建连接池
            if (task == null) {
                ExecutedTask<ConnectionPool> newTask = new ExecutedTask<ConnectionPool>(
                        callable);
                task = createConnectionPoolTasks.putIfAbsent(poolKey, newTask);
                if (task == null) {
                    task = newTask;
                    task.run();
                }
            }

            try {
                // 如果获取的连接池为null，则尝试重新获取
                pool = task.get();
                if (pool == null) {
                    if (i + 1 < retry) {
                        continue;
                    }
                    this.createConnectionPoolTasks.remove(poolKey);
                }
            } catch (InterruptedException e) {
                if (i + 1 < retry) {
                    continue;
                }
                this.createConnectionPoolTasks.remove(poolKey);
                throw e;
            } catch (ExecutionException e) {
                this.createConnectionPoolTasks.remove(poolKey);
                throw new RemotingException(e);
            }
        }

        return pool;
    }

    /**
     * 生成数据库连接池，如果指定需要初始化连接，则异步初始化
     *
     *
     * @author lihao
     * @date 2020年8月31日
     * @version 1.0
     */
    private class ConnectionPoolCallable implements Callable<ConnectionPool> {
        /**
         * 是否初始化连接
         */
        private boolean whetherInitConnection;

        /**
         * 初始化连接时的参数需从url中获取
         */
        private Url url;

        public ConnectionPoolCallable() {
            this.whetherInitConnection = false;
        }

        public ConnectionPoolCallable(Url url) {
            this.whetherInitConnection = true;
            this.url = url;
        }

        @Override
        public ConnectionPool call() throws Exception {
            final ConnectionPool pool = new ConnectionPool(selectStrategy);
            if (whetherInitConnection) {
                try {
                    createConnections(this.url, pool, this.getClass().getSimpleName(), 1);
                } catch (Exception e) {
                    pool.removeAllAndTryClose();
                    throw e;
                }
            }
            return pool;
        }

    }

    /**
     * 创建所有连接，并置入连接池
     *
     * @param url
     * @param pool
     * @param taskName
     * @param syncCreateNumWhenNotWarmup
     * @throws RemotingException
     */
    private void createConnections(Url url, ConnectionPool pool, String taskName,
            int syncCreateNumWhenNotWarmup) throws RemotingException {
        int actualNum = pool.size();
        int expectNum = url.getConnectionNum();
        if (actualNum >= expectNum) {
            return;
        }

        // 如果需要连接预热，则此时同步创建需要的所有连接
        if (url.isConnectionWarmup()) {
            for (int i = actualNum; i < expectNum; i++) {
                Connection connection = create(url);
                pool.add(connection);
            }
        } else {
            if (syncCreateNumWhenNotWarmup > 0) {
                for (int i = 0; i < syncCreateNumWhenNotWarmup; ++i) {
                    Connection connection = create(url);
                    pool.add(connection);
                }
                if (syncCreateNumWhenNotWarmup >= url.getConnectionNum()) {
                    return;
                }
            }

            // 若不需预热，则异步创建连接并放入连接池
            pool.markAsyncCreationDone();
            try {
                asyncCreateConnectionExecutor.execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            for (int i = pool.size(); i < url.getConnectionNum(); i++) {
                                Connection connection = null;
                                try {
                                    connection = create(url);
                                } catch (RemotingException e) {
                                    logger.error(e.getMessage(), e);
                                }
                                pool.add(connection);
                            }
                        } finally {
                            pool.markAsyncCreationDone();
                        }
                    }
                });
            } catch (RejectedExecutionException e) {
                pool.markAsyncCreationDone();
                throw e;
            }
        }
    }

    /**
     * 创建连接
     *
     * @param url
     * @return
     * @throws RemotingException
     */
    private Connection create(Url url) throws RemotingException {
        Connection conn;
        try {
            conn = this.connectionFactory.createConnection(url);
        } catch (Exception e) {
            throw new RemotingException(
                    "Create connection failed. The address is " + url.getOriginalUrl(),
                    e);
        }
        return conn;
    }

    /**
     * 从任务中获取连接池
     *
     * @param task
     * @return
     */
    private ConnectionPool getConnectionPool(ExecutedTask<ConnectionPool> task) {
        ConnectionPool pool = null;
        if (task != null) {
            try {
                pool = task.getAfterRun();
            } catch (InterruptedException e) {
                logger.error(
                        sm.getString(
                                "defaultConnectionManager.getConnectionPool.interrupt"),
                        e);
            } catch (ExecutionException e) {
                logger.error(
                        sm.getString(
                                "defaultConnectionManager.getConnectionPool.execFailed"),
                        e);
            } catch (NotExecutedException e) {
                logger.error(
                        sm.getString(
                                "defaultConnectionManager.getConnectionPool.notRunYet"),
                        e);
            } catch (NotCompletedException e) {
                logger.error(sm.getString(
                        "defaultConnectionManager.getConnectionPool.notCompletYet"), e);
            }
        }
        return pool;
    }

    private void removeTask(String poolKey) {
        ExecutedTask<ConnectionPool> task = this.createConnectionPoolTasks
                .remove(poolKey);
        if (null != task) {
            ConnectionPool pool = getConnectionPool(task);
            if (null != pool) {
                pool.removeAllAndTryClose();
            }
        }
    }
}
