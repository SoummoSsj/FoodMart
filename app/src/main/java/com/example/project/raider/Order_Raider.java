package com.example.project.raider;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Activity.MarkerActivity;
import com.example.project.Domain.FoodDomain;
import com.example.project.R;
import com.example.project.location.SimpleLocation;
import com.example.project.raider.Order_Raider_Adapt;
import com.example.project.raider.UserDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Order_Raider extends AppCompatActivity implements OnMapReadyCallback {
    private TextView nameTextView, phoneTextView, addressTextView,amountTextView,DeliveryTimeTextView,methodTextView;
    private CheckBox pickedCheckBox,  deliverycheckbox;

    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView recyclerView;
    private Order_Raider_Adapt adapter;

    private Double Lat, Long;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private ArrayList<FoodDomain> dataList;
    private UserDetails userDetails;
    private GoogleMap mMap;
    private MapView mapView;
    private boolean isCalled = false;
    private Marker myMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_raider);

        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        methodTextView=findViewById(R.id.paymethod);
        amountTextView=findViewById(R.id.amount);
        DeliveryTimeTextView=findViewById(R.id.Dtime);
        nameTextView = findViewById(R.id.Name);
        phoneTextView = findViewById(R.id.phone);
        addressTextView = findViewById(R.id.address);
        pickedCheckBox = findViewById(R.id.picked);
        deliverycheckbox = findViewById(R.id.deliverycheckbox);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize the adapter with an empty list
        adapter = new Order_Raider_Adapt(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Retrieve data from Firebase
        fetchDataFromFirebase();
        getLocation();
        fetchUserDetails();

        String uid = getIntent().getStringExtra("id");
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Payment").child(uid);
        deliverycheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Save current location to Firebase
                Ref.child("Delivered").setValue("Yes");
                long buttonPressTimeMillis = System.currentTimeMillis();
                Date buttonPressDate = new Date(buttonPressTimeMillis);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String formattedTime = timeFormat.format(buttonPressDate);
                Ref.child("DTime").setValue(formattedTime);
            }
        });

    }

    private void fetchDataFromFirebase() {
        String uid = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("pending_orders").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<FoodDomain> dataList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodDomain data = snapshot.getValue(FoodDomain.class);
                    String title = String.valueOf(snapshot.child("name").getValue());
                    data.setTitle(title);
                    int num = snapshot.child("quantity").getValue(Integer.class);
                    data.setNumberInCart(num);
                    dataList.add(data);

                }

                // Update the adapter with the new data
                adapter.setData(dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void fetchUserDetails() {
        String uid = getIntent().getStringExtra("id");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Payment").child(uid);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String address = dataSnapshot.child("address").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String picked = dataSnapshot.child("Picked").getValue(String.class);
                String delivery = dataSnapshot.child("Delivered").getValue(String.class);
                Integer deliveryTime=dataSnapshot.child("DeliveryTime").getValue(Integer.class);
                Integer total=dataSnapshot.child("Total").getValue(Integer.class);
                String paymethod=dataSnapshot.child("Method").getValue(String.class);
                deliverycheckbox.setChecked("yes".equalsIgnoreCase(delivery));
                pickedCheckBox.setChecked( "yes".equalsIgnoreCase(picked) );
                saveCurrentLocation();

                userDetails = new UserDetails(name, address, phone);

                // Set the values to the corresponding TextViews
                amountTextView.setText(total+"$");
                DeliveryTimeTextView.setText(deliveryTime+"min");
                nameTextView.setText(userDetails.getName());
                addressTextView.setText(userDetails.getAddress());
                phoneTextView.setText(userDetails.getPhone());
                methodTextView.setText(paymethod);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Set onClickListener for the "Picked Up" checkbox
        pickedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Save current location to Firebase
                saveCurrentLocation();
            }
        });

        // You can similarly handle the "Delivered" checkbox if needed
    }

    private void saveCurrentLocation() {

        String uid = getIntent().getStringExtra("id");
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Payment").child(uid);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle the case where the user hasn't granted the permission.
            return;
        }

        if(isCalled) return;
        isCalled = true;

        SimpleLocation simpleLocation = new SimpleLocation(this,true,false,3000,true);
        simpleLocation.beginUpdates();
        simpleLocation.setListener(() -> {
            double latitude = simpleLocation.getLatitude();
            double longitude = simpleLocation.getLongitude();
            Ref.child("Picked").setValue("Yes");
            Ref.child("latitude").setValue(latitude);
            Ref.child("longitude").setValue(longitude);

            if(myMarker != null) myMarker.remove();

            LatLng currentLocation = new LatLng(latitude,longitude);
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title("Your Location").icon(bitmapDescriptor(getApplicationContext(),R.drawable.raider)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19f));

        });

//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
//            if (location != null) {
//                // Save the location to Firebase
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                Ref.child("Picked").setValue("Yes");
//                Ref.child("latitude").setValue(latitude);
//                Ref.child("longitude").setValue(longitude);
//
//                Toast.makeText(this, "Location saved to Firebase", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        String uid = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Payment").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double latitude = dataSnapshot.child("lat").getValue(Double.class);
                double longitude = dataSnapshot.child("long").getValue(Double.class);

               LatLng userLoc = new LatLng(latitude,longitude);
               addMarker(userLoc, "Raider", " ", MarkerActivity.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        //addMarker(new LatLng(22.8967301, 89.5024154), "Marker 1", "Description for Marker 1");
        //addMarker(new LatLng(22.8974917, 89.5029981), "Marker 2", "Description for Marker 2");
        //addMarker(new LatLng(22.8984322, 89.5027929), "Marker 3", "Description for Marker 3");
        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
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
                .icon(bitmapDescriptor(getApplicationContext(),R.drawable.userloc)); // Replace with your custom marker image

        Marker marker = mMap.addMarker(markerOptions);

        // Set the tag to store the destination class
        marker.setTag(destinationClass);
    }
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

    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
