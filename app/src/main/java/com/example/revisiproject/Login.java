package com.example.revisiproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterRedirectText;

    private DatabaseReference database;

    // Admin credentials
    private static final String ADMIN_EMAIL = "hanumanisa0905@gmail.com";
    private static final String ADMIN_PASSWORD = "Hanumanisarahma955955";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.loginEmail);
        etPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterRedirectText = findViewById(R.id.registerRedirectText);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email or Password cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Logika untuk admin
                    if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                        Toast.makeText(getApplicationContext(), "Admin Login Successful", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_role", "admin");
                        editor.putString("username", "admin"); //
                        editor.apply();


                        Intent adminIntent = new Intent(Login.this, MenuAdmin.class);
                        startActivity(adminIntent);
                        finish();
                    } else {
                        // Logika untuk user biasa
                        database = FirebaseDatabase.getInstance().getReference("users");

                        // Cari berdasarkan email menggunakan orderByChild
                        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        String dbPassword = userSnapshot.child("password").getValue(String.class);

                                        if (dbPassword != null && dbPassword.equals(password)) {
                                            Toast.makeText(getApplicationContext(), "User Login Successful", Toast.LENGTH_SHORT).show();


                                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("user_role", "user");
                                            editor.putString("fullname", userSnapshot.getKey()); // Simpan username
                                            editor.apply();


                                            Intent userIntent = new Intent(Login.this, MenuActivity.class);
                                            startActivity(userIntent);
                                            finish();
                                            return;
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });


        tvRegisterRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}
