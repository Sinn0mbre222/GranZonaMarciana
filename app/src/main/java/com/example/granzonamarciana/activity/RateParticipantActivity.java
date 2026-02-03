package com.example.granzonamarciana.activity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.GalaService;
import com.example.granzonamarciana.service.PuntuacionService;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RateParticipantActivity extends AppCompatActivity {

    private Spinner spinnerEdiciones, spinnerGalas;
    private RatingBar ratingBar;
    private Button btnEnviar;
    private TextView tvNombre, tvRatingValue;
    private ImageView ivFoto;

    private EdicionService edicionService;
    private GalaService galaService;
    private PuntuacionService puntuacionService;

    // Datos recibidos del Intent
    private int concursanteId, espectadorId;
    private int preSelectedEdicionId, preSelectedGalaId;
    private String concursanteNombre, concursanteFoto;

    // Listas de datos
    private List<Edicion> listaEdiciones = new ArrayList<>();
    private List<Gala> listaGalas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_participant);

        // 1. Recuperar datos del Intent
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        concursanteNombre = getIntent().getStringExtra("CONCURSANTE_NOMBRE");
        concursanteFoto = getIntent().getStringExtra("CONCURSANTE_FOTO");
        preSelectedEdicionId = getIntent().getIntExtra("GALA_EDICION_ID", -1);
        preSelectedGalaId = getIntent().getIntExtra("GALA_ID", -1);

        // 2. Recuperar sesión del usuario
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        espectadorId = prefs.getInt("id", -1);

        if (concursanteId == -1 || espectadorId == -1) {
            Toast.makeText(this, "Error de sesión o datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initServices();

        tvNombre.setText(concursanteNombre);
        cargarImagenConcursante();

        // 3. Iniciar carga de datos en cascada
        cargarEdiciones();

        // Listeners de la interfaz
        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) ->
                tvRatingValue.setText("Puntuación: " + (int)rating + "/5")
        );

        btnEnviar.setOnClickListener(v -> enviarVoto());

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        spinnerGalas = findViewById(R.id.spinnerActiveGala);
        ratingBar = findViewById(R.id.ratingBar);
        btnEnviar = findViewById(R.id.btnSubmitRating);
        tvNombre = findViewById(R.id.tvRateName);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        ivFoto = findViewById(R.id.ivRatePhoto);
    }

    private void initServices() {
        edicionService = new EdicionService(this);
        galaService = new GalaService(this);
        puntuacionService = new PuntuacionService(this);
    }

    private void cargarImagenConcursante() {
        if (concursanteFoto != null && concursanteFoto.startsWith("http")) {
            Picasso.get().load(concursanteFoto)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(ivFoto);
        } else {
            ivFoto.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = new ArrayList<>(ediciones);
                List<String> labels = new ArrayList<>();
                int posSeleccionada = 0;

                for (int i = 0; i < listaEdiciones.size(); i++) {
                    Edicion e = listaEdiciones.get(i);
                    labels.add("Edición " + e.getId());
                    if (e.getId() == preSelectedEdicionId) posSeleccionada = i;
                }

                // Usamos tu layout personalizado para que el texto sea blanco
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, labels);
                adapter.setDropDownViewResource(R.layout.spinner_rol_item);

                spinnerEdiciones.setAdapter(adapter);
                spinnerEdiciones.setSelection(posSeleccionada);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        cargarGalasDeEdicion(listaEdiciones.get(pos).getId());
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
            } else {
                bloquearBoton("No hay ediciones", Color.GRAY);
            }
        });
    }

    private void cargarGalasDeEdicion(int editionId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            listaGalas = new ArrayList<>();
            List<String> nombresGalas = new ArrayList<>();
            int posSeleccionada = 0;

            if (galas != null) {
                listaGalas = galas;
                for (int i = 0; i < listaGalas.size(); i++) {
                    Gala g = listaGalas.get(i);
                    nombresGalas.add("Gala " + g.getId() + " (" + g.getFecha() + ")");
                    if (g.getId() == preSelectedGalaId) posSeleccionada = i;
                }
            }

            if (listaGalas.isEmpty()) {
                nombresGalas.add("Sin galas registradas");
                bloquearBoton("No hay galas", Color.GRAY);
                spinnerGalas.setEnabled(false);
            } else {
                spinnerGalas.setEnabled(true);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresGalas);
            adapter.setDropDownViewResource(R.layout.spinner_rol_item);
            spinnerGalas.setAdapter(adapter);

            if (!listaGalas.isEmpty()) {
                spinnerGalas.setSelection(posSeleccionada);
                spinnerGalas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        evaluarEstadoGala(listaGalas.get(position));
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
        });
    }

    private void evaluarEstadoGala(Gala gala) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaGala = gala.getFecha();

        if (fechaGala.isAfter(hoy)) {
            bloquearBoton("La gala todavía no ha comenzado", Color.DKGRAY);
        } else if (fechaGala.isBefore(hoy.minusDays(1))) {
            bloquearBoton("Plazo de votación finalizado", Color.RED);
        } else {
            verificarSiYaVoto(gala.getId());
        }
    }

    private void verificarSiYaVoto(int galaId) {
        btnEnviar.setText("Comprobando...");
        btnEnviar.setEnabled(false);

        puntuacionService.haVotado(galaId, espectadorId, concursanteId).observe(this, yaVotado -> {
            if (yaVotado != null && yaVotado) {
                bloquearBoton("Voto ya registrado", Color.parseColor("#FF9800")); // Naranja
            } else {
                habilitarBoton();
            }
        });
    }

    private void bloquearBoton(String mensaje, int color) {
        btnEnviar.setEnabled(false);
        btnEnviar.setText(mensaje);
        btnEnviar.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void habilitarBoton() {
        btnEnviar.setEnabled(true);
        btnEnviar.setText("Confirmar Voto");
        // Uso de ContextCompat para mayor compatibilidad de colores
        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        btnEnviar.setBackgroundTintList(ColorStateList.valueOf(colorPrimary));
    }

    private void enviarVoto() {
        int pos = spinnerGalas.getSelectedItemPosition();
        if (pos < 0 || listaGalas.isEmpty()) return;

        Gala galaSeleccionada = listaGalas.get(pos);
        LocalDate hoy = LocalDate.now();

        // Validación final de fecha antes de procesar
        if (galaSeleccionada.getFecha().isAfter(hoy) ||
                galaSeleccionada.getFecha().isBefore(hoy.minusDays(1))) {
            Toast.makeText(this, "Gala fuera de plazo", Toast.LENGTH_SHORT).show();
            evaluarEstadoGala(galaSeleccionada);
            return;
        }

        int rating = (int) ratingBar.getRating();
        if (rating < 1) {
            Toast.makeText(this, "Selecciona una puntuación", Toast.LENGTH_SHORT).show();
            return;
        }

        Puntuacion voto = new Puntuacion(
                espectadorId,
                concursanteId,
                galaSeleccionada.getId(),
                rating,
                LocalDate.now()
        );

        puntuacionService.puntuar(voto);
        Toast.makeText(this, "¡Voto enviado con éxito!", Toast.LENGTH_SHORT).show();
        finish();
    }
}