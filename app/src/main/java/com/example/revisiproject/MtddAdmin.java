package com.example.revisiproject;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MtddAdmin extends AppCompatActivity {

    private LinearLayout userListContainer;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtdd_admin);


        databaseReference = FirebaseDatabase.getInstance().getReference("MTDD Registrations");


        userListContainer = findViewById(R.id.userListContainer);


        fetchBodRegistrations();
    }

    private void fetchBodRegistrations() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userListContainer.removeAllViews();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String name = snapshot.child("name").getValue(String.class);
                    String studentID = snapshot.child("studentID").getValue(String.class);
                    String batch = snapshot.child("batch").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String birthday = snapshot.child("birthday").getValue(String.class);
                    String reason = snapshot.child("reason").getValue(String.class);


                    TextView textView = new TextView(MtddAdmin.this);
                    String displayText = "Name: " + name + "\n" +
                            "Student ID: " + studentID + "\n" +
                            "Batch: " + batch + "\n" +
                            "Phone: " + phone + "\n" +
                            "Birthday: " + birthday + "\n" +
                            "Reason: " + reason + "\n\n";
                    textView.setText(displayText);
                    textView.setTextSize(14);
                    textView.setPadding(16, 16, 16, 16);


                    userListContainer.addView(textView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MtddAdmin.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}