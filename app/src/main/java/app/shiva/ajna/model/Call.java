package app.shiva.ajna.model;

public class Call {
    private String callerId;
    private String recieverId;
    private String callType;
    private String callStatus;
    private String sessionId;
    private  String tokenId;
    private String startTimeStamp;
    private String endTimeStamp;

    public Call() {
    }

    public Call(String callerId, String recieverId, String callType, String callStatus, String sessionId, String tokenId, String startTimeStamp, String endTimeStamp) {
        this.callerId = callerId;
        this.recieverId = recieverId;
        this.callType = callType;
        this.callStatus = callStatus;
        this.sessionId = sessionId;
        this.tokenId = tokenId;
        this.startTimeStamp = startTimeStamp;
        this.endTimeStamp = endTimeStamp;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(String startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public String getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(String endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }
}
