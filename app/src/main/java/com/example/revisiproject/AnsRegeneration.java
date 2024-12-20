package com.example.revisiproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

public class AnsRegeneration extends AppCompatActivity {

    private EditText ansBatch, ansPhone, ansBirthday, ansReason;
    private TextView ansName, ansStudentID;
    private Button btnCvans, btnPsAns, btnSubmitans;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ans_regeneration);

        // Initialize views
        ansName = findViewById(R.id.ansName);
        ansStudentID = findViewById(R.id.ansStudentID);
        ansBatch = findViewById(R.id.ansBatch);
        ansPhone = findViewById(R.id.ansPhone);
        ansBirthday = findViewById(R.id.ansBirthday);
        ansReason = findViewById(R.id.ansReason);
        btnCvans = findViewById(R.id.btnCvAns);
        btnPsAns = findViewById(R.id.btnPsAns);
        btnSubmitans = findViewById(R.id.btnSubmitans);

        databaseReference = FirebaseDatabase.getInstance().getReference("ANS Registrations");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        if (loggedInUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String studentID = snapshot.child("studentID").getValue(String.class);

                        if (ansName != null && ansStudentID != null) {
                            ansName.setText(name != null ? name : "");
                            ansStudentID.setText(studentID != null ? studentID : "");
                        }
                    } else {
                        Toast.makeText(AnsRegeneration.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(AnsRegeneration.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }

        // Set listeners
        btnCvans.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1pb4_aAfjp0rBeRwHTytnuF8c6E9XajX6?usp=sharing"));
        btnPsAns.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1-SvC0MO-tZwSX1lKUmYE1SUUORGolTSA?usp=drive_link"));
        ansBirthday.setOnClickListener(view -> showDatePicker());
        btnSubmitans.setOnClickListener(view -> submitRegistration());
    }

    private void openUploadLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            ansBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void submitRegistration() {
        String name = ansName.getText().toString().trim();
        String studentID = ansStudentID.getText().toString().trim();
        String batch = ansBatch.getText().toString().trim();
        String phone = ansPhone.getText().toString().trim();
        String birthday = ansBirthday.getText().toString().trim();
        String reason = ansReason.getText().toString().trim();

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
                            Toast.makeText(AnsRegeneration.this, "Registration submitted successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(AnsRegeneration.this, "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        ansBatch.setText("");
        ansPhone.setText("");
        ansBirthday.setText("");
        ansReason.setText("");
    }
}
