package leemos.orion.remoting;

import leemos.orion.Lifecycle;

/**
 * 连接工厂类
 *
 * @author lihao
 * @date 2020年8月18日
 * @version 1.0
 */
public interface ConnectionFactory extends Lifecycle {

    /**
     * 根据{@link Url}生成连接
     *
     * @param url
     * @return
     * @throws Exception
     */
    public Connection createConnection(Url url) throws Exception;

}
