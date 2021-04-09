package leemos.orion.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leemos.orion.client.Constants;
import leemos.orion.client.ProxyInvoker;
import leemos.orion.client.ServiceInvocation;
import leemos.orion.commons.StringManager;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcResponse;

public class JdkInvocationHandler<T> implements InvocationHandler {

    private static final Logger logger = LoggerFactory
            .getLogger(JdkInvocationHandler.class);
    private static final StringManager sm = StringManager.getManager(Constants.PACAKGE);

    private Class<T> proxyClass;
    private ProxyInvoker proxyInvoker;

    public JdkInvocationHandler(Class<T> proxyClass, ProxyInvoker proxyInvoker) {
        this.proxyClass = proxyClass;
        this.proxyInvoker = proxyInvoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ServiceInvocation service = method.getAnnotation(ServiceInvocation.class);
        if (service == null) {
            if (logger.isWarnEnabled()) {
                logger.warn(sm.getString("JdkInvocationHandler.invoke.annotationAbsent",
                        proxyClass.getName(), method.getName()));
            }
            return null;
        }

        RequestBody requestBody = new RequestBody();
        requestBody.setServiceName(service.name());
        requestBody.setParameters(args);

        // FIXME 处理异常
        RpcResponse rpcResponse = proxyInvoker.invoke(requestBody);

        return rpcResponse.getResponseBody();
    }

}
