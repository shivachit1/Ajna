package app.shiva.ajna.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import app.shiva.ajna.Model.SimpleCoupon;
import app.shiva.ajna.R;

public class SimpleCouponsAdapter extends RecyclerView.Adapter<SimpleCouponsAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onViewClick(SimpleCoupon item);
        void LongClick(SimpleCoupon item);
    }
    private Context context;
    private final OnItemClickListener listener;
    private ArrayList<SimpleCoupon> data = new ArrayList();


    public SimpleCouponsAdapter(Context context, ArrayList<SimpleCoupon> data,OnItemClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        View itemview;
        public TextView couponsID;





        public MyViewHolder(View v) {
            super(v);
            itemview = v;
            couponsID= v.findViewById(R.id.couponsID);



        }

        public void bind(final SimpleCoupon item, final OnItemClickListener listener) {


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onViewClick(item);
                }
            });

            itemview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.LongClick(item);
                    return false;
                }
            });

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simplecouponseachview, parent, false);
        Context context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (data != null) {
            holder.bind(data.get(position), listener);

        }
    }




    @Override
    public int getItemCount() {
        return data.size();
    }
}