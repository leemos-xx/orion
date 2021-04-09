package leemos.orion.core.engine;

/**
 * 注解扫描时出现异常
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class ScannotationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ScannotationException(Throwable throwable) {
        super(throwable);
    }

    public ScannotationException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

}
