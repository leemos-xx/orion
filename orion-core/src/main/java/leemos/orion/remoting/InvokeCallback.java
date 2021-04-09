package leemos.orion.remoting;

/**
 * 支持回调模式
 *
 *
 * @author lihao
 * @date 2020年8月26日
 * @version 1.0
 */
public interface InvokeCallback {

    /**
     * 执行回调
     *
     * @param future
     */
    public void performCallback(InvokeFuture future);

    /**
     * 是否已经被调用
     *
     * @return
     */
    public boolean calledAlready();
}
