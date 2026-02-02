package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Solicitud;
import com.example.granzonamarciana.entity.EstadoSolicitud;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.SolicitudService;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistroConcursanteActivity extends AppCompatActivity {

    private ConcursanteService concursanteService;
    private EdicionService edicionService;
    private SolicitudService solicitudService;

    private EditText etUsername, etPassword, etName, etApellido1, etApellido2, etEmail, etPhone, etImageUrl, etMotivo;
    private Spinner spEdicion;
    private ImageView ivTogglePassword;
    private boolean passwordVisible = false;
    private List<Edicion> listaEdiciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_concursante);

        concursanteService = new ConcursanteService(this);
        edicionService = new EdicionService(this);
        solicitudService = new SolicitudService(this);

        initViews();
        setupEditionSpinner();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etImageUrl = findViewById(R.id.etImageUrl);
        etMotivo = findViewById(R.id.etMotivo);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        spEdicion = findViewById(R.id.spEdicion);
    }

    private void setupEditionSpinner() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> nombresEdiciones = new ArrayList<>();
                for (Edicion e : ediciones) {
                    nombresEdiciones.add("Edición #" + e.getId() + " (" + e.getFechaInicio() + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresEdiciones);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spEdicion.setAdapter(adapter);
            }
        });
    }

    private void setupListeners() {
        ivTogglePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            passwordVisible = !passwordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        findViewById(R.id.btnFinalizeRegister).setOnClickListener(v -> registrarUsuario());
        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        // 1. Obtener todos los textos
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String apellido1 = etApellido1.getText().toString().trim();
        String apellido2 = etApellido2.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String motivacion = etMotivo.getText().toString().trim();
        String imageUrlInput = etImageUrl.getText().toString().trim();

        // 2. Validación: Todos obligatorios EXCEPTO la imagen
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() ||
                apellido1.isEmpty() || apellido2.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || motivacion.isEmpty()) {

            Toast.makeText(this, "Todos los campos son obligatorios (excepto imagen)", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Lógica de la imagen para Picasso
        // Si el usuario deja el campo vacío, usamos la referencia local "ic_default_avatar".
        // Si escribe algo (una URL), guardamos esa URL tal cual.
        String finalImageUrl;
        if (imageUrlInput.isEmpty()) {
            finalImageUrl = "ic_default_avatar";
        } else {
            finalImageUrl = imageUrlInput;
        }

        // 4. Crear el objeto Concursante
        Concursante c = new Concursante(
                username,
                BCrypt.hashpw(password, BCrypt.gensalt()),
                name,
                apellido1,
                apellido2,
                phone,
                email,
                finalImageUrl, // Guardamos la URL o el avatar por defecto
                TipoRol.CONCURSANTE,
                LocalDate.now()
        );

        // 5. Insertar concursante y LUEGO crear la solicitud
        concursanteService.insert(c);

        // Esperamos a que se inserte para recuperar el ID
        concursanteService.buscarConcursantePorUsername(username).observe(this, nuevoCon -> {
            if (nuevoCon != null) {

                // Validación extra por si la lista de ediciones no ha cargado
                if (listaEdiciones.isEmpty()) {
                    Toast.makeText(this, "No hay ediciones disponibles", Toast.LENGTH_SHORT).show();
                    return;
                }

                int edicionId = listaEdiciones.get(spEdicion.getSelectedItemPosition()).getId();

                Solicitud nuevaSolicitud = new Solicitud(
                        edicionId,
                        nuevoCon.getId(),
                        motivacion,
                        EstadoSolicitud.PENDIENTE
                );

                solicitudService.insert(nuevaSolicitud);

                Toast.makeText(this, "Registro completado. ¡Mucha suerte!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });
    }
}