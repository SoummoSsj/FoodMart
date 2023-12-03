package com.example.project.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.Activity.ShowDetailActivity;
import com.example.project.Domain.FoodDomain;
import com.example.project.R;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private List<FoodDomain> foodList;

    public SearchResultsAdapter(List<FoodDomain> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodDomain food = foodList.get(position);
        holder.foodTitle.setText(food.getTitle());
        holder.foodPrice.setText("$"+food.getFee());
        // Add other bindings as needed
        //int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(foodList.get(position).getPic(), "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(foodList.get(position).getPic())
                .into(holder.img);
        holder.Addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ShowDetailActivity.class);
                intent.putExtra("object", foodList.get(holder.getAdapterPosition()));
                intent.putExtra("id", foodList.get(holder.getAdapterPosition()).getResID());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodTitle;
        TextView foodPrice;
        ImageView img;
        Button Addtocart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTitle = itemView.findViewById(R.id.Menu_Foodname);
            foodPrice = itemView.findViewById(R.id.Menu_Price);
            img = itemView.findViewById(R.id.menu_item);
            Addtocart=itemView.findViewById(R.id.Addtocart);
            // Add other view bindings as needed
        }
    }
}

