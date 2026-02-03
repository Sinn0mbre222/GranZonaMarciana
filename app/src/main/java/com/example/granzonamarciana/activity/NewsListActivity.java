package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.NoticiaAdapter;
import com.example.granzonamarciana.entity.Noticia;
import com.example.granzonamarciana.service.NoticiaService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity {
    private NoticiaService noticiaService;
    private ListView lvNews;
    private FloatingActionButton fabAddNews;
    private NoticiaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        initViews();
        noticiaService = new NoticiaService(this);

        // 1. Configurar Adaptador inicialmente vacío
        adapter = new NoticiaAdapter(this, new ArrayList<>());
        lvNews.setAdapter(adapter);

        // 2. Control de permisos (Solo Admin ve el botón de añadir)
        checkAdminPermissions();

        // 3. Cargar y Observar Noticias (Reactivo)
        cargarNoticias();

        // 4. Listeners
        fabAddNews.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEditNewsActivity.class);
            startActivity(intent);
        });

        // Click en una noticia para editar (Si eres admin) o ver detalle
        lvNews.setOnItemClickListener((parent, view, position, id) -> {
            Noticia seleccionada = (Noticia) adapter.getItem(position);
            if (seleccionada != null) {
                SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
                String rol = prefs.getString("rol", "");

                if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
                    // El admin va a la pantalla de edición/borrado
                    Intent intent = new Intent(this, CreateEditNewsActivity.class);
                    intent.putExtra("NOTICIA_ID", seleccionada.getId());
                    startActivity(intent);
                } else {
                    // Invitados, Espectadores y Concursantes van al Detalle de lectura
                    Intent intent = new Intent(this, NewsDetailActivity.class);
                    intent.putExtra("ID", seleccionada.getId());
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        lvNews = findViewById(R.id.lvNews);
        fabAddNews = findViewById(R.id.fabAddNews);
    }

    private void checkAdminPermissions() {
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");
        if ("ADMINISTRADOR".equalsIgnoreCase(rol)) {
            fabAddNews.setVisibility(View.VISIBLE);
        } else {
            fabAddNews.setVisibility(View.GONE);
        }
    }

    private void cargarNoticias() {
        // Al usar LiveData, este bloque se ejecuta automáticamente
        // cada vez que insertas o borras una noticia en la base de datos
        noticiaService.listarNoticias().observe(this, listaNoticias -> {
            if (listaNoticias != null) {
                Log.d("DEBUG_NEWS", "Noticias recibidas: " + listaNoticias.size());
                adapter.setNoticias(listaNoticias); // Usamos el método que creamos en el adapter
            }
        });
    }
}