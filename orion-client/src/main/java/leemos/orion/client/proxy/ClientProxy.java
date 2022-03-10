package leemos.orion.client.proxy;

import leemos.orion.client.Client;
import leemos.orion.client.Proxy;
import leemos.orion.client.ProxyInvoker;
import leemos.orion.client.proxy.jdk.JdkProxy;
import leemos.orion.client.proxy.jdk.JdkProxyInvoker;

public class ClientProxy {

    public static <T> T  getProxy(String address, Class<T> interfaceClass, Client client) {
        Proxy proxy = new JdkProxy();
        ProxyInvoker proxyInvoker = new JdkProxyInvoker(address, client);

        return proxy.getProxy(interfaceClass, proxyInvoker);
    }
}
