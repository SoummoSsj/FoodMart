package com.example.project.Activity;

import static java.lang.Math.round;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project.Admin.Restaurant;
import com.example.project.Domain.FoodDomain;
import com.example.project.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference databaseReference;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private double Lat,Long;
    private List<Marker> restaurantMarkers = new ArrayList<>();
    Marker myMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationAndSetMarker();

        EditText searchEditText = findViewById(R.id.search);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchText = searchEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(searchText)) {
                    // Perform the search
                    searchRestaurant(searchText);
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng marker1Position = new LatLng(22.8967301, 89.5024154);
        addMarker(marker1Position, "Bismillah Hotel", "Hotel", MarkerActivity.class);

        LatLng marker2Position = new LatLng(22.8974917, 89.5029981);
        addMarker(marker2Position, "Food Club", "Hotel", MarkerActivity.class);

        LatLng marker3Position = new LatLng(22.8984322, 89.5027929);
        addMarker(marker3Position, "KUET Cafeteria", "Cafe", MarkerActivity.class);
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant data = snapshot.getValue(Restaurant.class);
                    LatLng restaurantPosition = new LatLng(data.getLat(), data.getLong());
                    addMarker(restaurantPosition, "Restaurant", "Restaurant", MarkerActivity.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Map.this, "Error fetching restaurant details", Toast.LENGTH_SHORT).show();
            }
        });
        //addMarker(new LatLng(22.8967301, 89.5024154), "Marker 1", "Description for Marker 1");
        //addMarker(new LatLng(22.8974917, 89.5029981), "Marker 2", "Description for Marker 2");
        //addMarker(new LatLng(22.8984322, 89.5027929), "Marker 3", "Description for Marker 3");
        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Lat=location.getLatitude();
                            Long=location.getLongitude();
                            mMap.addMarker(new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                        }
                    });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void addMarker(LatLng position, String title, String snippet, Class<?> destinationClass) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.locs)); // Replace with your custom marker image

        Marker marker = mMap.addMarker(markerOptions);
        restaurantMarkers.add(marker); // Add the marker to the list

        // Set the tag to store the destination class
        marker.setTag(destinationClass);
    }

    private void addMarker2(LatLng position, String title, String snippet, Class<?> destinationClass) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
                .icon(bitmapDescriptor(getApplicationContext(),R.drawable.raider)); // Replace with your custom marker image

        if(myMarker != null){
            myMarker.remove();
        }

        myMarker = mMap.addMarker(markerOptions);

        // Set the tag to store the destination class
        myMarker.setTag(destinationClass);
    }


    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, re-run onMapReady
                onMapReady(mMap);
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getLocationAndSetMarker() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        System.out.println("user id : " + uid);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Payment");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot resSnap : dataSnapshot.getChildren()) {
                        for (DataSnapshot mySnap : resSnap.getChildren()) {
                            if (mySnap.getKey().equals(uid)) {
                                Double latitude = mySnap.child("latitude").getValue(Double.class);
                                Double longitude = mySnap.child("longitude").getValue(Double.class);

                                // Check if latitude and longitude are not null and not equal to zero
                                if (latitude != null && longitude != null && latitude != 0 && longitude != 0) {
                                    LatLng raiderPosition = new LatLng(latitude, longitude);
                                    LatLng userLoc = new LatLng(Lat, Long);
                                    String str = mySnap.child("Picked").getValue(String.class);

                                    if (str != null && str.equals("Yes")) {
                                        double distance = SphericalUtil.computeDistanceBetween(userLoc, raiderPosition);
                                        addMarker2(raiderPosition, "Raider", distance + "m", MarkerActivity.class);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Map.this, "Error fetching location from Firebase", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void searchRestaurant(String searchText) {
        // Remove all existing markers
        for (Marker marker : restaurantMarkers) {
            marker.remove();
        }
        restaurantMarkers.clear();

        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant data = snapshot.getValue(Restaurant.class);
                    if (data.getRestaurantName().toLowerCase().contains(searchText.toLowerCase())) {
                        LatLng restaurantPosition = new LatLng(data.getLat(), data.getLong());
                        addMarker(restaurantPosition, searchText, "Restaurant", MarkerActivity.class);
                        isFound = true;
                        // Move the camera to the restaurant location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantPosition, 15f));
                    }
                }
                if (!isFound) {
                    Toast.makeText(Map.this, "Restaurant not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Map.this, "Error fetching restaurant details", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
