package com.example.project.Admin;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddMyRestaurant extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Button addRestaurantButton, chooseImageButton;
    private EditText restaurantNameEditText, description_res;
    private DatabaseReference mDatabase;
    private String restaurantName, description;
    private double Lat, Long;
    private CheckBox checkBoxLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_restaurant);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkBoxLocation = findViewById(R.id.checkBox);
        description_res = findViewById(R.id.description_res);
        addRestaurantButton = findViewById(R.id.addRestaurantButton);
        restaurantNameEditText = findViewById(R.id.restaurantNameEditText);
        chooseImageButton = findViewById(R.id.chooseimg);


        checkBoxLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantName = restaurantNameEditText.getText().toString().trim();
                description = description_res.getText().toString().trim();

                if (!restaurantName.isEmpty() && imageUri != null) {
                    // Get the currently logged-in user's ID
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        String userId = currentUser.getUid();

                        // Upload the image to Firebase Storage
                        uploadImage(userId);
                    } else {
                        // Handle the case where the user is not logged in
                        Log.e(TAG, "User not logged in");
                    }
                } else {
                    // Handle the case where the restaurant name or image is empty
                    Toast.makeText(AddMyRestaurant.this, "Please enter restaurant name and choose an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Lat = location.getLatitude();
                            Long = location.getLongitude();
                        }
                    });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void uploadImage(String userId) {
        // Get a reference to the Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("restaurant_images").child(userId + ".jpg");

        // Upload the image
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the download URL and call the method to save data to Firebase Database
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveDataToDatabase(userId, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Log.e(TAG, "Image upload failed: " + e.getMessage());
                });
    }

    private void saveDataToDatabase(String resID, String imageUrl) {
        // Create a unique key for the restaurant in the "Restaurants" node
        //String restaurantKey = mDatabase.child("Restaurants").push().getKey();

        if (resID != null) {
            // Create a Restaurant object with the image URL
            Restaurant restaurant = new Restaurant(restaurantName, resID, imageUrl, Lat, Long, description);

            // Save the restaurant to Firebase Database
            mDatabase.child("Restaurants").child(resID).setValue(restaurant);

            // You can add a success message or perform other actions after saving data
            Toast.makeText(AddMyRestaurant.this, "Restaurant added successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after successful addition
        } else {
            // Handle the case where restaurantKey is null
            Log.e(TAG, "Error generating restaurant key");
        }
    }
}
