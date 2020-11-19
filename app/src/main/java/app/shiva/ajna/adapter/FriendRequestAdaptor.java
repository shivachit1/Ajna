package app.shiva.ajna.adapter;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import app.shiva.ajna.R;
import app.shiva.ajna.model.User;
import app.shiva.ajna.model.Contact;

public class FriendRequestAdaptor extends RecyclerView.Adapter<FriendRequestAdaptor.MyViewHolder> {

    public interface OnItemClickListener {
        void onViewClick(Contact consumer);
        void onDelete(Contact contact);
        void onAccept(Contact contact);
    }
    private final OnItemClickListener onItemClickListener;
    private final ArrayList<Contact> data ;
    private final ArrayList<User> userArrayList;


    public FriendRequestAdaptor(ArrayList<Contact> data, ArrayList<User> userArrayList, OnItemClickListener onItemClickListener) {

        this.data = data;
        this.userArrayList=userArrayList;
        this.onItemClickListener = onItemClickListener;

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ConstraintLayout itemview;
        final ImageView profileImage;
        final TextView username;
        final ConstraintLayout deleteAccept;
        final TextView line;
        final ImageButton delete;
        final ImageButton accept;
        final ConstraintLayout activeTimerViewCons;
        final TextView activeTextView;

        final OnItemClickListener mListener;


        MyViewHolder(View v,OnItemClickListener listener) {
            super(v);
            itemview = v.findViewById(R.id.itemView);
            profileImage=v.findViewById(R.id.userImageView);
            username= v.findViewById(R.id.username);
            deleteAccept=v.findViewById(R.id.deleteAccept);
            line=v.findViewById(R.id.line);
            delete=v.findViewById(R.id.delete);
            accept=v.findViewById(R.id.accept);
            activeTimerViewCons= v.findViewById(R.id.activeTimerViewCons);
            activeTextView=v.findViewById(R.id.activeTextView);
            mListener=listener;
            itemview.setOnClickListener(this);
            delete.setOnClickListener(this);
            accept.setOnClickListener(this);



        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.itemView:
                    mListener.onViewClick(data.get(getBindingAdapterPosition()));
                    break;

                case R.id.delete:
                    mListener.onDelete(data.get(getBindingAdapterPosition()));
                    break;

                case R.id.accept:
                    mListener.onAccept(data.get(getBindingAdapterPosition()));
                    break;

            }

        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eachcontactrequestview, parent, false);

        return new MyViewHolder(itemView, onItemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, int position) {
        if (data != null && FirebaseAuth.getInstance().getCurrentUser()!=null) {
            final String UserId=(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            Handler handler = new Handler();
            Contact contact=data.get(position);

            if(contact.getStatus().equals("Connected")){
                holder.deleteAccept.setVisibility(View.GONE);


            }
            if(contact.getStatus().equals("Pending") && contact.getSenderID().equals(UserId)){
                holder.line.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
                holder.activeTimerViewCons.setVisibility(View.GONE);
            }

            String friendID="";
            if (contact.getSenderID().equals(UserId)) {
                friendID = contact.getReceiverID();
            } else if (contact.getReceiverID().equals(UserId)) {
                friendID = contact.getSenderID();
            }

            for(User user:userArrayList){
                if(user.getUserID().equals(friendID)){
                    Picasso.get().load(user.getPhotoUri()).fit().centerCrop().into(holder.profileImage);

                    String string = user.getUserName();
                    String[] parts = string.split(" ");
                    String part1 = parts[0];
                    holder.username.setText(part1);


                    if(user.getUserActive().getActiveStatus().equals("true") && user.getUserActive().getActiveStatusShareSetting().equals("true")){
                        holder.activeTimerViewCons.setVisibility(View.VISIBLE);
                        holder.activeTextView.setText("online");
                    }
                    else if(user.getUserActive().getActiveStatusShareSetting().equals("true") && user.getUserActive().getActiveStatus().equals("false")){

                        if(!contact.getStatus().equals("Pending")){
                            holder.activeTimerViewCons.setVisibility(View.VISIBLE);
                        }

                       handler.removeCallbacksAndMessages(null);

                        handler.postDelayed(new MyRunnable(holder.activeTextView,user.getUserActive().getTimestamp()), 1);


                    }
                    else{
                       handler.removeCallbacksAndMessages(null);
                        holder.activeTimerViewCons.setVisibility(View.GONE);
                    }
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}