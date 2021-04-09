package leemos.orion.codec.hrpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import leemos.orion.codec.CodecException;
import leemos.orion.commons.StringManager;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.heartbeat.HeartbeatRequest;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcRequest;
import leemos.orion.remoting.serializer.Serializer;
import leemos.orion.remoting.serializer.Serializers;

/**
 * 用于将{@link FullHttpRequest}转换为{@link RpcRequest}
 *
 *
 * @author lihao
 * @date 2020年9月8日
 * @version 1.0
 */
public class Http2RpcRequestDecoder extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private HrpcProtocol protocol;

    public Http2RpcRequestDecoder(HrpcProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
            throws Exception {

        if (ctx.channel().attr(Connection.PROTOCOL).get() == null) {
            ctx.channel().attr(Connection.PROTOCOL).set(protocol);
        }

        byte protocolCode = protocol.getProtocolCode();
        byte protocolVersion = protocol.getProtocolVersion();

        // 标识位
        boolean isReqOrRsp = true;
        boolean isOneway = getBoolean(msg.headers().get(Constants.ONEWAY_HEADER_NAME));
        boolean isHeartbeat = getBoolean(
                msg.headers().get(Constants.HEARTBEAT_HEADER_NAME));
        boolean isCrcRequired = false;
        byte serializationType = getByte(
                msg.headers().get(Constants.SERIALIZATION_TYPE_HEADER_NAME));

        // 消息唯一标识
        long id = getLong(msg.headers().get(Constants.MESSAGE_ID_HEADER_NAME));

        // 读取content部分到字节数组
        int dataLength = getInt(msg.headers().get(Constants.BODY_LENGTH_HEADER_NAME));
        byte[] data = null;
        if (dataLength == 0) {
            data = new byte[0];
        } else {
            data = new byte[dataLength];
            msg.content().readBytes(data, 0, dataLength);
        }

        RpcRequest rpcRequest;
        if (isHeartbeat) {
            rpcRequest = new HeartbeatRequest(protocolCode, protocolVersion);
        } else {
            Serializer serializer = Serializers.getSerializer(serializationType);
            if (serializer == null) {
                throw new CodecException(sm.getString(
                        "http2RpcRequestDecoder.channelRead0.noSerializerFound",
                        String.valueOf(serializationType)));
            }
            rpcRequest = new RpcRequest(protocolCode, protocolVersion);

            RequestBody requestBody = new RequestBody();
            requestBody.setServiceName(
                    msg.headers().getAsString(Constants.SERVICE_HEADER_NAME));
            if (data.length != 0) {
                requestBody.setParameters(serializer.deserialize(data));
            }
            rpcRequest.setRequestBody(requestBody);
        }

        rpcRequest.setReqOrRsp(isReqOrRsp);
        rpcRequest.setOneway(isOneway);
        rpcRequest.setHeartbeat(isHeartbeat);
        rpcRequest.setCrcRequired(isCrcRequired);
        rpcRequest.setSerializationType(serializationType);
        rpcRequest.setId(id);
        rpcRequest.setDataLength(dataLength);
        rpcRequest.setData(data);

        ctx.fireChannelRead(rpcRequest);
    }

    private int getInt(String valueStr) {
        return Integer.parseInt(valueStr);
    }

    private boolean getBoolean(String valueStr) {
        return Boolean.parseBoolean(valueStr);
    }

    private byte getByte(String valueStr) {
        return Byte.parseByte(valueStr);
    }

    private long getLong(String valueStr) {
        return Long.parseLong(valueStr);
    }

}
