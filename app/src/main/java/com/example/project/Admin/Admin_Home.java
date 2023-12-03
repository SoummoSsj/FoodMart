package com.example.project.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.Activity.CartListActivity;
import com.example.project.Activity.LogInPage;
import com.example.project.Activity.MainActivity;
import com.example.project.Activity.Profile;
import com.example.project.Activity.StartActivity;
import com.example.project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin_Home extends AppCompatActivity {
    private Button logout;
    GoogleSignInClient mGoogleSignInClient;
    private Button add;
    private Button addmenu;
    private Button deleteButton;
    private TextView textViewOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        mGoogleSignInClient = YourInitializationMethodHere();
        logout = findViewById(R.id.log);
        add = findViewById(R.id.addres);
        add.setVisibility(View.GONE);
        addmenu = findViewById(R.id.additem);
        addmenu.setVisibility(View.GONE);
        //textViewOrderNo = findViewById(R.id.orderno); // Initialize the TextView

        deleteButton = findViewById(R.id.del);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        checkUser();
        //updateCartCount(); // Update cart count when the view is created


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout2();

            }
        });
        addmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout22();

            }
        });


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
                startActivity(new Intent(Admin_Home.this, Admin_Home.class));
            }
        });

        profBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin_Home.this, Restaurant_Profile.class));
            }
        });



    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteData();

                        // User clicked Yes button
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No button
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }
    /*private void updateCartCount() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Carts").child(userId);

        cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int itemCount = (int) dataSnapshot.getChildrenCount();
                textViewOrderNo.setText(String.valueOf(itemCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching cart data", databaseError.toException());
            }
        });
    }*/

    private void deleteData() {
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User account deleted successfully
                        // You may want to sign out the user or perform other actions
                        FirebaseAuth.getInstance().signOut();
                        mGoogleSignInClient.signOut().addOnCompleteListener(this, task2 -> {
                            if (task2.isSuccessful()) {
                                // Successfully signed out from Google
                                Intent intent = new Intent(Admin_Home.this, LogInPage.class);
                                startActivity(intent);
                                this.finish();
                            } else {
                                // Handle any errors that occurred during Google sign-out.
                                // You can show an error message if needed.
                                Toast.makeText(this, "Google Sign out failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(Admin_Home.this, StartActivity.class);

                    } else {
                        // Handle failure, e.g., display an error message
                    }
                });
    }

    private void deleteAccount() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);
        DatabaseReference googleRef = FirebaseDatabase.getInstance().getReference().child("Google").child(userId);


        userRef.removeValue()
                .addOnCompleteListener(tasks -> {
                            if (tasks.isSuccessful()) {
                                Log.e("TAG", "User data deleted successfully");
                                // User data deleted successfully
                            } else {
                                Log.e("TAG", "User data deletion failed");

                                // Handle failure, e.g., display an error message
                            }
                        }
                );
        googleRef.removeValue()
                .addOnCompleteListener(tasks -> {
                            if (tasks.isSuccessful()) {
                                Log.e("TAG", "User data deleted successfully");
                                // User data deleted successfully
                            } else {
                                Log.e("TAG", "User data deletion failed");

                                // Handle failure, e.g., display an error message
                            }
                        }
                );


    }

    private void checkUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User data exists, check occupation
                        String occupation = dataSnapshot.child("status").getValue(String.class);

                        // Show/hide button based on occupation
                        if ("Manager".equals(occupation)) {
                            add.setVisibility(View.VISIBLE);
                            addmenu.setVisibility(View.VISIBLE);

                        } else {
                            add.setVisibility(View.GONE);
                            addmenu.setVisibility(View.GONE);

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }
    private void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in with email/password
            mAuth.signOut();
        }

        // Sign out from Google (if signed in with Google)
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Successfully signed out from Google
                Intent intent = new Intent(this, LogInPage.class);
                startActivity(intent);
                this.finish();
            } else {
                // Handle any errors that occurred during Google sign-out.
                // You can show an error message if needed.
                Toast.makeText(this, "Google Sign out failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private GoogleSignInClient YourInitializationMethodHere() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(this, gso);

    }
    public void logout2() {
        Intent intent = new Intent(Admin_Home.this, AddMyRestaurant.class);
        startActivity(intent);

    }
    public void logout22() {
        Intent intent = new Intent(Admin_Home.this, Add_Item.class);
        startActivity(intent);
    }

    public void startNextActivity(View view) {
        Intent intent = new Intent(this, Pending_Orders_front.class);
        startActivity(intent);
    }
}