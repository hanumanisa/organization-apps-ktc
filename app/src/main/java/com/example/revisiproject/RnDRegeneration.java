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

public class RnDRegeneration extends AppCompatActivity {

    private EditText rndBatch, rndPhone, rndBirthday, rndReason;
    private TextView rndName, rndStudentID;
    private Button btnCvUpload, btnPersonalUpload, submitRegis;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rn_dregeneration);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("RND Registrations");

        // Load user preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        // Initialize UI components
        rndName = findViewById(R.id.rndName);
        rndStudentID = findViewById(R.id.rndStudent);
        rndBatch = findViewById(R.id.rndBatch);
        rndPhone = findViewById(R.id.rndPhone);
        rndBirthday = findViewById(R.id.rndBirthday);
        rndReason = findViewById(R.id.rndReason);
        btnCvUpload = findViewById(R.id.btncvUP);
        btnPersonalUpload = findViewById(R.id.btnpersonalUP);
        submitRegis = findViewById(R.id.submit_regis);

        if (loggedInUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String studentID = snapshot.child("studentID").getValue(String.class);

                        if (rndName != null && rndStudentID != null) {
                            rndName.setText(name != null ? name : "");
                            rndStudentID.setText(studentID != null ? studentID : "");
                        }
                    } else {
                        Toast.makeText(RnDRegeneration.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RnDRegeneration.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }

        // Set listeners for buttons
        btnCvUpload.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1TUA-HhcrcJm8Hhb2b-Uu_sEYcR8DAVRL?usp=drive_link"));
        btnPersonalUpload.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1ylrZjpYCVssQg1c5Au3jVUmQbQ5SBuF_?usp=drive_link"));
        rndBirthday.setOnClickListener(view -> showDatePicker());
        submitRegis.setOnClickListener(view -> submitRegistration());
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
            rndBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void submitRegistration() {
        String name = rndName.getText().toString().trim();
        String studentID = rndStudentID.getText().toString().trim();
        String batch = rndBatch.getText().toString().trim();
        String phone = rndPhone.getText().toString().trim();
        String birthday = rndBirthday.getText().toString().trim();
        String reason = rndReason.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(studentID) || TextUtils.isEmpty(batch) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(birthday) || TextUtils.isEmpty(reason)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a data map
        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("studentID", studentID);
        data.put("batch", batch);
        data.put("phone", phone);
        data.put("birthday", birthday);
        data.put("reason", reason);

        // Save to Firebase
        String id = databaseReference.push().getKey();
        if (id != null) {
            databaseReference.child(id).setValue(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RnDRegeneration.this, "Registration submitted successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(RnDRegeneration.this, "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        rndBatch.setText("");
        rndPhone.setText("");
        rndBirthday.setText("");
        rndReason.setText("");
    }
}
