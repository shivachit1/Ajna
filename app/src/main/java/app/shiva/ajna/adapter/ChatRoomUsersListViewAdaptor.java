package app.shiva.ajna.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import app.shiva.ajna.R;
import app.shiva.ajna.model.User;

public class ChatRoomUsersListViewAdaptor extends RecyclerView.Adapter<ChatRoomUsersListViewAdaptor.MyViewHolder> {


    private final ArrayList<User> data;


    public ChatRoomUsersListViewAdaptor( ArrayList<User> data) {
        this.data = data;

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        final View itemview;
        final ImageView profileImage;
        final TextView username;
        final ConstraintLayout activeTimerViewCons;
        final TextView activeTextView;
        final Handler handler;



        MyViewHolder(View v) {
            super(v);
            itemview = v;
            profileImage = v.findViewById(R.id.userImageView);
            username = v.findViewById(R.id.name);
            activeTimerViewCons=v.findViewById(R.id.activeTimerViewCons);
            activeTextView = v.findViewById(R.id.activeTextView);
            handler = new Handler();


        }


    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatroomusereachview, parent, false);


        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, int position) {
        if (data != null) {
            String UserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            User user = data.get(position);

            holder.itemview.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            if(!user.getUserID().equals(UserId)){

                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                if (user.getPhotoUri() != null) {
                    Picasso.get().load(user.getPhotoUri()).fit().into(holder.profileImage);
                }
                if (user.getUserID().equals(UserId)) {
                    holder.username.setText("You");
                } else {
                    holder.username.setText(user.getUserName().substring(0, user.getUserName().indexOf(" ")));
                }

                if(user.getUserActive().getActiveStatus().equals("true")){
                    holder.activeTimerViewCons.setVisibility(View.VISIBLE);
                    holder.activeTextView.setText("online");
                }
                else if(user.getUserActive().getActiveStatusShareSetting().equals("true") && user.getUserActive().getActiveStatus().equals("false")){

                    holder.activeTimerViewCons.setVisibility(View.VISIBLE);
                    holder.handler.removeCallbacksAndMessages(null);
                    holder.handler.postDelayed(new ChatRoomUsersListViewAdaptor.MyRunnable(holder.activeTextView,user.getUserActive().getTimestamp()), 1);


                }
                else{
                    holder.handler.removeCallbacksAndMessages(null);
                    holder.activeTimerViewCons.setVisibility(View.GONE);
                }

            }

        }


    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    private class MyRunnable implements Runnable {
        final WeakReference<TextView> tvText;
        final String activeTime;
        MyRunnable(TextView tvText, String activeTime) {
            this.tvText = new WeakReference<>(tvText);
            this.activeTime = activeTime;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            //Save the TextView to a local variable because the weak referenced object could become empty at any time
            TextView mText = tvText.get();
            if (mText != null) {
                long currentDateTime = System.currentTimeMillis();
                long currentUserActiveTime=Long.valueOf(activeTime);
                long difference = currentDateTime-currentUserActiveTime;

                long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
                mText.setText(minutes+"m");

                if (minutes>60) {
                    long hrs=minutes/60;
                    mText.setText(hrs+" hr");
                    if(hrs>24){
                        long day = hrs/24;
                        mText.setText(day+" day");
                    }
                }
            }
        }
    }
}