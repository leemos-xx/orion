package leemos.orion.remoting.utils;

/**
 * {@link ExecutedTask}未执行完成时抛出该异常
 *
 *
 * @author lihao
 * @date 2020年8月29日 下午8:52:31
 * @version 1.0
 *
 */
public class NotCompletedException extends Exception {

    private static final long serialVersionUID = 787950012095477780L;

    public NotCompletedException() {

    }

    public NotCompletedException(String message) {
        super(message);
    }

    public NotCompletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
