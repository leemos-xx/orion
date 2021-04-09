package leemos.orion.lifecycle;

/**
 * 该异常特指影响容器生命周期正常执行的问题，当出现该类问题时，一般代表组件无法继续当前生命周期的事件。
 *
 * @author lihao
 * @date 2020年8月4日
 * @version 1.0
 */
public class LifecycleException extends Exception {

    private static final long serialVersionUID = 1L;

    public LifecycleException() {

    }

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(Throwable throwable) {
        super(throwable);
    }

    public LifecycleException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
