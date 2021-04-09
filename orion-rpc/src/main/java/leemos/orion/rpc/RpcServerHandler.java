package leemos.orion.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import leemos.orion.EventBus;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.RemotingUtils;
import leemos.orion.core.bus.EventDiscardException;
import leemos.orion.core.bus.RequestEvent;
import leemos.orion.core.engine.RequestFaced;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.Protocol;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.heartbeat.HeartbeatRequest;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.RpcRequest;

/**
 * Rpc消息处理核心流程
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private EventBus eventBus;

    public RpcServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Protocol protocol = ctx.channel().attr(Connection.PROTOCOL).get();
        MessageFactory messageFactory = protocol.getMessageFactory();

        // 当消息类型为心跳时，直接在当前线程中响应，
        // 若为业务消息，则由总线发布到业务线程池
        if (msg instanceof HeartbeatRequest) {
            HeartbeatRequest heartbeatReq = (HeartbeatRequest) msg;

            if (logger.isDebugEnabled()) {
                logger.debug(sm.getString("rpcServerHandler.channelRead.heartbeatRcv",
                        String.valueOf(((HeartbeatRequest) msg).getId())));
            }

            ctx.channel()
                    .writeAndFlush(messageFactory.createHeartbeatResponse(heartbeatReq))
                    .addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future)
                                throws Exception {
                            if (!future.isSuccess()) {
                                if (logger.isWarnEnabled()) {
                                    logger.warn(sm.getString(
                                            "rpcServerHandler.channelRead.heartbeatAckFailed",
                                            RemotingUtils.parseRemoteAddress(
                                                    future.channel())));
                                }
                            } else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(sm.getString(
                                            "rpcServerHandler.channelRead.heartbeatAck",
                                            String.valueOf(
                                                    ((HeartbeatRequest) msg).getId())));
                                }
                            }
                        }
                    });
        } else if (msg instanceof RpcRequest) {
            RpcRequest rpcRequest = (RpcRequest) msg;

            try {
                RequestEvent requestEvent = new RequestEvent(rpcRequest, ctx.channel());

                // 将请求事件发布到总线
                eventBus.postEvent(requestEvent);
            } catch (EventDiscardException e) {
                // 当出现该异常，说明服务端处理能力不够，则直接在io线程中返回服务器繁忙消息
                RequestFaced request = new RequestFaced(rpcRequest);

                ctx.channel()
                        .writeAndFlush(messageFactory.createExceptionResponse(e,
                                e.getMessage(), Status.SERVER_BUSY, request))
                        .addListener(new ChannelFutureListener() {

                            @Override
                            public void operationComplete(ChannelFuture future)
                                    throws Exception {
                                if (!future.isSuccess()) {
                                    if (logger.isWarnEnabled()) {
                                        logger.warn(sm.getString(
                                                "rpcServerHandler.channelRead.busyAck",
                                                RemotingUtils.parseRemoteAddress(
                                                        future.channel())));
                                    }
                                }
                            }
                        });
            }

        } else {
            logger.error(sm.getString("rpcServerHandler.channelRead.msgNotSupported",
                    msg.getClass().getName()));
        }
    }
}
