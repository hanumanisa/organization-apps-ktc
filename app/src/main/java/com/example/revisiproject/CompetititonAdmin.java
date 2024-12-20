package com.example.revisiproject;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompetititonAdmin extends AppCompatActivity {

    private TextView tvProgrushDetails, tvErpDetails, tvUiuxDetails;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_competititon_admin);


        tvProgrushDetails = findViewById(R.id.tvProgrushDetails);
        tvErpDetails = findViewById(R.id.tvErpDetails);
        tvUiuxDetails = findViewById(R.id.tvUiuxDetails);


        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pumaisapps-4ba9b-default-rtdb.firebaseio.com/");

        loadCompetitionData("Programming Rush Competition", tvProgrushDetails);

        loadCompetitionData("ERP Competition", tvErpDetails);

        loadCompetitionData("UI/UX Competition", tvUiuxDetails);
    }

    private void loadCompetitionData(String competitionName, TextView textView) {
        database.child(competitionName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                StringBuilder data = new StringBuilder();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);
                    String major = childSnapshot.child("major").getValue(String.class);
                    String email = childSnapshot.child("email").getValue(String.class);
                    String batch = childSnapshot.child("batch").getValue(String.class);
                    String fee = childSnapshot.child("fee").getValue(String.class);

                    data.append("Name: ").append(name).append("\n")
                            .append("Major: ").append(major).append("\n")
                            .append("Email: ").append(email).append("\n")
                            .append("Batch: ").append(batch).append("\n")
                            .append("Fee: ").append(fee).append("\n\n");
                }
                textView.setText(data.toString().isEmpty() ? "No data available" : data.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                textView.setText("Error loading data");
            }
        });
    }
}