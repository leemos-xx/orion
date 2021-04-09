package leemos.orion.codec.hrpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import leemos.orion.codec.CodecException;
import leemos.orion.commons.StringManager;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.heartbeat.HeartbeatResponse;
import leemos.orion.remoting.message.RpcResponse;
import leemos.orion.remoting.serializer.Serializer;
import leemos.orion.remoting.serializer.Serializers;

public class Http2RpcResponseDecoder extends ChannelInboundHandlerAdapter {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private HrpcProtocol protocol;

    public Http2RpcResponseDecoder(HrpcProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse httpResponse = (FullHttpResponse) msg;

            byte protocolCode = protocol.getProtocolCode();
            byte protocolVersion = protocol.getProtocolVersion();

            // 标识位
            boolean isReqOrRsp = true;
            boolean isOneway = getBoolean(
                    httpResponse.headers().get(Constants.ONEWAY_HEADER_NAME));
            boolean isHeartbeat = getBoolean(
                    httpResponse.headers().get(Constants.HEARTBEAT_HEADER_NAME));
            boolean isCrcRequired = false;
            byte serializationType = getByte(
                    httpResponse.headers().get(Constants.SERIALIZATION_TYPE_HEADER_NAME));

            // 消息状态
            byte status = getByte(
                    httpResponse.headers().get(Constants.STATUS_HEADER_NAME));

            // 消息唯一标识
            long id = getLong(
                    httpResponse.headers().get(Constants.MESSAGE_ID_HEADER_NAME));

            // 读取content部分到字节数组
            int dataLength = getInt(
                    httpResponse.headers().get(Constants.BODY_LENGTH_HEADER_NAME));
            byte[] data = null;
            if (dataLength == 0) {
                data = new byte[0];
            } else {
                data = new byte[dataLength];
                httpResponse.content().readBytes(data, 0, dataLength);
            }

            RpcResponse response;
            if (isHeartbeat) {
                response = new HeartbeatResponse(protocolCode, protocolVersion);
            } else {
                Serializer serializer = Serializers.getSerializer(serializationType);
                if (serializer == null) {
                    throw new CodecException(sm.getString(
                            "http2RpcResponseDecoder.channelRead0.noSerializerFound",
                            String.valueOf(serializationType)));
                }
                response = new RpcResponse(protocolCode, protocolVersion);
                if (data.length != 0) {
                    response.setResponseBody(serializer.deserialize(data));
                }
            }

            response.setReqOrRsp(isReqOrRsp);
            response.setOneway(isOneway);
            response.setHeartbeat(isHeartbeat);
            response.setCrcRequired(isCrcRequired);
            response.setSerializationType(serializationType);
            response.setStatus(Status.parse(status));
            response.setId(id);
            response.setDataLength(dataLength);
            response.setData(data);

            ctx.fireChannelRead(response);
        }
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
