package leemos.orion.remoting;

import java.util.List;

import leemos.orion.Lifecycle;
import leemos.orion.Scannable;

/**
 * 连接管理器，用于维护连接池状态
 *
 * @author lihao
 * @date 2020年8月11日
 * @version 1.0
 */
public interface ConnectionManager extends Lifecycle, Scannable {

    /**
     * 将连接放入连接池{@link ConnectionPool}
     *
     * @param connection
     */
    void add(Connection connection);

    /**
     * 从指定连接池{@link Connection}获取连接，当{@link ConnectionPool}中不存在可用连接时，
     * 返回<code>null</code>
     *
     * @param poolKey
     * @return
     */
    Connection get(String poolKey);

    /**
     * 返回指定连接池{@link ConnectionPool}中的所有连接
     *
     * @param poolKey
     * @return
     */
    List<Connection> getAll(String poolKey);

    /**
     * 从所有连接池{@link ConnectionPool}中移除并关闭该连接
     *
     * @param connection
     */
    void remove(Connection connection);

    /**
     * 从指定连接池{@link ConnectionPool}移除并关闭所有连接
     */
    void removeAll();

    /**
     * 检查连接是否为可用状态
     *
     * @param connection
     * @return
     */
    boolean check(Connection connection);

    /**
     * 获取指定连接池{@link ConnectionPool}的可用连接数
     *
     * @param poolKey
     * @return
     */
    int count(String poolKey);

    /**
     * 通过url获取连接，如果为空，则创建连接
     *
     * @param url
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    Connection getAndCreateIfAbsent(Url url)
            throws InterruptedException, RemotingException;

}
