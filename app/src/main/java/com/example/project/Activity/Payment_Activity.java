package com.example.project.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.Domain.FoodDomain;
import com.example.project.Helper.ManagementCart;
import com.example.project.R;
import com.example.project.location.SimpleLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Payment_Activity extends AppCompatActivity {

    private EditText nameEditText, addressEditText, phoneEditText;
    private TextView total;
    private CheckBox codCheckBox, stripeCheckBox;
    private Button placeOrderButton;
    private    String receivedData;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();

        // Retrieve the extra data from the Intent
        receivedData = intent.getStringExtra("key");
        // Initialize Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case where the user is not authenticated
            return;
        }
        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Payment");//.child(userId);

        // Initialize UI components
        nameEditText = findViewById(R.id.Name);
        addressEditText = findViewById(R.id.address);
        phoneEditText = findViewById(R.id.phone);
        codCheckBox = findViewById(R.id.COD);
        stripeCheckBox = findViewById(R.id.stripe);
        placeOrderButton = findViewById(R.id.placemyorder);
        total=findViewById(R.id.totalTxt);
        total.setText("$"+receivedData);
        // Set onClickListener for the "Place My Order" button
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Payment");
                ManagementCart managementCart = new ManagementCart(Payment_Activity.this);

                List<FoodDomain> cartItems = managementCart.getListCard();

                Map<String, Object> map = new HashMap<>();

                long buttonPressTimeMillis = System.currentTimeMillis();
                Date buttonPressDate = new Date(buttonPressTimeMillis);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String formattedTime = timeFormat.format(buttonPressDate);

                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String phone = phoneEditText.getText().toString();

//                databaseReference.child("name").setValue(name);
//                databaseReference.child("address").setValue(address);
//                databaseReference.child("phone").setValue(phone);
//                databaseReference.child("Total").setValue(receivedData);
//                databaseReference.child("Picked").setValue("No");
//                databaseReference.child("Delivered").setValue("No");
//                databaseReference.child("OrderTime").setValue(formattedTime);

                String[] paymentMethod = {null};

                // Check which payment method is selected
                if (codCheckBox.isChecked()) {
                    // Save the selected payment method to Firebase
                    paymentMethod[0] = "COD";
                } else if (stripeCheckBox.isChecked()) {
                    paymentMethod[0] = "Stripe";
                } else {
                    // Handle the case where no payment method is selected
                    Toast.makeText(Payment_Activity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                FusedLocationProviderClient fusedLocationClient;

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(Payment_Activity.this);

                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();


                        for(FoodDomain item : cartItems){

                            map.put(item.getResID()+"/"+userId+"/"+"name",name);
                            map.put(item.getResID()+"/"+userId+"/"+"address",address);
                            map.put(item.getResID()+"/"+userId+"/"+"phone",phone);
                            map.put(item.getResID()+"/"+userId+"/"+"Picked","No");
                            map.put(item.getResID()+"/"+userId+"/"+"Delivered","No");
                            map.put(item.getResID()+"/"+userId+"/"+"OrderTime",formattedTime);

                            map.put(item.getResID()+"/"+userId+"/"+"Method",paymentMethod[0]);


                            map.put(item.getResID()+"/"+userId+"/"+"lat",lat);
                            map.put(item.getResID()+"/"+userId+"/"+"long",lon);

                        }

                        ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    // Start a new activity (replace NewActivity.class with your actual activity class)
                                    if (paymentMethod[0] == "Stripe"){
                                        Intent intent = new Intent(Payment_Activity.this, Payment_win.class);
                                        intent.putExtra("TOTAL_AMOUNT", receivedData);

                                        startActivity(intent);
                                    }
                                    else if(paymentMethod[0] == "COD"){
                                        Intent intent = new Intent(Payment_Activity.this, Order_Confirm.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(Payment_Activity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                                    }

                                    Toast.makeText(Payment_Activity.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(Payment_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
