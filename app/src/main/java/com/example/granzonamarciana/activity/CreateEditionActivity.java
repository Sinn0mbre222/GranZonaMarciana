package com.example.granzonamarciana.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.service.EdicionService;

import java.time.LocalDate;
import java.util.Calendar;

public class CreateEditionActivity extends AppCompatActivity {
    private EdicionService service;
    private EditText etIn, etFi, etMa;
    private LocalDate fechaInicioSel, fechaFinSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edition);

        service = new EdicionService(this);
        etIn = findViewById(R.id.etStartDate);
        etFi = findViewById(R.id.etEndDate);
        etMa = findViewById(R.id.etMaxParticipants);
        TextView tvBack = findViewById(R.id.tvBack);

        // Listeners para abrir los DatePickers
        etIn.setOnClickListener(v -> mostrarCalendario(true));
        etFi.setOnClickListener(v -> mostrarCalendario(false));

        // Botón Volver
        tvBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnCreateEdition).setOnClickListener(v -> {
            validarYCrear();
        });
    }

    private void mostrarCalendario(boolean esInicio) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            LocalDate seleccionada = LocalDate.of(y, m + 1, d);
            if (esInicio) {
                fechaInicioSel = seleccionada;
                etIn.setText(seleccionada.toString());
            } else {
                fechaFinSel = seleccionada;
                etFi.setText(seleccionada.toString());
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void validarYCrear() {
        String maxStr = etMa.getText().toString();

        if (fechaInicioSel == null || fechaFinSel == null || maxStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación lógica: El inicio debe ser antes que el fin
        if (fechaInicioSel.isAfter(fechaFinSel)) {
            Toast.makeText(this, "La fecha de inicio no puede ser posterior al fin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int max = Integer.parseInt(maxStr);
            service.insertarEdicion(new Edicion(fechaInicioSel, fechaFinSel, max));
            Toast.makeText(this, "Edición creada con éxito", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número de participantes no válido", Toast.LENGTH_SHORT).show();
        }
    }
}