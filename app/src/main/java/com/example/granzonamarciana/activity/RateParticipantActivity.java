package com.example.granzonamarciana.activity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
    private RatingBar ratingBar;
    private Button btnEnviar;
    private TextView tvNombre, tvRatingValue;
    private ImageView ivFoto;

    // Servicios para interactuar con la base de datos Room
    private EdicionService edicionService;
    private GalaService galaService;
    private PuntuacionService puntuacionService;

    // Variables de control y datos del Intent
    private int concursanteId, espectadorId;
    private int preSelectedEdicionId, preSelectedGalaId;
    private String concursanteNombre, concursanteFoto;

    // Listas para manejar los datos de los selectores
    private List<Edicion> listaEdiciones = new ArrayList<>();
    private List<Gala> listaGalas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_participant);

        // 1. Recuperamos la información del concursante y el contexto de la gala
        recuperarDatosIntent();

        initViews();
        initServices();

        // 2. Mostramos los datos del concursante en la cabecera
        tvNombre.setText(concursanteNombre);
        cargarImagenConcursante();

        // 3. Iniciamos la carga en cascada de los selectores
        cargarEdiciones();

        // Actualizamos el texto informativo según se muevan las estrellas
        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) ->
                tvRatingValue.setText("Puntuación: " + (int)rating + "/5")
        );

        btnEnviar.setOnClickListener(v -> enviarVoto());

        // Botón para cerrar la actividad sin guardar
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void recuperarDatosIntent() {
        concursanteId = getIntent().getIntExtra("CONCURSANTE_ID", -1);
        concursanteNombre = getIntent().getStringExtra("CONCURSANTE_NOMBRE");
        concursanteFoto = getIntent().getStringExtra("CONCURSANTE_FOTO");

        // Si venimos de una gala específica, guardamos los IDs para pre-seleccionar los Spinners
        preSelectedEdicionId = getIntent().getIntExtra("GALA_EDICION_ID", -1);
        preSelectedGalaId = getIntent().getIntExtra("GALA_ID", -1);

        // Obtenemos el ID del espectador que está logueado
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

    private void cargarImagenConcursante() {
        // Lógica para cargar la foto: soporta URL de internet (Picasso) o recurso local
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

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                int pos = 0;

                for (int i = 0; i < ediciones.size(); i++) {
                    labels.add("Edición " + ediciones.get(i).getId());
                    if (ediciones.get(i).getId() == preSelectedEdicionId) pos = i;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_rol_item, labels) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setTextColor(Color.WHITE);
                        return v;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        TextView v = (TextView) super.getDropDownView(position, convertView, parent);
                        v.setTextColor(Color.BLACK);
                        return v;
                    }
                };

                spinnerEdiciones.setAdapter(adapter);
                spinnerEdiciones.setSelection(pos);

                // Al cambiar la edición, cargamos sus galas correspondientes
                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int position, long id) {
                        cargarGalasDeEdicion(listaEdiciones.get(position).getId());
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });

                if (!listaEdiciones.isEmpty()) {
                    cargarGalasDeEdicion(listaEdiciones.get(pos).getId());
                }
            }
        });
    }

    private void cargarGalasDeEdicion(int editionId) {
        galaService.getGalasByEdicion(editionId).observe(this, galas -> {
            listaGalas = (galas != null) ? galas : new ArrayList<>();
            List<String> nombres = new ArrayList<>();
            int pos = 0;

            if (listaGalas.isEmpty()) {
                nombres.add("Sin galas");
                bloquearBoton("No hay galas", Color.GRAY);
                spinnerGalas.setEnabled(false);
            } else {
                spinnerGalas.setEnabled(true);
                for (int i = 0; i < listaGalas.size(); i++) {
                    nombres.add("Gala " + listaGalas.get(i).getId());
                    if (listaGalas.get(i).getId() == preSelectedGalaId) pos = i;
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_rol_item, nombres) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView v = (TextView) super.getView(position, convertView, parent);
                    v.setTextColor(Color.WHITE);
                    return v;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    TextView v = (TextView) super.getDropDownView(position, convertView, parent);
                    v.setTextColor(Color.BLACK);
                    return v;
                }
            };

            spinnerGalas.setAdapter(adapter);

            if (!listaGalas.isEmpty()) {
                spinnerGalas.setSelection(pos);
                evaluarEstadoGala(listaGalas.get(pos));

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

    // Lógica de negocio: Comprobamos si la gala es apta para recibir votos
    private void evaluarEstadoGala(Gala gala) {
        LocalDate hoy = LocalDate.now();

        // 1. Gala futura
        if (gala.getFecha().isAfter(hoy)) {
            bloquearBoton("Gala no comenzada", Color.DKGRAY);
        }
        // 2. Gala terminada (más de 1 día de antigüedad)
        else if (gala.getFecha().isBefore(hoy.minusDays(1))) {
            bloquearBoton("Plazo cerrado", Color.RED);
        }
        // 3. Gala en curso: verificamos si el usuario ya emitió su voto
        else {
            verificarSiYaVoto(gala.getId());
        }
    }

    private void verificarSiYaVoto(int galaId) {
        puntuacionService.haVotado(galaId, espectadorId, concursanteId).observe(this, yaVotado -> {
            if (yaVotado != null && yaVotado) {
                bloquearBoton("Ya has votado", Color.parseColor("#FF9800"));
            } else {
                habilitarBoton();
            }
        });
    }

    // Cambia el estado del botón de envío para impedir acciones prohibidas
    private void bloquearBoton(String msg, int color) {
        btnEnviar.setEnabled(false);
        btnEnviar.setText(msg);
        btnEnviar.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void habilitarBoton() {
        btnEnviar.setEnabled(true);
        btnEnviar.setText("Confirmar Voto");
        btnEnviar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
    }

    private void enviarVoto() {
        int pos = spinnerGalas.getSelectedItemPosition();
        if (pos < 0 || listaGalas.isEmpty()) return;

        int rating = (int) ratingBar.getRating();
        if (rating < 1) {
            Toast.makeText(this, "Selecciona al menos 1 estrella", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos y guardamos el objeto puntuación en Room
        Puntuacion voto = new Puntuacion(espectadorId, concursanteId, listaGalas.get(pos).getId(), rating, LocalDate.now());
        puntuacionService.puntuar(voto);

        Toast.makeText(this, "¡Votado!", Toast.LENGTH_SHORT).show();
        finish();
    }
}