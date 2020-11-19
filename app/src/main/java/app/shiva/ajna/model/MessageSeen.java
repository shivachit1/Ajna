package app.shiva.ajna.model;

public class MessageSeen {
    private String messageTo;
    private String deliveredTimeStamp;
    private String seenTimeStamp;

    public MessageSeen() {
    }

    public MessageSeen(String messageTo, String deliveredTimeStamp, String seenTimeStamp) {
        this.messageTo = messageTo;
        this.deliveredTimeStamp = deliveredTimeStamp;
        this.seenTimeStamp = seenTimeStamp;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getDeliveredTimeStamp() {
        return deliveredTimeStamp;
    }

    public void setDeliveredTimeStamp(String deliveredTimeStamp) {
        this.deliveredTimeStamp = deliveredTimeStamp;
    }

    public String getSeenTimeStamp() {
        return seenTimeStamp;
    }

    public void setSeenTimeStamp(String seenTimeStamp) {
        this.seenTimeStamp = seenTimeStamp;
    }
}
