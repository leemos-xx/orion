package leemos.orion.rpc;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import leemos.orion.Configs;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.RemotingUtils;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.DefaultInvokeFuture;
import leemos.orion.remoting.InvokeFuture;
import leemos.orion.remoting.Protocol;
import leemos.orion.remoting.TimerHolder;
import leemos.orion.remoting.heartbeat.HeartbeatRequest;
import leemos.orion.remoting.heartbeat.HeartbeatTrigger;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.RpcResponse;

/**
 * Rpc协议的心跳机制
 *
 * @author lihao
 * @date 2020年8月23日 下午6:23:31
 * @version 1.0
 *
 */
public class RpcHeartbeatTrigger implements HeartbeatTrigger {

    private static final Logger logger = LoggerFactory
            .getLogger(RpcHeartbeatTrigger.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private static final long HEARTBEAT_TIMEOUT_MILLS = 5000;
    private Protocol protocol;

    public RpcHeartbeatTrigger(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void triggerHeartbeatRequest(ChannelHandlerContext ctx) {

        int heartbeatFailedTimes = ctx.channel().attr(Connection.HEARTBEAT_FAILED_TIMES)
                .get();
        Connection connection = ctx.channel().attr(Connection.CONNECTION).get();

        if (heartbeatFailedTimes >= Configs.heartbeatMaxFailedTimes()) {
            connection.close();
            logger.error(sm.getString(
                    "rpcHeartbeatTrigger.triggerHeartbeatRequest.timesExceeded",
                    String.valueOf(Configs.heartbeatMaxFailedTimes()),
                    RemotingUtils.parseRemoteAddress(ctx.channel())));
        } else {
            MessageFactory messageFactory = protocol.getMessageFactory();
            HeartbeatRequest heartbeatReq = messageFactory.createHeartbeatRequest();
            long requestId = heartbeatReq.getId();

            InvokeFuture future = new DefaultInvokeFuture(requestId, messageFactory,
                    new RpcInvokeCallback() {

                        @Override
                        protected void onResponse(RpcResponse response) {
                            // 如果成功，则重置当前连接的心跳失败次数
                            ctx.channel().attr(Connection.HEARTBEAT_FAILED_TIMES).set(0);
                            if (logger.isInfoEnabled()) {
                                logger.info(sm.getString(
                                        "rpcHeartbeatTrigger.triggerHeartbeatRequest.rcvSuccess",
                                        String.valueOf(requestId),
                                        RemotingUtils.parseRemoteAddress(ctx.channel())));
                            }
                        }

                        @Override
                        protected void onException(Exception e) {
                            // 心跳失败次数自增1，并记录日志
                            int times = ctx.channel()
                                    .attr(Connection.HEARTBEAT_FAILED_TIMES).get();
                            ctx.channel().attr(Connection.HEARTBEAT_FAILED_TIMES)
                                    .set(times + 1);
                            if (logger.isInfoEnabled()) {
                                logger.error(sm.getString(
                                        "rpcHeartbeatTrigger.triggerHeartbeatRequest.rcvFailed",
                                        String.valueOf(Configs.heartbeatMaxFailedTimes()),
                                        RemotingUtils.parseRemoteAddress(ctx.channel())),
                                        e);
                            }
                        }
                    });
            connection.addInvokeFuture(future);

            Timeout timeout = TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    // 消息超时
                    connection.removeInvokeFuture(requestId);
                    future.putResponse(messageFactory
                            .createTimeoutResponse(connection.getRemoteAddress()));
                    future.executeInvokeCallback();
                }
            }, HEARTBEAT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);

            try {
                connection.getChannel().writeAndFlush(heartbeatReq)
                        .addListener(new ChannelFutureListener() {

                            @Override
                            public void operationComplete(ChannelFuture f)
                                    throws Exception {
                                if (!f.isSuccess()) {
                                    // 心跳消息发送异常，记录日志
                                    connection.removeInvokeFuture(requestId);
                                    future.cancelTimeout();
                                    future.putResponse(
                                            messageFactory.createSendFailedResponse(
                                                    connection.getRemoteAddress(),
                                                    f.cause()));
                                    future.executeInvokeCallback();
                                    logger.error(sm.getString(
                                            "rpcHeartbeatTrigger.triggerHeartbeatRequest.sendFailed",
                                            String.valueOf(requestId),
                                            RemotingUtils
                                                    .parseRemoteAddress(ctx.channel())),
                                            f.cause());
                                } else {
                                    logger.info(sm.getString(
                                            "rpcHeartbeatTrigger.triggerHeartbeatRequest.sendSuccess",
                                            String.valueOf(requestId), RemotingUtils
                                                    .parseRemoteAddress(ctx.channel())));
                                }
                            }
                        });
            } catch (Exception e) {
                // 心跳消息发送异常，记录日志
                connection.removeInvokeFuture(requestId);
                future.cancelTimeout();
                future.putResponse(messageFactory
                        .createSendFailedResponse(connection.getRemoteAddress(), e));
                future.executeInvokeCallback();
                logger.error(sm.getString(
                        "rpcHeartbeatTrigger.triggerHeartbeatRequest.sendFailed",
                        String.valueOf(requestId),
                        RemotingUtils.parseRemoteAddress(ctx.channel())), e);
            }

        }

    }

}
