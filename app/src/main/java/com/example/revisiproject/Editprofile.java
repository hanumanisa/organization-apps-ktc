package com.example.revisiproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Editprofile extends AppCompatActivity {

    private EditText etName, etEmail, etMajor, etstudentID, etPassword;
    private Button btnSave;
    private DatabaseReference database;
    private String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        // Inisialisasi Views
        etName = findViewById(R.id.EditName);
        etEmail = findViewById(R.id.EditEmail);
        etMajor = findViewById(R.id.EditMajor);
        etstudentID = findViewById(R.id.EditstudentID);
        etPassword = findViewById(R.id.EditPassword);
        btnSave = findViewById(R.id.buttonsave);

        // Ambil username pengguna yang login dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loggedInUser = sharedPreferences.getString("fullname", null);

        if (loggedInUser != null) {
            database = FirebaseDatabase.getInstance().getReference("users");

            // Ambil data pengguna dari Firebase dan tampilkan di EditText
            database.child(loggedInUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Ambil data dari Firebase
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String major = snapshot.child("major").getValue(String.class);
                        String studentID = snapshot.child("studentID").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);

                        // Tampilkan data di EditText
                        etName.setText(name != null ? name : "");
                        etEmail.setText(email != null ? email : "");
                        etMajor.setText(major != null ? major : "");
                        etstudentID.setText(studentID != null ? studentID : "");
                        etPassword.setText(password != null ? password : "");
                    } else {
                        Toast.makeText(Editprofile.this, "User data not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Editprofile.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Tombol Simpan
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateUserData();
                }
            });
        } else {
            Toast.makeText(this, "No user data found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData() {
        // Ambil input dari EditText
        String newName = etName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newMajor = etMajor.getText().toString().trim();
        String newStudentID = etstudentID.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newEmail) ||
                TextUtils.isEmpty(newMajor) || TextUtils.isEmpty(newStudentID) ||
                TextUtils.isEmpty(newPassword)) {
            Toast.makeText(Editprofile.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perbarui data di Firebase
        database.child(loggedInUser).child("name").setValue(newName);
        database.child(loggedInUser).child("email").setValue(newEmail);
        database.child(loggedInUser).child("major").setValue(newMajor);
        database.child(loggedInUser).child("studentID").setValue(newStudentID);
        database.child(loggedInUser).child("password").setValue(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Editprofile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        // Redirect ke halaman Profile
                        Intent intent = new Intent(Editprofile.this, Profile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Editprofile.this, "Failed to update profile!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}