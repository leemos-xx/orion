package leemos.orion.codec.rpc;

import io.netty.channel.ChannelHandler;
import leemos.orion.codec.Codec;
import leemos.orion.remoting.Protocol;

/**
 * 基于rpc协议的{@link ChannelHandler}，提供对协议的编解码能力
 *
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public class OrionCodec implements Codec {

    private Protocol protocol;

    public OrionCodec(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public ChannelHandler[] newCoder() {
        return toArray(
                new OrionDecoder(protocol), 
                new OrionEncoder(protocol));
    }

}
