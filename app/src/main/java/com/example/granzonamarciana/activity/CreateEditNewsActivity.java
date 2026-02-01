package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Noticia;
import com.example.granzonamarciana.service.NoticiaService;

import java.time.LocalDate;

public class CreateEditNewsActivity extends AppCompatActivity {
    private NoticiaService service;
    private int idNoticia;
    private int edicionId; // <--- Nuevo campo necesario
    private EditText etT, etB, etI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_news);

        service = new NoticiaService(this);
        etT = findViewById(R.id.etNewsTitle);
        etB = findViewById(R.id.etNewsBody);
        etI = findViewById(R.id.etNewsImageUrl);

        // Recibimos el ID de la noticia (para editar) y el ID de la edición (obligatorio para el constructor)
        idNoticia = getIntent().getIntExtra("ID", -1);
        edicionId = getIntent().getIntExtra("EDICION_ID", -1);

        // Si es una noticia nueva, necesitamos saber a qué edición pertenece
        if (idNoticia == -1 && edicionId == -1) {
            Toast.makeText(this, "Error: No se especificó la edición", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Si estamos editando, cargamos los datos previos
        if (idNoticia != -1) {
            service.listarNoticiaePorid(idNoticia).observe(this, noticia -> {
                if (noticia != null) {
                    etT.setText(noticia.getCabecera());
                    etB.setText(noticia.getCuerpo());
                    etI.setText(noticia.getImagen());
                    this.edicionId = noticia.getEdicionId(); // Mantenemos el ID de la edición original
                }
            });
        }

        findViewById(R.id.btnSaveNews).setOnClickListener(v -> {
            String titulo = etT.getText().toString();
            String cuerpo = etB.getText().toString();
            String imagen = etI.getText().toString();

            // Constructor actualizado con el 5º parámetro: edicionId
            Noticia n = new Noticia(LocalDate.now(), cuerpo, titulo, imagen, edicionId);

            if (idNoticia == -1) {
                service.insertarNoticia(n);
            } else {
                n.setId(idNoticia);
                service.actualizarNoticia(n);
            }
            finish();
        });
    }
}