package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;

public class ApplicationReviewActivity extends AppCompatActivity {

    private TextView tvApplicantName, tvMotivationDetail;
    private SolicitudService solicitudService;
    private SolicitudConConcursante solConConcursanteActual;
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

        // CORRECCIÓN: Buscamos el botón de volver (en el XML lo llamaremos btnBackReview)
        View btnBack = findViewById(R.id.btnBackReview);

        // Recuperar ID de la solicitud
        int solicitudId = getIntent().getIntExtra("SOLICITUD_ID", -1);

        if (solicitudId != -1) {
            cargarDatos(solicitudId);
        } else {
            Toast.makeText(this, "Error al cargar la solicitud", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar botones con seguridad (null check)
        if (btnAccept != null) {
            btnAccept.setOnClickListener(v -> {
                if (solConConcursanteActual != null && solConConcursanteActual.solicitud != null) {
                    edicionService.listarEdicionePorid(solConConcursanteActual.solicitud.getEditionId()).observe(this, edicion -> {
                        if (edicion != null) {
                            solicitudService.aceptarSolicitud(solConConcursanteActual.solicitud, edicion.getNumeroParticipantesMax());
                            Toast.makeText(this, "Solicitud Aceptada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "No se encontró la edición vinculada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        if (btnReject != null) {
            btnReject.setOnClickListener(v -> {
                if (solConConcursanteActual != null && solConConcursanteActual.solicitud != null) {
                    solicitudService.rechazarSolicitud(solConConcursanteActual.solicitud);
                    Toast.makeText(this, "Solicitud Rechazada", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void cargarDatos(int id) {
        solicitudService.getSolicitudById(id).observe(this, solConConcursante -> {
            if (solConConcursante != null && solConConcursante.solicitud != null) {
                solConConcursanteActual = solConConcursante;

                // 1. Mostrar datos del aspirante
                if (solConConcursante.concursante != null) {
                    tvApplicantName.setText(solConConcursante.concursante.getNombre() + " " + solConConcursante.concursante.getPrimerApellido());
                }

                tvMotivationDetail.setText(solConConcursante.solicitud.getMensaje());

                // 2. RESTRICCIÓN DE DECISIÓN
                // Si el estado NO es PENDIENTE, significa que ya se tomó una decisión
                if (solConConcursante.solicitud.getEstado() != com.example.granzonamarciana.entity.EstadoSolicitud.PENDIENTE) {

                    // Buscamos los botones para ocultarlos
                    Button btnAccept = findViewById(R.id.btnAccept);
                    Button btnReject = findViewById(R.id.btnReject);

                    if (btnAccept != null) btnAccept.setVisibility(View.GONE);
                    if (btnReject != null) btnReject.setVisibility(View.GONE);

                    // Opcional: Mostrar un texto indicando la decisión tomada
                    tvMotivationDetail.append("\n\n(ESTA SOLICITUD YA FUE PROCESADA)");
                }
            }
        });
    }
}