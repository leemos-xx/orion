package leemos.orion.core.bus;

import leemos.orion.remoting.RemotingException;

/**
 * 事件被丢弃时抛出该异常
 *
 *
 * @author lihao
 * @date 2020年8月27日
 * @version 1.0
 */
public class EventDiscardException extends RemotingException {

    private static final long serialVersionUID = -8700291334243268925L;

    public EventDiscardException(String errorMsg) {
        super(errorMsg);
    }

    public EventDiscardException(Throwable e) {
        super(e);
    }
}
