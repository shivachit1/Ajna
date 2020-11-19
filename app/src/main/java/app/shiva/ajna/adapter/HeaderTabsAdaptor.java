package app.shiva.ajna.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.shiva.ajna.R;
import app.shiva.ajna.model.TabModel;
import app.shiva.ajna.model.User;

class HeaderTabsAdaptor extends RecyclerView.Adapter {

    private ArrayList<TabModel> tabModels;


    public HeaderTabsAdaptor() {

    }

    public HeaderTabsAdaptor(ArrayList<TabModel> tabModels, User userDetails) {
        this.tabModels = tabModels;
        User userDetails1 = userDetails;
    }



    class PagerViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageview;
        private CardView messagesCounterCons;

        PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageview=itemView.findViewById(R.id.tabImage);
            messagesCounterCons=itemView.findViewById(R.id.messagesCounterCons);

        }



    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_item,parent,false);

        return new PagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PagerViewHolder pagerViewHolder=(PagerViewHolder) holder;

        TabModel tabModel=tabModels.get(position);
        pagerViewHolder.imageview.setBackground(tabModel.getDrawable());

        if(tabModel.getNotificationCounter()>0){
            pagerViewHolder.messagesCounterCons.setVisibility(View.VISIBLE);

        }
        else{
            pagerViewHolder.messagesCounterCons.setVisibility(View.INVISIBLE);
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