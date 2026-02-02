package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.GalaAdapter;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.service.GalaService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GalasListActivity extends AppCompatActivity {

    private GalaService galaService;
    private GalaAdapter adapter;
    private int currentEditionId;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galas_list);

        galaService = new GalaService(getApplication());
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");
        currentEditionId = getIntent().getIntExtra("EDITION_ID", 1);

        ListView listView = findViewById(R.id.lvGalas);
        adapter = new GalaAdapter(this);
        listView.setAdapter(adapter);

        // CONFIGURACIÓN CLAVE: Pasar ambos IDs para que GalaScoresActivity funcione por gala
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Gala seleccionada = (Gala) adapter.getItem(position);
            Intent intent = new Intent(this, GalaScoresActivity.class);
            intent.putExtra("GALA_ID", seleccionada.getId());
            intent.putExtra("EDITION_ID", currentEditionId);
            startActivity(intent);
        });

        FloatingActionButton fabAddGala = findViewById(R.id.fabAddGala);
        if ("ADMINISTRADOR".equals(rol)) {
            fabAddGala.setVisibility(View.VISIBLE);
            fabAddGala.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateGalaActivity.class);
                intent.putExtra("EDITION_ID", currentEditionId);
                startActivity(intent);
            });
        } else {
            fabAddGala.setVisibility(View.GONE);
        }

        galaService.getGalasByEdicion(currentEditionId).observe(this, lista -> {
            if (lista != null) {
                adapter.setGalas(lista);
            }
        });

        tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> finish());
    }
}