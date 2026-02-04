package com.example.granzonamarciana.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApplyEditionActivity extends AppCompatActivity {

    private EditText etMotivationMessage;
    private Spinner spinnerEdiciones;
    private SolicitudService solicitudService;
    private EdicionService edicionService;
    private Button btnSubmitApplication;
    private int currentUserId;
    private Edicion edicionSeleccionada;
    private List<Edicion> listaEdicionesDisponibles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_edition);

        // Obtenemos el ID del usuario logueado desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);

        solicitudService = new SolicitudService(getApplication());
        edicionService = new EdicionService(getApplication());

        etMotivationMessage = findViewById(R.id.etMotivationMessage);
        spinnerEdiciones = findViewById(R.id.spinnerEdicionesApply);
        btnSubmitApplication = findViewById(R.id.btnSubmitApplication);

        // Carga el Spinner con ediciones que aún no han terminado
        cargarEdicionesAbiertas();

        btnSubmitApplication.setOnClickListener(v -> enviarSolicitud());
        findViewById(R.id.tvCancel).setOnClickListener(v -> finish());
    }

    private void cargarEdicionesAbiertas() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null) {
                listaEdicionesDisponibles.clear();
                List<String> etiquetas = new ArrayList<>();
                LocalDate hoy = LocalDate.now();

                // Filtramos las ediciones cuya fecha final no haya pasado todavía
                for (Edicion ed : ediciones) {
                    if (ed.getFechaFinal() != null && !ed.getFechaFinal().isBefore(hoy)) {
                        listaEdicionesDisponibles.add(ed);
                        etiquetas.add("Edición #" + ed.getId());
                    }
                }

                // Cargamos el adaptador para el Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, etiquetas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(adapter);

                // Cada vez que el usuario elige una edición, comprobamos su estado
                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        edicionSeleccionada = listaEdicionesDisponibles.get(position);
                        configurarBotonDinamico();
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void configurarBotonDinamico() {
        if (edicionSeleccionada == null) return;

        // PASO 1: Obtenemos el número de personas ya aceptadas en esta edición
        solicitudService.obtenerSolicitudesAceptadas(edicionSeleccionada.getId()).observe(this, aceptados -> {

            // PASO 2: Comprobamos si este concursante específico ya tiene una solicitud aquí
            solicitudService.comprobarSolicitudesConcursante(edicionSeleccionada.getId(), currentUserId).observe(this, solicitudActiva -> {

                int numAceptados = (aceptados != null) ? aceptados : 0;

                // VALIDACIÓN A: El usuario ya ha solicitado participar
                if (solicitudActiva != null) {
                    btnSubmitApplication.setEnabled(false);
                    if (solicitudActiva.getEstado() == EstadoSolicitud.ACEPTADA) {
                        btnSubmitApplication.setText("YA ERES PARTICIPANTE");
                    } else {
                        btnSubmitApplication.setText("SOLICITUD EN REVISIÓN");
                    }
                }
                // VALIDACIÓN B: La edición ha alcanzado el límite de participantes
                else if (numAceptados >= edicionSeleccionada.getNumeroParticipantesMax()) {
                    btnSubmitApplication.setEnabled(false);
                    btnSubmitApplication.setText("CUPO COMPLETO");
                }
                // TODO OK: El usuario puede enviar la solicitud
                else {
                    btnSubmitApplication.setEnabled(true);
                    btnSubmitApplication.setText("ENVIAR SOLICITUD");
                }
            });
        });
    }

    private void enviarSolicitud() {
        String mensaje = etMotivationMessage.getText().toString().trim();
        if (mensaje.isEmpty()) {
            Toast.makeText(this, "Escribe tus motivos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos el objeto solicitud con el ID de edición y de usuario
        Solicitud nueva = new Solicitud(
                edicionSeleccionada.getId(),
                currentUserId,
                mensaje,
                EstadoSolicitud.PENDIENTE
        );

        // Guardamos en la base de datos
        solicitudService.insert(nueva);
        Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
        finish();
    }
}