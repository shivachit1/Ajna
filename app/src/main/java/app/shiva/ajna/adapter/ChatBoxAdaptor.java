package app.shiva.ajna.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import app.shiva.ajna.R;
import app.shiva.ajna.model.Message;
import app.shiva.ajna.model.MessageSeen;

public class ChatBoxAdaptor extends RecyclerView.Adapter<ChatBoxAdaptor.MyViewHolder> {


    public interface OnItemClickListener {


        void deleteMessage(Message message,int position);
    }

    private final ArrayList<Message> data;

    private int oldDay=35;
    private final DateFormat simple;

    private final OnItemClickListener onItemClickListener;
    private final String UserID = (FirebaseAuth.getInstance().getCurrentUser()).getUid();
    int selectedPosition;
    int longPressPosition;


    public ChatBoxAdaptor(ArrayList<Message> data,OnItemClickListener onItemClickListener) {
        this.data = data;
        this.simple = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.onItemClickListener = onItemClickListener;
        selectedPosition=-1;
        longPressPosition=-1;

        }


class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    final TextView chatMessage;
    final TextView time;
    final ConstraintLayout chatBox;
    final ConstraintLayout bubblebox;
    final ConstraintLayout messageStatusLayout;
    final ImageView messageStatus;
    final TextView messageInfo;
    final ImageButton deleteMessage;
    final OnItemClickListener mListener;



    MyViewHolder(View v,OnItemClickListener listener) {
        super(v);
    View itemView=v;
        chatMessage=v.findViewById(R.id.chatMessage);
        time=v.findViewById(R.id.messageTime);
        chatBox=v.findViewById(R.id.chatBox);
        bubblebox=v.findViewById(R.id.bubblebox);
        messageStatusLayout =v.findViewById(R.id.messagestatusLayout);
        messageStatus=v.findViewById(R.id.messageStatus);
        messageInfo=v.findViewById(R.id.messageInfo);
        deleteMessage=v.findViewById(R.id.deleteMessage);
        mListener = listener;
        deleteMessage.setOnClickListener(this);
        chatBox.setOnClickListener(this);
        chatBox.setOnLongClickListener(this);
        v.getRootView().setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        Message message = data.get(getBindingAdapterPosition());

        if(v==chatBox){
            if(message.getSenderID().equals(UserID)){
              messageInfo.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                final Runnable r = new Runnable(){
                    public void run() {
                        messageInfo.setVisibility(View.GONE);
                    }
                };

                handler.postDelayed(r, 5000);
            }
        }
        if(v==deleteMessage){
            deleteMessage.setVisibility(View.INVISIBLE);
            mListener.deleteMessage(message,getBindingAdapterPosition());
            chatBox.setBackgroundResource(0);
            selectedPosition=-1;
        }


    }

    @Override
    public boolean onLongClick(View v) {

        Message message = data.get(getBindingAdapterPosition());

        selectedPosition=getBindingAdapterPosition();

        notifyDataSetChanged();


        return true;
    }
}


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bubble, parent, false);

        return new MyViewHolder(itemView, onItemClickListener);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, int position) {
        if (data != null && UserID!=null) {

            Message message = data.get(position);

            Date result = new Date(Long.parseLong(message.getDate()));
            holder.time.setVisibility(View.VISIBLE);
            holder.deleteMessage.setVisibility(View.GONE);
            holder.time.setText(simple.format(result));

            if(message.getMessageSeens()!=null && message.getMessageSeens().size()>0){
                MessageSeen messageSeen=message.getMessageSeens().get(0);
                Calendar calenderTime = Calendar.getInstance();
                calenderTime.setTimeInMillis(Long.parseLong(messageSeen.getDeliveredTimeStamp()));
                int newDay=calenderTime.get(Calendar.DATE);
                int newmnth=calenderTime.get(Calendar.MONTH);
                int newYear=calenderTime.get(Calendar.YEAR);
                Date date = new Date(Long.parseLong(message.getDate()));
                holder.messageInfo.setText("Delivered :  "+simple.format(date));
            }
            else{
                holder.messageInfo.setText("not delivered");
            }
            Calendar newTime = Calendar.getInstance();
            newTime.setTimeInMillis(Long.parseLong(message.getDate()));

            String status="sent";

            if(message.getMessageSeens()!=null && message.getMessageSeens().size()>0 ){


                String deliveredTime = message.getMessageSeens().get(0).getDeliveredTimeStamp();
                String seenTime = message.getMessageSeens().get(0).getSeenTimeStamp();
                if(!seenTime.equals("0")){
                    status="seen";

                }else if(!deliveredTime.equals("0")){

                    status="Delivered";

                }
                else{

                    status="sent";

                }

            }


            if (message.getSenderID().equals(UserID)) {

                holder.chatMessage.setText(message.getMessage());
                holder.bubblebox.setBackgroundResource(R.drawable.greenround);
                holder.messageStatusLayout.setVisibility(View.VISIBLE);
                if (status.equals("Delivered")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.delivered);
                } if (status.equals("sent")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.sent);
                }
                if (status.equals("seen")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.accept);
                }

                ConstraintSet set = new ConstraintSet();
                ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.mainconstraint);
                set.clone(constraintLayout);
                set.setHorizontalBias(holder.chatBox.getId(), 1);
                set.applyTo(constraintLayout);





            } else if (message.getSenderID().equals("auto")) {
                holder.chatMessage.setText(message.getMessage());
                holder.bubblebox.setBackgroundResource(0);
                holder.messageStatusLayout.setVisibility(View.GONE);
                ConstraintSet set = new ConstraintSet();
                ConstraintLayout constraintLayout =holder.itemView.findViewById(R.id.mainconstraint);
                set.clone(constraintLayout);

                set.setHorizontalBias(holder.chatBox.getId(), -1);
                set.applyTo(constraintLayout);


            } else if (!message.getSenderID().equals(UserID) && !message.getSenderID().equals("auto")) {

                holder.chatMessage.setText(message.getMessage());
                holder.bubblebox.setBackgroundResource(R.drawable.orangebackground);
                holder.messageStatusLayout.setVisibility(View.GONE);
                ConstraintSet set = new ConstraintSet();
                ConstraintLayout constraintLayout = holder.itemView.findViewById(R.id.mainconstraint);
                set.clone(constraintLayout);
                set.setHorizontalBias(holder.chatBox.getId(), 0);
                set.applyTo(constraintLayout);


            }

            if(selectedPosition!=-1 && selectedPosition==position){


                if(message.getSenderID().equals(UserID)){
                    holder.deleteMessage.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    final Runnable r = new Runnable(){
                        public void run() {
                            holder.deleteMessage.setVisibility(View.GONE);
                        }
                    };

                    handler.postDelayed(r, 5000);
                }

            }


        }




    }



    @Override
    public int getItemCount() {
        return data.size();
    }
}
