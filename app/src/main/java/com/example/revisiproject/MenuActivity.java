package com.example.revisiproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity {

    ImageView comp, sugges, regen, election, profile, profile2, home, logout;
    TextView users;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        comp = findViewById(R.id.compsphere);
        sugges = findViewById(R.id.suggestion);
        regen = findViewById(R.id.regeneration);
        election = findViewById(R.id.election);
        profile = findViewById(R.id.profile1);
        profile2 = findViewById(R.id.naviProfile);
        home = findViewById(R.id.naviHome);
        logout = findViewById(R.id.logout);
        users = findViewById(R.id.userLogin);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String usernameKey = sharedPreferences.getString("fullname", null); // Nama user disimpan saat login

        if (usernameKey != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users");
            databaseReference.child(usernameKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Mengambil data "name" dari database
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null) {
                            users.setText("Welcome, " + name);
                        } else {
                            users.setText("Welcome, Guest");
                        }
                    } else {
                        Toast.makeText(MenuActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                        users.setText("Welcome, Guest");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(MenuActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            users.setText("Welcome, Guest");
        }

        logout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MenuActivity.this, Login.class);
            startActivity(intent);
            finish();
        });

        comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Compsphere.class));
            }
        });

        sugges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Suggestion.class));
            }
        });

        regen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Regeneration.class));
            }
        });

        election.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Election.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

        profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
            }
        });
    }
}
