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

        tvApplicantName = findViewById(R.id.tvApplicantName);
        tvMotivationDetail = findViewById(R.id.tvMotivationDetail);
        Button btnAccept = findViewById(R.id.btnAccept);
        Button btnReject = findViewById(R.id.btnReject);
        View btnBack = findViewById(R.id.btnBackReview);

        int solicitudId = getIntent().getIntExtra("SOLICITUD_ID", -1);

        if (solicitudId != -1) {
            cargarDatos(solicitudId);
        } else {
            Toast.makeText(this, "Error al cargar la solicitud", Toast.LENGTH_SHORT).show();
            finish();
        }

        // LÓGICA PARA ACEPTAR: Se comprueba primero el cupo de la edición
        if (btnAccept != null) {
            btnAccept.setOnClickListener(v -> {
                if (solConConcursanteActual != null && solConConcursanteActual.solicitud != null) {
                    // Consultamos la edición para saber cuántos participantes máximos permite
                    edicionService.listarEdicionePorid(solConConcursanteActual.solicitud.getEditionId()).observe(this, edicion -> {
                        if (edicion != null) {
                            // El service se encarga de cambiar el estado a ACEPTADA y verificar cupos
                            solicitudService.aceptarSolicitud(solConConcursanteActual.solicitud, edicion.getNumeroParticipantesMax());
                            Toast.makeText(this, "Solicitud Aceptada", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
        }

        // LÓGICA PARA RECHAZAR: Simplemente cambia el estado a RECHAZADA
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

                // Mostramos nombre completo y el mensaje de motivación del aspirante
                if (solConConcursante.concursante != null) {
                    tvApplicantName.setText(solConConcursante.concursante.getNombre() + " " + solConConcursante.concursante.getPrimerApellido());
                }
                tvMotivationDetail.setText(solConConcursante.solicitud.getMensaje());

                // REGLA DE NEGOCIO: Si la solicitud ya no está PENDIENTE, ocultamos los botones de decisión
                // para evitar que el administrador cambie de opinión o cause errores en los cupos
                if (solConConcursante.solicitud.getEstado() != com.example.granzonamarciana.entity.EstadoSolicitud.PENDIENTE) {
                    Button btnAccept = findViewById(R.id.btnAccept);
                    Button btnReject = findViewById(R.id.btnReject);

                    if (btnAccept != null) btnAccept.setVisibility(View.GONE);
                    if (btnReject != null) btnReject.setVisibility(View.GONE);

                    tvMotivationDetail.append("\n\n(ESTA SOLICITUD YA FUE PROCESADA)");
                }
            }
        });
    }
}