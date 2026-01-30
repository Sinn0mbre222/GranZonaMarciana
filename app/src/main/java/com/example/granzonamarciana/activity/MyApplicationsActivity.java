package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.SolicitudAdapter;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.SolicitudService;

public class MyApplicationsActivity extends AppCompatActivity {

    private SolicitudService solicitudService;
    private SolicitudAdapter adapter;
    private int currentUserId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        solicitudService = new SolicitudService(getApplication());

        ListView listView = findViewById(R.id.lvApplications);
        adapter = new SolicitudAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Solicitud seleccionada = (Solicitud) adapter.getItem(position);
            Intent intent = new Intent(this, ApplicationDetailActivity.class);
            intent.putExtra("SOLICITUD_ID", seleccionada.getId());
            startActivity(intent);
        });

        solicitudService.getMisSolicitudes(currentUserId).observe(this, lista -> {
            if (lista != null) {
                adapter.setSolicitudes(lista);
            }
        });
    }
}