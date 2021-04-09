package leemos.orion.core.bus;

import io.netty.channel.Channel;
import leemos.orion.remoting.message.RpcResponse;

/**
 * 响应事件，内部包含{@link RpcResponse}和{@link Channel}
 *
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class ResponseEvent extends Event {

    private RpcResponse rpcResponse;
    private Channel channel;

    public ResponseEvent(RpcResponse rpcResponse, Channel channel) {
        this.rpcResponse = rpcResponse;
        this.channel = channel;
    }

    /*
     * @see leemos.orion.Event#getId()
     */
    @Override
    public long getId() {
        return rpcResponse.getId();
    }

    public RpcResponse getRpcResponse() {
        return rpcResponse;
    }

    public Channel getChannel() {
        return channel;
    }
}