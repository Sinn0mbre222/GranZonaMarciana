package com.example.granzonamarciana.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Noticia;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.service.NoticiaService;
import com.example.granzonamarciana.service.EdicionService; // Asumiendo que existe
import java.time.LocalDate;
import java.util.List;

public class CreateEditNewsActivity extends AppCompatActivity {
    private NoticiaService service;
    private EdicionService edicionService; // Para cargar el Spinner
    private EditText etT, etB, etI;
    private Spinner spEdiciones;
    private int idNoticia, adminId;
    private List<Edicion> listaEdiciones; // Guardamos la lista para buscar IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_news);

        // Inicialización
        service = new NoticiaService(this);
        edicionService = new EdicionService(this);

        etT = findViewById(R.id.etNewsTitle);
        etB = findViewById(R.id.etNewsBody);
        etI = findViewById(R.id.etNewsImageUrl);
        spEdiciones = findViewById(R.id.spEdiciones);

        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        adminId = prefs.getInt("id", -1);

        // 1. Cargar el Spinner con ediciones de la BD
        cargarSpinnerEdiciones();

        // 2. Si editamos, cargamos los datos
        if (idNoticia != -1) {
            service.buscarPorId(idNoticia).observe(this, n -> {
                if (n != null) {
                    etT.setText(n.getCabecera());
                    etB.setText(n.getCuerpo());
                    etI.setText(n.getImagen());
                    // Nota: Para seleccionar la edición en el spinner al editar,
                    // se hace dentro de cargarSpinnerEdiciones cuando la lista esté lista.
                }
            });
        }

        findViewById(R.id.btnSaveNews).setOnClickListener(v -> guardarNoticia());
    }

    private void cargarSpinnerEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null) {
                this.listaEdiciones = ediciones;
                // Usamos un simple ArrayAdapter. Asegúrate que Edicion.java tenga un buen toString()
                ArrayAdapter<Edicion> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, ediciones);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spEdiciones.setAdapter(adapter);

                // Si es edición, intentamos seleccionar la edición que ya tenía la noticia
                // (Requiere lógica adicional comparando IDs de n.getEdicionId() con listaEdiciones)
            }
        });
    }

    private void guardarNoticia() {
        // Obtener edición seleccionada
        Edicion edicionSeleccionada = (Edicion) spEdiciones.getSelectedItem();

        if (edicionSeleccionada == null) {
            Toast.makeText(this, "Por favor, selecciona una edición", Toast.LENGTH_SHORT).show();
            return;
        }

        Noticia n = new Noticia(
                LocalDate.now(),
                etB.getText().toString(),
                etT.getText().toString(),
                etI.getText().toString(),
                edicionSeleccionada.getId(), // Usamos el ID del Spinner
                adminId
        );

        if (idNoticia == -1) {
            service.insertarNoticia(n);
            Toast.makeText(this, "Noticia creada", Toast.LENGTH_SHORT).show();
        } else {
            n.setId(idNoticia);
            service.actualizarNoticia(n);
            Toast.makeText(this, "Noticia actualizada", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}