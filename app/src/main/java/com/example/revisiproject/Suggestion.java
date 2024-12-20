package com.example.revisiproject;

import android.app.DatePickerDialog;
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

import java.util.Calendar;

public class Suggestion extends AppCompatActivity {
    private TextView fieldName, fieldEmail, fieldMajor;
    private EditText fieldBatch, fieldDate, suggestion;
    private Button btnSubmit;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        View backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            ViewCompat.setOnApplyWindowInsetsListener(backButton, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        fieldName = findViewById(R.id.fieldName);
        fieldEmail = findViewById(R.id.fieldEmail);
        fieldMajor = findViewById(R.id.fieldMajor);
        fieldBatch = findViewById(R.id.fieldBatch);
        fieldDate = findViewById(R.id.fieldDate);
        suggestion = findViewById(R.id.notesSugges);
        btnSubmit = findViewById(R.id.buttonSubmit);

        database = FirebaseDatabase.getInstance().getReference("suggestion");

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        if (loggedInUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String major = snapshot.child("major").getValue(String.class);

                        fieldName.setText(name != null ? name : "");
                        fieldEmail.setText(email != null ? email : "");
                        fieldMajor.setText(major != null ? major : "");
                    } else {
                        Toast.makeText(Suggestion.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(Suggestion.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }

        fieldDate.setOnClickListener(view -> showDatePicker());

        btnSubmit.setOnClickListener(view -> {
            String nama = fieldName.getText().toString().trim();
            String email = fieldEmail.getText().toString().trim();
            String major = fieldMajor.getText().toString().trim();
            String batch = fieldBatch.getText().toString().trim();
            String date = fieldDate.getText().toString().trim();
            String sugges = suggestion.getText().toString().trim();

            if (nama.isEmpty() || email.isEmpty() || major.isEmpty() || batch.isEmpty() || date.isEmpty() || sugges.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Data must be complete", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference userRef = database.child(nama);
                userRef.child("name").setValue(nama);
                userRef.child("email").setValue(email);
                userRef.child("major").setValue(major);
                userRef.child("batch").setValue(batch);
                userRef.child("date").setValue(date);
                userRef.child("suggestion").setValue(sugges);

                Toast.makeText(Suggestion.this, "Submit Success", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            fieldDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void clearFields() {
        fieldBatch.setText("");
        fieldDate.setText("");
        suggestion.setText("");
    }
}
