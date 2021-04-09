package leemos.orion.rpc.test;

import leemos.orion.client.rpc.OrionClient;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.message.RequestBody;
import leemos.orion.remoting.message.RpcResponse;

public class OrionClientTest {

    public static void main(String[] args) {
        OrionClient client = new OrionClient();

        try {
            client.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        String serviceName = "counter.increase";
        RequestBody requestBody = new RequestBody();
        requestBody.setServiceName(serviceName);
        requestBody.setParameters(new Object[] {});
        try {
            RpcResponse response = client.invokeAsync("localhost:10880", requestBody, 5000).waitResponse();
            System.out.println(response.getStatus());
            System.out.println(response.getResponseBody());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemotingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
