package com.example.granzonamarciana.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.service.GalaService;
import java.time.LocalDate;
import java.util.Calendar;

public class CreateGalaActivity extends AppCompatActivity {

    private EditText etGalaDate;
    private GalaService galaService;
    private LocalDate selectedDate;
    private int editionId;

    // Fechas límites (Esto debería venir de la Edición seleccionada)
    private final LocalDate inicioEdicion = LocalDate.of(2026, 1, 1);
    private final LocalDate finEdicion = LocalDate.of(2026, 12, 31);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gala);

        galaService = new GalaService(getApplication());
        editionId = getIntent().getIntExtra("EDITION_ID", 1);

        etGalaDate = findViewById(R.id.etGalaDate);
        Button btnSaveGala = findViewById(R.id.btnSaveGala);

        // Al tocar el campo, abrimos el calendario del sistema
        etGalaDate.setOnClickListener(v -> showDatePicker());

        btnSaveGala.setOnClickListener(v -> {
            if (selectedDate == null) {
                Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            // Intentar insertar con la validación de rango del Service
            Gala nuevaGala = new Gala(editionId, selectedDate);
            boolean exito = galaService.insert(nuevaGala, inicioEdicion, finEdicion);

            if (exito) {
                Toast.makeText(this, "Gala creada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: La fecha debe estar dentro del rango de la edición", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                    etGalaDate.setText(selectedDate.toString());
                }, year, month, day);
        datePickerDialog.show();
    }
}