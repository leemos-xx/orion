package leemos.orion.config;

/**
 * 基于队列实现的EventBus的可配置项
 * 
 * @author lihao
 * @date 2020-12-05
 * @version 1.0
 */
public class QueueEventBusConfig extends EventBusConfig {

    private long discardTimeoutMillis;
    private int eventQueuedSize;

    public void setDiscardTimeoutMillis(long discardTimeoutMillis) {
        this.discardTimeoutMillis = discardTimeoutMillis;
    }

    public long getDiscardTimeoutMillis() {
        return discardTimeoutMillis;
    }

    public void setEventQueuedSize(int eventQueuedSize) {
        this.eventQueuedSize = eventQueuedSize;
    }

    public int getEventQueuedSize() {
        return eventQueuedSize;
    }
}
