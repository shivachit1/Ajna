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
import app.shiva.ajna.model.Consumer;
import app.shiva.ajna.model.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onViewClick(Contact consumer);
        void onDelete(Contact contact);
        void onAccept(Contact contact);
    }
    private Context context;
    private final OnItemClickListener listener;
    private ArrayList<Contact> data = new ArrayList();
    private DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private Consumer consumer;


    public ContactAdapter(Context context, ArrayList<Contact> data, ContactAdapter.OnItemClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
        consumer=new Consumer();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        View itemview;
        ImageView profileImage;
        TextView username;
        ConstraintLayout deleteAccept;
        TextView line;
        ImageButton delete;
        ImageButton accept;
        TextView online_status;







        MyViewHolder(View v) {
            super(v);
            itemview = v;
            profileImage=v.findViewById(R.id.profileImage);
            username= v.findViewById(R.id.username);
            deleteAccept=v.findViewById(R.id.deleteAccept);
            line=v.findViewById(R.id.line);
            delete=v.findViewById(R.id.delete);
            accept=v.findViewById(R.id.accept);
            online_status=v.findViewById(R.id.online_status);



        }

        void bind(final Contact contact, final ContactAdapter.OnItemClickListener listener) {


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onViewClick(contact);

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelete(contact);
                }
            });

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAccept(contact);
                }
            });

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eachcontactview, parent, false);
        Context context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (data != null) {
            String UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
            Contact contact=data.get(position);

           // holder.username.setText(contact.getSenderID());
            if(contact.getStatus().equals("Connected")){
                holder.deleteAccept.setVisibility(View.GONE);
            }
            if(contact.getStatus().equals("Pending") && contact.getSenderID().equals(UserId)){
                holder.line.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
            }

            String friendID="";
            if(contact.getSenderID().equals(UserId)){
                friendID=contact.getReceiverID();
            }
            else if(contact.getReceiverID().equals(UserId)){
                friendID=contact.getSenderID();
            }


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

                        consumer=new Consumer(consumerName,consumerEmail,photoUri);

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

            holder.bind(data.get(position), listener);
        }
    }




    @Override
    public int getItemCount() {
        return data.size();
    }
}