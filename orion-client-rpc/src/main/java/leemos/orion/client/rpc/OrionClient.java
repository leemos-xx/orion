package leemos.orion.client.rpc;

import io.netty.channel.ChannelHandler;
import leemos.orion.client.Client;
import leemos.orion.codec.Codec;
import leemos.orion.codec.rpc.OrionCodec;
import leemos.orion.codec.rpc.OrionProtocol;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.lifecycle.LifecycleSupport;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.ConnectionEventHandler;
import leemos.orion.remoting.ConnectionManager;
import leemos.orion.remoting.DefaultConnectionFactory;
import leemos.orion.remoting.DefaultConnectionManager;
import leemos.orion.remoting.InvokeCallback;
import leemos.orion.remoting.InvokeFuture;
import leemos.orion.remoting.Protocol;
import leemos.orion.remoting.RandomSelectStrategy;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.SelectStrategy;
import leemos.orion.remoting.Url;
import leemos.orion.remoting.UrlParser;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcResponse;
import leemos.orion.rpc.RpcClientHandler;
import leemos.orion.rpc.RpcRemoting;
import leemos.orion.rpc.RpcUrlParser;

/**
 * Rpc客户端实现，用于与服务端通信的核心接口实现，同时也实现连接管理、连接事件监听等功能
 *
 * @author lihao
 * @date 2020年8月20日
 * @version 1.0
 */
public class OrionClient extends LifecycleSupport implements Client {

    private Protocol protocol;
    private Codec codec;
    private RpcRemoting remoting;
    private ConnectionManager connectionManager;

    /*
     * @see leemos.orion.LifecycleSupport#initializeInternal()
     */
    @Override
    protected void initializeInternal() throws LifecycleException {
        this.protocol = new OrionProtocol();
        this.codec = new OrionCodec(protocol);

        ChannelHandler connectionEventHandler = new ConnectionEventHandler();
        ChannelHandler rpclientHandler = new RpcClientHandler();
        UrlParser addressParser = new RpcUrlParser(protocol);
        SelectStrategy<Connection> selectStrategy = new RandomSelectStrategy<Connection>();

        // 初始化连接管理器，用于生成和管理连接状态
        connectionManager = new DefaultConnectionManager(
                new DefaultConnectionFactory(protocol, codec, connectionEventHandler,
                        rpclientHandler),
                selectStrategy);

        // 客户端远程调用实现类
        remoting = new RpcRemoting(protocol.getMessageFactory(), addressParser,
                connectionManager);
    }

    /*
     * @see leemos.orion.LifecycleSupport#startInternal()
     */
    @Override
    protected void startInternal() throws LifecycleException {
        connectionManager.start();
    }

    /*
     * @see leemos.orion.LifecycleSupport#stopInternal()
     */
    @Override
    protected void stopInternal() throws LifecycleException {
        connectionManager.stop();
    }

    /*
     * @see leemos.orion.Client#oneway(java.lang.String,
     * leemos.orion.remoting.RequestBody)
     */
    @Override
    public void oneway(String address, RequestBody requestBody)
            throws RemotingException, InterruptedException {
        remoting.oneway(address, requestBody);
    }

    /*
     * @see leemos.orion.Client#oneway(leemos.orion.remoting.Url,
     * leemos.orion.remoting.RequestBody)
     */
    @Override
    public void oneway(Url url, RequestBody requestBody)
            throws RemotingException, InterruptedException {
        remoting.oneway(url, requestBody);
    }

    /*
     * @see leemos.orion.Client#oneway(leemos.orion.remoting.Connection,
     * leemos.orion.remoting.RequestBody)
     */
    @Override
    public void oneway(Connection conn, RequestBody requestBody)
            throws RemotingException {
        remoting.oneway(conn, requestBody);
    }

    /*
     * @see leemos.orion.Client#invokeSync(java.lang.String,
     * leemos.orion.remoting.RequestBody, int)
     */
    @Override
    public RpcResponse invoke(String address, RequestBody requestBody, int timeoutMillis)
            throws RemotingException, InterruptedException {
        return remoting.invoke(address, requestBody, timeoutMillis);
    }

    /*
     * @see leemos.orion.Client#invokeSync(leemos.orion.remoting.Url,
     * leemos.orion.remoting.RequestBody, int)
     */
    @Override
    public RpcResponse invoke(Url url, RequestBody requestBody, int timeoutMillis)
            throws RemotingException, InterruptedException {
        return remoting.invoke(url, requestBody, timeoutMillis);
    }

    /*
     * @see leemos.orion.Client#invokeSync(leemos.orion.remoting.Connection,
     * leemos.orion.remoting.RequestBody, int)
     */
    @Override
    public RpcResponse invoke(Connection conn, RequestBody requestBody, int timeoutMillis)
            throws RemotingException, InterruptedException {
        return remoting.invoke(conn, requestBody, timeoutMillis);
    }

    /*
     * @see leemos.orion.Client#invokeWithFuture(java.lang.String,
     * leemos.orion.remoting.RequestBody, int)
     */
    @Override
    public InvokeFuture invokeAsync(String address, RequestBody requestBody,
            int timeoutMillis) throws RemotingException, InterruptedException {
        return remoting.invokeAsync(address, requestBody, timeoutMillis);
    }

    /*
     * @see leemos.orion.Client#invokeWithFuture(leemos.orion.remoting.Url,
     * leemos.orion.remoting.RequestBody, int)
     */
    @Override
    public InvokeFuture invokeAsync(Url url, RequestBody requestBody, int timeoutMillis)
            throws RemotingException, InterruptedException {
        return remoting.invokeAsync(url, requestBody, timeoutMillis);
    }

    /*
     * @see leemos.orion.Client#invokeWithFuture(leemos.orion.remoting.
     * Connection, leemos.orion.remoting.RequestBody, int)
     */
    @Override
    public InvokeFuture invokeAsync(Connection conn, RequestBody requestBody,
            int timeoutMillis) throws RemotingException, InterruptedException {
        return remoting.invokeAsync(conn, requestBody, timeoutMillis);
    }

    /*
     * @see leemos.orion.Client#invokeAsync(java.lang.String,
     * leemos.orion.remoting.RequestBody,
     * leemos.orion.remoting.InvokeCallback, int)
     */
    @Override
    public void invokeAsync(String address, RequestBody requestBody,
            InvokeCallback callback, int timeoutMillis)
            throws RemotingException, InterruptedException {
        remoting.invokeAsync(address, requestBody, callback, timeoutMillis);
    }

    /*
     * @see leemos.orion.Client#invokeAsync(leemos.orion.remoting.Url,
     * leemos.orion.remoting.RequestBody,
     * leemos.orion.remoting.InvokeCallback, int)
     */
    @Override
    public void invokeAsync(Url url, RequestBody requestBody, InvokeCallback callback,
            int timeoutMillis) throws RemotingException, InterruptedException {
        remoting.invokeAsync(url, requestBody, callback, timeoutMillis);
    }

    /*
     * @see
     * leemos.orion.Client#invokeAsync(leemos.orion.remoting.Connection,
     * leemos.orion.remoting.RequestBody,
     * leemos.orion.remoting.InvokeCallback, int)
     */
    @Override
    public void invokeAsync(Connection conn, RequestBody requestBody,
            InvokeCallback callback, int timeoutMillis)
            throws RemotingException, InterruptedException {
        remoting.invokeAsync(conn, requestBody, callback, timeoutMillis);
    }

}
