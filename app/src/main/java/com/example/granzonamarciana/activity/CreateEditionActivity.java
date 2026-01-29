package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.service.EdicionService;

import java.time.LocalDate;

public class CreateEditionActivity extends AppCompatActivity {
    private EdicionService service;
    private EditText etIn, etFi, etMa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edition);

        service = new EdicionService(this);
        etIn = findViewById(R.id.etStartDate);
        etFi = findViewById(R.id.etEndDate);
        etMa = findViewById(R.id.etMaxParticipants);

        findViewById(R.id.btnCreateEdition).setOnClickListener(v -> {
            try {
                LocalDate inicio = LocalDate.parse(etIn.getText().toString());
                LocalDate fin = LocalDate.parse(etFi.getText().toString());
                int max = Integer.parseInt(etMa.getText().toString());

                service.insertarEdicion(new Edicion(inicio, fin, max));
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error en datos o formato YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
