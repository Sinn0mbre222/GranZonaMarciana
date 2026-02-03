package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;

public class MenuEspectadorActivity extends AppCompatActivity {

    private Button btnLogout, btnMyProfile, btnRateParticipants, btnAvailableRatings,
            btnMyRatings, btnViewEditions, btnViewNews, btnViewParticipants;
    private TextView tvWelcomeEspectador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectator_menu);

        tvWelcomeEspectador = findViewById(R.id.tvWelcomeEspectador);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnRateParticipants = findViewById(R.id.btnRateParticipants);
        btnAvailableRatings = findViewById(R.id.btnAvailableRatings);
        btnMyRatings = findViewById(R.id.btnMyRatings);
        btnViewEditions = findViewById(R.id.btnViewEditions);
        btnViewNews = findViewById(R.id.btnViewNews);
        btnViewParticipants = findViewById(R.id.btnViewParticipants);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Espectador");
        tvWelcomeEspectador.setText("Bienvenido/a, " + username);

        // Navegación
        btnMyProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnRateParticipants.setOnClickListener(v -> {
            // En lugar de ir directo a votar (que daría error), vamos a la lista para elegir a quién
            Intent intent = new Intent(this, ParticipantsListActivity.class);
            startActivity(intent);
        });
        btnAvailableRatings.setOnClickListener(v -> startActivity(new Intent(this, GalasListActivity.class)));
        btnMyRatings.setOnClickListener(v -> startActivity(new Intent(this, RatingHistoryActivity.class)));
        btnViewEditions.setOnClickListener(v -> startActivity(new Intent(this, EditionListActivity.class)));
        btnViewNews.setOnClickListener(v -> startActivity(new Intent(this, NewsListActivity.class)));
        btnViewParticipants.setOnClickListener(v -> startActivity(new Intent(this, ParticipantsListActivity.class)));

        btnLogout.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        getSharedPreferences("granZMUser", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}