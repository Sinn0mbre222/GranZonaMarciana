package com.example.granzonamarciana.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Noticia;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.NoticiaService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateEditNewsActivity extends AppCompatActivity {

    private NoticiaService noticiaService;
    private EdicionService edicionService;

    private EditText etTitulo, etCuerpo, etImagenUrl;
    private Spinner spEdiciones;
    private Button btnGuardar, btnEliminar;
    private TextView tvPantalla;

    private int idNoticiaExistente = -1;
    private int adminIdLogueado;
    private List<Edicion> listaEdicionesCargadas = new ArrayList<>();
    private Noticia noticiaActual; // Para tener el objeto completo en caso de delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_news);

        noticiaService = new NoticiaService(this);
        edicionService = new EdicionService(this);

        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        adminIdLogueado = prefs.getInt("id", -1);

        idNoticiaExistente = getIntent().getIntExtra("NOTICIA_ID", -1);

        initViews();

        // Si es edición, cambiamos el texto del botón y mostramos el de eliminar
        if (idNoticiaExistente != -1) {
            tvPantalla.setText("EDITAR NOTICIA");
            btnGuardar.setText("ACTUALIZAR NOTICIA");
            btnEliminar.setVisibility(View.VISIBLE);
        }

        cargarSpinnerEdiciones();

        btnGuardar.setOnClickListener(v -> guardarNoticia());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        etTitulo = findViewById(R.id.etNewsTitle);
        etCuerpo = findViewById(R.id.etNewsBody);
        etImagenUrl = findViewById(R.id.etNewsImageUrl);
        spEdiciones = findViewById(R.id.spEdiciones);
        btnGuardar = findViewById(R.id.btnSaveNews);
        btnEliminar = findViewById(R.id.btnDeleteNews);
        tvPantalla = findViewById(R.id.tvTitle);
    }

    private void cargarSpinnerEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                this.listaEdicionesCargadas = ediciones;
                List<String> nombres = new ArrayList<>();
                for (Edicion e : ediciones) {
                    nombres.add("Edición #" + e.getId());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.spinner_rol_item, nombres);
                adapter.setDropDownViewResource(R.layout.spinner_rol_item);
                spEdiciones.setAdapter(adapter);

                if (idNoticiaExistente != -1) {
                    cargarDatosNoticiaParaEditar();
                }
            }
        });
    }

    private void cargarDatosNoticiaParaEditar() {
        noticiaService.buscarPorId(idNoticiaExistente).observe(this, noticia -> {
            if (noticia != null) {
                noticiaActual = noticia;
                etTitulo.setText(noticia.getCabecera());
                etCuerpo.setText(noticia.getCuerpo());
                etImagenUrl.setText(noticia.getImagen());

                for (int i = 0; i < listaEdicionesCargadas.size(); i++) {
                    if (listaEdicionesCargadas.get(i).getId() == noticia.getEdicionId()) {
                        spEdiciones.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    private void guardarNoticia() {
        String cabecera = etTitulo.getText().toString().trim();
        String cuerpo = etCuerpo.getText().toString().trim();
        String urlImagen = etImagenUrl.getText().toString().trim();

        if (cabecera.isEmpty() || cuerpo.isEmpty() || spEdiciones.getSelectedItem() == null) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int edicionId = listaEdicionesCargadas.get(spEdiciones.getSelectedItemPosition()).getId();

        if (idNoticiaExistente == -1) {
            Noticia n = new Noticia(LocalDate.now(), cuerpo, cabecera, urlImagen, edicionId, adminIdLogueado);
            noticiaService.insertarNoticia(n);
            Toast.makeText(this, "Noticia publicada", Toast.LENGTH_SHORT).show();
        } else {
            noticiaActual.setCabecera(cabecera);
            noticiaActual.setCuerpo(cuerpo);
            noticiaActual.setImagen(urlImagen);
            noticiaActual.setEdicionId(edicionId);
            noticiaService.actualizarNoticia(noticiaActual);
            Toast.makeText(this, "Noticia actualizada", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Noticia")
                .setMessage("¿Estás seguro de que deseas eliminar esta noticia? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (noticiaActual != null) {
                        noticiaService.eliminarNoticia(noticiaActual);
                        Toast.makeText(this, "Noticia eliminada", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}