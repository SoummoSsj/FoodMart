package com.example.foodmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class homee extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerView);
        // Initialize the GoogleSignInClient
        mGoogleSignInClient = YourInitializationMethodHere();

        //Button logout = findViewById(R.id.logout);
        //logout.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //logout();
            //}
        //});
    }

    private void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in with email/password
            mAuth.signOut();
        }

        // Sign out from Google (if signed in with Google)
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed out from Google
                    } else {
                        // Handle any errors that occurred during Google sign-out.
                        // You can show an error message if needed.
                        Toast.makeText(homee.this, "Google Sign out failed", Toast.LENGTH_SHORT).show();
                    }

                    // Redirect to the login page
                    Intent intent = new Intent(homee.this, LogInPage.class);
                    startActivity(intent);
                    finish();
                });
    }

    // Initialize the GoogleSignInClient with your configuration here
    private GoogleSignInClient YourInitializationMethodHere() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        return GoogleSignIn.getClient(this, gso);

    }
}
