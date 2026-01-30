package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;

public class MenuEspectadorActivity extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvWelcomeEspectador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectator_menu);

        // Inicializar vistas
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcomeEspectador = findViewById(R.id.tvWelcomeEspectador);

        // Recuperar datos de sesión
        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Espectador");

        // Mostrar mensaje de bienvenida
        tvWelcomeEspectador.setText("Bienvenido a la zona, " + username);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("granZMUser", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                Intent intent = new Intent(MenuEspectadorActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}