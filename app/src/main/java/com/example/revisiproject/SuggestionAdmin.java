package com.example.revisiproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SuggestionAdmin extends AppCompatActivity {

    private LinearLayout userListContainer;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggestion_admin);

        userListContainer = findViewById(R.id.userListContainer);
        database = FirebaseDatabase.getInstance().getReference("suggestion");

         loadUserSuggestions();
    }

    private void loadUserSuggestions() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userListContainer.removeAllViews(); // Clear existing views
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String name = userSnapshot.child("name").getValue(String.class);
                        String email = userSnapshot.child("email").getValue(String.class);
                        String major = userSnapshot.child("major").getValue(String.class);
                        String batch = userSnapshot.child("batch").getValue(String.class);
                        String date = userSnapshot.child("date").getValue(String.class);
                        String suggestion = userSnapshot.child("suggestion").getValue(String.class);


                        View userCard = LayoutInflater.from(SuggestionAdmin.this).inflate(R.layout.user_suggestion_card_admin, null, false);

                        TextView tvName = userCard.findViewById(R.id.tvName);
                        TextView tvEmail = userCard.findViewById(R.id.tvEmail);
                        TextView tvMajor = userCard.findViewById(R.id.tvMajor);
                        TextView tvBatch = userCard.findViewById(R.id.tvBatch);
                        TextView tvDate = userCard.findViewById(R.id.tvDate);
                        TextView tvSuggestion = userCard.findViewById(R.id.tvSuggestion);

                        tvName.setText("Name: " + name);
                        tvEmail.setText("Email: " + email);
                        tvMajor.setText("Major: " + major);
                        tvBatch.setText("Batch: " + batch);
                        tvDate.setText("Date: " + date);
                        tvSuggestion.setText("Suggestion: " + suggestion);

                        userListContainer.addView(userCard);
                    }
                } else {
                    Toast.makeText(SuggestionAdmin.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SuggestionAdmin.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}