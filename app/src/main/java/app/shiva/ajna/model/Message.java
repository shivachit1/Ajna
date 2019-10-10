package app.shiva.ajna.model;

import java.io.Serializable;

public class Message implements Serializable {
   private String message;
    private String messageId;
    private String date;
    private String senderID;
    private String status;

    public Message() {
    }

    public Message(String message, String messageId, String currentdate, String senderID, String status) {
        this.message = message;
        this.messageId=messageId;
        this.date=currentdate;
        this.senderID = senderID;
        this.status=status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


