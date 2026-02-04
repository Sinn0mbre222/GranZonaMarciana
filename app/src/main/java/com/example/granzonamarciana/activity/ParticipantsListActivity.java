package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.ParticipantAdapter;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.GalaService;
import java.util.ArrayList;
import java.util.List;

public class ParticipantsListActivity extends AppCompatActivity {

    private Spinner spinnerEdiciones, spinnerGalas;
    private ListView lvParticipantes;
    private EditText etBuscar;
    private ImageButton btnBuscar;

    private EdicionService edicionService;
    private ConcursanteService concursanteService;
    private GalaService galaService;

    private ParticipantAdapter adapter;
    private List<Concursante> listaConcursantesFull = new ArrayList<>();
    private List<Edicion> listaEdiciones = new ArrayList<>();
    private List<Gala> listaGalas = new ArrayList<>();

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        userRole = prefs.getString("rol", "INVITADO");

        initViews();

        edicionService = new EdicionService(this);
        concursanteService = new ConcursanteService(this);
        galaService = new GalaService(getApplication());

        // Cargar datos
        cargarEdiciones();

        // Listeners
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarListaLocal(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        lvParticipantes.setOnItemClickListener((parent, view, position, id) -> {
            Concursante seleccionado = (Concursante) parent.getAdapter().getItem(position);
            if (seleccionado != null) {
                navegarSegunRol(seleccionado);
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        spinnerGalas = findViewById(R.id.spinnerGalas);
        lvParticipantes = findViewById(R.id.lvParticipantes);
        etBuscar = findViewById(R.id.etBuscarParticipante);
        btnBuscar = findViewById(R.id.btnEjecutarBusqueda);
    }

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                for (Edicion e : ediciones) labels.add("Edición " + e.getId());

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        ParticipantsListActivity.this,
                        R.layout.spinner_rol_item,
                        labels
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(spinnerAdapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        int idEdicionSeleccionada = listaEdiciones.get(pos).getId();
                        cargarParticipantes(idEdicionSeleccionada);
                        cargarGalas(idEdicionSeleccionada);
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
            }
        });
    }

    private void cargarGalas(int idEdicion) {
        galaService.getGalasByEdicion(idEdicion).observe(this, galas -> {
            listaGalas = (galas != null) ? galas : new ArrayList<>();
            List<String> labels = new ArrayList<>();

            if (listaGalas.isEmpty()) {
                labels.add("Sin galas disponibles");
                spinnerGalas.setEnabled(false);
            } else {
                for (Gala g : listaGalas) labels.add("Gala: " + g.getFecha());
                spinnerGalas.setEnabled(true);
            }

            ArrayAdapter<String> adapterGalas = new ArrayAdapter<>(
                    ParticipantsListActivity.this,
                    R.layout.spinner_rol_item,
                    labels
            );
            adapterGalas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGalas.setAdapter(adapterGalas);
        });
    }

    private void cargarParticipantes(int idEdicion) {

        concursanteService.obtenerPorEdicion(idEdicion).observe(this, concursantes -> {
            if (concursantes != null) {
                listaConcursantesFull = new ArrayList<>(concursantes);

                if (adapter == null) {
                    adapter = new ParticipantAdapter(this, R.layout.item_participant, new ArrayList<>(listaConcursantesFull));
                    lvParticipantes.setAdapter(adapter);
                }
                filtrarListaLocal(etBuscar.getText().toString());
            }
        });
    }

    private void navegarSegunRol(Concursante seleccionado) {
        Intent intent;
        if ("ESPECTADOR".equals(userRole)) {
            int galaPos = spinnerGalas.getSelectedItemPosition();
            if (listaGalas.isEmpty() || galaPos < 0) {
                Toast.makeText(this, "Selecciona una gala válida", Toast.LENGTH_SHORT).show();
                return;
            }

            Gala galaSeleccionada = listaGalas.get(galaPos);
            // Aseguramos obtener el ID de edición correcto del spinner
            int edicionPos = spinnerEdiciones.getSelectedItemPosition();
            if(edicionPos < 0) return;
            int idEdicionActual = listaEdiciones.get(edicionPos).getId();

            intent = new Intent(this, RateParticipantActivity.class);
            intent.putExtra("CONCURSANTE_NOMBRE", seleccionado.getNombre() + " " + seleccionado.getPrimerApellido());
            intent.putExtra("CONCURSANTE_FOTO", seleccionado.getImagenUrl());
            intent.putExtra("GALA_EDICION_ID", idEdicionActual);
            intent.putExtra("GALA_ID", galaSeleccionada.getId());
        } else {
            intent = new Intent(this, ParticipantPublicActivity.class);
        }
        intent.putExtra("CONCURSANTE_ID", seleccionado.getId());
        startActivity(intent);
    }

    private void filtrarListaLocal(String texto) {
        if (adapter == null) return;
        List<Concursante> filtrados = new ArrayList<>();
        String busqueda = texto.toLowerCase().trim();
        if (busqueda.isEmpty()) {
            filtrados.addAll(listaConcursantesFull);
        } else {
            for (Concursante c : listaConcursantesFull) {
                String nombreCompleto = (c.getNombre() + " " + c.getPrimerApellido()).toLowerCase();
                if (nombreCompleto.contains(busqueda))
                    filtrados.add(c);
            }
        }
        adapter.updateData(filtrados);
    }
}