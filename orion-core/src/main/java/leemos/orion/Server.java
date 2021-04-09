package leemos.orion;

import leemos.orion.lifecycle.LifecycleException;

/**
 * Server相当于整个rpc服务容器，其中可以包含多个Service。
 * 
 * <ul>
 *   <li>本身包含服务端口，可以用于对Server的管理或状态监控；</li>
 *   <li>作为所有生命周期组件的入口，组件的初始化和启停均由Server进行驱动；</li>
 * </ul>
 *
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public interface Server extends Lifecycle {

    /**
     * 返回服务端口号
     */
    public int getAdminPort();

    /**
     * 设置服务端口号
     *
     * @param port
     * @throws LifecycleException 
     */
    public void setAdminPort(int port) throws LifecycleException;

    /**
     * 增加需要管理的Service
     *
     * @param service
     */
    public void addService(Service service);

    /**
     * 根据名称查找指定的Service，否则返回<code>null</code>
     *
     * @param name
     * @return 
     */
    public Service getService(String name);

    /**
     * 返回被管理的Service的集合，若为空，则返回长度为0的数组
     */
    public Service[] getServices();

    /**
     * 从集合中移除并销毁Service
     *
     * @param service
     */
    public void removeService(Service service);

    /**
     * 指定Server的抽象层级
     */
    default int getLevel() {
        return 0;
    }
}
