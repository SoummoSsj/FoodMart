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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        TextView textView=findViewById(R.id.forget);
        EditText editText=findViewById(R.id.EMAIL);
        EditText editText1=findViewById(R.id.editText2);
        Button button=findViewById(R.id.Login);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();

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
             * @param view
             */
            @Override
            public void onClick(View view) {
                String mail=editText.getText().toString().trim();
                String password=editText1.getText().toString().trim();
                if(mail.isEmpty())
                {
                    editText.setError("Email Can not be Empty");
                    editText.requestFocus();
                }
                else if(password.isEmpty())
                {
                    editText1.setError("Password Can not be Empty");
                    editText1.requestFocus();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        /**
                         * if user is Authorized the method Takes user to LogIn Page.
                         * @param task
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
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
                                        Toast.makeText(LogInPage.this, "Please sign up before using Sign-In. " , Toast.LENGTH_SHORT).show();


                                    }
                                }
                            }
                            else
                            {
                                Toast.makeText(LogInPage.this, "Log in Error: " , Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });
        btnSignIn =findViewById(R.id.google);
        progressDialog=new ProgressDialog(LogInPage.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creatingg your account");
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
int RC_SIGN_IN=40;
    private void signIn() {

        Intent intent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Check if the user is authenticated with Firebase


            if (account != null) {
                // User is authenticated with Firebase, allow access to "homee" activity
                Intent intent = new Intent(LogInPage.this, homee.class);
                startActivity(intent);
                finish();
            } else {
                // User is not authenticated or email is not verified, handle this case.

                    // User is not authenticated with Firebase, handle this case (e.g., show an error message).
                    Toast.makeText(this, "Please sign up before using Sign-In.", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult: failed code=" + e.getStatusCode());
        }
    }



    public void startNextActivity(View view) {
        Intent intent = new Intent(this, sign_up.class);
        startActivity(intent);
    }
}