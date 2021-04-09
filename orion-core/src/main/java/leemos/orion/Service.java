package leemos.orion;

/**
 * Service是抽象概念，包含一组用于接收请求的Connector，一条事件总线EventBus，
 * 以及一个执行请求的Engine。
 * 
 * <ul>
 *   <li>Connector用于暴露对外提供服务的端口，不同的Connector用于处理不同的协议，最终都会解析为业务层可处理的请求；</li>
 *   <li>EventBus用于进行内部事件流转，通过发布订阅机制，解耦内部逻辑；</li>
 *   <li>Engine为实际进行业务处理的服务引擎；</li>
 * </ul>
 * 
 * @author lihao
 * @date 2020年8月5日
 * @version 1.0
 */
public interface Service extends Lifecycle {

    /**
     * 获取关联的Server
     */
    public Server getServer();

    /**
     * 设置关联的Server
     * 
     * @param server
     */
    public void setServer(Server server);

    /**
     * 获取关联的EventBus
     *
     * @return
     */
    public EventBus getEventBus();

    /**
     * 设置关联的EventBus
     *
     * @param eventBus
     */
    public void setEventBus(EventBus eventBus);

    /**
     * 获取关联的Engine组件
     */
    public Engine getEngine();

    /**
     * 设置关联的Engine组件
     * 
     * @param engine
     */
    public void setEngine(Engine engine);

    /**
     * 添加关联的Connector组件
     *
     * @param connector
     */
    public void addConnector(Connector connector);

    /**
     * 获取用于接收请求的Connector的集合
     *
     * @return
     */
    public Connector[] getConnectors();

    /**
     * 移除指定的Connector组件
     *
     * @param connector
     */
    public void removeConnector(Connector connector);

    /**
     * 指定Service的抽象层级
     */
    default int getLevel() {
        return 1;
    }

}
