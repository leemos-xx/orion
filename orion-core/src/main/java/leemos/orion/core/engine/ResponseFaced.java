package leemos.orion.core.engine;

import io.netty.channel.Channel;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.message.RpcResponse;

/**
 * 用于在业务引擎中处理响应
 *
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class ResponseFaced {

    private Channel channel;
    private RpcResponse rpcResponse;

    public ResponseFaced(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    public Status getStatus() {
        if (rpcResponse != null) {
            return rpcResponse.getStatus();
        } else {
            return null;
        }
    }

    public Object getResponseBody() {
        if (rpcResponse != null) {
            return rpcResponse.getResponseBody();
        } else {
            return null;
        }
    }

}
