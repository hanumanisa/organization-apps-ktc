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

public class MTDDRegeneration extends AppCompatActivity {

    private EditText mtddBatch, mtddPhone, mtddBirthday, mtddReason;
    private TextView mtddName, mtddStudentID;
    private Button cvMtdd, personalMtdd, submitMtdd;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtddregeneration);

        databaseReference = FirebaseDatabase.getInstance().getReference("MTDD Registrations");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("fullname", null);

        mtddName = findViewById(R.id.mtddName);
        mtddStudentID = findViewById(R.id.mtddStudentID);
        mtddBatch = findViewById(R.id.mtddBatch);
        mtddPhone = findViewById(R.id.mtddPhone);
        mtddBirthday = findViewById(R.id.mtddBirthday);
        mtddReason = findViewById(R.id.mtddReason);

        cvMtdd = findViewById(R.id.cvMtdd);
        personalMtdd = findViewById(R.id.personalMtdd);
        submitMtdd = findViewById(R.id.submitMtdd);

        if (loggedInUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(loggedInUser);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String studentID = snapshot.child("studentID").getValue(String.class);

                        if (mtddName != null && mtddStudentID != null) {
                            mtddName.setText(name != null ? name : "");
                            mtddStudentID.setText(studentID != null ? studentID : "");
                        }
                    } else {
                        Toast.makeText(MTDDRegeneration.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MTDDRegeneration.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }

        cvMtdd.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1m-2lLV_tISYeHn3l7sTawcJkxb3K3sEt?usp=drive_link"));
        personalMtdd.setOnClickListener(view -> openUploadLink("https://drive.google.com/drive/folders/1iF61Xc1ZWh0lCNCiZW5wmk4KAK2ON4hl?usp=drive_link"));
        mtddBirthday.setOnClickListener(view -> showDatePicker());
        submitMtdd.setOnClickListener(view -> submitRegistration());
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
            mtddBirthday.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void submitRegistration() {
        String name = mtddName.getText().toString().trim();
        String studentID = mtddStudentID.getText().toString().trim();
        String batch = mtddBatch.getText().toString().trim();
        String phone = mtddPhone.getText().toString().trim();
        String birthday = mtddBirthday.getText().toString().trim();
        String reason = mtddReason.getText().toString().trim();

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
                            Toast.makeText(MTDDRegeneration.this, "Registration submitted successfully", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(MTDDRegeneration.this, "Submission failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        mtddName.setText("");
        mtddStudentID.setText("");
        mtddBatch.setText("");
        mtddPhone.setText("");
        mtddBirthday.setText("");
        mtddReason.setText("");
    }
}
