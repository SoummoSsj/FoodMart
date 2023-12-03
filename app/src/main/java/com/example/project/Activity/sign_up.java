package com.example.project.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.Adapter.User;
import com.example.project.Admin.Restaurant;
import com.example.project.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class sign_up extends AppCompatActivity {
    Button btnSignIn;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EditText nameEdit = findViewById(R.id.editTextText2);
        EditText emailEdit = findViewById(R.id.EMAIL);
        EditText passwordEdit = findViewById(R.id.editText2);
        //  EditText restaurantName = findViewById(R.id.restaurantName);
        //restaurantName.setVisibility(View.GONE);


        EditText confirmEdit = findViewById(R.id.editpass2);
        EditText job = findViewById(R.id.status);


        Button button = findViewById(R.id.Login);

        mAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = passwordEdit.getText().toString().trim();
                String pass2 = confirmEdit.getText().toString().trim();
                String name = nameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String stat = job.getText().toString().trim();




                if (name.isEmpty()) {
                    nameEdit.setError("Name Field is Required");
                }else if (stat.isEmpty()) {
                    job.setText("User");
                } else if (email.isEmpty()) {
                    emailEdit.setError("Email Field Is Required");
                } else if (pass.isEmpty()) {
                    passwordEdit.setError("Please Enter A Password");
                } else if (pass2.isEmpty()) {
                    confirmEdit.setError("Please Enter The password Again");
                } else if (!pass.equals(pass2)) {
                    confirmEdit.setError("Password Does not Match");

                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> emailTask) {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(sign_up.this, "Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(sign_up.this, "Email verification could not be sent.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                EditText s = findViewById(R.id.status);
                                String jobs = s.getText().toString().trim();

                                // User registration without manager affiliation
                                String id = user.getUid();
                                User userInfo = new User(name,email,jobs);
                                reference.child("User").child(id).setValue(userInfo);



                            }
                            else {
                                Toast.makeText(sign_up.this, "Sign Up Error: " + task.getException().getMessage (), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });







                }
            }
        });

        btnSignIn = findViewById(R.id.google);
        progressDialog = new ProgressDialog(sign_up.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    int RC_SIGN_IN = 40;

    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final String userId = user.getUid();
                            final String email = user.getEmail();

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.child("User").exists()&& dataSnapshot.child("Google").exists()) {
                                        // The email already exists in the database
                                        Toast.makeText(sign_up.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                    }

                                    else {
                                        // The email does not exist in any node
                                        // You can proceed with registration
                                        User users = new User();
                                        users.setUserId(user.getUid());
                                        users.setDisplayName(user.getDisplayName());
                                        users.setEmail(user.getEmail());
                                        databaseReference.child("Google").child(userId).setValue(users);
                                        Intent intent = new Intent(sign_up.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle any errors that may occur during the database query
                                    // For example, show an error message or log the error
                                    Toast.makeText(sign_up.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(sign_up.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });



    }

    public void startNextActivity(View view) {
        Intent intent = new Intent(this, LogInPage.class);
        startActivity(intent);
    }
}