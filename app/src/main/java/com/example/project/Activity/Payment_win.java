package com.example.project.Activity;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Payment_win extends AppCompatActivity{

    Button payment;
    String PublishableKey = "pk_test_51NPILjBMZgTuAxwTzz3Hm2BdoAvbHSsQoEulGsCdff4SzIxxmk9WzjT2wp6g3vmyjj8IyGJUo4yJdO2ioajOJZpv00bHqdeiRM";
    String SecretKey = "sk_test_51NPILjBMZgTuAxwT3qDP3kKoo9WhRiBDM9Zzn06OgQVqZHfk54dpcBelhqmUkJOpKtD72nfYKSxYqkUyrJHC6uCw00Q01Zx7pB";
    String CustomerId;
    String EphericalKey;
    String ClientSecret;
    String amount;
    String amountString;

    PaymentSheet paymentSheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_win);

        Intent intent = getIntent();
        amount = intent.getStringExtra("TOTAL_AMOUNT");
        payment = findViewById(R.id.btn);
        PaymentConfiguration.init(this,PublishableKey);
        // Convert amount to cents
        double amountDouble = Double.parseDouble(amount);
        int amountInCents = (int) (amountDouble * 100);
        amountString = String.valueOf(amountInCents);


        paymentSheet = new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentFlow();
            }
        });

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject object = new JSONObject(response);

                            CustomerId = object.getString("id");
                            Toast.makeText(Payment_win.this, "Starting", Toast.LENGTH_SHORT).show();
                            getEmpericalKey();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Payment_win.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SecretKey);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret,new PaymentSheet.Configuration("Food Mart",new PaymentSheet.CustomerConfiguration(
                CustomerId,
                EphericalKey
        )));
    }


    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Compplete", Toast.LENGTH_SHORT).show();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Carts").child(userId);

            // Remove all items from the user's cart
            cartReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Cart cleared successfully
                        Log.d("Payment", "User's cart cleared successfully");
                    } else {
                        // Handle the case where clearing the cart failed
                        Log.e("Payment", "Failed to clear user's cart: " + task.getException().getMessage());
                    }
                }
            });

            Intent intent = new Intent(this, Order_Confirm.class);
            startActivity(intent);
        }}

    private void getEmpericalKey() {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);

                            EphericalKey = object.getString("id");
                            Toast.makeText(Payment_win.this, "Wait", Toast.LENGTH_SHORT).show();
                            getClientSecret(CustomerId,EphericalKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Payment_win.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SecretKey);
                header.put("Stripe-Version","2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    private void getClientSecret(String customerId, String ephericalKey) {

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");

                            Toast.makeText(Payment_win.this, "Proceed to payment", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Payment_win.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String , String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SecretKey);
                return header;
            }
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);
                params.put("amount",amountString);
                params.put("currency","USD");
                params.put("automatic_payment_methods[enabled]","true");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }
}
