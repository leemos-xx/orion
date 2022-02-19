package leemos.orion.client.proxy;

import leemos.orion.client.Client;
import leemos.orion.client.ProxyInvoker;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcResponse;

public class JdkProxyInvoker implements ProxyInvoker {

    private String address;
    private Client client;

    public JdkProxyInvoker(String addresss, Client client) {
        this.address = addresss;
        this.client = client;
    }

    @Override
    public RpcResponse invoke(RequestBody requestBody) throws InterruptedException, RemotingException {
        try {
            return client.invokeAsync(address, requestBody, 5000).waitResponse();
        } catch (InterruptedException | RemotingException e) {
            throw e;
        }
    }

}
