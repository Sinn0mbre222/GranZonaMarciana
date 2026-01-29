package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.GalaAdapter;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.service.GalaService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GalasListActivity extends AppCompatActivity implements GalaAdapter.OnGalaClickListener {

    private GalaService galaService;
    private GalaAdapter adapter;
    private int currentEditionId = 1; // ID temporal de la edición
    private boolean isAdmin = true;   // Determinar si el usuario es admin (Persona D)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galas_list);

        galaService = new GalaService(getApplication());

        // Configurar RecyclerView
        RecyclerView rv = findViewById(R.id.rvGalas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GalaAdapter(this);
        rv.setAdapter(adapter);

        // Configurar botón flotante para añadir galas (Solo visible para Admin)
        FloatingActionButton fabAddGala = findViewById(R.id.fabAddGala);
        if (isAdmin) {
            fabAddGala.setVisibility(View.VISIBLE);
            fabAddGala.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateGalaActivity.class);
                intent.putExtra("EDITION_ID", currentEditionId);
                startActivity(intent);
            });
        } else {
            fabAddGala.setVisibility(View.GONE);
        }

        // Observar las galas de la edición
        galaService.getGalasByEdicion(currentEditionId).observe(this, galas -> {
            if (galas != null) {
                adapter.setGalas(galas);
            }
        });
    }

    @Override
    public void onGalaClick(Gala gala) {
        // Al pulsar una gala, se podría ir a ver las puntuaciones
        // Intent intent = new Intent(this, GalaScoresActivity.class);
        // intent.putExtra("GALA_ID", gala.getId());
        // startActivity(intent);
    }
}