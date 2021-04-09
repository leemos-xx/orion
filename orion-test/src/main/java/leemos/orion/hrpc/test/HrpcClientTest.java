package leemos.orion.hrpc.test;

import java.util.concurrent.TimeUnit;

import leemos.orion.client.hrpc.HrpcClient;
import leemos.orion.lifecycle.LifecycleException;
import leemos.orion.remoting.RemotingException;
import leemos.orion.remoting.message.RequestBody;

public class HrpcClientTest {

    public static void main(String[] args) {
        HrpcClient client = new HrpcClient();

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
            client.invokeAsync("localhost:9999", requestBody, 5000000).waitResponse();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemotingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            TimeUnit.DAYS.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
