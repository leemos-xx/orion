package leemos.orion.codec;

import leemos.orion.remoting.RemotingException;

/**
 * 特指编解码过程中抛出的异常
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class CodecException extends RemotingException {

    private static final long serialVersionUID = 1L;

    public CodecException(String msg) {
        super(msg);
    }

    public CodecException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}
