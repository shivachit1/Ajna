package app.shiva.ajna.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import app.shiva.ajna.R;
import app.shiva.ajna.model.User;

public class AllUsersRecycleViewAdaptor extends RecyclerView.Adapter<AllUsersRecycleViewAdaptor.MyViewHolder> {

    public interface OnItemClickListener {
        void onViewClick(User user);
        void onAddClick(User user);
    }

    private final OnItemClickListener onItemClickListener;
    private final ArrayList<User> data ;

    public AllUsersRecycleViewAdaptor(ArrayList<User> data, AllUsersRecycleViewAdaptor.OnItemClickListener onItemClickListener) {

        this.data = data;
        this.onItemClickListener = onItemClickListener;

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        final View itemView;
        final ImageView profileImage;
        final TextView username;
        final ConstraintLayout deleteAccept;
        final ConstraintLayout activeTimerViewCons;
        final TextView activeTextView;
        ImageButton delete;
        TextView line;
        ImageButton accept;
        OnItemClickListener mListener;

        MyViewHolder(View v, OnItemClickListener listener) {
            super(v);
            itemView = v;
            profileImage=v.findViewById(R.id.userImageView);
            username= v.findViewById(R.id.username);
            deleteAccept=v.findViewById(R.id.deleteAccept);
            delete=v.findViewById(R.id.delete);
            line=v.findViewById(R.id.line);
            accept=v.findViewById(R.id.accept);
            activeTimerViewCons= v.findViewById(R.id.activeTimerViewCons);
            activeTextView=v.findViewById(R.id.activeTextView);
            mListener=listener;

            itemView.setOnClickListener(this);
            accept.setOnClickListener(this);




        }


        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.itemView:
                    mListener.onViewClick(data.get(getBindingAdapterPosition()));
                    break;

                case R.id.accept:
                    mListener.onAddClick(data.get(getBindingAdapterPosition()));
                    break;


            }

        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eachcontactrequestview, parent, false);

        return new MyViewHolder(itemView,onItemClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, int position) {
        if (data != null) {

            User user =data.get(position);

            holder.deleteAccept.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
            holder.activeTimerViewCons.setVisibility(View.VISIBLE);

            holder.username.setText(user.getUserName());
            Picasso.get().load(user.getPhotoUri()).fit().into(holder.profileImage);
            holder.activeTextView.setText("View Profile");

        }
    }




    @Override
    public int getItemCount() {
        return data.size();
    }
}