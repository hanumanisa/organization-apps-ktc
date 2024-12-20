package com.example.revisiproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Regeneration extends AppCompatActivity {

    private ImageView btnBack, naviHome, naviProfile, bod, ans, rnd, comm, rm, mtdd;
    private TextView txtBod, ansText, rndText, commText, rmText, mtddText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_regeneration);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        naviHome = findViewById(R.id.naviHome);
        naviProfile = findViewById(R.id.naviProfile);
        bod = findViewById(R.id.bod);
        txtBod = findViewById(R.id.txtBod);
        ans = findViewById(R.id.ans);
        ansText = findViewById(R.id.txtans);
        rnd = findViewById(R.id.rnd);
        rndText = findViewById(R.id.rndText);
        comm = findViewById(R.id.comm);
        commText = findViewById(R.id.commText);
        rm = findViewById(R.id.rm);
        rmText = findViewById(R.id.rmText);
        mtdd = findViewById(R.id.mtdd);
        mtddText = findViewById(R.id.mtddText);
        btnBack = findViewById(R.id.btnBack);


        btnBack.setOnClickListener(view -> {
            Intent openAbout = new Intent(Regeneration.this, MenuActivity.class);
            startActivity(openAbout);
        });


        naviHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(Regeneration.this, MenuActivity.class);
                startActivity(profileIntent);
            }
        });

        naviProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(Regeneration.this, Profile.class);
                startActivity(profileIntent);
            }
        });

        View.OnClickListener redirectToBod = v -> {
            Intent intent = new Intent(Regeneration.this, BodRegeneration.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToRnd = v -> {
            Intent intent = new Intent(Regeneration.this, RnDRegeneration.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToAns = v -> {
            Intent intent = new Intent(Regeneration.this, AnsRegeneration.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToComm = v -> {
            Intent intent = new Intent(Regeneration.this, CommunicationRegeneration.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToRm = v -> {
            Intent intent = new Intent(Regeneration.this, RmRegister.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToMtdd = v -> {
            Intent intent = new Intent(Regeneration.this, MTDDRegeneration.class);
            startActivity(intent);
        };

        bod.setOnClickListener(redirectToBod);
        txtBod.setOnClickListener(redirectToBod);
        rnd.setOnClickListener(redirectToRnd);
        rndText.setOnClickListener(redirectToRnd);
        ans.setOnClickListener(redirectToAns);
        ansText.setOnClickListener(redirectToAns);
        comm.setOnClickListener(redirectToComm);
        commText.setOnClickListener(redirectToComm);
        rm.setOnClickListener(redirectToRm);
        rmText.setOnClickListener(redirectToRm);
        mtdd.setOnClickListener(redirectToMtdd);
        mtddText.setOnClickListener(redirectToMtdd);

    }
}