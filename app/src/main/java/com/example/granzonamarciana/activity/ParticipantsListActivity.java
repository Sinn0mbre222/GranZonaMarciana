package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.ParticipantAdapter;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EdicionService;
import java.util.ArrayList;
import java.util.List;

public class ParticipantsListActivity extends AppCompatActivity {

    private Spinner spinnerEdiciones;
    private ListView lvParticipantes;
    private EditText etBuscar;
    private ImageButton btnBuscar;
    private EdicionService edicionService;
    private ConcursanteService concursanteService;
    private ParticipantAdapter adapter;
    private List<Concursante> listaConcursantesFull = new ArrayList<>();
    private List<Edicion> listaEdiciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        initViews();

        edicionService = new EdicionService(this);
        concursanteService = new ConcursanteService(this);

        cargarEdiciones();

        // 1. Configurar buscador por botón
        btnBuscar.setOnClickListener(v -> {
            filtrarListaLocal(etBuscar.getText().toString());
        });

        // 2. Mantener buscador en tiempo real (opcional, pero recomendado)
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarListaLocal(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 3. CONFIGURAR CLICK EN LA LISTA PARA VALORAR
        lvParticipantes.setOnItemClickListener((parent, view, position, id) -> {
            Concursante seleccionado = (Concursante) parent.getItemAtPosition(position);

            if (seleccionado != null) {
                Intent intent = new Intent(this, RateParticipantActivity.class);
                // PASAMOS LOS EXTRAS QUE NECESITA LA ACTIVITY DE VOTAR
                intent.putExtra("CONCURSANTE_ID", seleccionado.getId());
                intent.putExtra("CONCURSANTE_NOMBRE", seleccionado.getNombre() + " " + seleccionado.getPrimerApellido());
                intent.putExtra("CONCURSANTE_FOTO", seleccionado.getImagenUrl());

                startActivity(intent);
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        lvParticipantes = findViewById(R.id.lvParticipantes);
        etBuscar = findViewById(R.id.etBuscarParticipante);
        btnBuscar = findViewById(R.id.btnEjecutarBusqueda);
    }

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                for (Edicion e : ediciones) {
                    labels.add("Edición " + e.getId() + " (" + e.getFechaInicio() + ")");
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        this, R.layout.spinner_rol_item, labels
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(spinnerAdapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cargarParticipantes(listaEdiciones.get(position).getId());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void cargarParticipantes(int idEdicion) {
        concursanteService.obtenerPorEdicion(idEdicion).observe(this, concursantes -> {
            if (concursantes != null) {
                listaConcursantesFull = new ArrayList<>(concursantes);
                adapter = new ParticipantAdapter(this, R.layout.item_participant, concursantes);
                lvParticipantes.setAdapter(adapter);
                filtrarListaLocal(etBuscar.getText().toString());
            }
        });
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
                if (nombreCompleto.contains(busqueda)) {
                    filtrados.add(c);
                }
            }
        }
        adapter.updateData(filtrados);
    }
}