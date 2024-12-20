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

public class ElectionAdmin extends AppCompatActivity {

    private TextView tvVotes1, tvVotes2;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_admin);

        tvVotes1 = findViewById(R.id.tvVotes1);
        tvVotes2 = findViewById(R.id.tvVotes2);

        databaseReference = FirebaseDatabase.getInstance("https://pumaisapps-4ba9b-default-rtdb.firebaseio.com/")
                .getReference();

        loadCandidateData("chairperson1", tvVotes1);
        loadCandidateData("chairperson2", tvVotes2);
    }

    private void loadCandidateData(String candidateId, TextView tvVotes) {
        databaseReference.child("candidates").child(candidateId).child("votes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int votes = snapshot.exists() ? snapshot.getValue(Integer.class) : 0;
                        tvVotes.setText("Votes: " + votes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvVotes.setText("Votes: Failed to load");
                    }
                });
    }
}