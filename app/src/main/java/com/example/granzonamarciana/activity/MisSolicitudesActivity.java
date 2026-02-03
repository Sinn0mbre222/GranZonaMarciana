package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.SolicitudAdapter;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;

import java.util.ArrayList;
import java.util.List;

public class MisSolicitudesActivity extends AppCompatActivity {

    private SolicitudService solicitudService;
    private EdicionService edicionService;

    private SolicitudAdapter adapter;
    private int currentUserId;

    private ListView listView;
    private Spinner spFilter;
    private TextView tvEmpty;
    private TextView tvBack;

    // CAMBIO: La lista ahora debe ser del POJO para ser compatible con el adaptador
    private List<SolicitudConConcursante> todasMisSolicitudes = new ArrayList<>();
    private List<Edicion> listaEdiciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        solicitudService = new SolicitudService(getApplication());
        edicionService = new EdicionService(getApplication());

        initViews();
        setupList();
        setupFilterSpinner();

        cargarDatos();
    }

    private void initViews() {
        listView = findViewById(R.id.lvApplications);
        spFilter = findViewById(R.id.spEdicionFilter);
        tvEmpty = findViewById(R.id.tvEmptyList);
        tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> finish());
    }

    private void setupList() {
        adapter = new SolicitudAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // CAMBIO: El item ahora es un wrapper SolicitudConConcursante
            SolicitudConConcursante seleccionada = (SolicitudConConcursante) adapter.getItem(position);
            if (seleccionada != null && seleccionada.solicitud != null) {
                Intent intent = new Intent(this, ApplicationDetailActivity.class);
                intent.putExtra("SOLICITUD_ID", seleccionada.solicitud.getId());
                startActivity(intent);
            }
        });
    }

    private void setupFilterSpinner() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null) {
                listaEdiciones = ediciones;
                List<String> opciones = new ArrayList<>();
                opciones.add("Todas las ediciones");

                for (Edicion e : ediciones) {
                    opciones.add("Edición #" + e.getId() + " (" + e.getFechaInicio().getYear() + ")");
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        this, R.layout.spinner_rol_item, opciones);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFilter.setAdapter(spinnerAdapter);

                spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filtrarLista(position);
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void cargarDatos() {
        // IMPORTANTE: Asegúrate de que getMisSolicitudes en el Service
        // ahora devuelva LiveData<List<SolicitudConConcursante>>
        solicitudService.getMisSolicitudes(currentUserId).observe(this, lista -> {
            if (lista != null) {
                todasMisSolicitudes = lista;
                filtrarLista(spFilter.getSelectedItemPosition());
            }
        });
    }

    private void filtrarLista(int spinnerPosition) {
        if (todasMisSolicitudes == null) return;
        if (spinnerPosition < 0) spinnerPosition = 0;

        List<SolicitudConConcursante> listaFiltrada = new ArrayList<>();

        if (spinnerPosition == 0) {
            listaFiltrada.addAll(todasMisSolicitudes);
        } else {
            int edicionIdSeleccionada = listaEdiciones.get(spinnerPosition - 1).getId();

            for (SolicitudConConcursante s : todasMisSolicitudes) {
                // Filtramos accediendo al objeto solicitud dentro del POJO
                if (s.solicitud.getEditionId() == edicionIdSeleccionada) {
                    listaFiltrada.add(s);
                }
            }
        }

        adapter.setSolicitudes(listaFiltrada);

        if (listaFiltrada.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}