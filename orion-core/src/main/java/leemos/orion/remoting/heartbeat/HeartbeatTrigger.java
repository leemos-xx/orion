package leemos.orion.remoting.heartbeat;

import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳消息触发器，针对不同协议，需做不同实现
 *
 *
 * @author lihao
 * @date 2020年8月23日 下午1:46:47
 * @version 1.0
 *
 */
public interface HeartbeatTrigger {

    /**
     * 触发心跳消息
     *
     * @param ctx
     */
    public void triggerHeartbeatRequest(ChannelHandlerContext ctx);
}
