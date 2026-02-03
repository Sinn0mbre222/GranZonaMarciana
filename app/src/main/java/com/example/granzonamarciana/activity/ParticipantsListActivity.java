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
import com.example.granzonamarciana.service.GalaService; // Importante
import java.util.ArrayList;
import java.util.List;

public class ParticipantsListActivity extends AppCompatActivity {

    private Spinner spinnerEdiciones, spinnerGalas;
    private ListView lvParticipantes;
    private EditText etBuscar;
    private ImageButton btnBuscar;

    private EdicionService edicionService;
    private ConcursanteService concursanteService;
    private GalaService galaService; // Nuevo servicio

    private ParticipantAdapter adapter;
    private List<Concursante> listaConcursantesFull = new ArrayList<>();
    private List<Edicion> listaEdiciones = new ArrayList<>();
    private List<Gala> listaGalas = new ArrayList<>(); // Lista para el spinner de galas

    // Variable para saber quién está usando la app
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        // 1. OBTENER ROL DEL USUARIO
        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        userRole = prefs.getString("rol", "INVITADO");

        initViews();

        // Inicializar servicios
        edicionService = new EdicionService(this);
        concursanteService = new ConcursanteService(this);
        galaService = new GalaService(getApplication()); // Inicializamos GalaService

        cargarEdiciones();

        // Buscador
        btnBuscar.setOnClickListener(v -> filtrarListaLocal(etBuscar.getText().toString()));
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarListaLocal(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 2. CONFIGURAR CLICK CON LÓGICA DE ROLES
        lvParticipantes.setOnItemClickListener((parent, view, position, id) -> {
            Concursante seleccionado = (Concursante) parent.getAdapter().getItem(position);

            if (seleccionado != null) {
                Intent intent;

                if ("ESPECTADOR".equals(userRole)) {
                    // CASO A: Espectador -> Va a VOTAR

                    // VALIDACIÓN IMPORTANTE: Necesitamos saber en qué Gala votar
                    int galaPos = spinnerGalas.getSelectedItemPosition();
                    if (listaGalas.isEmpty() || galaPos < 0) {
                        Toast.makeText(this, "Selecciona una gala para poder votar", Toast.LENGTH_SHORT).show();
                        return; // Detenemos si no hay gala
                    }

                    Gala galaSeleccionada = listaGalas.get(galaPos);

                    Log.d("NAVEGACION", "Usuario ESPECTADOR -> Yendo a RateParticipantActivity");
                    intent = new Intent(ParticipantsListActivity.this, RateParticipantActivity.class);

                    intent.putExtra("CONCURSANTE_NOMBRE", seleccionado.getNombre() + " " + seleccionado.getPrimerApellido());
                    intent.putExtra("CONCURSANTE_FOTO", seleccionado.getImagenUrl());
                    intent.putExtra("GALA_ID", galaSeleccionada.getId()); // Pasamos el ID de la gala del spinner

                } else {
                    // CASO B: Invitado/Admin/Concursante -> Va a PERFIL PÚBLICO
                    Log.d("NAVEGACION", "Usuario " + userRole + " -> Yendo a ParticipantPublicActivity");
                    intent = new Intent(ParticipantsListActivity.this, ParticipantPublicActivity.class);
                }

                // ID siempre es necesario en ambos casos
                intent.putExtra("CONCURSANTE_ID", seleccionado.getId());
                startActivity(intent);

            } else {
                Log.e("NAVEGACION", "Error: Concursante nulo");
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        spinnerGalas = findViewById(R.id.spinnerGalas); // Nuevo spinner
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

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, labels);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(spinnerAdapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        int idEdicionSeleccionada = listaEdiciones.get(pos).getId();

                        // Al cambiar de edición, cargamos Participantes Y Galas
                        cargarParticipantes(idEdicionSeleccionada);
                        cargarGalas(idEdicionSeleccionada);
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
            }
        });
    }

    // Método nuevo para cargar las galas en el segundo spinner
    private void cargarGalas(int idEdicion) {
        galaService.getGalasByEdicion(idEdicion).observe(this, galas -> {
            listaGalas = (galas != null) ? galas : new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (Gala g : listaGalas) {
                labels.add("Gala: " + g.getFecha());
            }

            if (labels.isEmpty()) {
                labels.add("Sin galas disponibles");
            }

            ArrayAdapter<String> adapterGalas = new ArrayAdapter<>(this, R.layout.spinner_rol_item, labels);
            adapterGalas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGalas.setAdapter(adapterGalas);
        });
    }

    private void cargarParticipantes(int idEdicion) {
        concursanteService.obtenerPorEdicion(idEdicion).observe(this, concursantes -> {
            if (concursantes != null) {
                listaConcursantesFull = new ArrayList<>(concursantes);
                adapter = new ParticipantAdapter(this, R.layout.item_participant, listaConcursantesFull);
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
                if ((c.getNombre() + " " + c.getPrimerApellido()).toLowerCase().contains(busqueda))
                    filtrados.add(c);
            }
        }
        adapter.updateData(filtrados);
    }
}