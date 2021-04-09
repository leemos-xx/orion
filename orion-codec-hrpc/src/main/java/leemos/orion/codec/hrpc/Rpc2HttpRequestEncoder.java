package leemos.orion.codec.hrpc;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import leemos.orion.codec.CodecException;
import leemos.orion.commons.StringManager;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcRequest;
import leemos.orion.remoting.serializer.Serializer;
import leemos.orion.remoting.serializer.Serializers;

public class Rpc2HttpRequestEncoder extends MessageToMessageEncoder<Object> {

    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out)
            throws Exception {
        if (msg instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) msg;

            FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.POST, "/hrpc");

            // 标识头
            httpRequest.headers().add(Constants.ONEWAY_HEADER_NAME, request.isOneway());
            httpRequest.headers().add(Constants.HEARTBEAT_HEADER_NAME,
                    request.isHeartbeat());
            httpRequest.headers().add(Constants.SERIALIZATION_TYPE_HEADER_NAME,
                    request.getSerializationType());

            // 唯一标识
            httpRequest.headers().add(Constants.MESSAGE_ID_HEADER_NAME, request.getId());

            RequestBody requestBody = request.getRequestBody();
            Object seralizableObject = null;
            if (requestBody != null) {
                httpRequest.headers().add(Constants.SERVICE_HEADER_NAME,
                        requestBody.getServiceName());
                if (requestBody.getParameters() != null
                        && requestBody.getParameters().length != 0) {
                    seralizableObject = requestBody.getParameters();
                }
            }

            if (seralizableObject == null) {
                httpRequest.headers().add(Constants.BODY_LENGTH_HEADER_NAME, 0);
                httpRequest.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
            } else {
                Serializer serializer = Serializers
                        .getSerializer(request.getSerializationType());
                if (serializer == null) {
                    throw new CodecException(sm.getString(
                            "rpc2HttpRequestEncoder.encode.noSerializerFound",
                            String.valueOf(request.getSerializationType())));
                }

                byte[] data = serializer.serialize(seralizableObject);
                httpRequest.headers().add(Constants.BODY_LENGTH_HEADER_NAME, data.length);
                httpRequest.headers().add(HttpHeaderNames.CONTENT_LENGTH, data.length);

                httpRequest.content().writeBytes(data);
            }

            out.add(httpRequest);
        }
    }

}
