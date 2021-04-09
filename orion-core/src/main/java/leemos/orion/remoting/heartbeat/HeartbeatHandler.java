package leemos.orion.remoting.heartbeat;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Heartbeat handler.
 *
 *
 * @author lihao
 * @date 2020年8月23日 下午6:37:47
 * @version 1.0
 *
 */
@Sharable
public class HeartbeatHandler extends ChannelDuplexHandler {

    private HeartbeatTrigger heartbeatTrigger;

    public HeartbeatHandler(HeartbeatTrigger heartbeatTrigger) {
        this.heartbeatTrigger = heartbeatTrigger;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 触发心跳请求
            heartbeatTrigger.triggerHeartbeatRequest(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}
