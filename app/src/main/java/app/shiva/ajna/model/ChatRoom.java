package app.shiva.ajna.model;
import java.util.HashMap;

public class ChatRoom implements Comparable<ChatRoom>{
    private final String chatRoomID;
    private String activeTime;
    private final HashMap<String,ChatRoomUser> chatRoomUsers;
    private final HashMap<String,Message> messages;


    public ChatRoom(String chatRoomID, String activeTime, HashMap<String,ChatRoomUser> chatRoomUsers, HashMap<String,Message> messages) {
        this.chatRoomID=chatRoomID;
        this.activeTime = activeTime;
        this.chatRoomUsers = chatRoomUsers;
        this.messages = messages;
    }

    public String getChatRoomID() {
        return chatRoomID;
    }



    private String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public HashMap<String, ChatRoomUser> getChatRoomUsers() {
        return chatRoomUsers;
    }


    public HashMap<String, Message> getMessages() {
        return messages;
    }





    @Override
    public int compareTo(ChatRoom chatRoom) {
        double compareTime = Double.valueOf(chatRoom.getActiveTime());
        /* For Ascending order*/
        double comparingToTime= Double.valueOf(this.activeTime);

        return (int)(compareTime - comparingToTime);
    }
}
