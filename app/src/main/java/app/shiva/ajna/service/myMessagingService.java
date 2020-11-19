package app.shiva.ajna.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import app.shiva.ajna.R;
import app.shiva.ajna.activities.MapsActivity;
import app.shiva.ajna.model.MessageSeen;


public class myMessagingService extends FirebaseMessagingService {

    private int friendRequestNotification = 1000;
    private int messageNotificationId = 3000;
    private final int callNotification = 2000;

    private NotificationManager notificationManager;

    private static final String TAG = "MyMessagingService";
    private DatabaseReference reference;

    private HashMap<String,ArrayList<CharSequence>> messageNotificationHashmap=new HashMap<>();
    private  HashMap<String,Integer> messageNotificationIdHashmap=new HashMap<>();
    @Override
    public void onNewToken(@NotNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "Refreshed token: " + s);
        // Create a new background thread for processing messages or runnables sequentially
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
         reference=FirebaseDatabase.getInstance().getReference();
        if (remoteMessage.getData().size() > 0) {

            String datatype = remoteMessage.getData().get("datatype");

            if (datatype != null) {
                if (datatype.equals("message")) {
                    String messageID = remoteMessage.getData().get("messageId");
                    HandlerThread handlerThread = new HandlerThread("message");

                    if(!handlerThread.isAlive()){
                        handlerThread.start();
                    }

            // Create a handler attached to the HandlerThread's Looper
                   Handler mHandler = new Handler(handlerThread.getLooper());
                    // Execute the specified code on the worker thread
                    mHandler.post(() ->
                            showMessageNotification(remoteMessage)
                    );


                } else if (datatype.equals("friendRequest")) {
                    // showFriendRequestNotification(remoteMessage);

                }
                else if(datatype.equals("call")){
                    showCallNotification(remoteMessage);
                }
            }


        }


    }

    private void showCallNotification(RemoteMessage remoteMessage) {
        // creating call class object
        String callerId = remoteMessage.getData().get("callerId");
        String recieverId = remoteMessage.getData().get("recieverId");
        String sessionId = remoteMessage.getData().get("sessionId");
        String tokenId = remoteMessage.getData().get("tokenId");
        String callerName = remoteMessage.getData().get("callerName");
        String callerPhotoUri = remoteMessage.getData().get("callerPhotoUri");
        createNotificationChannel(callerId,"call");

        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(callerPhotoUri)
                    .submit().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 113,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "incoming call")
                .setSmallIcon(R.drawable.ajna_icon)
                .setContentTitle(callerName)
                .setContentText("is calling")
                .setLargeIcon(bitmap)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingIntent, true)
                .addAction(R.drawable. accept , "Accept" , pendingIntent)
                .addAction(R.drawable. delete , "Reject" , pendingIntent);


        notificationManager.notify(callNotification, notificationBuilder.build());

    }

    private void createNotificationChannel(String channel_ID,String notificationType) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationType.equals("message")) {
                CharSequence name = "Message";
                String description = "New message";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("message", name, importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);

            }
            if (notificationType.equals("call")) {
                CharSequence name = "Incoming Call";
                String description = "Incoming Call Notification";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel("incoming call", name, importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.enableVibration(true);
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                channel.setSound(Uri.parse("android.resource://"
                        + getApplicationContext().getPackageName() + "/" + R.raw.phoneringtone), att);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

                notificationManager.createNotificationChannel(channel);
            }


        }
    }


    private void showMessageNotification(RemoteMessage remoteMessage) {
        Log.d("Message Notification", "True");
        int Message_SUMMARY_ID = 0;
        // creating message class object
        String chatroomID = remoteMessage.getData().get("chatRoomID");
        String messageID = remoteMessage.getData().get("messageId");
        String message = remoteMessage.getData().get("message");
        String messageSenderID = remoteMessage.getData().get("senderId");
        String senderName = remoteMessage.getData().get("senderName");
        String photoUri = remoteMessage.getData().get("senderImageUri");
        String date = remoteMessage.getData().get("date");
        String recieverID = remoteMessage.getData().get("recieverID");

        if (chatroomID != null && messageID != null && messageSenderID != null && photoUri != null) {


            long currentDateTime = System.currentTimeMillis();



            if (MapsActivity.eachChatViewActive) {

                if(chatroomID.equals(MapsActivity.activeChatRoom.getChatRoomID())){
                    Log.d("Chatting window active", chatroomID);
                    ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<>();
                    MessageSeen messageSeen = new MessageSeen(recieverID, String.valueOf(currentDateTime), String.valueOf(currentDateTime));
                    messageSeenArrayList.add(messageSeen);
                    reference.child("Messages Rooms").child(chatroomID).child("messages").child(messageID).child("messageSeens").setValue(messageSeenArrayList);

                }

            } else {

            /*  Data newMessageData = new Data.Builder()
                        .putString("chatroomID", chatroomID)
                        .putString("messageID", messageID)
                        .putString("recieverid", recieverID)
                        .build();
                OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(MessageDeliveryWorker.class)
                        .setInputData(newMessageData)
                        .build();

                WorkManager.getInstance().enqueue(uploadWorkRequest);
*/
            Log.d("Chatting window not active", chatroomID);

                createNotificationChannel(messageID,"message");




                String uniqueID = String.valueOf(System.currentTimeMillis());
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("notificationType", "Message");
                intent.putExtra("chatroomID", chatroomID);
                intent.putExtra("message", message);
                intent.setAction(uniqueID);


                // Create the PendingIntent
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(photoUri)
                            .submit().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"message")
                        .setSmallIcon(R.drawable.ajna_icon)
                        .setContentTitle(senderName)
                        .setGroup(chatroomID)
                        .setContentText(message)
                        .setTicker("New Message")
                        .setLargeIcon(bitmap)
                        .setAutoCancel(true)
                        .setContentIntent(notifyPendingIntent)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);


                StatusBarNotification[] activeNotifications=notificationManager.getActiveNotifications();


                        if(activeNotifications.length==0){
                            messageNotificationHashmap.clear();
                        }else{
                            boolean contains=false;
                            for(StatusBarNotification statusBarNotification:activeNotifications){
                                if(statusBarNotification.getGroupKey() .equals(chatroomID)){
                                    contains=true;
                                }
                            }
                        }


                    if(messageNotificationHashmap.containsKey(chatroomID)){
                        messageNotificationId =messageNotificationIdHashmap.get(chatroomID);
                        messageNotificationHashmap.get(chatroomID).add(message);
                        NotificationCompat.InboxStyle inboxStyle =
                                new NotificationCompat.InboxStyle();
                        ArrayList<CharSequence> messages=messageNotificationHashmap.get(chatroomID);

                        for(CharSequence msg:messages){
                            inboxStyle.addLine(msg);
                            Log.e("lineAdded",msg.toString());
                        }
                        notificationBuilder.setStyle(inboxStyle);
                    }
                    else{
                        messageNotificationId++;
                        messageNotificationIdHashmap.put(chatroomID, messageNotificationId);
                        ArrayList<CharSequence> messageNotificationContentText=new ArrayList<>();
                        messageNotificationContentText.add(message);
                        messageNotificationHashmap.put(chatroomID,messageNotificationContentText);
                    }



                ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<>();
                MessageSeen messageSeen = new MessageSeen(recieverID, String.valueOf(currentDateTime), "0");
                messageSeenArrayList.add(messageSeen);


                reference.child("Messages Rooms").child(chatroomID).child("messages")
                        .child(messageID).child("messageSeens").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            reference.child("Messages Rooms").child(chatroomID).child("messages")
                                    .child(messageID).child("messageSeens").setValue(messageSeenArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e("Message Delivered",messageID);
                                }
                            });


                        }else{
                            Log.e("Message Delivered already",messageID);
                        }
                        notificationManager.notify(messageNotificationId, notificationBuilder.build());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }

        }


    }


    private void showFriendRequestNotification(RemoteMessage remoteMessage) {
        Log.d("FriendReq Notification", "True");


        // creating message class object
        String contactID = remoteMessage.getData().get("contactID");
        String senderID = remoteMessage.getData().get("senderId");
        String senderName = remoteMessage.getData().get("senderName");
        String receiverID = remoteMessage.getData().get("receiverID");


        createNotificationChannel(senderID,"friendRequest");

        if(senderID!=null){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), senderID)
                    .setSmallIcon(R.drawable.contacts)
                    .setContentTitle(senderName)
                    .setContentText("Sent you Friend Request")
                    .setPriority(NotificationCompat.PRIORITY_MAX);


            // Issue the notification.
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(friendRequestNotification, builder.build());
            friendRequestNotification++;
        }


    }


}
