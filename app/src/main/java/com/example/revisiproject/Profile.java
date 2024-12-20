package com.example.revisiproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    private TextView tvName, tvEmail, Major, StudentID, tvPW;
    private Button btnedit;
    private ImageView btnBack;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        Major = findViewById(R.id.tvMajor);
        StudentID = findViewById(R.id.tvStudentID);
        tvPW = findViewById(R.id.tvPW);
        btnedit = findViewById(R.id.buttonedit);
        btnBack = findViewById(R.id.btnBack);

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Editprofile.class));
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent openAbout = new Intent(Profile.this, MenuActivity.class);
            startActivity(openAbout);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        if (loggedInUser != null) {
            database = FirebaseDatabase.getInstance().getReference("users");

            database.child(loggedInUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String major = snapshot.child("major").getValue(String.class);
                        String studentID = snapshot.child("studentID").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);

                        tvName.setText(name != null ? name : "");
                        tvEmail.setText(email != null ? email : "");
                        Major.setText(major != null ? major : "");
                        StudentID.setText(studentID != null ? studentID : "");
                        tvPW.setText(password != null ? password : "");
                    } else {
                        Toast.makeText(Profile.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Profile.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }
    }
}
