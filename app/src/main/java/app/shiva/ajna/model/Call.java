package app.shiva.ajna.model;

public class Call {
    private String callerId;
    private String receiverId;
    private String callType;

    public Call() {
    }

    public Call(String callerId, String receiverId, String callType) {
        this.callerId = callerId;
        this.receiverId = receiverId;
        this.callType = callType;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
