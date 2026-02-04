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

        // Inicializamos el servicio para realizar consultas a Room
        solicitudService = new SolicitudService(getApplication());
        initViews();

        // Recuperamos el ID de la solicitud enviado desde el intent
        int solicitudId = getIntent().getIntExtra("SOLICITUD_ID", -1);

        // Si el ID es válido, cargamos los datos de la base de datos
        if (solicitudId != -1) {
            cargarDatosSolicitud(solicitudId);
        }
    }

    private void initViews() {
        tvDetailEditionTitle = findViewById(R.id.tvDetailEditionTitle);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailMessage = findViewById(R.id.tvDetailMessage);
        Button btnBack = findViewById(R.id.btnBack);

        // Listener para cerrar la actividad y volver atrás
        btnBack.setOnClickListener(v -> finish());

        TextView tvBackText = findViewById(R.id.tvBack);
        if (tvBackText != null) {
            tvBackText.setOnClickListener(v -> finish());
        }
    }

    private void cargarDatosSolicitud(int id) {
        // Obtenemos un POJO que combina datos de Solicitud y Concursante mediante un Observer
        solicitudService.getSolicitudById(id).observe(this, solConcursante -> {
            if (solConcursante != null && solConcursante.solicitud != null) {

                // Mostramos el ID de la edición y el mensaje de motivación del usuario
                tvDetailEditionTitle.setText("Edición #" + solConcursante.solicitud.getEditionId());
                tvDetailMessage.setText(solConcursante.solicitud.getMensaje());

                // Transformamos el ENUM del estado a String para mostrarlo en pantalla
                String estadoStr = solConcursante.solicitud.getEstado().toString();
                tvDetailStatus.setText(estadoStr);

                // Lógica visual: Cambiamos el color del texto según el estado de la solicitud
                switch (solConcursante.solicitud.getEstado()) {
                    case ACEPTADA:
                        tvDetailStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                        break;
                    case RECHAZADA:
                        tvDetailStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                        break;
                    default:
                        // Estado PENDIENTE en gris
                        tvDetailStatus.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                        break;
                }

                // Personalizamos el título de la pantalla con el nombre del concursante
                if (solConcursante.concursante != null) {
                    setTitle("Solicitud de " + solConcursante.concursante.getNombre());
                }
            }
        });
    }
}