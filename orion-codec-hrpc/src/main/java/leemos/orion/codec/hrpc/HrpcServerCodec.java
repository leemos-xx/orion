package leemos.orion.codec.hrpc;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import leemos.orion.codec.Codec;

/**
 * 用作hrpc服务端的编解码
 *
 *
 * @author lihao
 * @date 2020年9月8日
 * @version 1.0
 */
public class HrpcServerCodec implements Codec {

    private HrpcProtocol protocol;

    public HrpcServerCodec(HrpcProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public ChannelHandler[] newCoder() {
        return toArray(
                new HttpServerCodec(), 
                new HttpObjectAggregator(5 * 1024), 
                new Rpc2HttpResponseEncoder(),
                new Http2RpcRequestDecoder(protocol));
    }

}
