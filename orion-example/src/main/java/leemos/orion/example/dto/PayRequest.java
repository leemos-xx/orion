package leemos.orion.example.dto;

import java.io.Serializable;

public class PayRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String payAcct;
    private String recvAcct;
    private double amount;

    public PayRequest(String payAcct, String recvAcct, double amount) {
        super();
        this.payAcct = payAcct;
        this.recvAcct = recvAcct;
        this.amount = amount;
    }

    public String getPayAcct() {
        return payAcct;
    }

    public String getRecvAcct() {
        return recvAcct;
    }

    public double getAmount() {
        return amount;
    }
}
