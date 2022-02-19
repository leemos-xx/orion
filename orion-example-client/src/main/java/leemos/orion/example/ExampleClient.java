package leemos.orion.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leemos.orion.client.Proxy;
import leemos.orion.client.ProxyInvoker;
import leemos.orion.client.proxy.JdkProxy;
import leemos.orion.client.proxy.JdkProxyInvoker;
import leemos.orion.client.rpc.OrionClient;
import leemos.orion.example.dto.PayRequest;
import leemos.orion.example.dto.PayResponse;
import leemos.orion.example.scene.Pay;
import leemos.orion.lifecycle.LifecycleException;

public class ExampleClient {

    private static final Logger logger = LoggerFactory
            .getLogger(ExampleClient.class);
    
    public static void main(String[] args) throws LifecycleException {
        OrionClient client = new OrionClient();
        client.start();

        Proxy proxy = new JdkProxy();
        ProxyInvoker proxyInvoker = new JdkProxyInvoker("127.0.0.1:10880", client);

        Pay payScene = proxy.getProxy(Pay.class, proxyInvoker);

        PayResponse response = payScene.pay(new PayRequest("62250000", "62251100", 10.24));

        if (response.isStatus()) {
            logger.info("successed");
        } else {
            logger.info("failed: " + response.getMessage());
        }

        client.stop();
    }

}
