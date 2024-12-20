package com.example.revisiproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Election extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);

        // Retrieve logged-in user's full name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        fullname = sharedPreferences.getString("fullname", null);

        if (fullname == null) {
            Toast.makeText(this, "User is not logged in. Please login first.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance("https://pumaisapps-4ba9b-default-rtdb.firebaseio.com/")
                .getReference();

        // Set up buttons for voting
        Button btnVoteChairperson1 = findViewById(R.id.btnCp1);
        Button btnVoteChairperson2 = findViewById(R.id.btnCp2);

        btnVoteChairperson1.setOnClickListener(v -> castVote("chairperson1"));
        btnVoteChairperson2.setOnClickListener(v -> castVote("chairperson2"));
    }

    private void castVote(String candidateId) {
        // Check if the user has already voted
        databaseReference.child("election_votes").child(fullname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(Election.this, "You have already voted!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Save user's vote in the database
                            databaseReference.child("election_votes").child(fullname).setValue(candidateId)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Update the candidate's vote count
                                            updateCandidateVotes(candidateId);
                                        } else {
                                            Toast.makeText(Election.this, "Failed to vote. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Election.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCandidateVotes(String candidateId) {
        databaseReference.child("candidates").child(candidateId).child("votes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentVotes = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;

                        // Increment vote count
                        databaseReference.child("candidates").child(candidateId).child("votes")
                                .setValue(currentVotes + 1)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(Election.this, "Successfully voted!", Toast.LENGTH_SHORT).show();
                                    // Navigate to the menu screen after voting
                                    Intent intent = new Intent(Election.this, MenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(Election.this, "Failed to update vote count.", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Election.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
