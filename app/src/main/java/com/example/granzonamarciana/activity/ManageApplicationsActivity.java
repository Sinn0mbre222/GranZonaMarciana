package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.SolicitudAdapter;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.SolicitudService;

public class ManageApplicationsActivity extends AppCompatActivity implements SolicitudAdapter.OnSolicitudClickListener {

    private SolicitudService solicitudService;
    private SolicitudAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_applications);

        // 1. Inicializar el Service que gestiona las solicitudes
        solicitudService = new SolicitudService(getApplication());

        // 2. Configurar el RecyclerView para mostrar la lista
        RecyclerView rv = findViewById(R.id.rvAllApplications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 3. Configurar el Adapter con el listener de clics
        adapter = new SolicitudAdapter(this);
        rv.setAdapter(adapter);

        // 4. Observar todas las solicitudes de la base de datos
        solicitudService.getAllSolicitudes().observe(this, solicitudes -> {
            if (solicitudes != null) {
                adapter.setSolicitudes(solicitudes);
            }
        });
    }

    @Override
    public void onSolicitudClick(Solicitud solicitud) {
        // Al pulsar en una solicitud, el administrador va a la pantalla de revisión
        Intent intent = new Intent(this, ApplicationReviewActivity.class);
        intent.putExtra("SOLICITUD_ID", solicitud.getId());
        startActivity(intent);
    }
}