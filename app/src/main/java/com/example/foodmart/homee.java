package com.example.foodmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.foodmart.Fragment.CartFragment;
import com.example.foodmart.Fragment.HistoryFragment;
import com.example.foodmart.Fragment.HomeFragment;
import com.example.foodmart.Fragment.ProfileFragment;
import com.example.foodmart.Fragment.SearchFragment;
import com.example.foodmart.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class homee extends AppCompatActivity {
    ActivityMainBinding binding;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFrag(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId())
            {
                case R.id.homeFragment:
                    replaceFrag(new HomeFragment());
                    break;
                case R.id.cartFragment:
                    replaceFrag(new CartFragment());
                    break;
                case R.id.historyFragment:
                    replaceFrag(new HistoryFragment());
                    break;
                case R.id.profileFragment:
                    replaceFrag(new ProfileFragment());
                    break;
                case R.id.searchFragment:
                    replaceFrag(new SearchFragment());
                    break;
            }
            return true;
        });
        // Initialize the GoogleSignInClient
        mGoogleSignInClient = YourInitializationMethodHere();

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    private void replaceFrag(Fragment fragment)
    {
        FragmentManager fragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView,fragment);
        fragmentTransaction.commit();
    }

    private void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in with email/password
            mAuth.signOut();
        }

        // Sign out from Google (if signed in with Google)
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successfully signed out from Google
                        Intent intent = new Intent(homee.this, LogInPage.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Handle any errors that occurred during Google sign-out.
                        // You can show an error message if needed.
                        Toast.makeText(homee.this, "Google Sign out failed", Toast.LENGTH_SHORT).show();
                    }

                    // Redirect to the login page

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
