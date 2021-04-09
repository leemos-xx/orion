package leemos.orion.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

import io.netty.channel.ChannelHandler.Sharable;
import leemos.orion.commons.StringManager;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.InvokeFuture;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.heartbeat.HeartbeatResponse;
import leemos.orion.remoting.message.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Rpc响应消息客户端处理流程
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
@Sharable
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            RpcResponse rpcResponse = (RpcResponse) msg;
            
            if (logger.isDebugEnabled() && !(msg instanceof HeartbeatResponse)) {
                if (rpcResponse.getStatus() != null) {
                    if (rpcResponse.getStatus() == Status.SUCCESS) {
                        logger.debug(sm.getString("rpcClientHandler.channelRead.response", new String[] {
                                String.valueOf(rpcResponse.getId()), rpcResponse.getStatus().name(),
                                rpcResponse.getResponseBody() == null ? ""
                                        : JSONArray
                                                .toJSONString(rpcResponse.getResponseBody()) }));
                    } else {
                        logger.debug(sm.getString("rpcClientHandler.channelRead.response",
                                new String[] { String.valueOf(rpcResponse.getId()),
                                        rpcResponse.getStatus().name(), "" }));
                    }
                }
            }
            
            // 根据id，获取连接中匹配的future
            Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
            InvokeFuture future = connection.removeInvokeFuture(rpcResponse.getId());

            if (future != null) {
                // 将响应消息放入future中，若为callback模式，则尝试执行
                future.cancelTimeout();
                future.putResponse(rpcResponse);
                future.executeInvokeCallback();
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn(
                            sm.getString("rpcClientHandler.channelRead.futureNotFound",
                                    String.valueOf(rpcResponse.getId())));
                }
            }
        }
    }
}
