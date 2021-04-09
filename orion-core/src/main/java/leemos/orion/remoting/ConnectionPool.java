package leemos.orion.remoting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import leemos.orion.Scannable;

/**
 * 连接池实现
 *
 *
 * @author lihao
 * @date 2020年8月11日
 * @version 1.0
 */
public class ConnectionPool implements Scannable {

    private CopyOnWriteArrayList<Connection> connections;
    private SelectStrategy<Connection> selector;
    private volatile long lastAccessTimestamp;
    private volatile boolean asyncCreationDone;

    public ConnectionPool(SelectStrategy<Connection> selector) {
        this.selector = selector;
        this.connections = new CopyOnWriteArrayList<Connection>();
        this.asyncCreationDone = true;
    }

    /**
     * 将连接放入连接池
     *
     * @param connection
     */
    public void add(Connection connection) {
        markAccess();
        if (connection == null) {
            return;
        }
        connections.addIfAbsent(connection);
    }

    /**
     * 检查连接池中是否已存在该连接
     *
     * @param connection
     * @return
     */
    public boolean contains(Connection connection) {
        return connections.contains(connection);
    }

    /**
     * 移除并关闭连接
     *
     * @param connection
     */
    public void removeAndTryClose(Connection connection) {
        if (connection == null) {
            return;
        }
        if (connections.remove(connection)) {
            connection.close();
        }
    }

    /**
     * 返回一条连接
     *
     * @return
     */
    public Connection get() {
        markAccess();
        if (connections != null) {
            List<Connection> snapshot = new ArrayList<Connection>(connections);
            if (snapshot.size() > 0) {
                return selector.select(snapshot);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 返回所有连接
     *
     * @return
     */
    public List<Connection> getAll() {
        markAccess();
        return new ArrayList<Connection>(connections);
    }

    /**
     * 返回可以用连接数
     *
     * @return
     */
    public int size() {
        return connections.size();
    }

    /**
     * 判断连接池是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return connections.isEmpty();
    }

    /**
     * 获取最后访问的时间戳
     *
     * @return
     */
    public long getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }

    /**
     * 异步创建连接是否完成
     *
     * @return
     */
    public boolean isAsyncCreationDone() {
        return asyncCreationDone;
    }

    /**
     * 标记为开始异步创建连接
     */
    public void markAsyncCreationStart() {
        asyncCreationDone = false;
    }

    /**
     * 标记为异步创建连接完成
     */
    public void markAsyncCreationDone() {
        asyncCreationDone = true;
    }

    /**
     * 关闭连接池
     */
    public void removeAllAndTryClose() {
        for (Connection conn : connections) {
            removeAndTryClose(conn);
        }
        connections.clear();
    }

    /*
     * @see leemos.orion.Scannable#scan()
     */
    @Override
    public void scan() {
        if (null != connections && !connections.isEmpty()) {
            for (Connection conn : connections) {
                if (!conn.isFine()) {
                    conn.close();
                    removeAndTryClose(conn);
                }
            }
        }
    }

    /**
     * 标记最后访问时间
     */
    private void markAccess() {
        lastAccessTimestamp = System.currentTimeMillis();
    }
}
