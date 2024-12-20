package com.example.revisiproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuAdmin extends AppCompatActivity {

    ImageView comp, sugges, regen, election, logout;
    TextView users;

    private static final String ADMIN_EMAIL = "hanumanisa0905@gmail.com";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        comp = findViewById(R.id.compsphere);
        sugges = findViewById(R.id.suggestion);
        regen = findViewById(R.id.regeneration);
        election = findViewById(R.id.election);
        logout = findViewById(R.id.logout);
        users = findViewById(R.id.userLogin);


        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String usernameKey = sharedPreferences.getString("username", null); // Mendapatkan username


        if (usernameKey != null) {
            if (usernameKey.equals("admin")) {

                users.setText("Welcome, hanumanisa0905");
            } else {

                users.setText("Welcome, " + usernameKey);
            }
        } else {
            users.setText("Welcome, Guest");
        }


        logout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(MenuAdmin.this, Login.class);
            startActivity(intent);
            finish();
        });


        comp.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CompsphereAdmin.class)));
        sugges.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SuggestionAdmin.class)));
        regen.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegenerationAdmin.class)));
        election.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ElectionAdmin.class)));
    }
}
