package leemos.orion.core.bus;

import leemos.orion.EventBus;

/**
 * 事件监听器，需要由总线分派{@link Event}的组件都应该实现该接口，并注册到{@link EventBus}。
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public interface EventListener {

    /**
     * 当{@link EventBus}接收到感兴趣的{@link Event}时触发
     *
     * @param event
     */
    public void eventReceived(Event event);
}
