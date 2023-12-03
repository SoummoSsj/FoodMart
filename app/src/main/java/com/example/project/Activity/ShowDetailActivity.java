package com.example.project.Activity;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.project.Admin.Restaurant;
import com.example.project.Domain.FoodDomain;
import com.example.project.Helper.ManagementCart;
import com.example.project.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.maps.android.SphericalUtil;

public class ShowDetailActivity extends AppCompatActivity {
    private TextView addToCardBtn;
    private RatingBar ratingBar;
    private TextView titleTxt, feeTxt, descriptionTxt, numberOrderTxt,plusBtn,minusBtn,ratingText,dis;
    private ImageView  picFood;
    private FoodDomain object;
    private int numberOrder = 1;
    private ManagementCart managementCart;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationClient;
    private double Lat, Long,distance;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        managementCart = new ManagementCart(this);
        dis=findViewById(R.id.textView26);
        initView();
        getBundle();
        getLocation();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                String ratingString = String.format("Rating: %.1f", rating);
                ratingText.setText(ratingString);
            }
        });

        String resID=getIntent().getStringExtra("id");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(resID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Restaurant restaurant;
                restaurant=dataSnapshot.getValue(Restaurant.class);
                if(Lat == 0 || Long == 0) {
                    Lat = restaurant.getLat();
                    Long = restaurant.getLong();
                }
                else {
                    LatLng userLoc = new LatLng(Lat, Long);
                    LatLng resLoc = new LatLng(restaurant.getLat(), restaurant.getLong());
                    distance = SphericalUtil.computeDistanceBetween(userLoc, resLoc);
                    Double time = distance / 166.667;
                    time = time + object.getTime();
                    time = Double.valueOf(round(time));
                    dis.setText(time + " min");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }


        });


    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            if(Lat == 0 || Long == 0) {
                                Lat = location.getLatitude();
                                Long = location.getLongitude();
                            }
                            else{
                                LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                LatLng resLoc = new LatLng(Lat,Long);
                                distance = SphericalUtil.computeDistanceBetween(userLoc, resLoc);
                                Double time = distance / 166.667;
                                time = time + object.getTime();
                                time = Double.valueOf(round(time));
                                dis.setText(time + " min");
                            }
                        }
                    });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    private void getBundle() {
        object = (FoodDomain) getIntent().getSerializableExtra("object");


        Glide.with(this)
                .load(object.getPic())
                .into(picFood);

        titleTxt.setText(object.getTitle());
        feeTxt.setText("$" + object.getFee());
        descriptionTxt.setText(object.getDescription());
        numberOrderTxt.setText(String.valueOf(numberOrder));

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOrder = numberOrder + 1;
                numberOrderTxt.setText(String.valueOf(numberOrder));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOrder > 1) {
                    numberOrder = numberOrder - 1;
                }
                numberOrderTxt.setText(String.valueOf(numberOrder));
            }
        });

        addToCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setNumberInCart(numberOrder);
                managementCart.insertFood(object);
            }
        });
    }

    private void initView() {
         ratingBar = findViewById(R.id.ratingBar);
         ratingText = findViewById(R.id.StarTxt);

        addToCardBtn = findViewById(R.id.addToCardBtn);
        titleTxt = findViewById(R.id.titleTxt);
        feeTxt = findViewById(R.id.priceTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        numberOrderTxt = findViewById(R.id.numberOrderTxt);
        plusBtn = findViewById(R.id.plusBtn);
        minusBtn = findViewById(R.id.minusBtn);
        picFood = findViewById(R.id.foodPic);
    }
}