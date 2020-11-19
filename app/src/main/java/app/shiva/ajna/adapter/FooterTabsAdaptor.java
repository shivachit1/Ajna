package app.shiva.ajna.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.shiva.ajna.R;
import app.shiva.ajna.model.TabModel;
import app.shiva.ajna.model.User;


public class FooterTabsAdaptor extends RecyclerView.Adapter<FooterTabsAdaptor.PagerViewHolder> {

    private ArrayList<TabModel> tabModels;
    private OnItemClickListener onItemClickListener;


    public interface OnItemClickListener {
        void onViewClick(TabModel tabModel);
    }


    public FooterTabsAdaptor() {

    }

    public FooterTabsAdaptor(ArrayList<TabModel> tabModels,OnItemClickListener onItemClickListener) {
        this.tabModels = tabModels;
        this.onItemClickListener=onItemClickListener;
    }



    class PagerViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private View itemView;
        private ImageView imageview;
        private CardView messagesCounterCons;
        private OnItemClickListener mListener;

        PagerViewHolder(@NonNull View view, OnItemClickListener listener) {
            super(view);
            itemView=view;
            imageview=itemView.findViewById(R.id.tabImage);
            messagesCounterCons=itemView.findViewById(R.id.messagesCounterCons);

            this.mListener=listener;
            imageview.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {


            mListener.onViewClick(tabModels.get(getBindingAdapterPosition()));

        }
    }



    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_item,parent,false);

        return new PagerViewHolder(view,onItemClickListener);
    }



    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {


        TabModel tabModel=tabModels.get(position);
        holder.imageview.setBackground(tabModel.getDrawable());

        if(position==2){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
           DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Users").child(mAuth.getUid()).child("photoUri").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Picasso.get().load(dataSnapshot.getValue(String.class)).centerCrop().fit().into(holder.imageview);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        if(tabModel.getNotificationCounter()>0){
            holder.messagesCounterCons.setVisibility(View.VISIBLE);

        }
        else{
            holder.messagesCounterCons.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return tabModels.size();
    }
}