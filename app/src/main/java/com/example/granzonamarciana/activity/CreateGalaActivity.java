package com.example.granzonamarciana.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.GalaService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateGalaActivity extends AppCompatActivity {

    private EditText etGalaDate;
    private Spinner spinnerEdiciones;
    private GalaService galaService;
    private EdicionService edicionService;

    private LocalDate selectedDate;
    private Edicion edicionSeleccionada;
    private List<Edicion> listaEdicionesActivas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gala);

        // Inicializar Servicios
        galaService = new GalaService(getApplication());
        edicionService = new EdicionService(getApplication());

        // Inicializar Vistas
        etGalaDate = findViewById(R.id.etGalaDate);
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        Button btnSaveGala = findViewById(R.id.btnSaveGala);

        cargarEdicionesActivas();

        etGalaDate.setOnClickListener(v -> showDatePicker());

        btnSaveGala.setOnClickListener(v -> guardarGala());
    }

    private void cargarEdicionesActivas() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null) {
                listaEdicionesActivas.clear();
                List<String> etiquetasEdiciones = new ArrayList<>();
                LocalDate hoy = LocalDate.now();

                for (Edicion ed : ediciones) {
                    if (ed.getFechaFinal() != null && !ed.getFechaFinal().isBefore(hoy)) {
                        listaEdicionesActivas.add(ed);
                        etiquetasEdiciones.add("Edición #" + ed.getId() + " (" + ed.getFechaInicio() + " / " + ed.getFechaFinal() + ")");
                    }
                }

                if (listaEdicionesActivas.isEmpty()) {
                    etiquetasEdiciones.add("No hay ediciones activas");
                }

                // Adaptador para el Spinner (Texto blanco para fondo oscuro)
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, R.layout.spinner_rol_item, etiquetasEdiciones);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(adapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!listaEdicionesActivas.isEmpty()) {
                            edicionSeleccionada = listaEdicionesActivas.get(position);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void guardarGala() {
        if (edicionSeleccionada == null) {
            Toast.makeText(this, "Por favor, selecciona una edición", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate == null) {
            Toast.makeText(this, "Selecciona una fecha para la gala", Toast.LENGTH_SHORT).show();
            return;
        }

        Gala nuevaGala = new Gala(edicionSeleccionada.getId(), selectedDate);

        boolean exito = galaService.insert(
                nuevaGala,
                edicionSeleccionada.getFechaInicio(),
                edicionSeleccionada.getFechaFinal()
        );

        if (exito) {
            Toast.makeText(this, "¡Gala creada con éxito!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error: La fecha debe estar entre "
                    + edicionSeleccionada.getFechaInicio() + " y "
                    + edicionSeleccionada.getFechaFinal(), Toast.LENGTH_LONG).show();
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDate = LocalDate.of(year, month + 1, day);
                    etGalaDate.setText(selectedDate.toString());
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}