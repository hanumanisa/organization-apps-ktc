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

public class AwardingCompsphere extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String userFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awarding_compsphere);

        // Retrieve logged-in user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userFullName = sharedPreferences.getString("fullname", null);

        if (userFullName == null) {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://pumaisapps-4ba9b-default-rtdb.firebaseio.com/")
                .getReference();

        // Set up vote buttons
        Button btnVoteLecturer1 = findViewById(R.id.lecturer1_vote_button);
        Button btnVoteLecturer2 = findViewById(R.id.lecturer2_vote_button);
        Button btnVoteStudent1 = findViewById(R.id.student1_vote_button);
        Button btnVoteStudent2 = findViewById(R.id.student2_vote_button);
        Button btnVoteProject1 = findViewById(R.id.project1_vote_button);
        Button btnVoteProject2 = findViewById(R.id.project2_vote_button);

        btnVoteLecturer1.setOnClickListener(v -> castVote("most_favorite_lecturer", "lecturer1"));
        btnVoteLecturer2.setOnClickListener(v -> castVote("most_favorite_lecturer", "lecturer2"));
        btnVoteStudent1.setOnClickListener(v -> castVote("most_achievable_student", "student1"));
        btnVoteStudent2.setOnClickListener(v -> castVote("most_achievable_student", "student2"));
        btnVoteProject1.setOnClickListener(v -> castVote("most_favorite_project", "project1"));
        btnVoteProject2.setOnClickListener(v -> castVote("most_favorite_project", "project2"));
    }

    private void castVote(String category, String candidateId) {
        // Check if the user has already voted
        databaseReference.child("votes awarding").child(category).child(userFullName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(AwardingCompsphere.this, "You have already voted in this category.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Record the user's vote
                            databaseReference.child("votes awarding").child(category).child(userFullName).setValue(candidateId)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Increment the candidate's vote count
                                            incrementCandidateVotes(category, candidateId);
                                        } else {
                                            Toast.makeText(AwardingCompsphere.this, "Failed to vote. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AwardingCompsphere.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void incrementCandidateVotes(String category, String candidateId) {
        databaseReference.child("candidates").child(category).child(candidateId).child("votes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentVotes = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                        databaseReference.child("candidates").child(category).child(candidateId).child("votes")
                                .setValue(currentVotes + 1)
                                .addOnSuccessListener(unused -> Toast.makeText(AwardingCompsphere.this, "Successfully voted!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(AwardingCompsphere.this, "Failed to update vote count.", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AwardingCompsphere.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Refresh the activity to allow a new vote
        Intent intent = new Intent(AwardingCompsphere.this, AwardingCompsphere.class);
        startActivity(intent);
        finish();
    }
}
