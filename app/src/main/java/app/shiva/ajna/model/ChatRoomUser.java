package app.shiva.ajna.model;

public class ChatRoomUser {

    private final String Id;
    private final String typingStatus;


    public ChatRoomUser(String id, String typingStatus) {
        this.Id = id;
        this.typingStatus = typingStatus;
    }

    public String getId() {
        return Id;
    }

    public String getTypingStatus() {
        return typingStatus;
    }

}
