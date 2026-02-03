package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;

public class ApplicationReviewActivity extends AppCompatActivity {

    private TextView tvApplicantName, tvMotivationDetail;
    private SolicitudService solicitudService;
    // CAMBIO: Ahora manejamos el POJO para tener acceso a los datos del concursante
    private SolicitudConConcursante wrapperActual;
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
        } else {
            Toast.makeText(this, "Error al cargar la solicitud", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar botones
        btnAccept.setOnClickListener(v -> {
            if (wrapperActual != null && wrapperActual.solicitud != null) {
                // Buscamos la edición para saber su aforo máximo real
                edicionService.listarEdicionePorid(wrapperActual.solicitud.getEditionId()).observe(this, edicion -> {
                    if (edicion != null) {
                        // Llamamos al service con el aforo real de la edición
                        solicitudService.aceptarSolicitud(wrapperActual.solicitud, edicion.getNumeroParticipantesMax());
                        Toast.makeText(this, "Solicitud Aceptada", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "No se encontró la edición vinculada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnReject.setOnClickListener(v -> {
            if (wrapperActual != null && wrapperActual.solicitud != null) {
                solicitudService.rechazarSolicitud(wrapperActual.solicitud);
                Toast.makeText(this, "Solicitud Rechazada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void cargarDatos(int id) {
        solicitudService.getSolicitudById(id).observe(this, wrapper -> {
            if (wrapper != null && wrapper.solicitud != null) {
                wrapperActual = wrapper;

                if (wrapper.concursante != null) {
                    tvApplicantName.setText(wrapper.concursante.getNombre() + " " + wrapper.concursante.getPrimerApellido());
                } else {
                    tvApplicantName.setText("Aspirante ID: " + wrapper.solicitud.getConcursanteId());
                }

                tvMotivationDetail.setText(wrapper.solicitud.getMensaje());
            }
        });
    }
}