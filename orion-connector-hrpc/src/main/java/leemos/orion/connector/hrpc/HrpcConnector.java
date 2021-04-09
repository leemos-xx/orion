package leemos.orion.connector.hrpc;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import leemos.orion.Configs;
import leemos.orion.codec.hrpc.HrpcProtocol;
import leemos.orion.codec.hrpc.HrpcServerCodec;
import leemos.orion.commons.NamedThreadFactory;
import leemos.orion.core.ConnectorBase;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.remoting.ConnectionEventHandler;
import leemos.orion.remoting.ServerIdleHandler;
import leemos.orion.remoting.utils.EventLoopUtils;
import leemos.orion.rpc.RpcServerHandler;

/**
 * 基于http协议的连接器实现
 *
 *
 * @author lihao
 * @date 2020年9月8日
 * @version 1.0
 */
public class HrpcConnector extends ConnectorBase {

    private static Logger logger = LoggerFactory.getLogger(HrpcConnector.class);

    private ServerBootstrap bootstrap;
    private EventLoopGroup acceptorGroup;
    private EventLoopGroup ioGroup;
    private ChannelFuture channelFuture;

    private HrpcProtocol protocol = new HrpcProtocol();
    private HrpcServerCodec codec = new HrpcServerCodec(protocol);

    public HrpcConnector(int port) {
        super(port);
    }

    public HrpcConnector(String ip, int port) {
        super(ip, port);
    }

    @Override
    protected void initializeInternal() throws LifecycleException {
        bootstrap = new ServerBootstrap();
        acceptorGroup = EventLoopUtils.newEventLoopGroup(1,
                new NamedThreadFactory("http-server-acceptor", false));
        ioGroup = EventLoopUtils.newEventLoopGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                new NamedThreadFactory("http-server-io", true));

        bootstrap.group(acceptorGroup, ioGroup)
                .channel(EventLoopUtils.getServerChannelClass())
                .option(ChannelOption.SO_BACKLOG, 1024)
//                .option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(codec.newCoder());

                if (Configs.heartbeatEnabled()) {
                    pipeline.addLast(new IdleStateHandler(0, 0, Configs.serverIdleTime(),
                            TimeUnit.MILLISECONDS));
                    pipeline.addLast(new ServerIdleHandler());
                }

                pipeline.addLast("connectionEventHandler", new ConnectionEventHandler());
                pipeline.addLast(new RpcServerHandler(getEventBus()));
            }

        });
    }

    @Override
    protected void startInternal() throws LifecycleException {
        try {
            channelFuture = bootstrap.bind(new InetSocketAddress(getIp(), getPort()))
                    .sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new LifecycleException(e);
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        acceptorGroup.shutdownGracefully();
        ioGroup.shutdownGracefully();
    }

}
