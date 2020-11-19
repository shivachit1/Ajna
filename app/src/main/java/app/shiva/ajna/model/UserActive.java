package app.shiva.ajna.model;

public class UserActive {
    private String activeStatus;
    private String activeStatusShareSetting;
    private String timestamp;

    public UserActive() {
    }

    public UserActive(String activeStatus,String activeStatusShareSetting, String timestamp) {
        this.activeStatus=activeStatus;
        this.activeStatusShareSetting = activeStatusShareSetting;
        this.timestamp = timestamp;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getActiveStatusShareSetting() {
        return activeStatusShareSetting;
    }

    public void setActiveStatusShareSetting(String activeStatusShareSetting) {
        this.activeStatusShareSetting = activeStatusShareSetting;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
