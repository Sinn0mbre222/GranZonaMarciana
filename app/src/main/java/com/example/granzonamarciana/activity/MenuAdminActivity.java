package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;

public class MenuAdminActivity extends AppCompatActivity {

    // Declaración de los botones para las diferentes funcionalidades de gestión
    private Button btnLogout, btnManageUsers, btnManageNews, btnManageEditions,
            btnManageApplications, btnManageGalas, btnCreateAdmin, btnMyProfile;
    private TextView tvWelcomeAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // Vinculación de los componentes del layout con los objetos Java
        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageNews = findViewById(R.id.btnManageNews);
        btnManageEditions = findViewById(R.id.btnManageEditions);
        btnManageApplications = findViewById(R.id.btnManageApplications);
        btnManageGalas = findViewById(R.id.btnManageGalas);
        btnCreateAdmin = findViewById(R.id.btnCreateAdmin);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Recuperamos las preferencias compartidas para personalizar el saludo con el nombre del Admin
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        tvWelcomeAdmin.setText("Panel Admin: " + prefs.getString("username", "Admin"));

        // Configuración de navegación para la gestión de usuarios registrados
        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));

        // Navegación hacia la lista de noticias (donde el admin puede crear/editar)
        btnManageNews.setOnClickListener(v -> startActivity(new Intent(this, NewsListActivity.class)));

        // Navegación hacia la creación y gestión de ediciones del programa
        btnManageEditions.setOnClickListener(v -> startActivity(new Intent(this, CreateEditionActivity.class)));

        // Navegación hacia la gestión de solicitudes de concursantes pendientes
        btnManageApplications.setOnClickListener(v -> startActivity(new Intent(this, ManageApplicationsActivity.class)));

        // Navegación hacia la gestión de galas y sus resultados
        btnManageGalas.setOnClickListener(v -> startActivity(new Intent(this, GalasListActivity.class)));

        // Navegación hacia el perfil propio del administrador logueado
        btnMyProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // Configuración especial para crear un nuevo administrador
        btnCreateAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAdminActivity.class);
            startActivity(intent);
        });

        // Lógica de cierre de sesión
        btnLogout.setOnClickListener(v -> {
            // Limpiamos todos los datos guardados en SharedPreferences para cerrar la sesión de forma segura
            getSharedPreferences("granZMUser", MODE_PRIVATE).edit().clear().apply();
            // Redirigimos al usuario a la pantalla de Login
            startActivity(new Intent(this, LoginActivity.class));
            // Cerramos esta actividad para que el usuario no pueda volver al menú con el botón "atrás"
            finish();
        });
    }
}