package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.SolicitudAdapter;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.SolicitudService;

import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity implements SolicitudAdapter.OnSolicitudClickListener {

    private SolicitudService solicitudService;
    private SolicitudAdapter adapter;
    private int currentUserId = 2; // ID temporal hasta que Persona D pase el usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        // 1. Inicializar el Service
        solicitudService = new SolicitudService(getApplication());

        // 2. Configurar el RecyclerView
        RecyclerView rv = findViewById(R.id.rvApplications);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SolicitudAdapter(this);
        rv.setAdapter(adapter);

        // 3. Observar los datos de la BD
        // El LiveData permite que la lista se actualice sola si algo cambia
        solicitudService.getMisSolicitudes(currentUserId).observe(this, solicitudes -> {
            if (solicitudes != null) {
                adapter.setSolicitudes(solicitudes);
            }
        });
    }

    @Override
    public void onSolicitudClick(Solicitud solicitud) {
        // Al pulsar, vamos al detalle de la solicitud
        Intent intent = new Intent(this, ApplicationDetailActivity.class);
        intent.putExtra("SOLICITUD_ID", solicitud.getId()); // Pasamos el ID para cargar los datos
        startActivity(intent);
    }
}