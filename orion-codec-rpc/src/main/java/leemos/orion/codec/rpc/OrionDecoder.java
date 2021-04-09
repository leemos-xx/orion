package leemos.orion.codec.rpc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import leemos.orion.codec.CodecException;
import leemos.orion.commons.StringManager;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.Protocol;

/**
 * Rpc decoder handler. {@link ChannelHandler}
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class OrionDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory
            .getLogger(OrionDecoder.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    private Protocol protocol;

    public OrionDecoder(Protocol protocol) {
        this.protocol = protocol;
    }

    /*
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.
     * ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        in.markReaderIndex();

        // 如果可读字节数小于协议标识位长度(magic & version)，则不处理
        if (in.readableBytes() < 2) {
            return;
        }

        // 读取协议标识
        byte protocolCode = in.readByte();
        byte protocolVersion = in.readByte();

        // 检查报文的协议码和协议版本是否与指定的protocol一致
        if (protocolCode == protocol.getProtocolCode()
                && protocolVersion == protocol.getProtocolVersion()) {
            if (ctx.channel().attr(Connection.PROTOCOL).get() == null) {
                ctx.channel().attr(Connection.PROTOCOL).set(protocol);
            }

            in.resetReaderIndex();
            // 进行实际的解码操作
            try {
                protocol.getDecoder().decode(ctx, in, out);
            } catch (CodecException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            throw new CodecException(sm.getString(
                    "orionDecoder.decode.illegalProtocol",
                    String.valueOf(protocolCode), String.valueOf(protocolVersion)));
        }
    }

}
