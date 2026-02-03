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

import androidx.appcompat.app.AlertDialog;
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
    private String rolUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galas_list);

        // 1. Inicializar Servicios
        galaService = new GalaService(getApplication());
        edicionService = new EdicionService(this);

        // 2. Recuperar el Rol de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        rolUsuario = prefs.getString("rol", "INVITADO");

        // 3. Vincular Vistas
        initViews();

        // 4. Configurar Adaptador
        ListView listView = findViewById(R.id.lvGalas);
        adapter = new GalaAdapter(this);
        listView.setAdapter(adapter);

        // 5. Cargar Datos Iniciales (Ediciones)
        cargarEdiciones();

        // 6. Configurar Eventos de Clic (Simple y Largo para eliminar)
        setupListListeners(listView);

        // 7. Control de visibilidad del botón flotante para Admin
        setupAdminFab();

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdicionesGalas);
    }

    private void setupAdminFab() {
        FloatingActionButton fabAddGala = findViewById(R.id.fabAddGala);

        if ("ADMINISTRADOR".equalsIgnoreCase(rolUsuario)) {
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
    }

    private void setupListListeners(ListView listView) {
        // CLIC CORTO: Ver puntuaciones de la gala
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Gala seleccionada = (Gala) adapter.getItem(position);
            if (seleccionada != null) {
                Intent intent = new Intent(this, GalaScoresActivity.class);
                intent.putExtra("GALA_ID", seleccionada.getId());
                intent.putExtra("EDITION_ID", currentEditionId);
                startActivity(intent);
            }
        });

        // CLIC LARGO: Eliminar gala (Solo si es Admin)
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if ("ADMINISTRADOR".equalsIgnoreCase(rolUsuario)) {
                Gala galaAEliminar = (Gala) adapter.getItem(position);
                if (galaAEliminar != null) {
                    confirmarEliminacion(galaAEliminar);
                }
            }
            return true;
        });
    }

    private void confirmarEliminacion(Gala gala) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Gala")
                .setMessage("¿Estás seguro de eliminar esta gala?")
                .setPositiveButton("ELIMINAR", (dialog, which) -> {
                    galaService.eliminar(gala);
                    Toast.makeText(this, "Gala eliminada correctamente", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();

                for (Edicion e : ediciones) {
                    labels.add("Edición #" + e.getId());
                }

                // CORREGIDO: Se usa el Layout R.layout.spinner_rol_item
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        this,
                        R.layout.spinner_rol_item,
                        labels
                );

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
            }
        });
    }

    private void cargarGalasPorEdicion(int editionId) {
        // Al usar LiveData, el adaptador recibirá la lista actualizada automáticamente
        galaService.getGalasByEdicion(editionId).observe(this, lista -> {
            if (lista != null) {
                adapter.setGalas(lista);
                // No es estrictamente necesario notifyDataSetChanged() si setGalas ya lo hace,
                // pero asegura el refresco visual inmediato.
                adapter.notifyDataSetChanged();
            }
        });
    }
}