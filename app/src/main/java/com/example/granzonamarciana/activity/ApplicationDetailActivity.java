package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Solicitud;
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
        solicitudService.getAllSolicitudes().observe(this, solicitudes -> {
            for (Solicitud s : solicitudes) {
                if (s.getId() == id) {
                    tvDetailEditionTitle.setText("Edición #" + s.getEditionId());
                    tvDetailMessage.setText(s.getMensaje());
                    tvDetailStatus.setText(s.getEstado().toString());

                    switch (s.getEstado()) {
                        case ACEPTADA:
                            tvDetailStatus.setTextColor(getColor(android.R.color.holo_green_light));
                            break;
                        case RECHAZADA:
                            tvDetailStatus.setTextColor(getColor(android.R.color.holo_red_light));
                            break;
                    }
                    break;
                }
            }
        });
    }
}