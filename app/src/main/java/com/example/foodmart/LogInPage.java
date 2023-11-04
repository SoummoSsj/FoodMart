package com.example.foodmart;

import static androidx.fragment.app.FragmentManager.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
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
import com.google.firebase.database.FirebaseDatabase;

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

    // ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        TextView textView=findViewById(R.id.forget);
        EditText editText = findViewById(R.id.EMAIL);
        EditText editText1 = findViewById(R.id.editText2);
        Button button = findViewById(R.id.Login);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            // User is already authenticated, skip the login page.
            Intent intent = new Intent(LogInPage.this, homee.class);
            startActivity(intent);
            finish();
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // User is already authenticated with Google, skip the login page.
            Intent intent = new Intent(LogInPage.this, homee.class);
            startActivity(intent);
            finish();
        }


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
                                    // User is authenticated with Firebase, allow access to "homee" activity
                                    Intent intent = new Intent(LogInPage.this, homee.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // User is not authenticated or email is not verified, handle this case.
                                    if (firebaseUser != null && !firebaseUser.isEmailVerified()) {
                                        Toast.makeText(LogInPage.this, "Email is not verified. Please check your email and verify your account" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                    } else {
                                        // User is not authenticated with Firebase, handle this case (e.g., show an error message).
                                        Toast.makeText(LogInPage.this, "Please sign up before using Sign-In. ", Toast.LENGTH_SHORT).show();


                                    }
                                }
                            } else {
                                Toast.makeText(LogInPage.this, "Log in Error: ", Toast.LENGTH_SHORT).show();
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

                            Intent intent = new Intent(LogInPage.this, homee.class);
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
    }}