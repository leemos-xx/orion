package leemos.orion.core.bus;

import io.netty.channel.Channel;
import leemos.orion.remoting.message.RpcRequest;

/**
 * 请求事件，内部包括{@link RpcRequest}和{@link Channel}
 *
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class RequestEvent extends Event {

    /**
     * Rpc请求消息
     */
    private RpcRequest rpcRequest;

    /**
     * 关联的channel
     */
    private Channel channel;

    public RequestEvent(RpcRequest rpcRequest, Channel channel) {
        this.rpcRequest = rpcRequest;
        this.channel = channel;
    }

    /*
     * @see leemos.orion.Event#getId()
     */
    @Override
    public long getId() {
        return rpcRequest.getId();
    }

    public RpcRequest getRpcRequest() {
        return rpcRequest;
    }

    public Channel getChannel() {
        return channel;
    }

}
