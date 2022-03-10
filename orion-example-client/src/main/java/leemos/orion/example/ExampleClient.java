package leemos.orion.example;

import leemos.orion.client.proxy.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import leemos.orion.client.Proxy;
import leemos.orion.client.ProxyInvoker;
import leemos.orion.client.proxy.jdk.JdkProxy;
import leemos.orion.client.proxy.jdk.JdkProxyInvoker;
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

        Pay payScene = ClientProxy.getProxy("127.0.0.1:10880", Pay.class, client);

        PayResponse response = payScene.pay(new PayRequest("62250000", "62251100", 10.24));

        if (response.isStatus()) {
            logger.info("succeeded");
        } else {
            logger.info("failed: " + response.getMessage());
        }

        client.stop();
    }

}
