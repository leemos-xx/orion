package leemos.orion.remoting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import leemos.orion.Constants;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.RemotingUtils;

/**
 * 服务端连接空闲管理器，当触发{@link IdleStateEvent}事件时，关闭连接。
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class ServerIdleHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerIdleHandler.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        // 当服务端触发空闲事件时，证明至少在Configs.serverIdleTime()内未接收到对端心跳，
        // 因此认为对端已下线，直接关闭
        if (evt instanceof IdleStateEvent) {
            ctx.close();
            if (logger.isWarnEnabled()) {
                logger.warn(
                        sm.getString("serverIdleHandler.userEventTriggered.connClosed",
                                RemotingUtils.parseRemoteAddress(ctx.channel())));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
