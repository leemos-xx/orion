package leemos.orion.remoting.message;

/**
 * RpcRequest
 *
 *
 * @author lihao
 * @date 2020年8月26日
 * @version 1.0
 */
public class RpcRequest extends RpcMessage {

    private static final long serialVersionUID = 4609962225712177045L;

    public RpcRequest(byte protocolCode, byte protocolVersion) {
        super(protocolCode, protocolVersion);
    }

    /**
     * 序列化后的请求对象
     */
    private RequestBody requestBody;

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

}
