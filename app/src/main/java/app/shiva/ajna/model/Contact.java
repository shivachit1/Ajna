package app.shiva.ajna.model;

public class Contact {
    private final String contactID;
    private final String senderID;
    private final String receiverID;
    private String status;

    public Contact(String contactID,String senderID, String receiverID,String status) {
        this.contactID=contactID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.status=status;
    }

    public String getContactID() {
        return contactID;
    }

    
    public String getSenderID() {
        return senderID;
    }


    public String getReceiverID() {
        return receiverID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
