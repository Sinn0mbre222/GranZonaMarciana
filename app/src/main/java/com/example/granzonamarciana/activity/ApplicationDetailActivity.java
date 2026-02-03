package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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

        tvDetailEditionTitle = findViewById(R.id.tvDetailEditionTitle);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailMessage = findViewById(R.id.tvDetailMessage);
        Button btnBack = findViewById(R.id.btnBack);

        int solicitudId = getIntent().getIntExtra("SOLICITUD_ID", -1);

        if (solicitudId != -1) {
            cargarDatosSolicitud(solicitudId);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarDatosSolicitud(int id) {
        // Observamos directamente la solicitud específica
        solicitudService.getSolicitudById(id).observe(this, wrapper -> {
            if (wrapper != null && wrapper.solicitud != null) {
                // Seteamos los datos accediendo al objeto solicitud dentro del POJO
                tvDetailEditionTitle.setText("Edición #" + wrapper.solicitud.getEditionId());
                tvDetailMessage.setText(wrapper.solicitud.getMensaje());
                tvDetailStatus.setText(wrapper.solicitud.getEstado().toString());

                switch (wrapper.solicitud.getEstado()) {
                    case ACEPTADA:
                        tvDetailStatus.setTextColor(getColor(android.R.color.holo_green_light));
                        break;
                    case RECHAZADA:
                        tvDetailStatus.setTextColor(getColor(android.R.color.holo_red_light));
                        break;
                    default:
                        tvDetailStatus.setTextColor(getColor(android.R.color.darker_gray));
                        break;
                }
            }
        });
    }
}