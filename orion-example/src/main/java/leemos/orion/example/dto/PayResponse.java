package leemos.orion.example.dto;

import java.io.Serializable;

public class PayResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean status;
    private String message;

    public PayResponse(boolean status) {
        this(status, null);
    }

    public PayResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public static PayResponse success() {
        return new PayResponse(true);
    }

    public static PayResponse failure(String message) {
        return new PayResponse(false, message);
    }

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
