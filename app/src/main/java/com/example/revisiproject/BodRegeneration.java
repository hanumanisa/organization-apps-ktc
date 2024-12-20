package com.example.revisiproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BodRegeneration extends AppCompatActivity {

    private EditText bodBatch, bodPhone, bodBirthday, bodReason;
    private TextView bodName, bodStudentID;
    private Button btnCvUpload, btnPersonalUpload, submitRegis;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bod_regeneration);

        // Initialize UI elements
        bodName = findViewById(R.id.bodName);
        bodStudentID = findViewById(R.id.bodStudentID);
        bodBatch = findViewById(R.id.bodBatch);
        bodPhone = findViewById(R.id.bodPhone);
        bodBirthday = findViewById(R.id.bodBirthday);
        bodReason = findViewById(R.id.bodReason);

        btnCvUpload = findViewById(R.id.btnCvBod);
        btnPersonalUpload = findViewById(R.id.btnPersonalBod);
        submitRegis = findViewById(R.id.btnSubmitBod);

        databaseReference = FirebaseDatabase.getInstance().getReference("BOD Registrations");

        // Retrieve logged-in user data
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        if (loggedInUser != null) {
            fetchUserData(loggedInUser);
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }

        // Set button click listeners
        btnCvUpload.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1NjqRT37hiOIXQLA1q_7m3G09VD845l5V?usp=sharing"));
        btnPersonalUpload.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1mu2cZkL7itHUJCJo41HFE0acdLNSjEQ7?usp=drive_link"));
        bodBirthday.setOnClickListener(view -> showDatePicker());
        submitRegis.setOnClickListener(view -> submitRegistration());
    }

    private void fetchUserData(String loggedInUser) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String studentID = snapshot.child("studentID").getValue(String.class);

                    if (bodName != null && bodStudentID != null) {
                        bodName.setText(name != null ? name : "");
                        bodStudentID.setText(studentID != null ? studentID : "");
                    }
                } else {
                    Toast.makeText(BodRegeneration.this, "User data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BodRegeneration.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openUploadLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            bodBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void submitRegistration() {
        String name = bodName.getText().toString().trim();
        String studentID = bodStudentID.getText().toString().trim();
        String batch = bodBatch.getText().toString().trim();
        String phone = bodPhone.getText().toString().trim();
        String birthday = bodBirthday.getText().toString().trim();
        String reason = bodReason.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(studentID) || TextUtils.isEmpty(batch) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(birthday) || TextUtils.isEmpty(reason)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("studentID", studentID);
        data.put("batch", batch);
        data.put("phone", phone);
        data.put("birthday", birthday);
        data.put("reason", reason);

        String id = databaseReference.push().getKey();
        if (id != null) {
            databaseReference.child(id).setValue(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(BodRegeneration.this, "Registration submitted successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(BodRegeneration.this, "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        bodBatch.setText("");
        bodPhone.setText("");
        bodBirthday.setText("");
        bodReason.setText("");
    }
}
