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

// Pantalla exclusiva para ESPECTADORES.
// Permite asignar una puntuación (1-5 estrellas) a un concursante en una gala específica.
public class RateParticipantActivity extends AppCompatActivity {

    // Componentes de la interfaz
    private Spinner spinnerEdiciones, spinnerGalas;
    private RatingBar ratingBar; // Las estrellas
    private Button btnEnviar;
    private TextView tvNombre, tvRatingValue; // Nombre del concursante y texto "Puntuación: 3/5"
    private ImageView ivFoto;

    // Servicios de BD
    private EdicionService edicionService;
    private GalaService galaService;
    private PuntuacionService puntuacionService;

    // Variables de datos
    private int concursanteId, espectadorId;
    // Estos "preSelected" vienen de la pantalla anterior para que el spinner ya salga en la posición correcta
    private int preSelectedEdicionId, preSelectedGalaId;
    private String concursanteNombre, concursanteFoto;

    // Listas para rellenar los selectores (Spinners)
    private List<Edicion> listaEdiciones = new ArrayList<>();
    private List<Gala> listaGalas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_participant);

        // 1. Recuperar info del Intent (quién soy y a quién voto)
        recuperarDatosIntent();

        initViews();
        initServices();

        // 2. Pintar datos básicos
        tvNombre.setText(concursanteNombre);
        cargarImagenConcursante();

        // 3. Iniciar la carga de datos (Ediciones -> Galas -> Estado Voto)
        cargarEdiciones();

        // Listener para actualizar el texto cuando cambias las estrellas
        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) ->
                tvRatingValue.setText("Puntuación: " + (int)rating + "/5")
        );

        btnEnviar.setOnClickListener(v -> enviarVoto());

        // Botón volver
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    // Obtiene los datos enviados desde ParticipantsListActivity o ParticipantPublicActivity
    private void recuperarDatosIntent() {
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        concursanteNombre = getIntent().getStringExtra("CONCURSANTE_NOMBRE");
        concursanteFoto = getIntent().getStringExtra("CONCURSANTE_FOTO");

        // IDs opcionales para pre-seleccionar en los spinners
        preSelectedEdicionId = getIntent().getIntExtra("GALA_EDICION_ID", -1);
        preSelectedGalaId = getIntent().getIntExtra("GALA_ID", -1);

        // ID del usuario logueado (Espectador)
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        espectadorId = prefs.getInt("id", -1);
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

    // Carga la foto usando Picasso (URL) o recursos locales
    private void cargarImagenConcursante() {
        if (concursanteFoto != null) {
            if(concursanteFoto.startsWith("http")) {
                Picasso.get().load(concursanteFoto).placeholder(R.drawable.ic_default_avatar).into(ivFoto);
            } else {
                int resId = getResources().getIdentifier(concursanteFoto, "drawable", getPackageName());
                if (resId != 0) ivFoto.setImageResource(resId);
                else ivFoto.setImageResource(R.drawable.ic_default_avatar);
            }
        } else {
            ivFoto.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    // Paso 1 de la carga: Obtener Ediciones
    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                int pos = 0;

                // Creamos lista de nombres y buscamos la posición preseleccionada
                for (int i = 0; i < ediciones.size(); i++) {
                    labels.add("Edición " + ediciones.get(i).getId());
                    if (ediciones.get(i).getId() == preSelectedEdicionId) pos = i;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RateParticipantActivity.this, R.layout.spinner_rol_item, labels);
                adapter.setDropDownViewResource(R.layout.spinner_rol_item); // Usamos el mismo diseño para el dropdown
                spinnerEdiciones.setAdapter(adapter);

                spinnerEdiciones.setSelection(pos);

                // Al cambiar de edición, cargamos sus galas
                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int position, long id) {
                        cargarGalasDeEdicion(listaEdiciones.get(position).getId());
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });

                // Carga inicial forzada
                if (!listaEdiciones.isEmpty()) {
                    cargarGalasDeEdicion(listaEdiciones.get(pos).getId());
                }

            } else {
                Toast.makeText(this, "No hay ediciones disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Paso 2 de la carga: Obtener Galas de la edición seleccionada
    private void cargarGalasDeEdicion(int editionId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            listaGalas = (galas != null) ? galas : new ArrayList<>();
            List<String> nombres = new ArrayList<>();
            int pos = 0;

            if (listaGalas.isEmpty()) {
                nombres.add("Sin galas");
                bloquearBoton("No hay galas", Color.GRAY); // Si no hay galas, no se puede votar
                spinnerGalas.setEnabled(false);
            } else {
                spinnerGalas.setEnabled(true);
                for (int i = 0; i < listaGalas.size(); i++) {
                    nombres.add("Gala " + listaGalas.get(i).getId());
                    if (listaGalas.get(i).getId() == preSelectedGalaId) pos = i;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(RateParticipantActivity.this, R.layout.spinner_rol_item, nombres);
            adapter.setDropDownViewResource(R.layout.spinner_rol_item);
            spinnerGalas.setAdapter(adapter);

            if (!listaGalas.isEmpty()) {
                spinnerGalas.setSelection(pos);
                // Validamos la gala seleccionada inicialmente
                evaluarEstadoGala(listaGalas.get(pos));

                // Si cambia la gala, re-evaluamos si se puede votar
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

    // LÓGICA DE VALIDACIÓN (Reglas de negocio)
    private void evaluarEstadoGala(Gala gala) {
        LocalDate hoy = LocalDate.now();

        // Regla 1: No se puede votar en galas futuras
        if (gala.getFecha().isAfter(hoy)) {
            bloquearBoton("Gala no comenzada", Color.DKGRAY);
        }
        // Regla 2: No se puede votar en galas pasadas (margen de 1 día)
        else if (gala.getFecha().isBefore(hoy.minusDays(1))) {
            bloquearBoton("Plazo cerrado", Color.RED);
        }
        // Regla 3: Si la fecha es válida, comprobamos si YA votó
        else {
            verificarSiYaVoto(gala.getId());
        }
    }

    // Consulta a la BD si existe un voto de este usuario para este concursante en esta gala
    private void verificarSiYaVoto(int galaId) {
        puntuacionService.haVotado(galaId, espectadorId, concursanteId).observe(this, yaVotado -> {
            if (yaVotado != null && yaVotado) {
                bloquearBoton("Ya has votado", Color.parseColor("#FF9800")); // Naranja
            } else {
                habilitarBoton(); // Todo OK -> Se puede votar
            }
        });
    }

    // Metodo visual para desactivar el botón y cambiar su color
    private void bloquearBoton(String msg, int color) {
        btnEnviar.setEnabled(false);
        btnEnviar.setText(msg);
        btnEnviar.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    // Metodo visual para reactivar el botón con el color original
    private void habilitarBoton() {
        btnEnviar.setEnabled(true);
        btnEnviar.setText("Confirmar Voto");
        btnEnviar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
    }

    // Acción final: Guardar en base de datos
    private void enviarVoto() {
        int pos = spinnerGalas.getSelectedItemPosition();
        if (pos < 0 || listaGalas.isEmpty()) return;

        // Validación de seguridad por si acaso
        int rating = (int) ratingBar.getRating();
        if (rating < 1 || rating > 5) {
            Toast.makeText(this, "Puntuación inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos el objeto Puntuacion y lo guardamos
        Puntuacion voto = new Puntuacion(espectadorId, concursanteId, listaGalas.get(pos).getId(), rating, LocalDate.now());
        puntuacionService.puntuar(voto);

        Toast.makeText(this, "¡Votado!", Toast.LENGTH_SHORT).show();
        finish(); // Cerramos la pantalla al terminar
    }
}