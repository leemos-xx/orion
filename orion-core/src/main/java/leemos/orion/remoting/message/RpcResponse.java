package leemos.orion.remoting.message;

/**
 * RpcReponse
 *
 *
 * @author lihao
 * @date 2020年8月26日
 * @version 1.0
 */
public class RpcResponse extends RpcMessage {

    private static final long serialVersionUID = 8039877472412786788L;

    /**
     * 响应消息的业务数据，如果服务端异常，则该字段为{@link Throwable}
     */
    private Object responseBody;

    public RpcResponse(byte protocolCode, byte protocolVersion) {
        super(protocolCode, protocolVersion);
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public Object getResponseBody() {
        return responseBody;
    }
}
