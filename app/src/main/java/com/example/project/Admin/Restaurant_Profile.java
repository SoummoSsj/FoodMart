package com.example.project.Admin;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.Activity.Profile;
import com.example.project.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Restaurant_Profile extends AppCompatActivity {
    private TextView descriptionTxt;
    private TextView titleTxt;
    private static TextView addressTxt;
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        titleTxt= findViewById(R.id.titleTxt);
        addressTxt=findViewById(R.id.addressTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        pic=findViewById(R.id.pic);
        // Assume your Firebase Realtime Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(userId);

        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Restaurant restaurant;
                 restaurant=dataSnapshot.getValue(Restaurant.class);
                descriptionTxt.setText(restaurant.getDescription());
                titleTxt.setText(restaurant.getRestaurantName());
                if (restaurant.getImageUrl() != null && !restaurant.getImageUrl().isEmpty()) {
                    Picasso.get().load(restaurant.getImageUrl()).into(pic);
                } else {
                    // Handle the case where the image URL is empty or null
                    // Set a default image or handle accordingly
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        new GetRegionTask(this).execute(22.8960727,89.5048111);
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
                startActivity(new Intent(Restaurant_Profile.this, Admin_Home.class));
            }
        });

        profBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Restaurant_Profile.this, Restaurant_Profile.class));
            }
        });

    }

    private static class GetRegionTask extends AsyncTask<Double, Void, String> {

        private Context context;

        GetRegionTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Double... params) {
            double latitude = params[0];
            double longitude = params[1];

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);

                    // Get the locality (region)
                    return address.getLocality();
                }
            } catch (IOException e) {
                Log.e("Geocoding", "Error getting region", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Update your UI with the obtained region
                addressTxt.setText("Region: " + result);
            } else {
                // Handle the case where the region could not be determined
                addressTxt.setText("Region not found");
            }
        }
    }
}
