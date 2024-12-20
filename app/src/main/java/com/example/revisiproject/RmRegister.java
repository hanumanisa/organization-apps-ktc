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

public class RmRegister extends AppCompatActivity {

    private EditText rmBatch, rmPhone, rmBirthday, rmReason;
    private TextView rmName, rmStudentID;
    private Button btnCvUpload, btnPersonalUpload, submitRegis;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rm_register);

        databaseReference = FirebaseDatabase.getInstance().getReference("RM Registrations");

        rmName = findViewById(R.id.RmName);
        rmStudentID = findViewById(R.id.RmStudentID);
        rmBatch = findViewById(R.id.RmBatch);
        rmPhone = findViewById(R.id.RmPhone);
        rmBirthday = findViewById(R.id.RmBirthday);
        rmReason = findViewById(R.id.RmReason);
        btnCvUpload = findViewById(R.id.btncvUP);
        btnPersonalUpload = findViewById(R.id.btnpersonalUP);
        submitRegis = findViewById(R.id.submit_regis);

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

                        rmName.setText(name != null ? name : "");
                        rmStudentID.setText(studentID != null ? studentID : "");
                    } else {
                        Toast.makeText(RmRegister.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RmRegister.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }

        btnCvUpload.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1kXhV0rP_t9RFmjHuOmogJ2s2ROns_gge?usp=drive_link"));

        btnPersonalUpload.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1eud_kcDdfc7ws3rHbz9BeZkgsZCTbt5v?usp=drive_link"));

        rmBirthday.setOnClickListener(view -> showDatePicker());

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
            rmBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void submitRegistration() {
        String name = rmName.getText().toString().trim();
        String studentID = rmStudentID.getText().toString().trim();
        String batch = rmBatch.getText().toString().trim();
        String phone = rmPhone.getText().toString().trim();
        String birthday = rmBirthday.getText().toString().trim();
        String reason = rmReason.getText().toString().trim();

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
                            Toast.makeText(RmRegister.this, "Registration submitted successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(RmRegister.this, "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        rmBatch.setText("");
        rmPhone.setText("");
        rmBirthday.setText("");
        rmReason.setText("");
    }
}
