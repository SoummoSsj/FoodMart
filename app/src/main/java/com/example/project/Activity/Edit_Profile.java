package com.example.project.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Edit_Profile extends AppCompatActivity {

    private EditText restaurantNameEditText, addressEditText, emailEditText, phoneEditText;
    private CheckBox setCurrentLocationCheckBox;
    private Button addImageButton, updateButton;

    // Firebase
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("profiles");

        // Initialize views
        restaurantNameEditText = findViewById(R.id.restaurantNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        setCurrentLocationCheckBox = findViewById(R.id.setCurrentLocationCheckBox);
        addImageButton = findViewById(R.id.addImageButton);
        updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void saveProfile() {
        String restaurantName = restaurantNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        boolean setCurrentLocation = setCurrentLocationCheckBox.isChecked();

        // Check if any field is empty
        if (restaurantName.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique key for the profile
        String profileId = restaurantName;

        // Create a Profile object
        Profile profile = new Profile(profileId, restaurantName, address, email, phone, setCurrentLocation);

        // Save the profile to Firebase
        databaseReference.child(profileId).setValue(profile);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }
}
