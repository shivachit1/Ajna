package app.shiva.ajna.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import app.shiva.ajna.model.MessageSeen;

class MessageDeliveryWorker extends Worker {

    public MessageDeliveryWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NotNull
    @Override
    public Result doWork() {
        // Do the work here--in this case.
        String chatroomID =
                getInputData().getString("chatroomID");
        String messageID =
                getInputData().getString("messageID");
        String recieverid =
                getInputData().getString("recieverid");
        String currentDateTime = String.valueOf(System.currentTimeMillis());

        CountDownLatch countDownLatch = new CountDownLatch(1);
        if(chatroomID!=null && messageID!=null && recieverid!=null){
            ArrayList<MessageSeen> messageSeenArrayList = new ArrayList<>();
            MessageSeen messageSeen = new MessageSeen(recieverid, currentDateTime, "0");
            messageSeenArrayList.add(messageSeen);

            FirebaseDatabase.getInstance().getReference()
                    .child("Messages Rooms").child(chatroomID).child("messages")
                    .child(messageID).child("messageSeens").setValue(messageSeenArrayList).addOnSuccessListener(aVoid -> {
                         //countDownLatch.countDown();
                        Log.e("Message Delivered",messageID);
                    });

        }


        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }
}