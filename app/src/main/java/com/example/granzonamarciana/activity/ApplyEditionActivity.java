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

        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);

        solicitudService = new SolicitudService(getApplication());
        edicionService = new EdicionService(getApplication());

        etMotivationMessage = findViewById(R.id.etMotivationMessage);
        spinnerEdiciones = findViewById(R.id.spinnerEdicionesApply);
        btnSubmitApplication = findViewById(R.id.btnSubmitApplication);

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

                for (Edicion ed : ediciones) {
                    if (ed.getFechaFinal() != null && !ed.getFechaFinal().isBefore(hoy)) {
                        listaEdicionesDisponibles.add(ed);
                        etiquetas.add("Edición #" + ed.getId());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, etiquetas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(adapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        edicionSeleccionada = listaEdicionesDisponibles.get(position);
                        // ¡AQUÍ ES DONDE USAMOS LOS MÉTODOS DEL SERVICE!
                        configurarBotonDinamico();
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    // --- ESTA ES LA FUNCIÓN QUE USA LOS MÉTODOS QUE ME PREGUNTABAS ---
    private void configurarBotonDinamico() {
        if (edicionSeleccionada == null) return;

        // 1. Usamos obtenerSolicitudesAceptadas para ver el cupo
        solicitudService.obtenerSolicitudesAceptadas(edicionSeleccionada.getId()).observe(this, aceptados -> {

            // 2. Usamos comprobarSolicitudesConcursante para ver si el usuario ya participó
            solicitudService.comprobarSolicitudesConcursante(edicionSeleccionada.getId(), currentUserId).observe(this, solicitudActiva -> {

                int numAceptados = (aceptados != null) ? aceptados : 0;

                if (solicitudActiva != null) {
                    // Si ya tiene una solicitud pendiente o aceptada
                    btnSubmitApplication.setEnabled(false);
                    if (solicitudActiva.getEstado() == EstadoSolicitud.ACEPTADA) {
                        btnSubmitApplication.setText("YA ERES PARTICIPANTE");
                    } else {
                        btnSubmitApplication.setText("SOLICITUD EN REVISIÓN");
                    }
                } else if (numAceptados >= edicionSeleccionada.getNumeroParticipantesMax()) {
                    // Si la edición está llena
                    btnSubmitApplication.setEnabled(false);
                    btnSubmitApplication.setText("CUPO COMPLETO");
                } else {
                    // Si todo está ok
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

        Solicitud nueva = new Solicitud(
                edicionSeleccionada.getId(),
                currentUserId,
                mensaje,
                EstadoSolicitud.PENDIENTE
        );

        solicitudService.insert(nueva);
        Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
        finish();
    }
}