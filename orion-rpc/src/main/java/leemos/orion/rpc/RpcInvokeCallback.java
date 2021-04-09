package leemos.orion.rpc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leemos.orion.commons.NamedThreadFactory;
import leemos.orion.commons.StringManager;
import leemos.orion.core.ServerException;
import leemos.orion.remoting.InvokeCallback;
import leemos.orion.remoting.InvokeFuture;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.message.RpcResponse;
import leemos.orion.rpc.exception.ConnectionClosedException;
import leemos.orion.rpc.exception.ServerBusyException;
import leemos.orion.rpc.exception.TimeoutException;

/**
 * 支持rpc方式的异步回调
 *
 *
 * @author lihao
 * @date 2020年8月26日
 * @version 1.0
 */
public abstract class RpcInvokeCallback implements InvokeCallback {

    private static final Logger logger = LoggerFactory.getLogger(RpcInvokeCallback.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 100,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000),
            new NamedThreadFactory("orion-client-callback", true));

    /**
     * 只允许回调一次
     */
    private AtomicBoolean called = new AtomicBoolean(false);

    /**
     * 异步执行回调任务，且只允许执行一次
     */
    @Override
    public void performCallback(InvokeFuture future) {
        if (called.compareAndSet(false, true)) {
            executor.execute(new CallbackTask(future));
        }
    }

    /*
     * @see leemos.orion.remoting.InvokeCallback#calledAlready()
     */
    @Override
    public boolean calledAlready() {
        return called.get();
    }

    /**
     * 回调的具体业务逻辑，由实现类提供
     *
     * @param response
     */
    protected abstract void onResponse(RpcResponse response);

    /**
     * 执行异常
     *
     * @param e
     */
    protected abstract void onException(Exception e);

    /**
     * Callback task.
     *
     *
     * @author lihao
     * @date 2020年8月26日
     * @version 1.0
     */
    private class CallbackTask implements Runnable {

        private InvokeFuture future;

        public CallbackTask(InvokeFuture future) {
            this.future = future;
        }

        @Override
        public void run() {
            RpcResponse rpcResponse = null;
            try {
                // 基于回调模式，此时response应该已经放入future，因此直接获取
                // 若任为null，则说明出现异常
                rpcResponse = (RpcResponse) future.waitResponse(0);
            } catch (InterruptedException e) {
                logger.error(sm.getString("rpcInvokeCallback.run.error"));
            }

            // 当响应为空或状态异常时，包装为异常信息并回调Callback的onException方法
            if (rpcResponse == null || rpcResponse.getStatus() != Status.SUCCESS) {
                Exception e = null;
                if (rpcResponse == null) {
                    e = new RemotingException(
                            sm.getString("rpcInvokeCallback.run.error"));
                } else {
                    switch (rpcResponse.getStatus()) {
                    case ERROR:
                        e = (Exception) rpcResponse.getResponseBody();
                        break;
                    case SERVER_EXCEPTION:
                        e = new ServerException(
                                (Throwable) rpcResponse.getResponseBody());
                        break;
                    case SERVER_BUSY:
                        e = new ServerBusyException();
                        break;
                    case CLIENT_EXCEPTION:
                        e = (Exception) rpcResponse.getResponseBody();
                        break;
                    case CONNECTION_CLOSED:
                        e = new ConnectionClosedException();
                        break;
                    case TIMEOUT:
                        e = new TimeoutException();
                        break;
                    case UNKNOWN:
                        e = new RemotingException("UNKNOWN");
                        break;
                    default:
                        break;
                    }
                }
                RpcInvokeCallback.this.onException(e);
            } else {
                RpcInvokeCallback.this.onResponse(rpcResponse);
            }
        }

    }
}
