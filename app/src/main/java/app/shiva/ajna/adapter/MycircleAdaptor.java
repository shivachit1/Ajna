package app.shiva.ajna.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import app.shiva.ajna.R;
import app.shiva.ajna.model.User;

class MycircleAdaptor extends RecyclerView.Adapter {

    private ArrayList<User> userArrayList;


    interface OnItemClickListener {
        void onViewClick(User user);
        void onSendMessage(User user);
        void onGetDirections(User user);
    }
    private final OnItemClickListener onItemClickListener;


    public MycircleAdaptor(ArrayList<User> userArrayList, User userDetails, OnItemClickListener onItemClickListener) {
        this.userArrayList = userArrayList;
        User userDetails1 = userDetails;
        this.onItemClickListener=onItemClickListener;
    }



    class PagerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView userImageView;
        private ConstraintLayout activeTimerViewCons;
        private TextView activeTextView;
        private TextView username;
        private OnItemClickListener mListener;


        PagerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            userImageView=itemView.findViewById(R.id.userImageView);
            activeTimerViewCons=itemView.findViewById(R.id.activeTimerViewCons);
            activeTextView=itemView.findViewById(R.id.activeTextView);
            username=itemView.findViewById(R.id.username);
            mListener=listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.itemView:
                    mListener.onViewClick(userArrayList.get(getBindingAdapterPosition()));
                    break;


            }

        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mycircleitem,parent,false);

        return new PagerViewHolder(view,onItemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PagerViewHolder pagerViewHolder=(PagerViewHolder) holder;
        Handler handler = new Handler();
        User user=userArrayList.get(position);
        Picasso.get().load(user.getPhotoUri()).fit().centerCrop().into(pagerViewHolder.userImageView);
        pagerViewHolder.username.setText(user.getUserName());

        if(user.getUserActive().getActiveStatus().equals("true") && user.getUserActive().getActiveStatusShareSetting().equals("true")){
            pagerViewHolder.activeTimerViewCons.setVisibility(View.VISIBLE);
            pagerViewHolder.activeTextView.setText("online");
        }
        else if(user.getUserActive().getActiveStatusShareSetting().equals("true") && user.getUserActive().getActiveStatus().equals("false")){

            pagerViewHolder.activeTimerViewCons.setVisibility(View.VISIBLE);
            handler.removeCallbacksAndMessages(null);

            handler.postDelayed(new MyRunnable(pagerViewHolder.activeTextView,user.getUserActive().getTimestamp()), 1);


        }
        else{
            handler.removeCallbacksAndMessages(null);
            pagerViewHolder.activeTimerViewCons.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
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