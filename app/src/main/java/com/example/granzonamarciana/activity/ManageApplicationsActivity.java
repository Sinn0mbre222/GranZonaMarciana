package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.SolicitudAdapter;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;
import java.util.ArrayList;
import java.util.List;

public class ManageApplicationsActivity extends AppCompatActivity {

    private SolicitudService solicitudService;
    private EdicionService edicionService;
    private SolicitudAdapter adapter;
    private Spinner spinnerFilter;
    private List<Edicion> listaEdiciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_applications);

        // 1. Inicializar servicios
        solicitudService = new SolicitudService(getApplication());
        edicionService = new EdicionService(getApplication());

        // 2. Vincular vistas
        spinnerFilter = findViewById(R.id.spinnerFilterEdicion);
        ListView listView = findViewById(R.id.lvApplications);
        TextView tvBack = findViewById(R.id.tvBack);

        // 3. Configurar adaptador de la lista
        adapter = new SolicitudAdapter(this);
        listView.setAdapter(adapter);

        // 4. Cargar Ediciones para el filtro
        cargarFiltroEdiciones();

        // 5. Click en solicitud para revisar
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Solicitud seleccionada = (Solicitud) adapter.getItem(position);
            if (seleccionada != null) {
                Intent intent = new Intent(this, ApplicationReviewActivity.class);
                intent.putExtra("SOLICITUD_ID", seleccionada.getId());
                startActivity(intent);
            }
        });

        tvBack.setOnClickListener(v -> finish());
    }

    private void cargarFiltroEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                labels.add("Todas las solicitudes"); // Opción por defecto

                for (Edicion e : ediciones) {
                    labels.add("Edición #" + e.getId());
                }

                ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
                        this, R.layout.spinner_rol_item, labels);
                spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFilter.setAdapter(spinAdapter);

                spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            // Cargar todas
                            cargarTodasLasSolicitudes();
                        } else {
                            // Filtrar por la edición seleccionada (ajuste de índice -1 por el "Todas")
                            int idEdicion = listaEdiciones.get(position - 1).getId();
                            cargarSolicitudesPorEdicion(idEdicion);
                        }
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                cargarTodasLasSolicitudes(); // Si no hay ediciones, intentamos cargar todas
            }
        });
    }

    private void cargarTodasLasSolicitudes() {
        solicitudService.getAllSolicitudes().observe(this, lista -> {
            if (lista != null) {
                adapter.setSolicitudes(lista);
            }
        });
    }

    private void cargarSolicitudesPorEdicion(int edicionId) {
        solicitudService.getSolicitudesByEdicion(edicionId).observe(this, lista -> {
            if (lista != null) {
                adapter.setSolicitudes(lista);
            }
        });
    }
}