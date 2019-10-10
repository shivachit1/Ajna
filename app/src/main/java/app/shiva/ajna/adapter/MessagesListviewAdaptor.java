package app.shiva.ajna.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import app.shiva.ajna.R;
import app.shiva.ajna.model.ChatRoom;
import app.shiva.ajna.model.Consumer;

public class MessagesListviewAdaptor extends RecyclerView.Adapter<MessagesListviewAdaptor.MyViewHolder> {

public interface OnItemClickListener {
    void onViewClick(ChatRoom chatRoom);
}
    public interface dataChange {
        void dataChange();
    }
    private Context context;
    private final MessagesListviewAdaptor.OnItemClickListener listener;
    private ArrayList<ChatRoom> data = new ArrayList();
    private DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private Consumer consumer;
    private HashMap<ChatRoom,ArrayList<String>> chatRoomHashmap=new HashMap<>();
    private GroupUserListAdaptor groupUserListAdaptor;
    private int unSeenMessagesCounter;
    MessagesListviewAdaptor adapter1;

        public MessagesListviewAdaptor(dataChange dataChange){
            listener=null;

        }

    public MessagesListviewAdaptor(Context context, ArrayList<ChatRoom> data, HashMap<ChatRoom,ArrayList<String>> chatRoomHashmap, MessagesListviewAdaptor.OnItemClickListener listener) {
        this.data = data;
        this.context = context;
        this.chatRoomHashmap=chatRoomHashmap;
        this.listener = listener;
        consumer=new Consumer();
    }





class MyViewHolder extends RecyclerView.ViewHolder{
    View itemview;
    ImageView profileImage;
    RecyclerView groupmembersList;
    TextView username;
    TextView message;
    ConstraintLayout messagestatusLayout;
    TextView messageStatus;
    TextView date;
    TextView online_status;

    MyViewHolder(View v) {
        super(v);
        itemview = v;
        profileImage=v.findViewById(R.id.friendimage);
        groupmembersList=v.findViewById(R.id.groupmembersList);
        username= v.findViewById(R.id.friendName);
        message=v.findViewById(R.id.message_text);
        messagestatusLayout =v.findViewById(R.id.messagestatusLayout);
        messageStatus=v.findViewById(R.id.messageStatus);
        date=v.findViewById(R.id.dateTextView);
        online_status=v.findViewById(R.id.online_status);



    }

    void bind(final ChatRoom chatRoom, final OnItemClickListener listener) {


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onViewClick(chatRoom);
            }
        });
        groupmembersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messagelisteachview, parent, false);
        Context context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    long last=1;
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (data != null) {
            String UserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
            holder.bind(data.get(position), listener);

           String friendID="";
            ArrayList<String> chatRoomUsers=chatRoomHashmap.get(data.get(position));
            for(String id:chatRoomUsers){
                if(id.equals(UserId)){

                }else{
                    friendID=id;
                }
            }


            if(chatRoomUsers.size()==2){
                mFirebaseDatabaseReference.child("Users").child("Consumers").child(friendID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String consumerName=dataSnapshot.child("consumerName").getValue(String.class);
                            String consumerEmail=dataSnapshot.child("consumerEmail").getValue(String.class);
                            String photoUri=dataSnapshot.child("photoUri").getValue(String.class);
                            if(photoUri!=null){
                                Picasso.get().load(photoUri).fit().centerCrop().into(holder.profileImage);
                            }
                            holder.username.setText(consumerName);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mFirebaseDatabaseReference.child("User-Active-time").child(friendID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String onlineStatus=dataSnapshot.getValue(String.class);
                            holder.online_status.setVisibility(View.VISIBLE);
                        }
                        else{
                            holder.online_status.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if(chatRoomUsers.size()>2){
                holder.profileImage.setVisibility(View.GONE);
                mFirebaseDatabaseReference.child("Messages Room Names").child(data.get(position).getChatRoomID()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String roomName=dataSnapshot.getValue(String.class);
                            holder.username.setText(roomName);
                            holder.profileImage.setBackgroundResource(R.drawable.contacts);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                LinearLayoutManager horizontalLayoutManager
                        = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder.groupmembersList.setLayoutManager(horizontalLayoutManager);
                groupUserListAdaptor=new GroupUserListAdaptor(context, chatRoomUsers);
                holder.groupmembersList.setAdapter(groupUserListAdaptor);

            }





            mFirebaseDatabaseReference.child("Messages").child(data.get(position).getChatRoomID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        unSeenMessagesCounter=0;
                        long lastMessageTime=0;

                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){

                            String message1=dataSnapshot1.child("message").getValue(String.class);
                            String messageid=dataSnapshot1.child("messageId").getValue(String.class);
                            String messageDate=dataSnapshot1.child("date").getValue(String.class);
                            String senderID=dataSnapshot1.child("senderID").getValue(String.class);
                            String status=dataSnapshot1.child("status").getValue(String.class);


                            if(senderID.equals(UserId)){
                                holder.message.setText("You : "+message1);

                                holder.message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                holder.itemview.setBackgroundResource(R.drawable.mychatbox);
                                holder.messagestatusLayout.setVisibility(View.VISIBLE);
                                holder.messageStatus.setText("");

                                if (status.equals("Delivered")) {

                                    holder.messageStatus.setBackgroundResource(R.drawable.delivered);
                                } if (status.equals("sent")) {
                                    holder.messageStatus.setBackgroundResource(R.drawable.sent);
                                }
                                if (status.equals("seen")) {
                                    holder.messageStatus.setBackgroundResource(R.drawable.seen);
                                }
                            }
                            else if(!senderID.equals(UserId)){
                                if (status.equals("Delivered")||status.equals("sent")) {
                                    unSeenMessagesCounter++;
                                    holder.messagestatusLayout.setVisibility(View.VISIBLE);
                                    holder.messageStatus.setBackgroundResource(R.drawable.red_rectangle);
                                    holder.messageStatus.setText(String.valueOf(unSeenMessagesCounter));


                                }
                                if (status.equals("seen")) {
                                    holder.messagestatusLayout.setVisibility(View.INVISIBLE);
                                }

                                holder.message.setText(message1);
                                holder.itemview.setBackgroundResource(R.drawable.mychatbox);
                                holder.message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                holder.message.setTypeface(null, Typeface.BOLD);

                                if(status.equals("typing") || message1.equals("...")){
                                    holder.message.setText("is typing...");
                                    holder.messagestatusLayout.setVisibility(View.INVISIBLE);
                                }

                            }




                        }



                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DateFormat simple = new SimpleDateFormat("HH:mm a");
            Date result = new Date(Long.valueOf(data.get(position).getActiveTime()));
            holder.date.setText(simple.format(result));


            holder.itemview.setVisibility(View.VISIBLE);
        }
    }




    @Override
    public int getItemCount() {
        return data.size();
    }
}