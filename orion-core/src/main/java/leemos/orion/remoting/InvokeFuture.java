package leemos.orion.remoting;

import java.net.InetSocketAddress;

import io.netty.util.Timeout;
import leemos.orion.remoting.message.RpcResponse;

/**
 * Invoke future.
 *
 * @author lihao
 * @date 2020年8月11日
 * @version 1.0
 */
public interface InvokeFuture {

    /**
     * 获取唯一标识
     *
     * @return
     */
    long invokeId();

    /**
     * 等待响应，除非被中断，否则将一直等待下去
     *
     * @return
     * @throws InterruptedException
     */
    RpcResponse waitResponse() throws InterruptedException;

    /**
     * 等待响应，当超过超时时间时，立即返回
     *
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    RpcResponse waitResponse(long timeoutMillis) throws InterruptedException;

    /**
     * 置入响应并通知等待方
     */
    void putResponse(final RpcResponse response);

    /**
     * 判断当前future是否已完成
     *
     * @return
     */
    boolean isDone();

    /**
     * 生成连接断开消息
     *
     * @param remoteAddress
     * @return
     */
    RpcResponse createConnectionClosedResponse(InetSocketAddress remoteAddress);

    /**
     * 关联timeout
     *
     * @param timeout
     */
    void addTimeout(Timeout timeout);

    /**
     * 取消超时等待
     */
    void cancelTimeout();
    
    /**
     * 执行异步回调
     */
    void executeInvokeCallback();

}
