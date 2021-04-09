package leemos.orion.rpc.test;

import leemos.orion.client.Proxy;
import leemos.orion.client.ProxyInvoker;
import leemos.orion.client.ServiceInvocation;
import leemos.orion.client.proxy.JdkProxy;
import leemos.orion.client.proxy.JdkProxyInvoker;
import leemos.orion.client.rpc.OrionClient;
import leemos.orion.lifecycle.LifecycleException;

public class OrionClientProxyTest {

    public static void main(String[] args) {
        OrionClient client = new OrionClient();

        try {
            client.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        Proxy proxy = new JdkProxy();
        ProxyInvoker proxyInvoker = new JdkProxyInvoker(client);

        CounterScene counterScene = proxy.getProxy(CounterScene.class, proxyInvoker);
        int result = counterScene.increase();

        System.out.println(result);
    }

    interface CounterScene {
        @ServiceInvocation(name = "counter.increase")
        public int increase();
    }
}
