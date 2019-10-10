package app.shiva.ajna.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.shiva.ajna.R;

public class ChatRoomUsersListViewAdaptor extends RecyclerView.Adapter<ChatRoomUsersListViewAdaptor.MyViewHolder> {

public interface OnItemClickListener {
    void onViewClick(String FriendID);
}
    private Context context;
    private final ChatRoomUsersListViewAdaptor.OnItemClickListener listener;
    private ArrayList<String> data = new ArrayList();
    private DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


    public ChatRoomUsersListViewAdaptor(Context context, ArrayList<String> data, ChatRoomUsersListViewAdaptor.OnItemClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
    }


class MyViewHolder extends RecyclerView.ViewHolder{
    View itemview;
    ImageView profileImage;
    TextView username;






    MyViewHolder(View v) {
        super(v);
        itemview = v;
        profileImage=v.findViewById(R.id.profileImage);
        username=v.findViewById(R.id.name);



    }

    void bind(final String friendID, final ChatRoomUsersListViewAdaptor.OnItemClickListener listener) {


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onViewClick(friendID);
            }
        });

    }
}


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatroomusereachview, parent, false);
        Context context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (data != null) {
            String UserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
            String friendID= data.get(position);

                    mFirebaseDatabaseReference.child("Users").child("Consumers").child(friendID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){
                                String consumerName=dataSnapshot.child("consumerName").getValue(String.class);
                                String consumerEmail=dataSnapshot.child("consumerEmail").getValue(String.class);
                                String photoUri=dataSnapshot.child("photoUri").getValue(String.class);
                                if(photoUri!=null){
                                        Picasso.get().load(photoUri).into(holder.profileImage);
                                }
                                if(dataSnapshot.getKey().equals(UserId)){
                                    holder.username.setText("You");
                                }
                                else{
                                    holder.username.setText(consumerName.substring(0,consumerName.indexOf(" ")));
                                }



                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    holder.bind(data.get(position), listener);
                }


    }




    @Override
    public int getItemCount() {
        return data.size();
    }
}