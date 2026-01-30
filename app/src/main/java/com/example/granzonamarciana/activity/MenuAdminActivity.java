package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;

public class MenuAdminActivity extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvWelcomeAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // Inicializar vistas
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);

        // Recuperar datos de sesión
        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Administrador");

        // Mostrar mensaje de bienvenida
        tvWelcomeAdmin.setText("Panel de Administración: " + username);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("granZMUser", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                Intent intent = new Intent(MenuAdminActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}