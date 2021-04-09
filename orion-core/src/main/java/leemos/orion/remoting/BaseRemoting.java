package leemos.orion.remoting;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import leemos.orion.Constants;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.RemotingUtils;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.RpcRequest;
import leemos.orion.remoting.message.RpcResponse;

/**
 * 远程调用的基础网络模型实现，目前支持单向、同步和异步请求
 *
 * @author lihao
 * @date 2020年8月18日
 * @version 1.0
 */
public abstract class BaseRemoting {

    private static final Logger logger = LoggerFactory.getLogger(BaseRemoting.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    protected MessageFactory messageFactory;

    public BaseRemoting(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * 单向请求
     *
     * @param connection
     * @param rpcRequest
     */
    protected void oneway(Connection connection, RpcRequest rpcRequest) {
        try {
            // 仅考虑单向消息发送，当消息发送失败时记录日志
            connection.getChannel().writeAndFlush(rpcRequest)
                    .addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future)
                                throws Exception {
                            if (!future.isSuccess()) {
                                logger.error(
                                        sm.getString("baseRemoting.oneway.sendFailed",
                                                RemotingUtils.parseRemoteAddress(
                                                        connection.getChannel())),
                                        future.cause());
                            } else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(sm.getString(
                                            "baseRemoting.oneway.sendSuccess",
                                            String.valueOf(rpcRequest.getId())));
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 发送请求，并同步等待响应结果
     *
     * @param connection
     * @param rpcRequest
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    protected RpcResponse invoke(Connection connection, RpcRequest rpcRequest,
            int timeoutMillis) throws InterruptedException {
        InvokeFuture future = createInvokeFuture(rpcRequest);
        connection.addInvokeFuture(future);

        long requestId = rpcRequest.getId();
        try {
            connection.getChannel().writeAndFlush(rpcRequest)
                    .addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture f) throws Exception {
                            if (!f.isSuccess()) {
                                // 如果消息发送失败，则返回状态为CLIENT_EXCEPTION的消息，并记录日志
                                connection.removeInvokeFuture(requestId);
                                future.putResponse(
                                        messageFactory.createSendFailedResponse(
                                                connection.getRemoteAddress(),
                                                f.cause()));
                                logger.error(
                                        sm.getString("baseRemoting.invokeSync.sendFailed",
                                                String.valueOf(requestId)),
                                        f.cause());
                            } else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(sm.getString(
                                            "baseRemoting.invokeSync.sendSuccess",
                                            String.valueOf(rpcRequest.getId())));
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            // 如果消息发送失败，则返回状态为CLIENT_EXCEPTION的消息，并记录日志
            connection.removeInvokeFuture(requestId);
            future.putResponse(messageFactory
                    .createSendFailedResponse(connection.getRemoteAddress(), e));
            logger.error(sm.getString("baseRemoting.invokeSync.sendFailed",
                    String.valueOf(requestId)), e);
        }

        // 同步等待调用结果，若timeoutMillis时间内为响应，则返回超时结果
        RpcResponse response = future.waitResponse(timeoutMillis);
        if (response == null) {
            // 此时如果response仍然为null，则认为服务端响应超时，构造超时信息
            connection.removeInvokeFuture(requestId);
            response = messageFactory
                    .createTimeoutResponse(connection.getRemoteAddress());
            if (logger.isWarnEnabled()) {
                logger.warn(sm.getString("baseRemoting.invokeSync.timeout",
                        String.valueOf(requestId)));
            }
        }

        return response;
    }

    /**
     * 发送请求，可通过{@link InvokeFuture}异步获取响应
     *
     * @param connection
     * @param rpcRequest
     * @param timeoutMillis
     * @return
     */
    protected InvokeFuture invokeAsync(Connection connection, RpcRequest rpcRequest,
            int timeoutMillis) {
        final InvokeFuture future = createInvokeFuture(rpcRequest);
        connection.addInvokeFuture(future);
        final long requestId = rpcRequest.getId();
        try {
            // 通过Timer进行超时控制
            Timeout timeout = TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    // 超时则记录日志并返回超时消息
                    connection.removeInvokeFuture(requestId);
                    future.putResponse(messageFactory
                            .createTimeoutResponse(connection.getRemoteAddress()));
                    if (logger.isWarnEnabled()) {
                        logger.warn(sm.getString("baseRemoting.invokeWithFuture.timeout",
                                String.valueOf(requestId)));
                    }
                }
            }, timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);

            connection.getChannel().writeAndFlush(rpcRequest)
                    .addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture f) throws Exception {
                            if (!f.isSuccess()) {
                                // 返回发送失败消息
                                connection.removeInvokeFuture(requestId);
                                future.cancelTimeout();
                                future.putResponse(
                                        messageFactory.createSendFailedResponse(
                                                connection.getRemoteAddress(),
                                                f.cause()));
                                logger.error(sm.getString(
                                        "baseRemoting.invokeWithFuture.sendFailed",
                                        String.valueOf(requestId)), f.cause());
                            } else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(sm.getString(
                                            "baseRemoting.invokeWithFuture.sendSuccess",
                                            String.valueOf(rpcRequest.getId())));
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            // 返回发送失败消息
            connection.removeInvokeFuture(requestId);
            future.cancelTimeout();
            future.putResponse(messageFactory
                    .createSendFailedResponse(connection.getRemoteAddress(), e));
            logger.error(sm.getString("baseRemoting.invokeSync.sendFailed",
                    String.valueOf(requestId)), e);
        }

        return future;
    }

    /**
     * 发送请求，响应采用回调模式
     *
     * @param connection
     * @param rpcRequest
     * @param callback
     * @param timeoutMillis
     */
    protected void invokeAsync(Connection connection, RpcRequest rpcRequest,
            InvokeCallback callback, int timeoutMillis) {
        final InvokeFuture future = createInvokeFuture(rpcRequest, callback);
        connection.addInvokeFuture(future);
        final long requestId = rpcRequest.getId();
        try {
            // 通过Timer进行超时控制
            Timeout timeout = TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    connection.removeInvokeFuture(requestId);
                    future.putResponse(messageFactory
                            .createTimeoutResponse(connection.getRemoteAddress()));
                    future.executeInvokeCallback();
                }
            }, timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);

            connection.getChannel().writeAndFlush(rpcRequest)
                    .addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture f) throws Exception {
                            if (!f.isSuccess()) {
                                connection.removeInvokeFuture(requestId);
                                future.cancelTimeout();
                                future.putResponse(
                                        messageFactory.createSendFailedResponse(
                                                connection.getRemoteAddress(),
                                                f.cause()));
                                future.executeInvokeCallback();
                                logger.error(sm.getString(
                                        "baseRemoting.invokeWithFuture.sendFailed",
                                        String.valueOf(requestId)), f.cause());
                            } else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(sm.getString(
                                            "baseRemoting.invokeWithFuture.sendSuccess",
                                            String.valueOf(rpcRequest.getId())));
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            connection.removeInvokeFuture(requestId);
            future.cancelTimeout();
            future.putResponse(messageFactory
                    .createSendFailedResponse(connection.getRemoteAddress(), e));
            future.executeInvokeCallback();
            logger.error(sm.getString("baseRemoting.invokeSync.sendFailed",
                    String.valueOf(requestId)), e);
        }
    }

    protected abstract InvokeFuture createInvokeFuture(RpcRequest rpcRequest);

    protected abstract InvokeFuture createInvokeFuture(RpcRequest rpcRequest,
            InvokeCallback callback);
}
