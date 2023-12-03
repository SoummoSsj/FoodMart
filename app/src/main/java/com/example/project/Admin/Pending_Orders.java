package com.example.project.Admin;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.Domain.FoodDomain;
import com.example.project.raider.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;

import com.example.project.R;

public class Pending_Orders extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PendingAdapt adapter;
    private DatabaseReference databaseReference,ref;
    private TextView tvTotal;
    private TextView tvTime,riderDelayedTextView,refundTextView,TotalTextView,PaymentTextView,NametextView,PhoneTextView,AddressTextView;
    private int time1,total1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);

        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvTotal = findViewById(R.id.total);
        tvTime = findViewById(R.id.time);
        riderDelayedTextView=findViewById(R.id.delay);
        refundTextView=findViewById(R.id.refund);
        TotalTextView=findViewById(R.id.tot);
        PaymentTextView=findViewById(R.id.stat);
        NametextView=findViewById(R.id.Name);
        PhoneTextView=findViewById(R.id.phone);
        AddressTextView=findViewById(R.id.address);


        String uid = getIntent().getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("pending_orders").child(uid);

        // Initialize the adapter with an empty list
        adapter = new PendingAdapt(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Retrieve data from Firebase
        fetchDataFromFirebase();
        fetchUserDetails();
    }

    private void fetchDataFromFirebase() {
        int[] total = {0};
        int[] time = {0};

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<FoodDomain> dataList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodDomain data = snapshot.getValue(FoodDomain.class);

                    if(data == null) continue;

                    String title = String.valueOf(snapshot.child("name").getValue());
                    data.setTitle(title);
                    int num = snapshot.child("quantity").getValue(Integer.class);

                    total[0] += snapshot.child("price").getValue(Double.class);
                    total1=total[0];
                    time[0] = Math.max(time[0],data.getTime());
                    time1=time[0];
                    data.setNumberInCart(num);
                    dataList.add(data);

                }

                tvTime.setText(time[0]+"min");
                tvTotal.setText(total[0]+"$");

                // Update the adapter with the new data
                adapter.setData(dataList);

               String uid = getIntent().getStringExtra("id");

                ref=FirebaseDatabase.getInstance().getReference().child("Payment").child(uid);
                ref.child("DeliveryTime").setValue(time[0]);
                ref.child("Total").setValue(total[0]);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void fetchUserDetails() {
        String uid = getIntent().getStringExtra("id");
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Payment").child(uid);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String orderedTime = dataSnapshot.child("OrderTime").getValue(String.class);
                String deliveredTime= dataSnapshot.child("DTime").getValue(String.class);
                String method=dataSnapshot.child("Method").getValue(String.class);
                String Name=dataSnapshot.child("name").getValue(String.class);
                String Address=dataSnapshot.child("address").getValue(String.class);
                String Phone=dataSnapshot.child("phone").getValue(String.class);


                NametextView.setText(Name);
                AddressTextView.setText(Address);
                PhoneTextView.setText(Phone);
                PaymentTextView.setText(method);
                if(orderedTime!=null && deliveredTime!=null) {

                    long delayed;
                    try {
                        long timeDifferenceMinutes = calculateTimeDifference(orderedTime, deliveredTime);
                        delayed = timeDifferenceMinutes - time1;

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    if(delayed>0 && delayed<100) {
                        riderDelayedTextView.setText(delayed + "min");
                        riderDelayedTextView.setTextColor(Color.parseColor("#FF0000"));
                        double refund = (total1 * ((double) delayed / 100));
                        refund = round(refund);
                        refundTextView.setText(refund + "$");
                        refundTextView.setTextColor(Color.parseColor("#FF0000"));
                        double all=total1-refund;
                        all=round(all);
                        TotalTextView.setText(all+"$");
                    }
                    else if(delayed==100){
                        TotalTextView.setText("Free Delivery");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private static long calculateTimeDifference(String orderedTime, String deliveredTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date orderedDate = format.parse(orderedTime);
        Date deliveredDate = format.parse(deliveredTime);

        long timeDifferenceMillis = deliveredDate.getTime() - orderedDate.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis);
    }
}