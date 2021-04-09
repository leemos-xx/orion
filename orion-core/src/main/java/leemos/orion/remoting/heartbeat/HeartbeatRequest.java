package leemos.orion.remoting.heartbeat;

import leemos.orion.remoting.message.RpcRequest;

/**
 * 心跳机制的请求消息
 *
 *
 * @author lihao
 * @date 2020年8月24日
 * @version 1.0
 */
public class HeartbeatRequest extends RpcRequest {

    private static final long serialVersionUID = -1321687064459007991L;

    public HeartbeatRequest(byte protocolCode, byte protocolVersion) {
        super(protocolCode, protocolVersion);
    }

}
