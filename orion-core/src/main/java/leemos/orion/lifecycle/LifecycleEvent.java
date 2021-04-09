package leemos.orion.lifecycle;

/**
 * 定义生命周期过程中的事件。当组件状态改变时，会封装成该事件实例，通知到已注册的监听器。
 *
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public final class LifecycleEvent {

    /**
     * 组件的最新状态
     */
    private LifecycleStatus status;

    /**
     * 需要传递的数据
     */
    private Object data;

    public LifecycleEvent(LifecycleStatus status) {
        this(status, null);
    }

    public LifecycleEvent(LifecycleStatus status, Object data) {
        this.status = status;
        this.data = data;
    }

    /**
     * 获取组件最新状态
     */
    public LifecycleStatus getStatus() {
        return status;
    }

    /**
     * 返回事件相关数据对象
     */
    public Object getData() {
        return data;
    }
}
