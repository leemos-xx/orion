package leemos.orion.remoting.utils;

/**
 * {@link ExecutedTask}未执行时抛出该异常
 *
 * @author lihao
 * @date 2020年8月29日 下午8:51:42
 * @version 1.0
 *
 */
public class NotExecutedException extends Exception {

    private static final long serialVersionUID = 8812643116870803855L;

    public NotExecutedException() {

    }

    public NotExecutedException(String message) {
        super(message);
    }

    public NotExecutedException(String message, Throwable cause) {
        super(message, cause);
    }
}