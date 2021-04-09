package leemos.orion.codec.hrpc;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import leemos.orion.codec.CodecException;
import leemos.orion.commons.StringManager;
import leemos.orion.remoting.message.RpcResponse;
import leemos.orion.remoting.serializer.Serializer;
import leemos.orion.remoting.serializer.Serializers;

/**
 * 用于将{@link RpcResponse}转换为{@link FullHttpResponse}
 *
 *
 * @author lihao
 * @date 2020年9月8日
 * @version 1.0
 */
public class Rpc2HttpResponseEncoder extends MessageToMessageEncoder<Object> {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out)
            throws Exception {
        if (msg instanceof RpcResponse) {
            RpcResponse rpcResponse = (RpcResponse) msg;

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

            // 标识头
            httpResponse.headers().add(Constants.ONEWAY_HEADER_NAME, false);
            httpResponse.headers().add(Constants.HEARTBEAT_HEADER_NAME,
                    rpcResponse.isHeartbeat());
            httpResponse.headers().add(Constants.SERIALIZATION_TYPE_HEADER_NAME,
                    rpcResponse.getSerializationType());

            // 状态
            httpResponse.headers().add(Constants.STATUS_HEADER_NAME,
                    rpcResponse.getStatus().getValue());

            // 唯一标识
            httpResponse.headers().add(Constants.MESSAGE_ID_HEADER_NAME,
                    rpcResponse.getId());

            // 待序列化的对象
            Object seralizableObject = rpcResponse.getResponseBody();
            if (seralizableObject == null) {
                httpResponse.headers().add(Constants.BODY_LENGTH_HEADER_NAME, 0);
                httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
            } else {
                Serializer serializer = Serializers
                        .getSerializer(rpcResponse.getSerializationType());
                if (serializer == null) {
                    throw new CodecException(sm.getString(
                            "rpc2HttpResponseEncoder.encode.noSerializerFound",
                            String.valueOf(rpcResponse.getSerializationType())));
                }

                byte[] data = serializer.serialize(seralizableObject);
                httpResponse.headers().add(Constants.BODY_LENGTH_HEADER_NAME,
                        data.length);
                httpResponse.headers().add(HttpHeaderNames.CONTENT_LENGTH, data.length);

                httpResponse.content().writeBytes(data);
            }

            out.add(httpResponse);
        }
    }
}
