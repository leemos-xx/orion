package leemos.orion.rpc;

import leemos.orion.Configs;
import leemos.orion.remoting.BaseRemoting;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.ConnectionManager;
import leemos.orion.remoting.DefaultInvokeFuture;
import leemos.orion.remoting.InvokeCallback;
import leemos.orion.remoting.InvokeFuture;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.Url;
import leemos.orion.remoting.UrlParser;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcRequest;
import leemos.orion.remoting.message.RpcResponse;

/**
 * 基于Rpc的通讯模型
 *
 *
 * @author lihao
 * @date 2020年8月20日
 * @version 1.0
 */
public class RpcRemoting extends BaseRemoting {

    protected UrlParser addressParser;
    protected ConnectionManager connectionManager;

    public RpcRemoting(MessageFactory messageFactory) {
        super(messageFactory);
    }

    public RpcRemoting(MessageFactory messageFactory, UrlParser addressParser,
            ConnectionManager connectionManager) {
        super(messageFactory);
        this.addressParser = addressParser;
        this.connectionManager = connectionManager;
    }

    /**
     * 单向请求
     *
     * @param addr
     * @param requestBody
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void oneway(final String addr, final RequestBody requestBody)
            throws RemotingException, InterruptedException {
        Url url = this.addressParser.parse(addr);
        this.oneway(url, requestBody);
    }

    /**
     * 单向请求
     *
     * @param url
     * @param requestBody
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void oneway(Url url, RequestBody requestBody)
            throws RemotingException, InterruptedException {
        final Connection conn = getConnection(url);
        this.connectionManager.check(conn);
        this.oneway(conn, requestBody);
    }

    /**
     * 单向请求
     *
     * @param connection
     * @param requestBody
     * @throws RemotingException
     */
    public void oneway(final Connection connection, final RequestBody requestBody)
            throws RemotingException {
        RpcRequest rpcRequest = createRpcRequest(requestBody);
        rpcRequest.setOneway(true);
        super.oneway(connection, rpcRequest);
    }

    /**
     * 发送请求并同步等待响应
     *
     * @param addr
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    public RpcResponse invoke(final String addr, final RequestBody requestBody,
            final int timeoutMillis) throws RemotingException, InterruptedException {
        Url url = this.addressParser.parse(addr);
        return this.invoke(url, requestBody, timeoutMillis);
    }

    /**
     * 发送请求并同步等待响应
     *
     * @param url
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    public RpcResponse invoke(Url url, RequestBody requestBody, int timeoutMillis)
            throws RemotingException, InterruptedException {
        final Connection conn = getConnection(url);
        this.connectionManager.check(conn);
        return this.invoke(conn, requestBody, timeoutMillis);
    }

    /**
     * 发送请求并同步等待响应
     *
     * @param connection
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    public RpcResponse invoke(final Connection connection, final RequestBody requestBody,
            final int timeoutMillis) throws InterruptedException {
        RpcRequest rpcRequest = createRpcRequest(requestBody);
        rpcRequest.setOneway(false);

        return super.invoke(connection, rpcRequest, timeoutMillis);
    }

    /**
     * 基于{@link InvokeFuture}的异步调用
     *
     * @param addr
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    public InvokeFuture invokeAsync(final String addr, final RequestBody requestBody,
            int timeoutMillis) throws RemotingException, InterruptedException {
        Url url = this.addressParser.parse(addr);
        return this.invokeAsync(url, requestBody, timeoutMillis);
    }

    /**
     * 基于{@link InvokeFuture}的异步调用
     *
     * @param url
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    public InvokeFuture invokeAsync(Url url, RequestBody requestBody, int timeoutMillis)
            throws RemotingException, InterruptedException {
        final Connection conn = getConnection(url);
        this.connectionManager.check(conn);
        return this.invokeAsync(conn, requestBody, timeoutMillis);
    }

    /**
     * 基于{@link InvokeFuture}的异步调用
     *
     * @param connection
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     */
    public InvokeFuture invokeAsync(final Connection connection,
            final RequestBody requestBody, final int timeoutMillis)
            throws RemotingException, InterruptedException {
        RpcRequest rpcRequest = createRpcRequest(requestBody);
        rpcRequest.setOneway(false);

        return super.invokeAsync(connection, rpcRequest, timeoutMillis);
    }

    /**
     * 基于{@link InvokeCallback}的回调调用
     *
     * @param addr
     * @param requestBody
     * @param callback
     * @param timeoutMillis
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void invokeAsync(final String addr, final RequestBody requestBody,
            InvokeCallback callback, int timeoutMillis)
            throws RemotingException, InterruptedException {
        Url url = this.addressParser.parse(addr);
        this.invokeAsync(url, requestBody, callback, timeoutMillis);
    }

    /**
     * 基于{@link InvokeCallback}的回调调用
     *
     * @param url
     * @param requestBody
     * @param callback
     * @param timeoutMillis
     */
    public void invokeAsync(Url url, RequestBody requestBody, InvokeCallback callback,
            int timeoutMillis) throws RemotingException, InterruptedException {
        final Connection conn = getConnection(url);
        this.connectionManager.check(conn);
        this.invokeAsync(conn, requestBody, callback, timeoutMillis);
    }

    /**
     * 基于{@link InvokeCallback}的回调调用
     *
     * @param connection
     * @param requestBody
     * @param callback
     * @param timeoutMillis
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void invokeAsync(final Connection connection, final RequestBody requestBody,
            InvokeCallback callback, final int timeoutMillis)
            throws RemotingException, InterruptedException {
        RpcRequest rpcRequest = createRpcRequest(requestBody);
        rpcRequest.setOneway(false);

        super.invokeAsync(connection, rpcRequest, callback, timeoutMillis);
    }

    /*
     * @see
     * leemos.orion.remoting.BaseRemoting#createInvokeFuture(leemos.orion.
     * Message)
     */
    @Override
    protected InvokeFuture createInvokeFuture(RpcRequest rpcRequest) {
        return new DefaultInvokeFuture(rpcRequest.getId(), messageFactory);
    }

    /*
     * @see
     * leemos.orion.remoting.BaseRemoting#createInvokeFuture(leemos.orion.
     * Message, leemos.orion.remoting.InvokeCallback)
     */
    @Override
    protected InvokeFuture createInvokeFuture(RpcRequest rpcRequest,
            InvokeCallback callback) {
        return new DefaultInvokeFuture(rpcRequest.getId(), messageFactory, callback);
    }

    /**
     * 生成{@link RpcRequestMessage}
     *
     * @param requestBody
     * @return
     */
    private RpcRequest createRpcRequest(RequestBody requestBody) {
        RpcRequest requestMessage = messageFactory.createRpcRequest(requestBody);

        // 是否开启crc校验，默认开启
        requestMessage.setCrcRequired(Configs.crcEnabled());
        // 默认使用Hessian进行序列化
        requestMessage.setSerializationType(Configs.serailizationType());

        return requestMessage;
    }

    /**
     * 获取连接
     *
     * @param url
     * @return
     * @throws InterruptedException
     * @throws RemotingException
     */
    private Connection getConnection(Url url)
            throws InterruptedException, RemotingException {
        return this.connectionManager.getAndCreateIfAbsent(url);
    }

}
