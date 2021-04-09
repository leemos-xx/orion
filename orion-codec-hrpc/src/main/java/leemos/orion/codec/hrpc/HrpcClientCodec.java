package leemos.orion.codec.hrpc;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import leemos.orion.codec.Codec;

public class HrpcClientCodec implements Codec {

    private HrpcProtocol protocol;

    public HrpcClientCodec(HrpcProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public ChannelHandler[] newCoder() {
        return toArray(
                new HttpClientCodec(),
                new HttpObjectAggregator(5 * 1024), 
                new Http2RpcResponseDecoder(protocol),
                new Rpc2HttpRequestEncoder());
    }

}
