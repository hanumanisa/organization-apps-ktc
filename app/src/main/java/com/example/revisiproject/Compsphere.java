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

public class Compsphere extends AppCompatActivity {


    private ImageView btnBack, imageViewSeminar, imageViewExhibition, imageViewAwarding, imageViewCompetititon, naviHome, naviProfile;
    private TextView textViewSeminarLogo, textViewExhibitionLogo, textViewAwardingLogo, textViewCompetititonLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compsphere);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        naviHome = findViewById(R.id.naviHome);
        naviProfile = findViewById(R.id.naviProfile);
        btnBack = findViewById(R.id.btnBack);
        imageViewSeminar = findViewById(R.id.seminarComp);
        textViewSeminarLogo = findViewById(R.id.txtSeminar);

        imageViewCompetititon = findViewById(R.id.competitionComp);
        textViewCompetititonLogo = findViewById(R.id.txtCompetition);

        imageViewExhibition = findViewById(R.id.exhibitionComp);
        textViewExhibitionLogo = findViewById(R.id.txtExhibition);

        imageViewAwarding = findViewById(R.id.awardingComp);
        textViewAwardingLogo = findViewById(R.id.txtAwarding);


        btnBack.setOnClickListener(view -> {
            Intent openAbout = new Intent(Compsphere.this, MenuActivity.class);
            startActivity(openAbout);
        });

        naviHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(Compsphere.this, MenuActivity.class);
                startActivity(profileIntent);
            }
        });

        naviProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(Compsphere.this, Profile.class);
                startActivity(profileIntent);
            }
        });

        View.OnClickListener redirectToSeminar = v -> {
            Intent intent = new Intent(Compsphere.this, CompsphereSeminar.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToExhibition = v -> {
            Intent intent = new Intent(Compsphere.this, ExhibitionCompsphere.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToAwarding = v -> {
            Intent intent = new Intent(Compsphere.this, AwardingCompsphere.class);
            startActivity(intent);
        };

        View.OnClickListener redirectToCompetition = v -> {
            Intent intent = new Intent(Compsphere.this, CompetititonCompsphere.class);
            startActivity(intent);
        };

        imageViewSeminar.setOnClickListener(redirectToSeminar);
        textViewSeminarLogo.setOnClickListener(redirectToSeminar);

        imageViewExhibition.setOnClickListener(redirectToExhibition);
        textViewExhibitionLogo.setOnClickListener(redirectToExhibition);

        imageViewAwarding.setOnClickListener(redirectToAwarding);
        textViewAwardingLogo.setOnClickListener(redirectToAwarding);

        imageViewCompetititon.setOnClickListener(redirectToCompetition);
        textViewCompetititonLogo.setOnClickListener(redirectToCompetition);
    }

}