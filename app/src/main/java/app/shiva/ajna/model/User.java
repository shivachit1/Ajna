package app.shiva.ajna.model;

public class User {
    private String UserID;
    private String userName;
    private String userEmail;
    private String photoUri;
    private String accountMode;
    private UserActive userActive;
    private LocationTracking locationTracking;

    public User() {
    }

    public User(String userID, String userName, String userEmail, String photoUri, String accountMode, UserActive userActive, LocationTracking locationTracking) {
        UserID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.photoUri = photoUri;
        this.accountMode = accountMode;
        this.userActive = userActive;
        this.locationTracking = locationTracking;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public String getAccountMode() {
        return accountMode;
    }

    public UserActive getUserActive() {
        return userActive;
    }

    public LocationTracking getLocationTracking() {
        return locationTracking;
    }


}
