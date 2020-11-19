package app.shiva.ajna.model;

import android.graphics.drawable.Drawable;

class HeaderTab {
    private String headerTitle;
    private Drawable drawable;
    private int notificationCounter;

    public HeaderTab(String headerTitle, Drawable drawable, int notificationCounter) {
        this.headerTitle = headerTitle;
        this.drawable = drawable;
        this.notificationCounter = notificationCounter;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getNotificationCounter() {
        return notificationCounter;
    }

    public void setNotificationCounter(int notificationCounter) {
        this.notificationCounter = notificationCounter;
    }
}
