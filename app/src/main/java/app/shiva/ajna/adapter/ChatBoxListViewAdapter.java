package app.shiva.ajna.adapter;

import android.app.Activity;
import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import app.shiva.ajna.R;
import app.shiva.ajna.model.Message;

public class ChatBoxListViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Message> data;
    String UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    int oldDay=35;

    public ChatBoxListViewAdapter(Context context, int layoutResourceId, ArrayList<Message> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.chatMessage=row.findViewById(R.id.chatMessage);
            holder.time=row.findViewById(R.id.messageTime);
            holder.bubble=row.findViewById(R.id.bubblebox);
            holder.messagestatusLayout=row.findViewById(R.id.messagestatusLayout);
            holder.messageStatus=row.findViewById(R.id.messageStatus);
            holder.dateTextView=row.findViewById(R.id.dateTextView);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Message item = data.get(position);

        DateFormat simple = new SimpleDateFormat("HH:mm:ss a");
        Date result = new Date(Long.valueOf(item.getDate()));
        holder.time.setVisibility(View.VISIBLE);
        holder.time.setText(simple.format(result));

        if(position!=0){
            Calendar oldTime = Calendar.getInstance();
            oldTime.setTimeInMillis(Long.valueOf(data.get(position-1).getDate()));
            oldDay=oldTime.get(Calendar.DATE);
        }
        Calendar newTime = Calendar.getInstance();
        newTime.setTimeInMillis(Long.valueOf(item.getDate()));
        int newday=newTime.get(Calendar.DATE);
        int newmnth=newTime.get(Calendar.MONTH);
        int newYear=newTime.get(Calendar.YEAR);

        if(oldDay!=35 && oldDay<=newday-1){
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(newday+"/"+newmnth+"/"+newYear);
        }
        else{
            holder.dateTextView.setVisibility(View.GONE);
        }


            if (item.getSenderID().equals(UserID)) {

                holder.chatMessage.setText(item.getMessage());
                holder.bubble.setBackgroundResource(R.drawable.mychatbox);
                holder.messagestatusLayout.setVisibility(View.VISIBLE);
                if (item.getStatus().equals("Delivered")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.delivered);
                } if (item.getStatus().equals("sent")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.sent);
                }
                if (item.getStatus().equals("seen")) {
                    holder.messageStatus.setBackgroundResource(R.drawable.seen);
                }

                ConstraintSet set = new ConstraintSet();
                ConstraintLayout constraintLayout = (ConstraintLayout) row.findViewById(R.id.mainconstraint);
                set.clone(constraintLayout);
                set.setHorizontalBias(holder.bubble.getId(), 1);
                set.applyTo(constraintLayout);





            } else if (item.getSenderID().equals("auto")) {
                holder.chatMessage.setText(item.getMessage());
                holder.bubble.setBackgroundResource(0);
                holder.messagestatusLayout.setVisibility(View.INVISIBLE);
                ConstraintSet set = new ConstraintSet();
                ConstraintLayout constraintLayout = (ConstraintLayout) row.findViewById(R.id.mainconstraint);
                set.clone(constraintLayout);

                set.setHorizontalBias(holder.bubble.getId(), 0.5f);
                set.applyTo(constraintLayout);


            } else if (!item.getSenderID().equals(UserID) && !item.getSenderID().equals("auto")) {
                holder.chatMessage.setText(item.getMessage());
                holder.bubble.setBackgroundResource(R.drawable.friendchatbubble);
                holder.messagestatusLayout.setVisibility(View.GONE);
                ConstraintSet set = new ConstraintSet();
                ConstraintLayout constraintLayout = (ConstraintLayout) row.findViewById(R.id.mainconstraint);
                set.clone(constraintLayout);
                set.setHorizontalBias(holder.bubble.getId(), 0);
                set.applyTo(constraintLayout);



            }

                    if(item.getStatus().equals("typing")){
                        holder.chatMessage.setText("...");
                        holder.time.setVisibility(View.GONE);
                        holder.messagestatusLayout.setVisibility(View.GONE);
                    }


        return row;
    }

    static class ViewHolder {

        TextView chatMessage;
        TextView time;
        ConstraintLayout bubble;
        ConstraintLayout messagestatusLayout;
        ImageView messageStatus;
        TextView dateTextView;

    }
}