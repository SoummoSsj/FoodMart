package com.example.project.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.Domain.FoodDomain;
import com.example.project.R;

import java.util.List;

public class PendingAdapt extends RecyclerView.Adapter<PendingAdapt.ViewHolder> {
    private List<FoodDomain> dataList;

    public PendingAdapt(List<FoodDomain> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(dataList.get(position).getTitle());
        holder.quantity.setText(String.valueOf(dataList.get(position).getNumberInCart() ));

        //int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(foodDomains.get(position).getPic(), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(dataList.get(position).getPic())
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setData(List<FoodDomain> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, quantity;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Menu_Foodname);
            quantity = itemView.findViewById(R.id.Menu_Price);
            pic = itemView.findViewById(R.id.menu_item);

        }
    }
}
