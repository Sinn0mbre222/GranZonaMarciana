package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;
import com.example.granzonamarciana.service.SolicitudService;

public class ApplicationDetailActivity extends AppCompatActivity {

    private TextView tvDetailEditionTitle, tvDetailStatus, tvDetailMessage;
    private SolicitudService solicitudService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_detail);

        solicitudService = new SolicitudService(getApplication());
        initViews();

        int solicitudId = getIntent().getIntExtra("SOLICITUD_ID", -1);

        if (solicitudId != -1) {
            cargarDatosSolicitud(solicitudId);
        }
    }

    private void initViews() {
        tvDetailEditionTitle = findViewById(R.id.tvDetailEditionTitle);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailMessage = findViewById(R.id.tvDetailMessage);
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        TextView tvBackText = findViewById(R.id.tvBack);
        if (tvBackText != null) {
            tvBackText.setOnClickListener(v -> finish());
        }
    }

    private void cargarDatosSolicitud(int id) {
        solicitudService.getSolicitudById(id).observe(this, solConcursante -> {
            if (solConcursante != null && solConcursante.solicitud != null) {

                // USANDO GETTERS: solConcursante.solicitud.get...
                tvDetailEditionTitle.setText("Edición #" + solConcursante.solicitud.getEditionId());
                tvDetailMessage.setText(solConcursante.solicitud.getMensaje());

                // Formateamos el estado del texto usando el getter del estado
                String estadoStr = solConcursante.solicitud.getEstado().toString();
                tvDetailStatus.setText(estadoStr);

                // Aplicamos colores según el estado
                switch (solConcursante.solicitud.getEstado()) {
                    case ACEPTADA:
                        tvDetailStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                        break;
                    case RECHAZADA:
                        tvDetailStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                        break;
                    default:
                        tvDetailStatus.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                        break;
                }

                // Si el concursante existe, usamos su getter de nombre
                if (solConcursante.concursante != null) {
                    // Cambiamos el título de la barra superior (ActionBar)
                    setTitle("Solicitud de " + solConcursante.concursante.getNombre());
                }
            }
        });
    }
}