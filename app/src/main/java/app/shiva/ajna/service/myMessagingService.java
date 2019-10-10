package app.shiva.ajna.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.shiva.ajna.R;
import app.shiva.ajna.activities.MapsActivity;


public class myMessagingService extends FirebaseMessagingService {

    private int friendRequestNotification=1000;
    private int messageNotification=2000;
    NotificationManager notificationManager;
    ArrayList<RemoteMessage> notificationData=new ArrayList<>();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getToken();
        Log.d("NEW_TOKEN",s);




    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(remoteMessage.getData().size()>0){
            notificationData.add(remoteMessage);

        String datatype=remoteMessage.getData().get("datatype");

        if(datatype.equals("message")){
            showMessageNotification(remoteMessage);

        }
        else if(datatype.equals("friendRequest")){
            showFriendRequestNotification(remoteMessage);

        }

        }


    }

    private void createNotificationChannel(String channel_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_ID;
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channel_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            notificationManager.createNotificationChannel(channel);
        }
    }

HashMap<String,ArrayList<String>> messageNotificationDataHashMap=new HashMap<>();
    ArrayList<String> messages=new ArrayList<>();
    NotificationCompat.Builder notification;
    private void showMessageNotification(RemoteMessage remoteMessage) {
        Log.d("Message Notification","True");
        int Message_SUMMARY_ID = 0;

        // creating message class object
        String chatroomID=remoteMessage.getData().get("chatRoomID");
        String messageID=remoteMessage.getData().get("messageId");
        String message=remoteMessage.getData().get("message");
        String messageSenderID=remoteMessage.getData().get("senderId");
        String senderName=remoteMessage.getData().get("senderName");
        String photoUri=remoteMessage.getData().get("senderImageUri");
        String date=remoteMessage.getData().get("date");


        StatusBarNotification[] activeNotifications=notificationManager.getActiveNotifications();

        if(activeNotifications.length==0){
            messageNotificationDataHashMap.clear();
            messages.clear();
            Log.d("id","No Notification Active");
        }
        else{
            for (StatusBarNotification activeNotification : activeNotifications) {

                if (activeNotification.getId() == 2000) {
                    Log.d("id","active");

                }
            }
        }


        messages.add(message);
        messageNotificationDataHashMap.put(chatroomID,messages);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Messages").child(chatroomID).child(messageID).child("status").setValue("Delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Message Status","Delivered");
            }
        });

        createNotificationChannel(chatroomID);


        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notificationType","Message");
        intent.putExtra("chatroomID",chatroomID);

        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                                            0, intent, PendingIntent.FLAG_UPDATE_CURRENT);



        Bitmap bmp = null;

        if (photoUri.equals("default")) {
            bmp = getCroppedBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.profileavatar));
        } else {

            try {
                InputStream in = new URL(photoUri).openStream();
                bmp = getCroppedBitmap(BitmapFactory.decodeStream(in));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        notification =new NotificationCompat.Builder(this, chatroomID)
                .setSmallIcon(R.drawable.message)
                .setContentTitle(senderName)
                .setGroup(chatroomID)
                .setContentText(message)
                .setTicker("New Message")
                .setLargeIcon(bmp)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setContentIntent(notifyPendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_HIGH);


        ArrayList<String> value=messageNotificationDataHashMap.get(chatroomID);
        NotificationCompat.InboxStyle iStyle =  new NotificationCompat.InboxStyle();
        for(String mesz:value){
            iStyle.addLine(mesz);
            Log.d("Message",mesz);
        }
        notification.setStyle(iStyle);
        notificationManager.notify(messageNotification, notification.build());



    }


    private void showFriendRequestNotification(RemoteMessage remoteMessage) {
        Log.d("FriendReq Notification","True");


        // creating message class object
        String contactID=remoteMessage.getData().get("contactID");
        String SenderID=remoteMessage.getData().get("senderId");
        String senderName=remoteMessage.getData().get("senderName");
        String receiverID=remoteMessage.getData().get("receiverID");


        createNotificationChannel(SenderID);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, SenderID)
                .setSmallIcon(R.drawable.message)
                .setContentTitle(senderName)
                .setContentText("Sent you Friend Request")
                .setPriority(NotificationCompat.PRIORITY_MAX);


        // Issue the notification.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(friendRequestNotification, builder.build());
        friendRequestNotification++;

    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

    }
}
