package com.example.project.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.Admin.Add_Item;
import com.example.project.Admin.Admin_Home;
import com.example.project.R;
import com.example.project.raider.Raider_Home;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
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

public class LogInPage extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    Button btnSignIn;

    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;


    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private FirebaseAuth gAuth;
    private TextView forgot;


    // ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);
//        TextView textView=findViewById(R.id.forget);
        EditText editText = findViewById(R.id.EMAIL);
        EditText editText1 = findViewById(R.id.editText2);
        Button button = findViewById(R.id.Login);
        forgot = findViewById(R.id.fogot);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LogInPage.this);
                View dialogView = getLayoutInflater().inflate(R.layout.fogot_pass, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = emailBox.getText().toString().trim();
                        if (email.isEmpty()) {
                            emailBox.setError("Email Can not be Empty");
                            emailBox.requestFocus();
                        } else {
                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                /**
                                 * if user is Authorized the method Takes user to LogIn Page.
                                 * @param task
                                 */
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(LogInPage.this, "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(LogInPage.this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                });
                if(dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.background_light);}
                dialog.show();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            /**
             * LogIn Page Intent Method
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                String mail = editText.getText().toString().trim();
                String password = editText1.getText().toString().trim();
                if (mail.isEmpty()) {
                    editText.setError("Email Can not be Empty");
                    editText.requestFocus();
                } else if (password.isEmpty()) {
                    editText1.setError("Password Can not be Empty");
                    editText1.requestFocus();
                } else {
                    mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        /**
                         * if user is Authorized the method Takes user to LogIn Page.
                         * @param task
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid()).child("status");
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String val = String.valueOf(snapshot.getValue());
                                            if(val.equals("Manager")){
                                                Intent intent = new Intent(LogInPage.this, Admin_Home.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else if (val.equals("User")){
                                                Intent intent = new Intent(LogInPage.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }


                                            else if (val.equals("Raider")){
                                                Intent intent = new Intent(LogInPage.this, Raider_Home.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            // User is authenticated with Firebase, allow access to "homee" activity

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("FirebaseError", "Database error: " + error.getMessage());


                                        }
                                    });
                                } else {
                                    // User is not authenticated or email is not verified, handle this case.

                                    Toast.makeText(LogInPage.this, "Signup not complete!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();



                                }
                            } else {
                                Toast.makeText(LogInPage.this, "Log in Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }
        });
        btnSignIn = findViewById(R.id.google);
        progressDialog = new ProgressDialog(LogInPage.this);
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

                            Intent intent = new Intent(LogInPage.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LogInPage.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void startNextActivity(View view) {
        Intent intent = new Intent(this, sign_up.class);
        startActivity(intent);
    }
}