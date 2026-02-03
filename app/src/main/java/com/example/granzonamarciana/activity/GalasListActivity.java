package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.GalaAdapter;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.GalaService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GalasListActivity extends AppCompatActivity {

    private GalaService galaService;
    private EdicionService edicionService;
    private GalaAdapter adapter;
    private Spinner spinnerEdiciones;
    private List<Edicion> listaEdiciones = new ArrayList<>();
    private int currentEditionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galas_list);

        // 1. Inicializar Servicios
        galaService = new GalaService(getApplication());
        edicionService = new EdicionService(this);

        // 2. Vincular Vistas
        spinnerEdiciones = findViewById(R.id.spinnerEdicionesGalas);
        ListView listView = findViewById(R.id.lvGalas);
        TextView tvBack = findViewById(R.id.tvBack);
        FloatingActionButton fabAddGala = findViewById(R.id.fabAddGala);

        // 3. Configurar Listview
        adapter = new GalaAdapter(this);
        listView.setAdapter(adapter);

        // 4. Cargar Ediciones (Paso crítico)
        cargarEdiciones();

        // 5. Configurar Eventos
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Gala seleccionada = (Gala) adapter.getItem(position);
            if (seleccionada != null) {
                Intent intent = new Intent(this, GalaScoresActivity.class);
                intent.putExtra("GALA_ID", seleccionada.getId());
                intent.putExtra("EDITION_ID", currentEditionId);
                startActivity(intent);
            }
        });

        // Control de Admin
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");
        if ("ADMINISTRADOR".equals(rol)) {
            fabAddGala.setVisibility(View.VISIBLE);
            fabAddGala.setOnClickListener(v -> {
                if (currentEditionId != -1) {
                    Intent intent = new Intent(this, CreateGalaActivity.class);
                    intent.putExtra("EDITION_ID", currentEditionId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Selecciona una edición primero", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fabAddGala.setVisibility(View.GONE);
        }

        tvBack.setOnClickListener(v -> finish());
    }

    private void cargarEdiciones() {
        // Observamos el LiveData del servicio
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                Log.d("DEBUG_GALAS", "Ediciones cargadas: " + ediciones.size());
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();

                for (Edicion e : ediciones) {
                    labels.add("Edición #" + e.getId());
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        this, R.layout.spinner_rol_item, labels);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerEdiciones.setAdapter(spinnerAdapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentEditionId = listaEdiciones.get(position).getId();
                        cargarGalasPorEdicion(currentEditionId);
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                Toast.makeText(this, "No hay ediciones disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarGalasPorEdicion(int editionId) {
        galaService.getGalasByEdicion(editionId).observe(this, lista -> {
            if (lista != null) {
                adapter.setGalas(lista);
                adapter.notifyDataSetChanged(); // Forzar refresco
            }
        });
    }
}