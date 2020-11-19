package app.shiva.ajna.model;

class Notification {
    private String notificationID;
    private String notificationType;
    private String notificationSender;
    private String dataID;

    public Notification(String notificationID, String notificationType, String notificationSender, String dataID) {
        this.notificationID = notificationID;
        this.notificationType = notificationType;
        this.notificationSender = notificationSender;
        this.dataID = dataID;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationSender() {
        return notificationSender;
    }

    public void setNotificationSender(String notificationSender) {
        this.notificationSender = notificationSender;
    }

    public String getDataID() {
        return dataID;
    }

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }
}
