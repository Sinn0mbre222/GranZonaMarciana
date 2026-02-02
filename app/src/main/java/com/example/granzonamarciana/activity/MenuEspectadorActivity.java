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

        // 1. Inicialización de vistas
        tvWelcomeEspectador = findViewById(R.id.tvWelcomeEspectador);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnRateParticipants = findViewById(R.id.btnRateParticipants);
        btnAvailableRatings = findViewById(R.id.btnAvailableRatings);
        btnMyRatings = findViewById(R.id.btnMyRatings);
        btnViewEditions = findViewById(R.id.btnViewEditions);
        btnViewNews = findViewById(R.id.btnViewNews);
        btnViewParticipants = findViewById(R.id.btnViewParticipants);
        btnLogout = findViewById(R.id.btnLogout);

        // 2. Cargar datos de sesión
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        tvWelcomeEspectador.setText("Bienvenido, " + prefs.getString("username", "Espectador"));

        // 3. Configuración de Listeners (Navegación)

        // Perfil personal
        btnMyProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // VOTAR: Va a la nueva actividad de selección de Gala/Edición
        btnRateParticipants.setOnClickListener(v -> {
            startActivity(new Intent(this, RateSelectionActivity.class));
        });

        // CONSULTAR PARTICIPANTES: Va a la lista de consulta (modo invitado/público)
        btnViewParticipants.setOnClickListener(v -> {
            startActivity(new Intent(this, ParticipantsListActivity.class));
        });

        // Ver resultados de galas
        btnAvailableRatings.setOnClickListener(v -> startActivity(new Intent(this, GalasListActivity.class)));

        // Historial de votos realizados por el espectador
        btnMyRatings.setOnClickListener(v -> startActivity(new Intent(this, RatingHistoryActivity.class)));

        // Listado de ediciones
        btnViewEditions.setOnClickListener(v -> startActivity(new Intent(this, EditionListActivity.class)));

        // Listado de noticias
        btnViewNews.setOnClickListener(v -> startActivity(new Intent(this, NewsListActivity.class)));

        // Cerrar sesión
        btnLogout.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        getSharedPreferences("granZMUser", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}