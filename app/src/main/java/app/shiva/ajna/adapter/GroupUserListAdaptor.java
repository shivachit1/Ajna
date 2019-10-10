package app.shiva.ajna.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.shiva.ajna.R;

public class GroupUserListAdaptor extends RecyclerView.Adapter<GroupUserListAdaptor.MyViewHolder> {

    private Context context;
    private ArrayList<String> data = new ArrayList<>();
    private DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();



    public GroupUserListAdaptor(Context context, ArrayList<String> data) {
        this.data = data;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        View itemview;
        ImageView groupuserImage;

        MyViewHolder(View v) {
            super(v);
            itemview = v;
            groupuserImage=v.findViewById(R.id.groupuserImage);

        }

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imageview, parent, false);
        Context context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (data != null) {
            String UserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
            String groupmemberID=data.get(position);
            mFirebaseDatabaseReference.child("Users").child("Consumers").child(groupmemberID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String consumerName=dataSnapshot.child("consumerName").getValue(String.class);
                        String consumerEmail=dataSnapshot.child("consumerEmail").getValue(String.class);
                        String photoUri=dataSnapshot.child("photoUri").getValue(String.class);
                        if(photoUri!=null){

                            Picasso.get().load(photoUri).fit().centerCrop().into(holder.groupuserImage);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }




    @Override
    public int getItemCount() {
        return data.size();
    }
}