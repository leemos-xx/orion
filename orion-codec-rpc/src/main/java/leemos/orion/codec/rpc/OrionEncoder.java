package leemos.orion.codec.rpc;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import leemos.orion.remoting.Protocol;

/**
 * Rpc encoder handler. {@link ChannelHandler}
 *
 * @author lihao
 * @date 2020年8月14日
 * @version 1.0
 */
public class OrionEncoder extends MessageToByteEncoder<Serializable> {

    private Protocol protocol;

    public OrionEncoder(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out)
            throws Exception {
        protocol.getEncoder().encode(ctx, msg, out);
    }

}
