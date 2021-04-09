package leemos.orion.codec.hrpc;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import leemos.orion.remoting.Protocol;
import leemos.orion.remoting.heartbeat.HeartbeatRequest;
import leemos.orion.remoting.heartbeat.HeartbeatResponse;
import leemos.orion.remoting.heartbeat.HeartbeatTrigger;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.RpcRequest;
import leemos.orion.remoting.message.RpcResponse;
import leemos.orion.remoting.message.codec.MessageDecoder;
import leemos.orion.remoting.message.codec.MessageEncoder;
import leemos.orion.rpc.RpcHeartbeatTrigger;
import leemos.orion.rpc.RpcMessageFactory;

/**
 * RPC over HTTP的实现，项目中简称为hrpc.
 * 
 * <ul>
 *   <li>Http协议的编解码较为复杂，此处我们并不直接提供{@link MessageDecoder}或{@link MessageEncoder}，而是将协议的编解码交由Netty来实现。</li>
 *   <ul>
 *     <li>hrpc中仅负责将{@link FullHttpRequest}转换为{@link RpcRequest}或{@link HeartbeatRequest}</li>
 *     <li>hrpc中仅负责将{@link FullHttpResponse}转换为{@link RpcResponse}或{@link HeartbeatResponse}</li>
 *   </ul>
 *   <li>大部分流程我们仍然沿用rpc的通用流程，尽量保证只需要修改协议编解码的部分即可</li>
 * </ul>
 *
 * @author lihao
 * @date 2020年9月8日
 * @version 1.0
 */
public class HrpcProtocol implements Protocol {

    /**
     * 协议位，仅用作对象字段填充，目前在hrpc的实现中，并不会通过该协议位做操作
     */
    private static final byte PROTOCOL_CODE = 0x20;
    private static final byte PROTOCOL_VERSION = 0x01;

    /*
     * @see leemos.orion.remoting.Protocol#getProtocolCode()
     */
    @Override
    public byte getProtocolCode() {
        return PROTOCOL_CODE;
    }

    /*
     * @see leemos.orion.remoting.Protocol#getProtocolVersion()
     */
    @Override
    public byte getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    /**
     * 未实现
     * 
     * @exception IllegalAccessError
     */
    @Override
    public MessageEncoder getEncoder() {
        throw new IllegalAccessError();
    }

    /**
     * 未实现
     * 
     * @exception IllegalAccessError
     */
    @Override
    public MessageDecoder getDecoder() {
        throw new IllegalAccessError();
    }

    /*
     * @see leemos.orion.remoting.Protocol#getMessageFactory()
     */
    @Override
    public MessageFactory getMessageFactory() {
        return new RpcMessageFactory(this);
    }

    /*
     * @see leemos.orion.remoting.Protocol#getHeartbeatTrigger()
     */
    @Override
    public HeartbeatTrigger getHeartbeatTrigger() {
        return new RpcHeartbeatTrigger(this);
    }

}
