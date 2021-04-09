package leemos.orion.core.engine;

/**
 * 引擎在反射执行服务时出现异常
 *
 *
 * @author lihao
 * @date 2020年8月29日 上午10:50:22
 * @version 1.0
 *
 */
public class InvocationException extends Exception {

    private static final long serialVersionUID = 736366934797893565L;

    public InvocationException(String errorMsg) {
        super(errorMsg);
    }

    public InvocationException(Throwable t) {
        super(t);
    }

    public InvocationException(String errorMsg, Throwable t) {
        super(errorMsg, t);
    }

}
