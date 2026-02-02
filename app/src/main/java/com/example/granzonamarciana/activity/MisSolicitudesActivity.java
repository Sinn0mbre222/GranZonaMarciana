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
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;

import java.util.ArrayList;
import java.util.List;

public class MisSolicitudesActivity extends AppCompatActivity {

    // Servicios para acceder a la base de datos
    private SolicitudService solicitudService;
    private EdicionService edicionService;

    // Adaptador y variables de estado
    private SolicitudAdapter adapter;
    private int currentUserId;

    // Elementos de la interfaz
    private ListView listView;
    private Spinner spFilter;
    private TextView tvEmpty;
    private TextView tvBack;

    // Listas para manejar los datos y el filtrado
    private List<Solicitud> todasMisSolicitudes = new ArrayList<>(); // Almacena todas las solicitudes descargadas
    private List<Edicion> listaEdiciones = new ArrayList<>(); // Almacena las ediciones para el filtro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        // 1. Verificación de sesión: Recuperamos el ID del usuario actual
        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Inicialización de servicios
        solicitudService = new SolicitudService(getApplication());
        edicionService = new EdicionService(getApplication());

        // 3. Configuración de componentes
        initViews();
        setupList();
        setupFilterSpinner();

        // 4. Carga inicial de datos
        cargarDatos();
    }

    //Vincula las vistas del XML con las variables Java y configura listeners básicos.

    private void initViews() {
        listView = findViewById(R.id.lvApplications);
        spFilter = findViewById(R.id.spEdicionFilter);
        tvEmpty = findViewById(R.id.tvEmptyList);
        tvBack = findViewById(R.id.tvBack);

        // Acción para el botón de volver atrás
        tvBack.setOnClickListener(v -> finish());
    }

    //Configura el ListView y el comportamiento al hacer clic en una solicitud.

    private void setupList() {
        adapter = new SolicitudAdapter(this);
        listView.setAdapter(adapter);

        // Al pulsar en una solicitud, abrimos el detalle de la misma
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Solicitud seleccionada = (Solicitud) adapter.getItem(position);
            Intent intent = new Intent(this, ApplicationDetailActivity.class);
            intent.putExtra("SOLICITUD_ID", seleccionada.getId());
            startActivity(intent);
        });
    }

    //Carga las ediciones disponibles en el Spinner para permitir filtrar.

    private void setupFilterSpinner() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null) {
                listaEdiciones = ediciones;

                // Creamos la lista de opciones para el desplegable
                List<String> opciones = new ArrayList<>();
                opciones.add("Todas las ediciones"); // Opción por defecto (índice 0)

                for (Edicion e : ediciones) {
                    opciones.add("Edición #" + e.getId() + " (" + e.getFechaInicio().getYear() + ")");
                }

                // Configuramos el adaptador del Spinner
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        this, R.layout.spinner_rol_item, opciones);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFilter.setAdapter(spinnerAdapter);

                // Listener para detectar cambios en la selección del filtro
                spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filtrarLista(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    //Obtiene TODAS las solicitudes del usuario desde la base de datos.

    private void cargarDatos() {
        solicitudService.getMisSolicitudes(currentUserId).observe(this, lista -> {
            if (lista != null) {
                todasMisSolicitudes = lista;
                // Una vez cargados los datos, aplicamos el filtro actual (por defecto "Todas")
                filtrarLista(spFilter.getSelectedItemPosition());
            }
        });
    }

    //Filtra la lista de solicitudes basándose en la selección del Spinner.
    private void filtrarLista(int spinnerPosition) {
        if (todasMisSolicitudes == null) return;
        if (spinnerPosition < 0) spinnerPosition = 0; // Seguridad por si viene -1

        List<Solicitud> listaFiltrada = new ArrayList<>();

        if (spinnerPosition == 0) {
            // Opción 0 es "Todas las ediciones", mostramos la lista completa
            listaFiltrada.addAll(todasMisSolicitudes);
        } else {
            // Cualquier otra opción corresponde a una edición específica.
            // Restamos 1 al índice porque la posición 0 del spinner no es una edición real.
            int edicionIdSeleccionada = listaEdiciones.get(spinnerPosition - 1).getId();

            for (Solicitud s : todasMisSolicitudes) {
                if (s.getEditionId() == edicionIdSeleccionada) {
                    listaFiltrada.add(s);
                }
            }
        }

        // Actualizamos el adaptador con la lista filtrada
        adapter.setSolicitudes(listaFiltrada);

        // Gestionamos la visibilidad del mensaje "Lista vacía"
        if (listaFiltrada.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}