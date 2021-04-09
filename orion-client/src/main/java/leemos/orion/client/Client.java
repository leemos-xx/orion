package leemos.orion.client;

import leemos.orion.Hierarchy;
import leemos.orion.Lifecycle;
import leemos.orion.remoting.BaseRemoting;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.InvokeCallback;
import leemos.orion.remoting.InvokeFuture;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.Url;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcResponse;

/**
 * 客户端接口定义，主要定义与服务端通讯的网络模型，目前的网络模型包括单向、同步、异步和
 * callback的实现方式，其具体实现依托{@link BaseRemoting}
 *
 * @author lihao
 * @date 2020年8月20日
 * @version 1.0
 */
public interface Client extends Lifecycle, Hierarchy {

    /**
     * 单向请求，客户端仅发送请求，并不需要响应
     *
     * @param address
     * @param requestBody
     * @throws RemotingException
     * @throws InterruptedException
     */
    void oneway(final String address, final RequestBody requestBody)
            throws RemotingException, InterruptedException;

    /**
     * 单向请求，客户端仅发送请求，并不需要响应
     *
     * @param url
     * @param requestBody
     * @throws RemotingException
     * @throws InterruptedException
     */
    void oneway(final Url url, final RequestBody requestBody)
            throws RemotingException, InterruptedException;

    /**
     * 单向请求，客户端仅发送请求，并不需要响应
     *
     * @param conn
     * @param requestBody
     * @throws RemotingException
     */
    void oneway(final Connection conn, final RequestBody requestBody)
            throws RemotingException;

    /**
     * 客户端发送请求，并同步等待结果。
     * 在该模式中执行线程将一直处于阻塞状态，直至服务端返回或超时
     *
     * @param address
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    RpcResponse invoke(final String address, final RequestBody requestBody,
            final int timeoutMillis) throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，并同步等待结果。
     * 在该模式中执行线程将一直处于阻塞状态，直至服务端返回或超时
     *
     * @param url
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    RpcResponse invoke(final Url url, final RequestBody requestBody,
            final int timeoutMillis) throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，并同步等待结果。
     * 在该模式中执行线程将一直处于阻塞状态，直至服务端返回或超时
     *
     * @param conn
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    RpcResponse invoke(final Connection conn, final RequestBody requestBody,
            final int timeoutMillis) throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，请求发送成功后将返回{@link InvokeFuture}，客户端需要结果时，从InvokeFuture中获取。
     * 在该模式下，从InvokeFuture中获取结果的过程会阻塞线程
     *
     * @param address
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    InvokeFuture invokeAsync(final String address, final RequestBody requestBody,
            final int timeoutMillis) throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，请求发送成功后将返回{@link InvokeFuture}，客户端需要结果时，从InvokeFuture中获取。
     * 在该模式下，从InvokeFuture中获取结果的过程会阻塞线程
     *
     * @param url
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    InvokeFuture invokeAsync(final Url url, final RequestBody requestBody,
            final int timeoutMillis) throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，请求发送成功后将返回{@link InvokeFuture}，客户端需要结果时，从InvokeFuture中获取。
     * 在该模式下，从InvokeFuture中获取结果的过程会阻塞线程
     *
     * @param conn
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    InvokeFuture invokeAsync(final Connection conn, final RequestBody requestBody,
            int timeoutMillis) throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，当服务端返回处理结果时，会在新的线程中唤起{@link InvokeCallback}的执行流程。
     * 该模式为纯异步模式
     *
     * @param address
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    void invokeAsync(final String address, final RequestBody requestBody,
            InvokeCallback callback, final int timeoutMillis)
            throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，当服务端返回处理结果时，会在新的线程中唤起{@link InvokeCallback}的执行流程。
     * 该模式为纯异步模式
     *
     * @param url
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    void invokeAsync(final Url url, final RequestBody requestBody,
            InvokeCallback callback, final int timeoutMillis)
            throws RemotingException, InterruptedException;

    /**
     * 客户端发送请求，当服务端返回处理结果时，会在新的线程中唤起{@link InvokeCallback}的执行流程。
     * 该模式为纯异步模式
     *
     * @param conn
     * @param requestBody
     * @param timeoutMillis
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    void invokeAsync(final Connection conn, final RequestBody requestBody,
            InvokeCallback callback, int timeoutMillis)
            throws RemotingException, InterruptedException;

    /**
     * 指定Client的抽象层级
     */
    default int getLevel() {
        return 0;
    }
}
