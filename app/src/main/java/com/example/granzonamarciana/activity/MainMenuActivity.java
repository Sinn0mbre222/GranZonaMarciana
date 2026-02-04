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

        // Inicializar Vistas
        tvWelcome = findViewById(R.id.tvWelcome);
        btnProfile = findViewById(R.id.btnProfile);
        btnEditions = findViewById(R.id.btnEditions);
        btnNews = findViewById(R.id.btnNews);
        btnGalas = findViewById(R.id.btnGalas);
        btnParticipants = findViewById(R.id.btnParticipants);
        btnLogout = findViewById(R.id.btnLogout);

        // Cargar Sesión
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);

        //Se recoge el nombre del usuario del que esté logueado, en este caso va a ser el usuasrio invitado
        String nombreUsuario = prefs.getString("username", "Invitado");
        int userId = prefs.getInt("id", -1);

        tvWelcome.setText("Bienvenido/a, " + nombreUsuario);

        // RESTRICCIÓN: El invitado no puede ver Galas (puntuaciones)
        if (userId == -1) {
            btnGalas.setVisibility(View.GONE);
        }

        // --- LISTENERS ---

        // El invitado no tiene perfil propio, le avisamos
        btnProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Acceso solo para usuarios registrados", Toast.LENGTH_SHORT).show();
        });

        // Ediciones (Consulta pública)
        btnEditions.setOnClickListener(v -> {
            startActivity(new Intent(this, EditionListActivity.class));
        });

        // Noticias (Consulta pública)
        btnNews.setOnClickListener(v -> {
            startActivity(new Intent(this, NewsListActivity.class));
        });

        // Participantes (Consulta pública -> ParticipantsListActivity)
        btnParticipants.setOnClickListener(v -> {
            startActivity(new Intent(this, ParticipantsListActivity.class));
        });

        // Salir: Borra todo y vuelve al Login
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            // Esto limpia la pila de actividades para que no pueda volver atrás
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}