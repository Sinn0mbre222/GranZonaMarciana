package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;

public class MainMenuActivity extends AppCompatActivity {

    private Button btnProfile, btnEditions, btnNews, btnGalas, btnParticipants, btnLogout;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnProfile = findViewById(R.id.btnProfile);
        btnEditions = findViewById(R.id.btnEditions);
        btnNews = findViewById(R.id.btnNews);
        btnGalas = findViewById(R.id.btnGalas);
        btnParticipants = findViewById(R.id.btnParticipants);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String nombreUsuario = sharedPreferences.getString("username", "Invitado");
        int userId = sharedPreferences.getInt("id", -1); // Si no hay ID, asumimos -1 (Invitado)

        tvWelcome.setText("Bienvenido/a, " + nombreUsuario);


        btnProfile.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(MainMenuActivity.this, "Función no disponible para invitados. Regístrate para acceder.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MainMenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnEditions.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, EditionListActivity.class);
            startActivity(intent);
        });

        btnNews.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, NewsListActivity.class);
            startActivity(intent);
        });

        btnGalas.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, GalasListActivity.class);
            startActivity(intent);
        });

        btnParticipants.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ParticipantsListActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();

            Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}