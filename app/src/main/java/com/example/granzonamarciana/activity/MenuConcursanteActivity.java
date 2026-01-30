package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;

public class MenuConcursanteActivity extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvWelcomeConcursante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contestant_menu);

        // Inicializar vistas
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcomeConcursante = findViewById(R.id.tvWelcomeConcursante);

        // Recuperar datos de sesión
        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Concursante");

        // Mostrar mensaje de bienvenida
        tvWelcomeConcursante.setText("¡Buena suerte, " + username + "!");

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("granZMUser", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                Intent intent = new Intent(MenuConcursanteActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}