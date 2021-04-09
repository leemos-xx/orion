package leemos.orion.core.bus;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.util.StrUtil;
import leemos.orion.Configs;
import leemos.orion.Constants;
import leemos.orion.commons.NamedThreadFactory;
import leemos.orion.commons.StringManager;
import leemos.orion.config.QueueEventBusConfig;
import leemos.orion.lifecycle.LifecycleException;

/**
 * 基于队列实现的异步事件总线
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public class QueuedEventBus extends DefaultEventBus {

    private static final Logger logger = LoggerFactory.getLogger(QueuedEventBus.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    /**
     * 当事件进入队列的等待时间超过该超时时间，则直接忽略该事件
     */
    private static final long DISCARD_TIMEOUT_MILLIS = 5000;

    private volatile boolean stopped = false;
    private Executor executor;
    private BlockingQueue<Event> queue;

    protected QueueEventBusConfig queuedEventBusConfig;

    public QueuedEventBus() {
        
    }
    
    public QueuedEventBus(QueueEventBusConfig queuedEventBusConfig) {
        this.queuedEventBusConfig = queuedEventBusConfig;
    }

    /**
     * 事件提交，将事件放入事件队列，由异步线程进行事件投递。
     */
    @Override
    public void postEvent(Event event) throws EventDiscardException {
        checkEventType(event);

        try {
            long discardTimeoutMillis = queuedEventBusConfig.getDiscardTimeoutMillis() == 0 ? DISCARD_TIMEOUT_MILLIS
                    : queuedEventBusConfig.getDiscardTimeoutMillis();

            if (!queue.offer(event, discardTimeoutMillis, TimeUnit.MILLISECONDS)) {
                String errorMsg = sm.getString("queuedEventBus.postEvent.timeout", String.valueOf(event.getId()));
                logger.warn(errorMsg);

                throw new EventDiscardException(errorMsg);
            }
        } catch (InterruptedException e) {
            throw new EventDiscardException(e);
        }
    }

    /*
     * @see leemos.orion.LifecycleSupport#initializeInternal()
     */
    @Override
    protected void initializeInternal() throws LifecycleException {
        setName(StrUtil.blankToDefault(queuedEventBusConfig.getName(), "default-event-bus"));
        
        queue = createBlockingQueue();
        // 单线程执行事件分发任务
        executor = Executors.newFixedThreadPool(1, new NamedThreadFactory("orion-eventbus", true));
    }

    /**
     * 创建阻塞队列，用于存放事件。
     *
     * @return
     */
    protected BlockingQueue<Event> createBlockingQueue() {
        int capacity = queuedEventBusConfig.getEventQueuedSize() == 0 ? Configs.eventQueueSize()
                : queuedEventBusConfig.getEventQueuedSize();
        return new ArrayBlockingQueue<Event>(capacity);
    }

    /*
     * @see leemos.orion.LifecycleSupport#startInternal()
     */
    @Override
    protected void startInternal() throws LifecycleException {
        executor.execute(new DispatcherTask());
    }

    /*
     * @see leemos.orion.LifecycleSupport#stopInternal()
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        // 未处理完的事件将直接丢弃
        stopped = true;
    }

    /**
     * 分发事件到监听器
     *
     * @author lihao
     * @date 2020年8月27日
     * @version 1.0
     */
    private class DispatcherTask implements Runnable {

        @Override
        public void run() {

            while (!stopped) {
                Event event = null;
                try {
                    event = queue.take();
                    triggerEventPost(event);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

    }

}
