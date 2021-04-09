package leemos.orion.codec.rpc;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import leemos.orion.codec.CodecException;
import leemos.orion.commons.StringManager;
import leemos.orion.commons.utils.CrcUtils;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.heartbeat.HeartbeatRequest;
import leemos.orion.remoting.heartbeat.HeartbeatResponse;
import leemos.orion.remoting.message.RpcMessage;
import leemos.orion.remoting.message.RpcRequest;
import leemos.orion.remoting.message.RpcResponse;
import leemos.orion.remoting.message.codec.MessageDecoder;
import leemos.orion.remoting.serializer.Serializer;
import leemos.orion.remoting.serializer.Serializers;
import leemos.orion.rpc.MarkerByte;

/**
 * Rpc消息解码器，将字节流解码为{@link RpcMessage}
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class OrionMessageDecoder implements MessageDecoder {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws CodecException {
        // 可读字节长度小于协议定义的最小报文头长度，则不处理
        if (in.readableBytes() < OrionProtocol.FIXED_HEADER_LENGTH) {
            return;
        }
        in.markReaderIndex();

        // crc校验的起始位
        int startIndex = in.readerIndex();

        // 协议位
        byte protocolCode = in.readByte();
        byte protocolVersion = in.readByte();

        // 解析marker byte
        MarkerByte markerByte = new MarkerByte(in.readByte());
        boolean isReqOrRsp = markerByte.isReqOrRsp();
        boolean isOneway = markerByte.isOneway();
        boolean isHeartbeat = markerByte.isHeartbeat();
        boolean isCrcRequired = markerByte.isCrcRequired();
        byte serializationType = markerByte.getSerializationType();

        // 状态
        byte status = in.readByte();
        // 消息唯一标识
        long id = in.readLong();
        // 报文体长度
        int dataLength = in.readInt();

        // 校验剩余报文报文体长度是否合法(dataLength + crc*)，不合法则暂不处理
        int remainingLength = isCrcRequired ? dataLength + 4 : dataLength;
        if (in.readableBytes() < remainingLength) {
            in.resetReaderIndex();
            return;
        }

        // 读取content部分到字节数组
        byte[] data = null;
        if (dataLength == 0) {
            data = new byte[0];
        } else {
            data = new byte[dataLength];
            in.readBytes(data);
        }

        RpcMessage rpcMessage;
        if (isHeartbeat) {
            // 心跳请求消息
            if (isReqOrRsp) {
                rpcMessage = new HeartbeatRequest(protocolCode, protocolVersion);
                // 请求消息直接忽略status
            } else {
                rpcMessage = new HeartbeatResponse(protocolCode, protocolVersion);

                ((HeartbeatResponse) rpcMessage).setStatus(Status.parse(status));
            }
        }
        // 正常业务报文，目前的反序列化在io线程处理，但考虑可以延迟到业务线程处理？
        else {
            Serializer serializer = Serializers.getSerializer(serializationType);
            if (serializer == null) {
                throw new CodecException(
                        sm.getString("orionMessageDecoder.decode.noSerializerFound",
                                String.valueOf(serializationType)));
            }

            if (isReqOrRsp) {
                rpcMessage = new RpcRequest(protocolCode, protocolVersion);

                if (data.length != 0) {
                    ((RpcRequest) rpcMessage)
                            .setRequestBody(serializer.deserialize(data));
                }
            } else {
                rpcMessage = new RpcResponse(protocolCode, protocolVersion);

                ((RpcResponse) rpcMessage).setStatus(Status.parse(status));
                if (data.length != 0) {
                    ((RpcResponse) rpcMessage)
                            .setResponseBody(serializer.deserialize(data));
                }
            }
        }
        rpcMessage.setReqOrRsp(isReqOrRsp);
        rpcMessage.setOneway(isOneway);
        rpcMessage.setHeartbeat(isHeartbeat);
        rpcMessage.setCrcRequired(isCrcRequired);
        rpcMessage.setSerializationType(serializationType);
        rpcMessage.setId(id);
        rpcMessage.setDataLength(dataLength);
        rpcMessage.setData(data);

        // 进行crc校验
        if (isCrcRequired) {
            int crc = checkCRC(in, startIndex);
            rpcMessage.setCrc(crc);
        }

        out.add(rpcMessage);
    }

    /**
     * CRC校验
     *
     * @param in
     * @param startIndex
     * @throws CodecException
     */
    private int checkCRC(ByteBuf in, int startIndex) throws CodecException {
        int endIndex = in.readerIndex();
        int expectedCrc = in.readInt();

        byte[] frame = new byte[endIndex - startIndex];
        in.getBytes(startIndex, frame, 0, endIndex - startIndex);

        int actualCrc = CrcUtils.crc32(frame);
        if (expectedCrc != actualCrc) {
            throw new CodecException(sm.getString("orionMessageDecoder.checkCRC.error",
                    String.valueOf(expectedCrc), String.valueOf(expectedCrc)));
        }
        return actualCrc;
    }

}
