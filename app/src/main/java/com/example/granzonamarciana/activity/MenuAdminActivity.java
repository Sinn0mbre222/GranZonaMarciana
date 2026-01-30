package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;

public class MenuAdminActivity extends AppCompatActivity {

    private Button btnLogout, btnManageUsers, btnManageNews, btnManageEditions,
            btnManageApplications, btnManageGalas, btnCreateAdmin, btnMyProfile;
    private TextView tvWelcomeAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // Inicializar vistas
        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageNews = findViewById(R.id.btnManageNews);
        btnManageEditions = findViewById(R.id.btnManageEditions);
        btnManageApplications = findViewById(R.id.btnManageApplications);
        btnManageGalas = findViewById(R.id.btnManageGalas);
        btnCreateAdmin = findViewById(R.id.btnCreateAdmin);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Recuperar datos de sesión
        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Administrador");
        tvWelcomeAdmin.setText("Panel de Administración: " + username);

        // Navegación
        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        btnManageNews.setOnClickListener(v -> startActivity(new Intent(this, NewsListActivity.class)));
        btnManageEditions.setOnClickListener(v -> startActivity(new Intent(this, EditionListActivity.class)));
        btnManageApplications.setOnClickListener(v -> startActivity(new Intent(this, ManageApplicationsActivity.class)));
        btnManageGalas.setOnClickListener(v -> startActivity(new Intent(this, GalasListActivity.class)));

        btnCreateAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            intent.putExtra("isAdminCreation", true);
            startActivity(intent);
        });

        btnMyProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        btnLogout.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        getSharedPreferences("granZMUser", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}