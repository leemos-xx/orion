package leemos.orion.remoting.message.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import leemos.orion.codec.CodecException;

/**
 * 消息解码器
 *
 *
 * @author lihao
 * @date 2020年8月6日
 * @version 1.0
 */
public interface MessageDecoder {

    /**
     * 将字节流解码为对象
     *
     * @param ctx
     * @param in
     * @param out
     * @throws CodecException
     * @throws Exception
     */
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws CodecException;
}
