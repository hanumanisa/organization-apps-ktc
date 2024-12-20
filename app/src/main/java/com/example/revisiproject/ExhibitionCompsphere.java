package com.example.revisiproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class ExhibitionCompsphere extends AppCompatActivity {

    ImageView heart, seribu, mockup, startup, desa, aphrodite;
    Button btnSeribu, btnHeart, btnMockup, btnStartup, btnDesa, btnAphrodite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exhibition_compsphere);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        heart = findViewById(R.id.heart);
        seribu = findViewById(R.id.seribu);
        mockup = findViewById(R.id.mockup);
        startup = findViewById(R.id.startup);
        desa = findViewById(R.id.desa);
        aphrodite = findViewById(R.id.aphrodite);

        btnSeribu = findViewById(R.id.btnSeribu);
        btnHeart = findViewById(R.id.btnHeart);
        btnMockup = findViewById(R.id.btnMockup);
        btnStartup = findViewById(R.id.btnStartup);
        btnDesa = findViewById(R.id.btnDesa);
        btnAphrodite = findViewById(R.id.btnAphrodite);


        btnDesa.setOnClickListener(v -> openUrl("https://barurejo.desa.id/"));
        btnMockup.setOnClickListener(v -> openUrl("https://www.mockupworld.co/"));
        btnStartup.setOnClickListener(v -> openUrl("https://www.tokopedia.com/"));
        btnAphrodite.setOnClickListener(v -> openUrl("https://oasisinet.com/"));
        btnHeart.setOnClickListener(v -> openUrl("https://hellosehat.com/"));
        btnSeribu.setOnClickListener(v -> openUrl("https://www.eventbrite.com/"));
    }


    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}

