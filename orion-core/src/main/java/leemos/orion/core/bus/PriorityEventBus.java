package leemos.orion.core.bus;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import leemos.orion.Configs;
import leemos.orion.config.QueueEventBusConfig;

/**
 * 基于优先级队列实现的事件总线
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public class PriorityEventBus extends QueuedEventBus {

    public PriorityEventBus(QueueEventBusConfig queuedEventBusConfig) {
        super(queuedEventBusConfig);
    }

    /**
     * 使用优先级队列，指定优先级较高的事件将会先执行。
     */
    @Override
    protected BlockingQueue<Event> createBlockingQueue() {
        int capacity = queuedEventBusConfig.getEventQueuedSize() == 0 ? Configs.eventQueueSize()
                : queuedEventBusConfig.getEventQueuedSize();
        return new PriorityBlockingQueue<Event>(capacity);
    }

}
