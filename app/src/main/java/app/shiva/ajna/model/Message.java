package app.shiva.ajna.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable,Comparable<Message> {
   private String message;
    private String messageId;
    private String date;
    private String senderID;
    private String messageType;
    private ArrayList<MessageSeen> messageSeens;

    public Message() {
    }

    public Message(String message, String messageId, String date, String senderID, String messageType) {
        this.message = message;
        this.messageId = messageId;
        this.date = date;
        this.senderID = senderID;
        this.messageType = messageType;
        this.messageSeens=new ArrayList<>();
    }

    public Message(String message, String messageId, String currentdate, String senderID, String messageType, ArrayList<MessageSeen> messageSeens) {
        this.message = message;
        this.messageId=messageId;
        this.date=currentdate;
        this.senderID = senderID;
        this.messageType = messageType;
        this.messageSeens = messageSeens;
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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }


    public ArrayList<MessageSeen> getMessageSeens() {
        return messageSeens;
    }

    public void setMessageSeens(ArrayList<MessageSeen> messageSeens) {
        this.messageSeens = messageSeens;
    }


    @Override
    public int compareTo(Message message) {
        double compareTime = Double.valueOf(message.getDate());
        /* For Ascending order*/
        double comparingToTime= Double.valueOf(this.date);
        return (int)(compareTime-comparingToTime);
    }
}


