package leemos.orion;

import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.remoting.Protocol;

/**
 * Connector的职责是监听指定端口，接收消息。不同Connector的实现用以解析不同的协议。
 *
 * @author lihao
 * @date 2020年8月5日
 * @version 1.0
 */
public interface Connector extends Hierarchy, Lifecycle {

    /**
     * 获取ip地址
     *
     * @return
     */
    public String getIp();

    /**
     * 获取监听的端口
     *
     * @return
     */
    public int getPort();

    /**
     * 获取关联的{@link Service}
     *
     * @return
     */
    public Service getService();

    /**
     * 设置关联的{@link Service}
     *
     * @param service
     */
    public void setService(Service service);

    /**
     * 设置关联的{@link EventBus}
     *
     * @param eventBus
     */
    public void setEventBus(EventBus eventBus);

    /**
     * 获取关联的{@link EventBus}
     *
     * @return
     */
    public EventBus getEventBus();

    /**
     * 获取该Connector支持的{@link Protocol}
     *
     * @return
     */
    public Protocol getProtocol();

    /**
     * 设置该Connector支持的{@link Protocol}
     *
     * @param protocol
     */
    public void setProtocol(Protocol protocol);

    /**
     * 进行预初始化操作，在该实现中，应该同步初始化所有容器相关组件
     *
     * @throws LifecycleException
     */
    public void initialize() throws LifecycleException;
    
    /**
     * 指定Connector的抽象层级
     */
    default int getLevel() {
        return 2;
    }

}
