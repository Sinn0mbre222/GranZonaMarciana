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

        // Configurar buscador en tiempo real
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

        // Botón volver
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        lvParticipantes = findViewById(R.id.lvParticipantes);
        etBuscar = findViewById(R.id.etBuscarParticipante);
    }

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                for (Edicion e : ediciones) {
                    labels.add("Edición " + e.getId() + " (" + e.getFechaInicio() + ")");
                }

                // USAR R.layout.spinner_rol_item para que el texto sea blanco
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
                // Inicializar el adapter con la lista completa
                adapter = new ParticipantAdapter(this, R.layout.item_participant, concursantes);
                lvParticipantes.setAdapter(adapter);

                // Si había algo escrito en el buscador, volver a filtrar
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
        // Método que debes tener en tu ParticipantAdapter para actualizar la lista
        adapter.updateData(filtrados);
    }
}