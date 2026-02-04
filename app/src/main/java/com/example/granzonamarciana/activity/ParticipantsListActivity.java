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

// Esta pantalla muestra el listado de todos los concursantes.
// Permite filtrar por Edición (Spinner) y buscar por Nombre (EditText).
public class ParticipantsListActivity extends AppCompatActivity {

    // Elementos visuales
    private Spinner spinnerEdiciones, spinnerGalas;
    private ListView lvParticipantes;
    private EditText etBuscar;
    private ImageButton btnBuscar;

    // Servicios para obtener datos de la BD
    private EdicionService edicionService;
    private ConcursanteService concursanteService;
    private GalaService galaService;

    // Adaptador para mostrar la lista visualmente
    private ParticipantAdapter adapter;

    // Listas de datos en memoria
    private List<Concursante> listaConcursantesFull = new ArrayList<>(); // Copia completa para el buscador
    private List<Edicion> listaEdiciones = new ArrayList<>();
    private List<Gala> listaGalas = new ArrayList<>();

    private String userRole; // Rol del usuario actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        // 1. Obtener el rol del usuario para saber cómo comportarse al hacer click
        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        userRole = prefs.getString("rol", "INVITADO");

        initViews();

        // 2. Inicializar servicios
        edicionService = new EdicionService(this);
        concursanteService = new ConcursanteService(this);
        galaService = new GalaService(getApplication());

        // 3. Cargar la lista de Ediciones (Esto dispara el resto de cargas en cascada)
        cargarEdiciones();

        // 4. Configurar el buscador en tiempo real (mientras escribes)
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtramos la lista localmente cada vez que cambia el texto
                filtrarListaLocal(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 5. Configurar qué pasa al tocar un concursante
        lvParticipantes.setOnItemClickListener((parent, view, position, id) -> {
            Concursante seleccionado = (Concursante) parent.getAdapter().getItem(position);
            if (seleccionado != null) {
                navegarSegunRol(seleccionado);
            }
        });

        // Botón volver atrás
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        spinnerGalas = findViewById(R.id.spinnerGalas);
        lvParticipantes = findViewById(R.id.lvParticipantes);
        etBuscar = findViewById(R.id.etBuscarParticipante);
        btnBuscar = findViewById(R.id.btnEjecutarBusqueda);
    }

    // Carga las ediciones disponibles en el primer Spinner
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

                // Cuando seleccionamos una edición, cargamos sus concursantes y sus galas
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

    // Carga las galas de la edición seleccionada en el segundo Spinner
    // (Necesario para que el espectador sepa en qué Gala está votando)
    private void cargarGalas(int idEdicion) {
        galaService.getGalasByEdicion(idEdicion).observe(this, galas -> {
            listaGalas = (galas != null) ? galas : new ArrayList<>();
            List<String> labels = new ArrayList<>();

            if (listaGalas.isEmpty()) {
                labels.add("Sin galas disponibles");
                spinnerGalas.setEnabled(false); // Desactivamos si no hay galas
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

    // Obtiene los concursantes de la edición seleccionada desde la BD
    private void cargarParticipantes(int idEdicion) {
        concursanteService.obtenerPorEdicion(idEdicion).observe(this, concursantes -> {
            if (concursantes != null) {
                // Guardamos la lista completa para poder filtrar después sin recargar
                listaConcursantesFull = new ArrayList<>(concursantes);

                // Si es la primera vez, creamos el adaptador
                if (adapter == null) {
                    adapter = new ParticipantAdapter(this, R.layout.item_participant, new ArrayList<>(listaConcursantesFull));
                    lvParticipantes.setAdapter(adapter);
                }
                // Aplicamos filtro por si había texto escrito en el buscador
                filtrarListaLocal(etBuscar.getText().toString());
            }
        });
    }

    // Decide a dónde enviar al usuario al hacer click en un concursante
    private void navegarSegunRol(Concursante seleccionado) {
        Intent intent;

        // Si es ESPECTADOR, intentamos ir directo a VOTAR
        if ("ESPECTADOR".equals(userRole)) {
            int galaPos = spinnerGalas.getSelectedItemPosition();

            // Validamos que haya galas disponibles para votar
            if (listaGalas.isEmpty() || galaPos < 0) {
                Toast.makeText(this, "Selecciona una gala válida", Toast.LENGTH_SHORT).show();
                return;
            }

            Gala galaSeleccionada = listaGalas.get(galaPos);
            int edicionPos = spinnerEdiciones.getSelectedItemPosition();
            if(edicionPos < 0) return;
            int idEdicionActual = listaEdiciones.get(edicionPos).getId();

            // Vamos a la pantalla de votación (RateParticipantActivity)
            intent = new Intent(this, RateParticipantActivity.class);
            // Pasamos datos extra necesarios para registrar el voto
            intent.putExtra("CONCURSANTE_NOMBRE", seleccionado.getNombre() + " " + seleccionado.getPrimerApellido());
            intent.putExtra("CONCURSANTE_FOTO", seleccionado.getImagenUrl());
            intent.putExtra("GALA_EDICION_ID", idEdicionActual);
            intent.putExtra("GALA_ID", galaSeleccionada.getId());
        } else {
            // Si es INVITADO, ADMIN o CONCURSANTE, vamos al Perfil Público
            intent = new Intent(this, ParticipantPublicActivity.class);
        }

        // Siempre pasamos el ID del concursante
        intent.putExtra("CONCURSANTE_ID", seleccionado.getId());
        startActivity(intent);
    }

    // Filtra la lista que ya tenemos en memoria (No consulta la BD de nuevo)
    private void filtrarListaLocal(String texto) {
        if (adapter == null) return;

        List<Concursante> filtrados = new ArrayList<>();
        String busqueda = texto.toLowerCase().trim();

        if (busqueda.isEmpty()) {
            // Si no hay texto, mostramos todos
            filtrados.addAll(listaConcursantesFull);
        } else {
            // Si hay texto, buscamos coincidencias en el nombre completo
            for (Concursante c : listaConcursantesFull) {
                String nombreCompleto = (c.getNombre() + " " + c.getPrimerApellido()).toLowerCase();
                if (nombreCompleto.contains(busqueda))
                    filtrados.add(c);
            }
        }
        // Actualizamos el ListView con los resultados filtrados
        adapter.updateData(filtrados);
    }
}