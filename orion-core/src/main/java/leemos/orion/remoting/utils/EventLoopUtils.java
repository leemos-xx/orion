package leemos.orion.remoting.utils;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import leemos.orion.Configs;
import leemos.orion.commons.NamedThreadFactory;

/**
 * Event loop utils.
 *
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class EventLoopUtils {

    /**
     * 是否支持epoll模式
     */
    // FIXME
    private static boolean epollEnabled = Configs.epollEnabled();// &&
                                                                 // Epoll.isAvailable();

    public static EventLoopGroup newEventLoopGroup(int nThreads,
            NamedThreadFactory threadFactory) {
        return epollEnabled ? new EpollEventLoopGroup(nThreads, threadFactory)
                : new NioEventLoopGroup(nThreads, threadFactory);
    }

    public static Class<? extends ServerChannel> getServerChannelClass() {
        return epollEnabled ? EpollServerSocketChannel.class
                : NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> getSocketChannelClass() {
        return epollEnabled ? EpollSocketChannel.class : NioSocketChannel.class;
    }
}
