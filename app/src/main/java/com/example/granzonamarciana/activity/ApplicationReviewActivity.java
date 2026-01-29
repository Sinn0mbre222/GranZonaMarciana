package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.SolicitudService;

public class ApplicationReviewActivity extends AppCompatActivity {

    private TextView tvApplicantName, tvMotivationDetail;
    private SolicitudService solicitudService;
    private Solicitud solicitudActual;
    private int maxParticipantesEdicion = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_review);

        solicitudService = new SolicitudService(getApplication());

        // Vincular vistas
        tvApplicantName = findViewById(R.id.tvApplicantName);
        tvMotivationDetail = findViewById(R.id.tvMotivationDetail);
        Button btnAccept = findViewById(R.id.btnAccept);
        Button btnReject = findViewById(R.id.btnReject);

        // Recuperar ID de la solicitud
        int solicitudId = getIntent().getIntExtra("SOLICITUD_ID", -1);

        if (solicitudId != -1) {
            cargarDatos(solicitudId);
        }

        // Configurar botones
        btnAccept.setOnClickListener(v -> {
            if (solicitudActual != null) {
                // Ejecuta la lógica de aceptar y cancelar el resto si se llena el cupo
                solicitudService.aceptarSolicitud(solicitudActual, maxParticipantesEdicion);
                Toast.makeText(this, "Solicitud Aceptada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnReject.setOnClickListener(v -> {
            if (solicitudActual != null) {
                solicitudService.rechazarSolicitud(solicitudActual);
                Toast.makeText(this, "Solicitud Rechazada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cargarDatos(int id) {
        solicitudService.getAllSolicitudes().observe(this, solicitudes -> {
            for (Solicitud s : solicitudes) {
                if (s.getId() == id) {
                    solicitudActual = s;
                    tvApplicantName.setText("Aspirante ID: " + s.getContestantId());
                    tvMotivationDetail.setText(s.getMensaje());
                    break;
                }
            }
        });
    }
}