package com.example.project.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.Admin.PendingAdapt;
import com.example.project.Admin.Pending_Orders;
import com.example.project.Domain.FoodDomain;
import com.example.project.R;
import com.example.project.raider.Order_Raider;

import java.util.List;

public class PendingOrderAdapterUser extends RecyclerView.Adapter<PendingOrderAdapterUser.ViewHolder> {

    private List<String> users;

    public PendingOrderAdapterUser(List<String> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_text_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUser.setText((position+1)+"");

        holder.itemView.setOnClickListener((View v)->{
            Intent intent = new Intent( holder.itemView.getContext(), Pending_Orders.class);
            intent.putExtra("id",users.get(holder.getAdapterPosition()));
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvOrderItem);
        }
    }
}
