package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;

public class ApplicationReviewActivity extends AppCompatActivity {

    private TextView tvApplicantName, tvMotivationDetail;
    private SolicitudService solicitudService;
    private Solicitud solicitudActual;
    private EdicionService edicionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_review);

        solicitudService = new SolicitudService(getApplication());
        edicionService = new EdicionService(getApplication());

        // Vincular vistas
        tvApplicantName = findViewById(R.id.tvApplicantName);
        tvMotivationDetail = findViewById(R.id.tvMotivationDetail);
        Button btnAccept = findViewById(R.id.btnAccept);
        Button btnReject = findViewById(R.id.btnReject);

        // Recuperar ID de la solicitud
        int solicitudId = getIntent().getIntExtra("SOLICITUD_ID", -1);

        if (solicitudId != -1) {
            cargarDatos(solicitudId);
        }else {
            Toast.makeText(this, "Error al cargar la solicitud", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar botones
        btnAccept.setOnClickListener(v -> {
            if (solicitudActual != null) {
                // Buscamos la edición para saber su aforo máximo real
                edicionService.listarEdicionePorid(solicitudActual.getEditionId()).observe(this, edicion -> {
                    if (edicion != null) {
                        // Llamamos al service con el aforo real de la edición
                        solicitudService.aceptarSolicitud(solicitudActual, edicion.getNumeroParticipantesMax());
                        Toast.makeText(this, "Solicitud procesada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "No se encontró la edición", Toast.LENGTH_SHORT).show();
                    }
                });
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
            if (solicitudes != null) {
                for (Solicitud s : solicitudes) {
                    if (s.getId() == id) {
                        solicitudActual = s;
                        tvApplicantName.setText("Aspirante ID: " + s.getConcursanteId());
                        tvMotivationDetail.setText(s.getMensaje());
                        break;
                    }
                }
            }
        });
    }
}