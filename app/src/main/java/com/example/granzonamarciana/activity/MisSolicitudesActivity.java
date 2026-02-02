package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.SolicitudAdapter;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.service.SolicitudService;

public class MisSolicitudesActivity extends AppCompatActivity {

    private SolicitudService solicitudService;
    private SolicitudAdapter adapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        SharedPreferences prefs = getSharedPreferences("granZMUser", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("id", -1);

        // Si no hay usuario logueado, cerramos
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

                if (lista.isEmpty()) {
                    Toast.makeText(this, "No tienes solicitudes todavía", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}