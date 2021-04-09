package leemos.orion.remoting;

/**
 * 远程相关异常的基类
 *
 *
 * @author lihao
 * @date 2020年8月26日
 * @version 1.0
 */
public class RemotingException extends Exception {

    private static final long serialVersionUID = 3344500725217182767L;

    public RemotingException() {

    }

    public RemotingException(Throwable throwable) {
        super(throwable);
    }

    public RemotingException(String errorMsg) {
        super(errorMsg);
    }

    public RemotingException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }

}
