package app.shiva.ajna.model;

public class LocationTracking {
    private String locationTrackingStatus;
    private UserLatLng userLatLng;


    public LocationTracking(String locationTrackingStatus, UserLatLng userLatLng) {
        this.locationTrackingStatus = locationTrackingStatus;
        this.userLatLng = userLatLng;
    }

    public String getLocationTrackingStatus() {
        return locationTrackingStatus;
    }

    public void setLocationTrackingStatus(String locationTrackingStatus) {
        this.locationTrackingStatus = locationTrackingStatus;
    }

    public UserLatLng getUserLatLng() {
        return userLatLng;
    }

    public void setUserLatLng(UserLatLng userLatLng) {
        this.userLatLng = userLatLng;
    }
}
