package leemos.orion.remoting.heartbeat;

import leemos.orion.remoting.message.RpcResponse;

/**
 * 心跳机制的响应消息
 *
 *
 * @author lihao
 * @date 2020年8月24日
 * @version 1.0
 */
public class HeartbeatResponse extends RpcResponse {

    private static final long serialVersionUID = -2588376882798683502L;

    public HeartbeatResponse(byte protocolCode, byte protocolVersion) {
        super(protocolCode, protocolVersion);
    }

}
