package com.example.revisiproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    private EditText etName, etEmail, etStudentID, etMajor, etPassword;
    private Button btnRegis;
    private TextView tvLoginRedirectText;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etName = findViewById(R.id.signup_name);
        etEmail = findViewById(R.id.signup_email);
        etStudentID = findViewById(R.id.signup_studentID);
        etMajor = findViewById(R.id.signup_major);
        etPassword = findViewById(R.id.signup_password);
        btnRegis = findViewById(R.id.btnSignup);
        tvLoginRedirectText = findViewById(R.id.loginRedirectText);

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("users");

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String studentID = etStudentID.getText().toString().trim();
                String major = etMajor.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validate inputs
                if (fullName.isEmpty() || email.isEmpty() || studentID.isEmpty() || major.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the name is already registered
                    database.child(fullName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(Register.this, "Name is already registered. Please use a different name.", Toast.LENGTH_SHORT).show();
                            } else {
                                registerUser(fullName, email, studentID, major, password);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(Register.this, "Error checking name: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        tvLoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void registerUser(String fullName, String email, String studentID, String major, String password) {
        // Save user data to Firebase using the name as the primary key
        database.child(fullName).child("name").setValue(fullName);
        database.child(fullName).child("email").setValue(email);
        database.child(fullName).child("studentID").setValue(studentID);
        database.child(fullName).child("major").setValue(major);
        database.child(fullName).child("password").setValue(password);

        Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();

        // Navigate to the login screen
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}
