package leemos.orion.client.proxy;

import java.lang.reflect.InvocationHandler;

import leemos.orion.client.Proxy;
import leemos.orion.client.ProxyInvoker;

public class JdkProxy implements Proxy {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProxy(Class<T> interfaceClass,
            ProxyInvoker proxyInvoker) {
        InvocationHandler handler = new JdkInvocationHandler<T>(interfaceClass,
                proxyInvoker);
        T result = (T) java.lang.reflect.Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[] { interfaceClass }, handler);
        return result;
    }

}
