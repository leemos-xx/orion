package leemos.orion.lifecycle;

/**
 * 枚举生命周期过程中的所有可能出现的状态
 *
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public enum LifecycleStatus {

    /**
     * 当前组件刚被实例化，处于原始状态。
     */
    NEW,

    /**
     * 组件正在初始化过程中
     */
    INITIALIZING,

    /**
     * 组件已初始化完成
     */
    INITIALIZED,

    /**
     * 组件正在启动过程中
     */
    STARTING,

    /**
     * 组件启动完成
     */
    STARTED,

    /**
     * 组件正在停止过程中
     */
    STOPPING,

    /**
     * 组件已停止
     */
    STOPPED,

    /**
     * 组件处在异常状态
     */
    ERROR;

    private LifecycleStatus() {
    }

}
