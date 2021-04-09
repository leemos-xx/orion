package leemos.orion.client.proxy;

import leemos.orion.client.Client;
import leemos.orion.client.ProxyInvoker;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcResponse;

public class JdkProxyInvoker implements ProxyInvoker {

    private Client client;

    public JdkProxyInvoker(Client client) {
        this.client = client;
    }

    @Override
    public RpcResponse invoke(RequestBody requestBody) {
        try {
            return client.invokeAsync("localhost:8888", requestBody, 5000).waitResponse();
        } catch (InterruptedException | RemotingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
