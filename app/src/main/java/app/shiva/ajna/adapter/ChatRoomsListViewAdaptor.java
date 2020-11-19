package app.shiva.ajna.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.shiva.ajna.R;
import app.shiva.ajna.model.ChatRoom;
import app.shiva.ajna.model.ChatRoomUser;
import app.shiva.ajna.model.MessageSeen;
import app.shiva.ajna.model.User;
import app.shiva.ajna.model.Message;

import static java.lang.Long.parseLong;

public class ChatRoomsListViewAdaptor extends RecyclerView.Adapter<ChatRoomsListViewAdaptor.MyViewHolder> {

public interface OnItemClickListener {
    void onViewClick(ChatRoom chatRoom);
    void onViewLongClick(ChatRoom chatRoom);
    void onDeleteChatRoom(ChatRoom chatRoom,int position);
}



    private final ArrayList<ChatRoom> data;
    private final ArrayList<User> userArrayList;
    private final OnItemClickListener onItemClickListener;
    private SimpleDateFormat simple;
    int selectedPosition=-1;



    public ChatRoomsListViewAdaptor(ArrayList<ChatRoom> data, ArrayList<User> userArrayList, OnItemClickListener onItemClickListener) {

        this.data = data;
        this.userArrayList=userArrayList;
        this.onItemClickListener = onItemClickListener;
        this.simple = new SimpleDateFormat("HH:mm a", Locale.getDefault());
    }





class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
    final View chatRoomItemView;
    ConstraintLayout messageView;
    ImageView senderImageView;
    final TextView userName;
    final TextView newConnectionText;
    final TextView message;
    final ConstraintLayout messageStatusLayout;
    final TextView messageStatus;
    final TextView date;
    final TextView activeTextView;
    final ConstraintLayout activeTimerViewCons;
    ImageButton deleteChatRoomButton;
    final Handler handler;

    final OnItemClickListener mListener;

    MyViewHolder(View v,OnItemClickListener listener) {
        super(v);
        chatRoomItemView = v;
        messageView=v.findViewById(R.id.messageView);
        senderImageView=v.findViewById(R.id.senderImageView);
        userName =v.findViewById(R.id.userName);
        newConnectionText=v.findViewById(R.id.newConnectionText);
        message=v.findViewById(R.id.message_text);
        messageStatusLayout =v.findViewById(R.id.messagestatusLayout);
        messageStatus=v.findViewById(R.id.messageStatus);
        date=v.findViewById(R.id.messageInfo);
        activeTextView =  v.findViewById(R.id.activeTextView);
        activeTimerViewCons=  v.findViewById(R.id.activeTimerViewCons);
        deleteChatRoomButton=v.findViewById(R.id.deleteChatRoomButton);
        handler =new Handler();
        mListener = listener;
        deleteChatRoomButton.setOnClickListener(this);
        chatRoomItemView.setOnClickListener(this);
        chatRoomItemView.setOnLongClickListener(this);
        chatRoomItemView.getRootView().setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        ChatRoom chatRoom=data.get(getBindingAdapterPosition());
        if(v==deleteChatRoomButton){
            mListener.onDeleteChatRoom(chatRoom,getBindingAdapterPosition());
            selectedPosition=-1;
            notifyDataSetChanged();
        }

        if(v==chatRoomItemView){
            mListener.onViewClick(chatRoom);

        }

        if(v==chatRoomItemView.getRootView()){
           selectedPosition=-1;
           notifyDataSetChanged();
        }

    }

    @Override
    public boolean onLongClick(View v) {
        Log.e("Long Pressed", "ChatView" + getBindingAdapterPosition());

        selectedPosition=getBindingAdapterPosition();
        notifyDataSetChanged();


        return true;
    }
}


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messagelisteachview, parent, false);
        Context context = parent.getContext();


        return new MyViewHolder(itemView, onItemClickListener);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, int position) {
        if (data != null && FirebaseAuth.getInstance().getCurrentUser()!=null) {
            String UserId= (FirebaseAuth.getInstance().getCurrentUser()).getUid();

           ChatRoom chatRoom=data.get(position);
           HashMap<String, ChatRoomUser> chatRoomUsers=chatRoom.getChatRoomUsers();
            holder.deleteChatRoomButton.setVisibility(View.GONE);

           Message lastMessage=null;
            for (Map.Entry<String,Message> entry : chatRoom.getMessages().entrySet()) {

                Message message = entry.getValue();
                if(lastMessage==null){
                    lastMessage=message;
                }else{
                    double compareTime = Double.valueOf(((message).getDate()));
                    /* For Ascending order*/
                    double comparingToTime= Double.valueOf(lastMessage.getDate());
                    int difference=(int)(compareTime-comparingToTime);
                    if(difference>0){
                        lastMessage=message;
                    }
                }
            }

            String status="sent";

           if(lastMessage!=null && lastMessage.getMessageSeens()!=null && lastMessage.getMessageSeens().size()>0){

               MessageSeen messageSeen = lastMessage.getMessageSeens().get(0);
               String deliveredTime=messageSeen.getDeliveredTimeStamp();
               String seenTime=messageSeen.getSeenTimeStamp();


               if(!seenTime.equals("0")){
                   status="seen";

               }else if(!deliveredTime.equals("0")){

                   status="Delivered";

               }
               else{

                   status="sent";

               }

           }


            holder.messageStatusLayout.setVisibility(View.VISIBLE);
            Date result = new Date(Long.valueOf(lastMessage.getDate()));
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(simple.format(result));



            if (lastMessage.getSenderID().equals(UserId)) {

                holder.message.setText("You:"+lastMessage.getMessage());

                holder.messageStatusLayout.setVisibility(View.VISIBLE);
                if (status.equals("Delivered")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.delivered);
                } if (status.equals("sent")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.sent);
                }
                if (status.equals("seen")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.accept);
                }



            } else if (lastMessage.getSenderID().equals("auto")) {
                holder.message.setText(lastMessage.getMessage());
                holder.messageStatusLayout.setVisibility(View.GONE);


            } else if (!lastMessage.getSenderID().equals(UserId) && !lastMessage.getSenderID().equals("auto")) {

                holder.message.setText(lastMessage.getMessage());

                if (status.equals("Delivered")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.red_rectangle);
                    holder.newConnectionText.setVisibility(View.VISIBLE);
                }else{
                    holder.messageStatusLayout.setVisibility(View.GONE);
                    holder.newConnectionText.setVisibility(View.GONE);
                }

            }


            for (Map.Entry<String,ChatRoomUser> entry : chatRoomUsers.entrySet()) {

                ChatRoomUser chatRoomUser = entry.getValue();
                if(chatRoomUser.getTypingStatus().equals("true") && !chatRoomUser.getId().equals(UserId)){
                    holder.message.setText("is typing...");
                    holder.messageStatusLayout.setVisibility(View.GONE);
                    holder.date.setVisibility(View.GONE);
                }

                for(User user:userArrayList){

                        if(chatRoomUser.getId().equals(user.getUserID()) && !chatRoomUser.getId().equals(UserId)){


                            holder.userName.setText(user.getUserName());

                            if(lastMessage.getSenderID().equals("auto")){
                                holder.newConnectionText.setVisibility(View.VISIBLE);
                            }
                           Picasso.get().load(user.getPhotoUri()).fit().into(holder.senderImageView);
                            if(user.getUserActive().getActiveStatus().equals("true")){
                                holder.activeTimerViewCons.setVisibility(View.VISIBLE);
                                holder.activeTextView.setText("online");
                            }
                            else if(user.getUserActive().getActiveStatusShareSetting().equals("true") && user.getUserActive().getActiveStatus().equals("false")){

                                holder.activeTimerViewCons.setVisibility(View.VISIBLE);
                                holder.handler.removeCallbacksAndMessages(null);

                                holder.handler.postDelayed(new MyRunnable(holder.activeTextView, user.getUserActive().getTimestamp()), 1);


                            }
                            else{
                                holder.handler.removeCallbacksAndMessages(null);
                                holder.activeTimerViewCons.setVisibility(View.GONE);
                            }

                    }

                }

            }

        if(selectedPosition!=-1 && selectedPosition==position){

                holder.deleteChatRoomButton.setVisibility(View.VISIBLE);
                holder.messageView.setAlpha(0.2f);
            Handler handler = new Handler();
            final Runnable r = new Runnable(){
                public void run() {
                    holder.deleteChatRoomButton.setVisibility(View.GONE);
                    holder.messageView.setAlpha(1.0f);
                }
            };

            handler.postDelayed(r, 5000);
            holder.messageView.setAlpha(0.2f);
        }
        else{
            holder.messageView.setAlpha(1.0f);
        }

        }
    }





    @Override
    public int getItemCount() {
        return data.size();
    }



    private static class MyRunnable implements Runnable {
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
            if (mText != null ) {
                mText.setVisibility(View.VISIBLE);
                long currentDateTime = System.currentTimeMillis();
                long currentUserActiveTime= parseLong(activeTime);
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