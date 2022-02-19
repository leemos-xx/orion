package leemos.orion.example.scene;

import leemos.orion.core.engine.Api;
import leemos.orion.core.engine.Scene;
import leemos.orion.example.dto.PayRequest;
import leemos.orion.example.dto.PayResponse;

@Scene(name = "example")
public class ExampleScene {

    @Api(name = "pay")
    public PayResponse pay(PayRequest request) {
        System.out.println("payment...");
        System.out.println("PayAcct: " + request.getPayAcct());
        System.out.println("RecvAcct: " + request.getRecvAcct());
        System.out.println("Amount: " + request.getAmount());

        return PayResponse.success();
    }

}
