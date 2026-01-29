package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
    private List<Concursante> listaConcursantesFull; // Guardamos todos para poder filtrar localmente

    private List<Edicion> listaEdiciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        // 1. Inicializar Vistas
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        lvParticipantes = findViewById(R.id.lvParticipantes);
        etBuscar = findViewById(R.id.etBuscarParticipante);

        // 2. Inicializar Servicios
        edicionService = new EdicionService(getApplicationContext());
        concursanteService = new ConcursanteService(this);
        listaConcursantesFull = new ArrayList<>();

        // 3. Cargar datos
        cargarEdiciones();

        // 4. Configurar Buscador
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

        // 5. Click en un participante -> Ir a su perfil público
        lvParticipantes.setOnItemClickListener((parent, view, position, id) -> {
            Concursante seleccionado = adapter.getItem(position);
            if (seleccionado != null) {
                // ESTA LÍNEA DARÁ ERROR HASTA QUE CREEMOS EL SIGUIENTE ARCHIVO
                // Si te molesta el rojo, coméntala temporalmente:
                // Intent intent = new Intent(ParticipantsListActivity.this, ParticipantPublicActivity.class);
                // intent.putExtra("CONCURSANTE_ID", seleccionado.getId());
                // startActivity(intent);
            }
        });
    }

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;

                List<String> labels = new ArrayList<>();
                for (Edicion e : ediciones) {
                    // Ajusta "getFechaInicio()" si tu entidad Edicion tiene otro nombre para la fecha
                    labels.add("Edición " + e.getId() + " (" + e.getFechaInicio() + ")");
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        labels
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(spinnerAdapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (listaEdiciones != null && !listaEdiciones.isEmpty()) {
                            int idEdicion = listaEdiciones.get(position).getId();
                            cargarParticipantes(idEdicion);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            } else {
                Toast.makeText(this, "No se encontraron ediciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarParticipantes(int idEdicion) {
        // Asegúrate de que este método 'obtenerPorEdicion' exista en tu ConcursanteService
        concursanteService.obtenerPorEdicion(idEdicion).observe(this, concursantes -> {
            if (concursantes != null) {
                listaConcursantesFull = concursantes;
                adapter = new ParticipantAdapter(
                        this,
                        R.layout.item_participant, // Asegúrate de que este XML existe
                        new ArrayList<>(concursantes)
                );
                lvParticipantes.setAdapter(adapter);
            }
        });
    }

    private void filtrarListaLocal(String texto) {
        if (adapter == null) return;

        adapter.clear();
        if (texto.isEmpty()) {
            adapter.addAll(listaConcursantesFull);
        } else {
            String busqueda = texto.toLowerCase();
            for (Concursante c : listaConcursantesFull) {
                // AQUÍ: Usa getPrimerApellido() o getPrimerApellidp() según cómo lo tengas en tu entidad
                String apellido = (c.getPrimerApellido() != null) ? c.getPrimerApellido().toLowerCase() : "";

                if (c.getNombre().toLowerCase().contains(busqueda) || apellido.contains(busqueda)) {
                    adapter.add(c);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}