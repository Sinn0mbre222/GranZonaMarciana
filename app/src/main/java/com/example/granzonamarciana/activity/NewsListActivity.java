package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.service.NoticiaService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NewsListActivity extends AppCompatActivity {
    private NoticiaService noticiaService;
    private ListView lvNews;
    private FloatingActionButton fabAddNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        lvNews = findViewById(R.id.lvNews);
        fabAddNews = findViewById(R.id.fabAddNews);
        noticiaService = new NoticiaService(this);

        // 1. Recuperar el rol desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String rolGuardado = prefs.getString("rol", "");

        // 2. Control de visibilidad del botón (Debe ser ADMINISTRADOR)
        if (rolGuardado != null && rolGuardado.equalsIgnoreCase("ADMINISTRADOR")) {
            fabAddNews.setVisibility(View.VISIBLE);
        } else {
            fabAddNews.setVisibility(View.GONE);
        }

        // 3. Salto a la pantalla de creación
        fabAddNews.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEditNewsActivity.class);
            intent.putExtra("EDICION_ID", 1);
            startActivity(intent);
        });

        // Cargar noticias en el ListView
        noticiaService.listarNoticias().observe(this, noticias -> {
            if (noticias != null) {
                // NoticiaAdapter adapter = new NoticiaAdapter(this, noticias);
                // lvNews.setAdapter(adapter);
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }
}