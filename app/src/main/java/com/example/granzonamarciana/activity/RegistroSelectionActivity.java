package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;

public class RegistroSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_selection);

        Button btnEspec = findViewById(R.id.btnSelectionEspectador);
        Button btnConcu = findViewById(R.id.btnSelectionConcursante);
        TextView tvBack = findViewById(R.id.tvBackFromSelection);

        btnEspec.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroEspectadorActivity.class));
        });

        btnConcu.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroConcursanteActivity.class));
        });

        tvBack.setOnClickListener(v -> finish());
    }
}