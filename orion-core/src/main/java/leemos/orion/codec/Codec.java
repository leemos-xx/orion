package leemos.orion.codec;

import io.netty.channel.ChannelHandler;

/**
 * 构造协议编解码所需的{@link ChannelHandler}
 *
 *
 * @author lihao
 * @date 2020年8月13日
 * @version 1.0
 */
public interface Codec {

    /**
     * 生成解解码器实例
     *
     * @return
     */
    ChannelHandler[] newCoder();

    /**
     * 转换为ChannelHandler数组
     *
     * @param channelHandlers
     * @return
     */
    default ChannelHandler[] toArray(ChannelHandler... channelHandlers) {
        return channelHandlers;
    }
}
