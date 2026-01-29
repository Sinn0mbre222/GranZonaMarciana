package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Noticia;
import com.example.granzonamarciana.service.NoticiaService;

import java.time.LocalDate;

public class CreateEditNewsActivity extends AppCompatActivity {
    private NoticiaService service;
    private int idNoticia;
    private EditText etT, etB, etI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_news);

        service = new NoticiaService(this);
        etT = findViewById(R.id.etNewsTitle);
        etB = findViewById(R.id.etNewsBody);
        etI = findViewById(R.id.etNewsImageUrl);

        idNoticia = getIntent().getIntExtra("ID", -1);

        findViewById(R.id.btnSaveNews).setOnClickListener(v -> {
            Noticia n = new Noticia(LocalDate.now(),
                    etB.getText().toString(),
                    etT.getText().toString(),
                    etI.getText().toString());
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
