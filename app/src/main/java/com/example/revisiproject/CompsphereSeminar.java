package com.example.revisiproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompsphereSeminar extends AppCompatActivity {

    private EditText etBatch;
    private TextView etNama, etEmail, etMajor;
    private Button btnRegister;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compsphere_seminar);

        // Adjusting for system bars (Edge-to-Edge display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (systemBars != null) {
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        // Initialize UI components
        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etMajor = findViewById(R.id.etMajor);
        etBatch = findViewById(R.id.etBatch);
        btnRegister = findViewById(R.id.btnRegister);

        // Firebase database reference
        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pumaisapps-4ba9b-default-rtdb.firebaseio.com/");

        // Retrieve and pre-fill user data if available
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        if (loggedInUser != null && !loggedInUser.isEmpty()) {
            preFillUserData(loggedInUser);
        } else {
            Toast.makeText(this, "No user data found! Please log in again.", Toast.LENGTH_SHORT).show();
        }

        // Register button click listener
        btnRegister.setOnClickListener(view -> handleRegistration());
    }

    private void preFillUserData(String loggedInUser) {
        DatabaseReference userRef = database.child("users").child(loggedInUser);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String major = snapshot.child("major").getValue(String.class);

                    etNama.setText(name != null ? name : "");
                    etEmail.setText(email != null ? email : "");
                    etMajor.setText(major != null ? major : "");
                } else {
                    Toast.makeText(CompsphereSeminar.this, "User data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CompsphereSeminar.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRegistration() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String major = etMajor.getText().toString().trim();
        String batch = etBatch.getText().toString().trim();

        if (nama.isEmpty() || email.isEmpty() || major.isEmpty() || batch.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
        } else {
            saveRegistrationData(nama, email, major, batch);
        }
    }

    private void saveRegistrationData(String nama, String email, String major, String batch) {
        DatabaseReference seminarRef = database.child("seminar").child(nama);
        seminarRef.child("name").setValue(nama);
        seminarRef.child("email").setValue(email);
        seminarRef.child("major").setValue(major);
        seminarRef.child("batch").setValue(batch);

        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Compsphere.class);
        startActivity(intent);
        finish();
    }
}
