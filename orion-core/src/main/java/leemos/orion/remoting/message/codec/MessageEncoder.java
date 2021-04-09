package leemos.orion.remoting.message.codec;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import leemos.orion.codec.CodecException;

/**
 * 消息编码器
 *
 * @author lihao
 * @date 2020年8月6日
 * @version 1.0
 */
public interface MessageEncoder {

    /**
     * 将消息编码为字节流
     *
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    public void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out)
            throws CodecException;
}
