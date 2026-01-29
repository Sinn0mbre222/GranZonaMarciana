package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.SolicitudService;

public class ApplyEditionActivity extends AppCompatActivity {

    private EditText etMotivationMessage;
    private SolicitudService solicitudService;
    private int currentEditionId = 1; // Temporalmente fijo (Persona A proporcionará esto)
    private int currentUserId = 2;    // Temporalmente fijo (Persona D proporcionará esto)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_edition);

        // Inicializar Service
        solicitudService = new SolicitudService(getApplication());

        // Vincular vistas
        etMotivationMessage = findViewById(R.id.etMotivationMessage);
        Button btnSubmit = findViewById(R.id.btnSubmitApplication);
        TextView tvCancel = findViewById(R.id.tvCancel);

        // Acción al pulsar "Enviar"
        btnSubmit.setOnClickListener(v -> {
            String mensaje = etMotivationMessage.getText().toString().trim();

            if (mensaje.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
            } else {
                enviarSolicitud(mensaje);
            }
        });

        // Acción al pulsar "Cancelar"
        tvCancel.setOnClickListener(v -> finish());
    }

    private void enviarSolicitud(String mensaje) {
        // Creamos la nueva solicitud con estado PENDIENTE
        Solicitud nuevaSolicitud = new Solicitud(
                currentEditionId,
                currentUserId,
                mensaje,
                EstadoSolicitud.PENDIENTE
        );

        // La guardamos usando el service
        solicitudService.insert(nuevaSolicitud);

        Toast.makeText(this, "Solicitud enviada correctamente", Toast.LENGTH_LONG).show();
        finish(); // Cerramos la pantalla al terminar
    }
}