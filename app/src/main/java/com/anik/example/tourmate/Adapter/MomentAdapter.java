package com.anik.example.tourmate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {
    private ArrayList<Moment> momentLists;
    private Context context;

    public MomentAdapter(ArrayList<Moment> momentLists, Context context) {
        this.momentLists = momentLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_moment_itemview_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Moment currentMoment = momentLists.get(position);

        Picasso.with(context).load(currentMoment.getImageURL()).fit().into(holder.image);

    }

    @Override
    public int getItemCount() {
        return momentLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.momentImageIV);
        }
    }
}
