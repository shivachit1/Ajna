package app.shiva.ajna.model;

import java.util.ArrayList;

public class ChatRoom {
    private String chatRoomID;
    private String activeTime;
    private ArrayList<String> chatRoomUsers;
    private String lastMessageID;

    public ChatRoom(String chatRoomID,String activeTime, ArrayList<String> chatRoomUsers,String lastMessageID) {
        this.chatRoomID=chatRoomID;
        this.activeTime = activeTime;
        this.chatRoomUsers = chatRoomUsers;
        this.lastMessageID=lastMessageID;
    }

    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public String getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public ArrayList<String> getChatRoomUsers() {
        return chatRoomUsers;
    }

    public void setChatRoomUsers(ArrayList<String> chatRoomUsers) {
        this.chatRoomUsers = chatRoomUsers;
    }

    public String getLastMessageID() {
        return lastMessageID;
    }

    public void setLastMessageID(String lastMessageID) {
        this.lastMessageID = lastMessageID;
    }
}
