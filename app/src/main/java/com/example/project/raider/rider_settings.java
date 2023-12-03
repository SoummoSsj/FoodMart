package com.example.project.raider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.project.Activity.CartListActivity;
import com.example.project.Activity.LogInPage;
import com.example.project.Activity.MainActivity;
import com.example.project.Activity.Notification_user;
import com.example.project.Activity.ProfileSettings;
import com.example.project.Activity.Profile_Info;
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

public class rider_settings extends AppCompatActivity {
    private Button logout;
    GoogleSignInClient mGoogleSignInClient;
    private Button add;
    private Button addmenu;
    private Button deleteButton;
    private TextView textViewOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_settings);
        mGoogleSignInClient = YourInitializationMethodHere();
        logout = findViewById(R.id.log);



        deleteButton = findViewById(R.id.del);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

            }
        });

        bottomNavigation();


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
    private void bottomNavigation() {
        FloatingActionButton floatingActionButton = findViewById(R.id.card_btn);
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout profBtn=findViewById(R.id.profile_btn);
        LinearLayout notifi=findViewById(R.id.notification_btn);
        LinearLayout settings=findViewById(R.id.settings_btn);



        homeBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           startActivity(new Intent(rider_settings.this, Raider_Home.class));
                                       }
                                   });

        notifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(rider_settings.this, rider_settings.class));
            }
        });




    }


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
                                Intent intent = new Intent(rider_settings.this, LogInPage.class);
                                startActivity(intent);
                                this.finish();
                            } else {
                                // Handle any errors that occurred during Google sign-out.
                                // You can show an error message if needed.
                                Toast.makeText(this, "Google Sign out failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(rider_settings.this, StartActivity.class);

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

}