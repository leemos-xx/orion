package leemos.orion.remoting;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import leemos.orion.Constants;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.RemotingUtils;

/**
 * 将{@link SocketChannel}抽象为Connection
 *
 * @author lihao
 * @date 2020年8月11日
 * @version 1.0
 */
public class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    public static final AttributeKey<Connection> CONNECTION = AttributeKey
            .valueOf("connection");
    public static final AttributeKey<Integer> HEARTBEAT_FAILED_TIMES = AttributeKey
            .valueOf("heartbeatFailedTimes");
    public static final AttributeKey<Protocol> PROTOCOL = AttributeKey
            .valueOf("protocol");

    /**
     * 关联的{@link Channel}
     */
    private Channel channel;

    /**
     * 指定的url
     */
    private Url url;

    /**
     * 当前连接归属的连接池名
     */
    private String poolKey;

    /**
     * 协议
     */
    private Protocol protocol;

    /**
     * 连接是否关闭
     */
    private AtomicBoolean closed = new AtomicBoolean(false);

    private ConcurrentHashMap<Long, InvokeFuture> invokeFutureMap = new ConcurrentHashMap<Long, InvokeFuture>();
    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    public Connection(Channel channel, Protocol protocol, Url url) {
        this.channel = channel;
        this.url = url;
        this.init();
    }

    private void init() {
        channel.attr(CONNECTION).set(this);
        channel.attr(HEARTBEAT_FAILED_TIMES).set(0);
        channel.attr(PROTOCOL).set(protocol);
    }

    /**
     * 连接是否为活跃状态
     *
     * @return
     */
    public boolean isFine() {
        return channel != null && channel.isActive();
    }

    /**
     * 获取对端地址
     *
     * @return
     */
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    /**
     * 获取对端ip
     *
     * @return
     */
    public String getRemoteIP() {
        return RemotingUtils.parseRemoteIP(channel);
    }

    /**
     * 获取对端端口
     *
     * @return
     */
    public int getRemotePort() {
        return RemotingUtils.parseRemotePort(channel);
    }

    /**
     * 获取本地地址
     *
     * @return
     */
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) this.channel.localAddress();
    }

    /**
     * 获取本地ip
     *
     * @return
     */
    public String getLocalIP() {
        return RemotingUtils.parseLocalIP(this.channel);
    }

    /**
     * 获取本地端口
     *
     * @return
     */
    public int getLocalPort() {
        return RemotingUtils.parseLocalPort(this.channel);
    }

    /**
     * 返回{@link Channel}
     *
     * @return
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * 根据id返回{@link InvokeFuture}
     *
     * @param id
     * @return
     */
    public InvokeFuture getInvokeFuture(long id) {
        return invokeFutureMap.get(id);
    }

    /**
     * 关联{@link InvokeFuture}
     *
     * @param future
     * @return
     */
    public InvokeFuture addInvokeFuture(InvokeFuture future) {
        return invokeFutureMap.putIfAbsent(future.invokeId(), future);
    }

    /**
     * 解绑指定{@link InvokeFuture}
     *
     * @param id
     * @return
     */
    public InvokeFuture removeInvokeFuture(long id) {
        return invokeFutureMap.remove(id);
    }

    /**
     * 当前是否没有已关联的{@link InvokeFuture}
     *
     * @return
     */
    public boolean isInvokeFutureMapFinish() {
        return invokeFutureMap.isEmpty();
    }

    /**
     * 返回{@link InvokeFuture}的集合
     *
     * @return
     */
    public ConcurrentHashMap<Long, InvokeFuture> getInvokeFutureMap() {
        return invokeFutureMap;
    }

    /**
     * 设置关联的连接池名称
     *
     * @param poolKey
     */
    public void setPoolKey(String poolKey) {
        this.poolKey = poolKey;
    }

    /**
     * 获取关联的连接池名称
     *
     * @return
     */
    public String getPoolKey() {
        return poolKey;
    }

    /**
     * 返回url
     *
     * @return
     */
    public Url getUrl() {
        return url;
    }

    /**
     * 新增属性
     *
     * @param key
     * @param value
     * @return
     */
    public Object setAttributeIfAbsent(String key, Object value) {
        return attributes.putIfAbsent(key, value);
    }

    /**
     * 删除属性
     *
     * @param key
     */
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    /**
     * 获取属性值
     *
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * 清空所有属性设置
     */
    public void clearAttributes() {
        attributes.clear();
    }

    /**
     * 在关闭连接的时候，处理连接中关联的所有future
     */
    public void onClose() {
        Iterator<Entry<Long, InvokeFuture>> iter = invokeFutureMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, InvokeFuture> entry = iter.next();
            iter.remove();
            InvokeFuture future = entry.getValue();
            if (future != null) {
                future.putResponse(
                        future.createConnectionClosedResponse(this.getRemoteAddress()));
                future.cancelTimeout();
                future.executeInvokeCallback();
            }
        }
    }

    /**
     * 关联当前连接
     */
    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                if (this.getChannel() != null) {
                    this.getChannel().close().addListener(new ChannelFutureListener() {

                        @Override
                        public void operationComplete(ChannelFuture future)
                                throws Exception {
                            if (logger.isInfoEnabled()) {
                                logger.info(sm.getString("connection.close.complete",
                                        RemotingUtils.parseRemoteAddress(channel)));
                            }
                        }

                    });
                }
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(sm.getString("connection.close.error",
                            RemotingUtils.parseRemoteAddress(channel)));
                }
            }
        }
    }
}
