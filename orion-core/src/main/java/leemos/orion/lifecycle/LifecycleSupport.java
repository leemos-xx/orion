package leemos.orion.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leemos.orion.Constants;
import leemos.orion.Hierarchy;
import leemos.orion.Lifecycle;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.StringUtils;

/**
 * 生命周期的骨架实现类
 * 
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public abstract class LifecycleSupport implements Lifecycle, Hierarchy {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleSupport.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    /**
     * 组件名称，用于日志和展示。
     */
    private String name;

    /**
     * 当前组件的状态
     */
    protected LifecycleStatus status = LifecycleStatus.NEW;

    /**
     * {@link LifecycleListener}的集合
     */
    private LifecycleListener[] listeners = new LifecycleListener[0];

    /*
     * @see leemos.orion.Lifecycle#initialize()
     */
    @Override
    public void initialize() throws LifecycleException {
        if (status != LifecycleStatus.NEW) {
            throw new LifecycleException(sm.getString(
                    "lifecycleSupport.initialize.alreadyInitialized", getName()));
        }
        triggerStatusChanged(LifecycleStatus.INITIALIZING);

        try {
            initializeInternal();
            triggerStatusChanged(LifecycleStatus.INITIALIZED);

        } catch (Exception e) {
            triggerStatusChanged(LifecycleStatus.ERROR);
            throw new LifecycleException(e);
        }
    }

    protected abstract void initializeInternal() throws LifecycleException;

    /*
     * @see leemos.orion.Lifecycle#start()
     */
    @Override
    public void start() throws LifecycleException {
        if (status == LifecycleStatus.NEW) {
            initialize();
        }
        if (status != LifecycleStatus.INITIALIZED) {
            throw new LifecycleException(
                    sm.getString("lifecycleSupport.start.abnormalStatus", getName()));
        }
        triggerStatusChanged(LifecycleStatus.STARTING);

        try {
            if (logger.isInfoEnabled()) {
                printHierarchicalInfo(
                        sm.getString("lifecycleSupport.start.starting", getName()));
            }

            startInternal();
            triggerStatusChanged(LifecycleStatus.STARTED);

            if (logger.isInfoEnabled()) {
                printHierarchicalInfo(
                        sm.getString("lifecycleSupport.start.started", getName()));
            }
        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                printHierarchicalInfo(
                        sm.getString("lifecycleSupport.start.error", getName()));
            }
            triggerStatusChanged(LifecycleStatus.ERROR);
            throw new LifecycleException(e);
        }
    }

    protected abstract void startInternal() throws LifecycleException;

    /*
     * @see leemos.orion.Lifecycle#stop()
     */
    @Override
    public void stop() throws LifecycleException {
        // 组件未启动
        if (status != LifecycleStatus.STARTED) {
            throw new LifecycleException(
                    sm.getString("lifecycleSupport.stop.notStarted", getName()));
        }
        // 组件已执行过停止方法
        if (status == LifecycleStatus.STOPPING || status == LifecycleStatus.STOPPED) {
            throw new LifecycleException(
                    sm.getString("lifecycleSupport.stop.alreadyStopped", getName()));
        }
        triggerStatusChanged(LifecycleStatus.STOPPING);

        try {
            if (logger.isInfoEnabled()) {
                printHierarchicalInfo(
                        sm.getString("lifecycleSupport.stop.stopping", getName()));
            }

            stopInternal();
            triggerStatusChanged(LifecycleStatus.STOPPED);

            if (logger.isInfoEnabled()) {
                printHierarchicalInfo(
                        sm.getString("lifecycleSupport.stop.stopped", getName()));
            }
        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                printHierarchicalInfo(
                        sm.getString("lifecycleSupport.stop.error", getName()));
            }
            triggerStatusChanged(LifecycleStatus.ERROR);
            throw new LifecycleException(e);
        }
    }

    protected abstract void stopInternal() throws LifecycleException;

    /**
     * 注册事件的监听器
     *
     * @param listener
     */
    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (listeners) {
            LifecycleListener[] results = new LifecycleListener[listeners.length + 1];

            System.arraycopy(listeners, 0, results, 0, listeners.length);
            results[listeners.length] = listener;

            listeners = results;
        }
    }

    /**
     * 返回当已注册的监听器的集合，如果没有已注册的监听器，则返回长度为0的数组。
     */
    public LifecycleListener[] findLifecycleListeners() {
        return listeners;
    }

    /**
     * 从集合中移除指定监听器
     *
     * @param listener
     */
    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (listeners) {
            int k = -1;
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == listener) {
                    k = i;
                    break;
                }
            }
            if (k < 0) {
                return;
            }

            LifecycleListener[] results = new LifecycleListener[listeners.length - 1];
            int j = 0;
            for (int i = 0; i < listeners.length; i++) {
                if (i != k) {
                    results[j++] = listeners[i];
                }
            }
            listeners = results;
        }
    }

    /**
     * 获取组件名称
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName() + "[\"" + this.name + "\"]";
    }

    /**
     * 设置组件名称
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 使传入的生命周期组件与当前组件一致
     *
     * @param lifecycle
     */
    protected void synchronizeStatus(Lifecycle lifecycle) {

        if (status == LifecycleStatus.INITIALIZING
                || status == LifecycleStatus.INITIALIZED) {
            try {
                lifecycle.initialize();
            } catch (LifecycleException e) {
                e.printStackTrace(System.err);
            }
        }

        if (status == LifecycleStatus.STARTING || status == LifecycleStatus.STARTED) {
            try {
                lifecycle.start();
            } catch (LifecycleException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * 打印具有层级前缀的日志
     *
     * @param info
     */
    protected void printHierarchicalInfo(String info) {
        logger.info(StringUtils.repeats(Hierarchy.SPACES, getLevel() - 1)
                + (getLevel() > 0 ? Hierarchy.SYMBOL : "") + info);
    }

    /**
     * 将状态封装为事件，分发到已注册的监听器。
     *
     * @param newStatus
     */
    private void triggerStatusChanged(LifecycleStatus newStatus) {
        status = newStatus;

        LifecycleListener[] interested;
        synchronized (listeners) {
            interested = listeners.clone();
        }

        LifecycleEvent event = new LifecycleEvent(newStatus, null);
        for (int i = 0; i < interested.length; i++) {
            interested[i].lifecycleEvent(event);
        }
    }

}
