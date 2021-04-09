package leemos.orion.remoting;

import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import leemos.orion.Constants;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.RemotingUtils;

/**
 * 记录连接事件日志，并触发相应动作
 *
 *
 * @author lihao
 * @date 2020年8月20日
 * @version 1.0
 */
@Sharable
public class ConnectionEventHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory
            .getLogger(ConnectionEventHandler.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
            SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("connectionEventHandler.connect.info",
                    remoteAddress == null ? "UNKNOWN"
                            : RemotingUtils.parseSocketAddressToString(remoteAddress)));
        }
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise)
            throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("connectionEventHandler.disconnect.info",
                    RemotingUtils.parseRemoteAddress(ctx.channel())));
        }
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise)
            throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("connectionEventHandler.close.info",
                    RemotingUtils.parseRemoteAddress(ctx.channel())));
        }
        Attribute<Connection> attr = ctx.channel().attr(Connection.CONNECTION);
        if (null != attr) {
            Connection connection = (Connection) attr.get();
            if (connection != null) {
                connection.onClose();
            }
        }
        super.close(ctx, promise);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("connectionEventHandler.channelRegistered.info",
                    RemotingUtils.parseRemoteAddress(ctx.channel())));
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("connectionEventHandler.channelUnregistered.info",
                    RemotingUtils.parseRemoteAddress(ctx.channel())));
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("connectionEventHandler.channelActive.info",
                    RemotingUtils.parseRemoteAddress(ctx.channel())));
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info(sm.getString("connectionEventHandler.channelInactive.info",
                    RemotingUtils.parseRemoteAddress(ctx.channel())));
        }
        super.channelInactive(ctx);
        Attribute<Connection> attr = ctx.channel().attr(Connection.CONNECTION);
        if (null != attr) {
            Connection connection = (Connection) attr.get();
            if (connection != null) {
                userEventTriggered(ctx, ConnectionEventType.CLOSE);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof ConnectionEventType) {
            ConnectionEventType eventType = (ConnectionEventType) evt;
            Channel channel = ctx.channel();

            Connection connection = channel.attr(Connection.CONNECTION).get();
            switch (eventType) {
            case CONNECT:
                break;
            case CONNECT_FAILED:
            case CLOSE:
            case EXCEPTION:
                submitReconnectTask(connection.getUrl());
                break;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

    private void submitReconnectTask(Url url) {
        // FIXME
    }

}
