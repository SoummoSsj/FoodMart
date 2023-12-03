package com.example.project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.project.Admin.Admin_Home;
import com.example.project.R;
import com.example.project.raider.Raider_Home;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private Button yourButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        yourButton = findViewById(R.id.button);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if ((user != null && user.isEmailVerified()) || (account!=null)) {
            yourButton.setVisibility(View.GONE);

        }
        else {
            yourButton.setVisibility(View.VISIBLE);
        }



        if (user != null && user.isEmailVerified()) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid()).child("status");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String val = String.valueOf(snapshot.getValue());
                    if(val.equals("Manager")){
                        Intent intent = new Intent(StartActivity.this, Admin_Home.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (val.equals("User")){
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }


                    else if (val.equals("Raider")){
                        Intent intent = new Intent(StartActivity.this, Raider_Home.class);
                        startActivity(intent);
                        finish();
                    }
                    // User is authenticated with Firebase, allow access to "homee" activity

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (account != null) {
            // User is already authenticated with Google, skip the login page.
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            // User is not authenticated or email is not verified, handle this case.

            yourButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(StartActivity.this, LogInPage.class));
                }
            });


        }



    }
}