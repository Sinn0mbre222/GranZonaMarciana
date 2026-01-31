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

        // 1. Inicializar Servicio
        galaService = new GalaService(getApplication());

        // 2. Obtener datos reales de sesión y navegación
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");

        // Obtenemos el ID de la edición desde el Intent (por defecto 1 si falla)
        currentEditionId = getIntent().getIntExtra("EDITION_ID", 1);

        // 3. Configurar ListView y Adapter
        ListView listView = findViewById(R.id.lvGalas);
        adapter = new GalaAdapter(this);
        listView.setAdapter(adapter);

        // 4. Configurar el clic en cada gala (Ver puntuaciones/detalles)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Gala seleccionada = (Gala) adapter.getItem(position);
            Intent intent = new Intent(this, GalaScoresActivity.class);
            intent.putExtra("GALA_ID", seleccionada.getId());
            startActivity(intent);
        });

        // 5. Control de visibilidad del botón de añadir (Solo Administrador)
        FloatingActionButton fabAddGala = findViewById(R.id.fabAddGala);

        if ("ADMINISTRADOR".equals(rol)) {
            fabAddGala.setVisibility(View.VISIBLE);
            fabAddGala.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateGalaActivity.class);
                intent.putExtra("EDITION_ID", currentEditionId);
                startActivity(intent);
            });
        } else {
            // Si es Concursante o Espectador, el botón desaparece
            fabAddGala.setVisibility(View.GONE);
        }

        // 6. Cargar las galas de la base de datos de forma reactiva
        galaService.getGalasByEdicion(currentEditionId).observe(this, lista -> {
            if (lista != null) {
                adapter.setGalas(lista);
                if (lista.isEmpty()) {
                    Toast.makeText(this, "No hay galas registradas en esta edición", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvBack = findViewById(R.id.tvBack); // Usa el ID que tengas en tu XML
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}