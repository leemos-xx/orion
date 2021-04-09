package leemos.orion.core;

import leemos.orion.remoting.RemotingException;

/**
 * 特指服务端异常
 *
 *
 * @author lihao
 * @date 2020年8月19日
 * @version 1.0
 */
public class ServerException extends RemotingException {

    private static final long serialVersionUID = -4195541135926619917L;

    public ServerException() {
    }

    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
