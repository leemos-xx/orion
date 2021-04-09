package leemos.orion.lifecycle;

/**
 * 定义生命周期过程中的事件监听器
 *
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public interface LifecycleListener {

    /**
     * 当组件的生命周期状态发生改变时，将触发该方法。
     *
     * @param event
     */
    public void lifecycleEvent(LifecycleEvent event);
}
