package com.example.project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.project.Adapter.PopularAdapter;
import com.example.project.Adapter.SearchResultsAdapter;
import com.example.project.Domain.FoodDomain;
import com.example.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSearchResults;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        // Get the search text from the intent
        String searchText = getIntent().getStringExtra("searchText");

        // Filter the food list based on the search text
       // ArrayList<FoodDomain> filteredFoodList = filterFoodList(searchText);

        // Create and set the adapter for the RecyclerView

        filterFoodList(searchText);


    }


    private void filterFoodList(String searchText) {
        ArrayList<FoodDomain> filteredList = new ArrayList<>();
        ArrayList<FoodDomain> foodlist = new ArrayList<>();
//        foodlist.add(new FoodDomain("Pepperoni pizza", "pizza1", "slices pepperoni ,mozzarella cheese, fresh oregano,  ground black pepper, pizza sauce", 9.76,"pizza"));
//        foodlist.add(new FoodDomain("Cheese Burger", "burger", "beef, Gouda Cheese, Special sauce, Lettuce, tomato ", 8.79,"burger"));
//        foodlist.add(new FoodDomain("Vegetable pizza", "pizza2", " olive oil, Vegetable oil, pitted Kalamata, cherry tomatoes, fresh oregano, basil", 8.5,"pizza"));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Food");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<FoodDomain> dataList = new ArrayList<>();

                for (DataSnapshot resSnap : dataSnapshot.getChildren()) {

                    for(DataSnapshot foodSnap : resSnap.getChildren()) {
                        FoodDomain data = foodSnap.getValue(FoodDomain.class);

                        if (data.getType().toLowerCase().contains(searchText.toLowerCase())) {
                            dataList.add(data);
                        }
                    }
                    //dataList.add(data);
                }

                // Update the adapter with the new

                adapter = new SearchResultsAdapter(dataList);
                recyclerViewSearchResults.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    public void startNextActivity(View view) {
        Intent intent = new Intent(this, ShowDetailActivity.class);
        startActivity(intent);
    }
}
