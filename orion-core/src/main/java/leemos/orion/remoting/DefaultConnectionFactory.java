package leemos.orion.remoting;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import leemos.orion.Configs;
import leemos.orion.Constants;
import leemos.orion.codec.Codec;
import leemos.orion.commons.NamedThreadFactory;
import leemos.orion.commons.StringManager;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.lifecycle.LifecycleSupport;
import leemos.orion.remoting.heartbeat.HeartbeatHandler;
import leemos.orion.remoting.utils.EventLoopUtils;

/**
 * 连接工厂类的骨架实现
 *
 *
 * @author lihao
 * @date 2020年8月18日
 * @version 1.0
 */
public class DefaultConnectionFactory extends LifecycleSupport
        implements ConnectionFactory {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);
    private static final Logger logger = LoggerFactory
            .getLogger(DefaultConnectionFactory.class);

    private EventLoopGroup ioGroup;
    private Protocol protocol;
    private Codec codec;
    private ChannelHandler connectionEventHandler;
    private ChannelHandler rpcClientHandler;
    private ChannelHandler heartbeatHandler;

    private Bootstrap bootstrap;

    public DefaultConnectionFactory(Protocol protocol, Codec codec,
            ChannelHandler connectionEventHandler, ChannelHandler rpcClientHandler) {
        this.codec = codec;
        this.protocol = protocol;
        this.connectionEventHandler = connectionEventHandler;
        this.rpcClientHandler = rpcClientHandler;

        this.heartbeatHandler = new HeartbeatHandler(protocol.getHeartbeatTrigger());
    }

    /*
     * @see leemos.orion.LifecycleSupport#initializeInternal()
     */
    @Override
    protected void initializeInternal() throws LifecycleException {
        bootstrap = new Bootstrap();

        ioGroup = EventLoopUtils.newEventLoopGroup(
                // 默认为处理器数 * 2
                Configs.clientIoThreads() == -1
                        ? Runtime.getRuntime().availableProcessors() * 2
                        : Configs.clientIoThreads(),
                new NamedThreadFactory("orion-client-io", true));

        bootstrap.group(ioGroup).channel(EventLoopUtils.getSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // FIXME water mark

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(codec.newCoder());

                if (Configs.heartbeatEnabled()) {
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0,
                            Configs.clientIdleTime(), TimeUnit.MILLISECONDS));
                    pipeline.addLast("heartbeatHandler", heartbeatHandler);
                }
                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", rpcClientHandler);
            }
        });
    }

    /*
     * @see leemos.orion.LifecycleSupport#startInternal()
     */
    @Override
    protected void startInternal() throws LifecycleException {
        // do noting
    }

    /*
     * @see leemos.orion.LifecycleSupport#stopInternal()
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        if (ioGroup != null) {
            ioGroup.shutdownGracefully();
        }
    }

    /*
     * @see
     * leemos.orion.connector.ConnectionFactory#createConnection(leemos.
     * orion.connector.Url)
     */
    @Override
    public Connection createConnection(Url url) throws Exception {
        Channel channel = doCreateConnection(url.getIp(), url.getPort(),
                url.getConnectTimeout());
        Connection connection = new Connection(channel, protocol, url);
        if (channel.isActive()) {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        } else {
            channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
        }

        return connection;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    private Channel doCreateConnection(String ip, int port, int connectTimeout)
            throws Exception {
        String address = ip + UrlParser.COLON + port;
        connectTimeout = Math.max(connectTimeout, 1000);

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(ip, port));

        future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errorMsg = sm.getString(
                    "defaultConnectionFactory.doCreateConnection.timeout", address);
            logger.warn(errorMsg);
            throw new Exception(errorMsg);
        }
        if (future.isCancelled()) {
            String errorMsg = sm.getString(
                    "defaultConnectionFactory.doCreateConnection.cancelled", address);
            logger.warn(errorMsg);
            throw new Exception(errorMsg);
        }
        if (!future.isDone()) {
            String errorMsg = sm.getString(
                    "defaultConnectionFactory.doCreateConnection.error", address);
            logger.warn(errorMsg);
            throw new Exception(errorMsg);
        }
        return future.channel();
    }

}
