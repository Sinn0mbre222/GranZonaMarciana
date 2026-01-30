package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;

public class MenuConcursanteActivity extends AppCompatActivity {

    private Button btnLogout, btnMyProfile, btnApplyEdition, btnMyApplications,
            btnMyEditions, btnMyGalas, btnViewNews;
    private TextView tvWelcomeConcursante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contestant_menu);

        tvWelcomeConcursante = findViewById(R.id.tvWelcomeConcursante);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnApplyEdition = findViewById(R.id.btnApplyEdition);
        btnMyApplications = findViewById(R.id.btnMyApplications);
        btnMyEditions = findViewById(R.id.btnMyEditions);
        btnMyGalas = findViewById(R.id.btnMyGalas);
        btnViewNews = findViewById(R.id.btnViewNews);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Concursante");
        tvWelcomeConcursante.setText("¡Buena suerte, " + username + "!");

        // Navegación
        btnMyProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnApplyEdition.setOnClickListener(v -> startActivity(new Intent(this, ApplyEditionActivity.class)));
        btnMyApplications.setOnClickListener(v -> startActivity(new Intent(this, MyApplicationsActivity.class)));
        btnMyEditions.setOnClickListener(v -> startActivity(new Intent(this, EditionListActivity.class)));
        btnMyGalas.setOnClickListener(v -> startActivity(new Intent(this, GalasListActivity.class)));
        btnViewNews.setOnClickListener(v -> startActivity(new Intent(this, NewsListActivity.class)));

        btnLogout.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        getSharedPreferences("granZMUser", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}