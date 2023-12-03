package com.example.project.raider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.project.Adapter.PendingOrderAdapterUser;
import com.example.project.Admin.Admin_Home;
import com.example.project.Admin.Restaurant_Profile;
import com.example.project.R;
import com.example.project.location.Point;
import com.example.project.location.SimpleLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Raider_Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raider_home);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("pending_orders");

        // Retrieve data from Firebase
        fetchDataFromFirebase();
        bottomNavigation();
    }

    private void bottomNavigation() {
        FloatingActionButton floatingActionButton = findViewById(R.id.card_btn);
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout profBtn=findViewById(R.id.profile_btn);
        LinearLayout notifi=findViewById(R.id.notification_btn);
        LinearLayout settings=findViewById(R.id.settings_btn);



        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Raider_Home.this, Raider_Home.class));
            }
        });

        notifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Raider_Home.this, rider_settings.class));
            }
        });




    }

    private void fetchDataFromFirebase() {
        Map<String, LatLng> mapPoints = new HashMap<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                List<String> list = new ArrayList<>();

                                for(DataSnapshot resSnap : dataSnapshot.getChildren()) {
                                    for (DataSnapshot foodSnap : resSnap.getChildren()) {

                                            list.add(resSnap.getKey() + "/" + foodSnap.getKey());

                                    }
                                }

                                order_adapter_front_raider adapterUser = new order_adapter_front_raider(list);
                                recyclerView.setAdapter(adapterUser);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}