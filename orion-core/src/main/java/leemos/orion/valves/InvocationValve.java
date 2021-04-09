package leemos.orion.valves;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import leemos.orion.Constants;
import leemos.orion.EventBus;
import leemos.orion.ValveChain;
import leemos.orion.commons.StringManager;
import leemos.orion.core.bus.EventDiscardException;
import leemos.orion.core.bus.ResponseEvent;
import leemos.orion.core.engine.Api;
import leemos.orion.core.engine.Invocation;
import leemos.orion.core.engine.InvocationException;
import leemos.orion.core.engine.RequestFaced;
import leemos.orion.core.engine.ResponseFaced;
import leemos.orion.remoting.Connection;
import leemos.orion.remoting.Status;
import leemos.orion.remoting.message.MessageFactory;
import leemos.orion.remoting.message.RpcResponse;

/**
 * 业务执行单元，是整个业务处理流程中最核心的单元，用于实际执行{@link Api}标记的服务。
 *
 *
 * @author lihao
 * @date 2020年8月28日
 * @version 1.0
 */
public class InvocationValve extends ValveBase {

    private static final Logger logger = LoggerFactory.getLogger(InvocationValve.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE);

    public InvocationValve(EventBus eventBus) {
        super(eventBus);
    }

    /*
     * @see leemos.orion.Valve#invoke(leemos.orion.RequestFaced,
     * leemos.orion.ResponseFaced, leemos.orion.Invocation,
     * leemos.orion.ValveChain)
     */
    @Override
    public void invoke(RequestFaced request, ResponseFaced response,
            Invocation invocation, ValveChain chain) {

        Channel channel = response.getChannel();
        MessageFactory messageFactory = channel.attr(Connection.PROTOCOL).get()
                .getMessageFactory();

        RpcResponse rpcResponse = null;
        Object responseBody = null;
        try {
            responseBody = invocation.invoke(request.getParameters());

            rpcResponse = messageFactory.createRpcResponse(responseBody, request);
        } catch (InvocationException e) {
            logger.error(sm.getString("invocationValve.invoke.error"), e);
            rpcResponse = messageFactory.createExceptionResponse(e,
                    sm.getString("invocationValve.invoke.error"), Status.SERVER_EXCEPTION,
                    request);
        } catch (Throwable t) {
            // 此处的捕获异常，应该为服务层未处理的异常，
            // 我们在此处包装为异常消息返回给客户端，并记录日志
            logger.error(t.getMessage(), t);
            rpcResponse = messageFactory.createExceptionResponse(t, t.getMessage(),
                    Status.ERROR, request);
        }
        response.setRpcResponse(rpcResponse);

        if (request.isOneway()) {
            return;
        }

        // 生成ResponseEvent，发布到总线
        ResponseEvent event = new ResponseEvent(rpcResponse, channel);
        try {
            // 此处发生异常，仅记录日志？
            getEventBus().postEvent(event);
        } catch (EventDiscardException e) {
            logger.error(sm.getString("invocationValve.invoke.discard"), e);
        }

    }

}
