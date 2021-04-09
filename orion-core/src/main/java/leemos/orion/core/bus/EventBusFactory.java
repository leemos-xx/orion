package leemos.orion.core.bus;

import leemos.orion.EventBus;
import leemos.orion.config.EventBusConfig;
import leemos.orion.config.QueueEventBusConfig;

/**
 * EventBus工厂类
 * 
 * @author lihao
 * @date 2020-12-05
 * @version 1.0
 */
public class EventBusFactory {

    public static EventBus createEventBus(EventBusConfig eventBusConfig) {
        switch (EventBusType.parse(eventBusConfig.getType())) {
        case DEFAULT:
            return new DefaultEventBus(eventBusConfig);
        case QUEUED:
            return new QueuedEventBus((QueueEventBusConfig) eventBusConfig);
        case PRIORITY:
            return new PriorityEventBus((QueueEventBusConfig) eventBusConfig);
        default:
            break;
        }
        return null;
    }

}
