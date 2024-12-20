package com.example.revisiproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AwardingAdmin extends AppCompatActivity {

    private TextView tvLecturerVotes;
    private TextView tvStudentVotes;
    private TextView tvProjectVotes;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awarding_admin);

        tvLecturerVotes = findViewById(R.id.favorite_lecturer_votes);
        tvStudentVotes = findViewById(R.id.most_achievable_student_votes);
        tvProjectVotes = findViewById(R.id.most_favorite_project_votes);

        databaseReference = FirebaseDatabase.getInstance("https://pumaisapps-4ba9b-default-rtdb.firebaseio.com/")
                .getReference();

        loadVotingData("most_favorite_lecturer", tvLecturerVotes);
        loadVotingData("most_achievable_student", tvStudentVotes);
        loadVotingData("most_favorite_project", tvProjectVotes);
    }

    private void loadVotingData(String category, TextView tvVotes) {
        databaseReference.child("votes awarding").child(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Integer> voteCountMap = new HashMap<>();

                        for (DataSnapshot voterSnapshot : snapshot.getChildren()) {
                            String candidate = voterSnapshot.getValue(String.class);
                            voteCountMap.put(candidate, voteCountMap.getOrDefault(candidate, 0) + 1);
                        }

                        StringBuilder voteResult = new StringBuilder();
                        for (Map.Entry<String, Integer> entry : voteCountMap.entrySet()) {
                            voteResult.append(entry.getKey()).append(": ").append(entry.getValue()).append(" votes\n");
                        }
                        tvVotes.setText(voteResult.length() > 0 ? voteResult.toString() : "No votes yet.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvVotes.setText("Votes: Failed to load");
                    }
                });
    }
}
