package com.example.foodmart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
                            if(task.isSuccessful())
                            {
                                Intent intent=new Intent(LogInPage.this,homee.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LogInPage.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                // Handle sign-in failure (e.g., user cancelled the sign-in)
                e.printStackTrace();
            }
        }
    }




    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            User users = new User();
                            users.setUserId(user.getUid());
                            users.setDisplayName(user.getDisplayName());
                            users.setEmail(user.getEmail());
                            FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid()).setValue(users);
                            Intent intent=new Intent(LogInPage.this,homee.class );
                            startActivity(intent);



                        }
                        else
                        {
                            Toast.makeText(LogInPage.this,"Error",Toast.LENGTH_SHORT).show();
                        }



                    }});}}