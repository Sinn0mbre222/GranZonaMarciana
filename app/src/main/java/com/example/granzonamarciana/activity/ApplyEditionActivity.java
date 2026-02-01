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

        // 1. Obtener ID del usuario desde la sesión real
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Inicializar Servicios
        solicitudService = new SolicitudService(getApplication());
        edicionService = new EdicionService(getApplication());

        // 3. Vincular vistas
        etMotivationMessage = findViewById(R.id.etMotivationMessage);
        spinnerEdiciones = findViewById(R.id.spinnerEdicionesApply);
        btnSubmitApplication = findViewById(R.id.btnSubmitApplication);
        TextView tvCancel = findViewById(R.id.tvCancel);

        // 4. Cargar ediciones abiertas
        cargarEdicionesAbiertas();

        btnSubmitApplication.setOnClickListener(v -> {
            String mensaje = etMotivationMessage.getText().toString().trim();
            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Escribe tus motivos", Toast.LENGTH_SHORT).show();
            } else if (edicionSeleccionada == null) {
                Toast.makeText(this, "Selecciona una edición", Toast.LENGTH_SHORT).show();
            } else {
                enviarSolicitud(mensaje);
            }
        });

        tvCancel.setOnClickListener(v -> finish());
    }

    private void cargarEdicionesAbiertas() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null) {
                listaEdicionesDisponibles.clear();
                List<String> etiquetas = new ArrayList<>();
                LocalDate hoy = LocalDate.now();

                for (Edicion ed : ediciones) {
                    // Solo permitimos ediciones cuya fecha final no haya pasado
                    if (ed.getFechaFinal() != null && !ed.getFechaFinal().isBefore(hoy)) {
                        listaEdicionesDisponibles.add(ed);
                        etiquetas.add("Edición #" + ed.getId() + " (Hasta: " + ed.getFechaFinal() + ")");
                    }
                }

                if (listaEdicionesDisponibles.isEmpty()) {
                    etiquetas.add("No hay ediciones abiertas");
                    btnSubmitApplication.setEnabled(false);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, R.layout.spinner_rol_item, etiquetas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(adapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!listaEdicionesDisponibles.isEmpty()) {
                            edicionSeleccionada = listaEdicionesDisponibles.get(position);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void enviarSolicitud(String mensaje) {
        Solicitud nuevaSolicitud = new Solicitud(
                edicionSeleccionada.getId(),
                currentUserId,
                mensaje,
                EstadoSolicitud.PENDIENTE
        );

        solicitudService.insert(nuevaSolicitud);
        Toast.makeText(this, "Solicitud enviada a la Edición #" + edicionSeleccionada.getId(), Toast.LENGTH_LONG).show();
        finish();
    }
}