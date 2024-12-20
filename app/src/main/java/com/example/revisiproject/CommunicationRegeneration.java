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

public class CommunicationRegeneration extends AppCompatActivity {

    private EditText comBatch, comPhone, comBirthday, comReason;
    private TextView comName, comStudentID;
    private Button cvComm, personalComm, submitComm;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_regeneration);

        // Initialize views
        comName = findViewById(R.id.comName);
        comStudentID = findViewById(R.id.comStudentID);
        comBatch = findViewById(R.id.comBatch);
        comPhone = findViewById(R.id.comPhone);
        comBirthday = findViewById(R.id.comBirthday);
        comReason = findViewById(R.id.comReason);
        cvComm = findViewById(R.id.cvComm);
        personalComm = findViewById(R.id.personalComm);
        submitComm = findViewById(R.id.submitComm);

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("COMMUNICATION Registrations");

        // Load user data
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        if (loggedInUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String studentID = snapshot.child("studentID").getValue(String.class);

                        if (comName != null && comStudentID != null) {
                            comName.setText(name != null ? name : "");
                            comStudentID.setText(studentID != null ? studentID : "");
                        }
                    } else {
                        Toast.makeText(CommunicationRegeneration.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CommunicationRegeneration.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }

        // Set click listeners
        cvComm.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1hUe8Wxn9Rqz45_oZIgPRbDZEs13upvfA?usp=sharing"));
        personalComm.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1tVlIwEu68tpiUJKKUP3oWtco3JkJn2vq?usp=drive_link"));
        comBirthday.setOnClickListener(view -> showDatePicker());
        submitComm.setOnClickListener(view -> submitRegistration());
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
            comBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void submitRegistration() {
        String name = comName.getText().toString().trim();
        String studentID = comStudentID.getText().toString().trim();
        String batch = comBatch.getText().toString().trim();
        String phone = comPhone.getText().toString().trim();
        String birthday = comBirthday.getText().toString().trim();
        String reason = comReason.getText().toString().trim();

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
                            Toast.makeText(CommunicationRegeneration.this, "Registration submitted successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(CommunicationRegeneration.this, "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        comBatch.setText("");
        comPhone.setText("");
        comBirthday.setText("");
        comReason.setText("");
    }
}
