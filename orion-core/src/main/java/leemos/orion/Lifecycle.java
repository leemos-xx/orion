package leemos.orion;

import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.lifecycle.LifecycleListener;

/**
 * 定义组件的生命周期
 * 
 * 对于核心组件，都应该直接或间接的实现该接口，以便提供统一的生命周期状态管理机制。
 *
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public interface Lifecycle extends Hierarchy {

    /**
     * 注册生命周期事件监听器，当生命周期状态发生改变时，监听器将会得到通知。
     *
     * @param listener
     */
    public void addLifecycleListener(LifecycleListener listener);

    /**
     * 返回当前生命周期组件关联的监听器的集合，如果没有已注册的监听器，则返回长度为0的数组。
     */
    public LifecycleListener[] findLifecycleListeners();

    /**
     * 从集合中移除指定的监听器
     *
     * @param listener
     */
    public void removeLifecycleListener(LifecycleListener listener);

    /**
     * 用于进行组件内部的初始化操作，如初始的赋值操作、配置修改等。
     * 
     * <ul>
     *   <li>先执行本方法后，才能执行组件的start方法；</li>
     *   <li>在该方法中会修改组件为INITIALIZING & INITIALIZED状态，并分发事件；</li>
     * </ul>
     *
     * @throws LifecycleException
     */
    public void initialize() throws LifecycleException;

    /**
     * 启动该组件，使该组件成为一个对外可用的状态。
     * 
     * <ul>
     *   <li>应该保证在组件的其它公开方法前被调用前，先调用该方法；</li>
     *   <li>在该方法中会修改组件为STARTING & STARTED状态，并分发事件</li>
     * </ul>
     *
     * @throws LifecycleException
     */
    public void start() throws LifecycleException;

    /**
     * 停止当前的组件，应当类似对initialize & start方法的逆操作。
     * 
     * <ul>
     *   <li>该方法执行后，组件被销毁，所有公共方法应该不允许被调用；</li>
     *   <li>在该方法中会修改组件为STOPPING & STOPPED状态，并分发事件</li>
     * </ul>
     *
     * @throws LifecycleException
     */
    public void stop() throws LifecycleException;
}
