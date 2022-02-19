package leemos.orion.example.scene;

import leemos.orion.client.ServiceInvocation;
import leemos.orion.example.dto.PayRequest;
import leemos.orion.example.dto.PayResponse;

public interface Pay {

    @ServiceInvocation(name = "example.pay")
    public PayResponse pay(PayRequest request);
}
