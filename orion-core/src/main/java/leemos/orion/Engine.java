package leemos.orion;

import leemos.orion.core.bus.EventListener;
import leemos.orion.core.engine.Pipeline;

/**
 * Engine是服务容器的核心组件，用于完成对请求{@link RpcRequest}和响应{@link RpcResponse}的处理。
 * 一个Engine实例包含一条{@link Pipeline}及一组特定的{@link Valve}，完成实际的
 * <code>invoke()</code>操作。
 *
 * @author lihao
 * @date 2020年8月5日
 * @version 1.0
 */
public interface Engine extends Hierarchy, Lifecycle, EventListener, Pipeline {

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
     * 获取事件总线
     *
     * @return
     */
    public EventBus getEventBus();

    /**
     * 设置关联的事件总线
     *
     * @param eventBus
     */
    public void setEventBus(EventBus eventBus);

    /**
     * 指定Engine的抽象层级
     */
    default int getLevel() {
        return 2;
    }

}
