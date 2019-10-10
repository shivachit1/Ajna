package app.shiva.ajna.model;

public class Contact {
    private String contactID;
    private String senderID;
    private String receiverID;
    private String status;

    public Contact() {
    }

    public Contact(String contactID,String senderID, String receiverID,String status) {
        this.contactID=contactID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.status=status;
    }

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
