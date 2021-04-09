package leemos.orion;

import leemos.orion.core.bus.Event;
import leemos.orion.core.bus.EventDiscardException;
import leemos.orion.core.bus.EventListener;

/**
 * 事件总线总线，负责核心组件间的事件流转。
 *
 * @author lihao
 * @date 2020年8月6日
 * @version 1.0
 */
public interface EventBus extends Lifecycle, Hierarchy {

    /**
     * 注册事件监听器，当事件发生时，总线会根据事件类型分发给对应的监听器。
     *
     * @param interestedEvent
     * @param listener
     */
    public void registerEventListener(Class<? extends Event> interestedEvent,
            EventListener listener);

    /**
     * 注销事件监听器
     *
     * @param interestedEvent
     * @param listener
     */
    public void unregisterEventListener(Class<? extends Event> interestedEvent,
            EventListener listener);

    /**
     * 发布事件，发布的事件如果超过一定时间还未能成功分发，将抛出EventDiscardException，并终止该事件。
     *
     * @param event
     * @throws EventDiscardException
     */
    public void postEvent(Event event) throws EventDiscardException;

    /**
     * 指定EventBus的抽象层级
     */
    default int getLevel() {
        return 2;
    }

}
