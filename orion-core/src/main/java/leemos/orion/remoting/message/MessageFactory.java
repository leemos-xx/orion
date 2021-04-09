package leemos.orion.remoting.message;

import java.net.InetSocketAddress;

import leemos.orion.core.engine.RequestFaced;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.heartbeat.HeartbeatRequest;
import leemos.orion.remoting.heartbeat.HeartbeatResponse;

/**
 * 消息工厂类
 *
 *
 * @author lihao
 * @date 2020年8月18日
 * @version 1.0
 */
public interface MessageFactory {

    /**
     * 创建请求消息
     *
     * @return
     */
    RpcRequest createRpcRequest();

    /**
     * 创建请求消息
     *
     * @param requestBody
     * @return
     */
    RpcRequest createRpcRequest(RequestBody requestBody);

    /**
     * 创建响应消息，并根据{@link RequestFaced} request来填充响应消息字段
     *
     * @param responseBody
     * @param request
     * @return
     */
    RpcResponse createRpcResponse(Object responseBody, RequestFaced request);

    /**
     * 根据异常消息生成响应
     *
     * @param errorMsg
     * @param request
     * @return
     */
    RpcResponse createExceptionResponse(String errorMsg, RequestFaced request);

    /**
     * 根据异常消息生成响应
     *
     * @param errorMsg
     * @param status
     * @param request
     * @return
     */
    RpcResponse createExceptionResponse(String errorMsg, Status status,
            RequestFaced request);

    /**
     * 根据异常消息生成响应
     *
     * @param t
     * @param errorMsg
     * @param status
     * @param request
     * @return
     */
    RpcResponse createExceptionResponse(Throwable t, String errorMsg, Status status,
            RequestFaced request);

    /**
     * 生成超时响应
     *
     * @param address
     * @return
     */
    RpcResponse createTimeoutResponse(final InetSocketAddress address);

    /**
     * 生成发送消息失败响应
     *
     * @param address
     * @param throwable
     * @return
     */
    RpcResponse createSendFailedResponse(final InetSocketAddress address,
            Throwable throwable);

    /**
     * 生成连接关闭响应
     *
     * @param address
     * @param errorMsg
     * @return
     */
    RpcResponse createConnectionClosedResponse(final InetSocketAddress address,
            String errorMsg);

    /**
     * 生成心跳请求消息
     *
     * @return
     */
    HeartbeatRequest createHeartbeatRequest();

    /**
     * 生成心跳响应消息
     *
     * @param request
     * @return
     */
    HeartbeatResponse createHeartbeatResponse(HeartbeatRequest request);

}
