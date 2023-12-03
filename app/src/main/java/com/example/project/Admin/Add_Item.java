package com.example.project.Admin;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.Domain.FoodDomain;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class Add_Item extends AppCompatActivity {

    private EditText nameEditText, timeEditText, calEditText, priceEditText, descriptionEditText,typeText;
    private DatabaseReference mDatabase;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // Uri to store the selected image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize Firebase


        // Initialize EditText fields
        typeText=findViewById(R.id.type);
        nameEditText = findViewById(R.id.name);
        timeEditText = findViewById(R.id.time);
        calEditText = findViewById(R.id.cal);
        priceEditText = findViewById(R.id.price);
        descriptionEditText = findViewById(R.id.Description);

        // Initialize Button for adding to menu
        Button addToMenuButton = findViewById(R.id.add_menu);
        addToMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method to save data to Firebase
                uploadImage();
            }
        });

        Button addImageButton = findViewById(R.id.button10);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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
            // You can display the selected image if needed
            // ImageView imageView = findViewById(R.id.imageView);
            // imageView.setImageURI(imageUri);
        }
    }


    private void uploadImage() {
        if (imageUri != null) {
            // Get a reference to the Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(System.currentTimeMillis() + ".jpg");

            // Upload the image
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Get the download URL and call the method to save data to Firebase Database
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            saveDataToDatabase(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        Log.e(TAG, "Image upload failed: " + e.getMessage());
                    });
        } else {
            // If no image is selected, save data to Firebase Database directly
            saveDataToDatabase("");
        }
    }

    private void saveDataToDatabase(String imageUrl) {
        // Create a unique key for the item in the "menu" node
        String name = nameEditText.getText().toString().trim();
        int time = Integer.parseInt(timeEditText.getText().toString().trim());
        String calorie = calEditText.getText().toString().trim();
        Double price = Double.parseDouble(priceEditText.getText().toString().trim()) ;
        String type = typeText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Create a unique key for the item in the "menu" node
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Create a Menu object
        FoodDomain menu = new FoodDomain(name, imageUrl, description, price,0,type,4.2,time,currentUser.getUid());

        // Save the menu item to Firebase

        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Food").child(userId).child(menu.getTitle()).setValue(menu);



        } else {
            // Handle the case where the user is not logged in
            Log.e(TAG, "User not logged in");
        }

        // You can add a success message or perform other actions after saving data

    }
}
