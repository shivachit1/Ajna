package app.shiva.ajna.model;

import android.graphics.drawable.Drawable;

public class TabModel {
   private String title;
   private Drawable drawable;
   private int notificationCounter;

    public TabModel(String title, Drawable drawable, int notificationCounter) {
        this.title = title;
        this.drawable = drawable;
        this.notificationCounter = notificationCounter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
