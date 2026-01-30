package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RegistroActivity extends AppCompatActivity {

    // Servicios
    private ConcursanteService concursanteService;
    private EspectadorService espectadorService;

    // Vistas
    private EditText etUsername, etPassword, etName, etApellido1, etApellido2, etEmail, etPhone, etImageUrl;
    private ImageView ivTogglePassword;
    private Spinner spRol;
    private Button btnFinalizeRegister;
    private TextView tvBackToLogin;

    // Variables de control
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        concursanteService = new ConcursanteService(this);
        espectadorService = new EspectadorService(this);

        initViews();

        setupSpinner();

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
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        spRol = findViewById(R.id.spRol);
        btnFinalizeRegister = findViewById(R.id.btnFinalizeRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupSpinner() {
        List<String> roles = new ArrayList<>();
        roles.add("Concursante");
        roles.add("Espectador");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_rol_item,
                roles
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spRol.setAdapter(adapter);
    }

    private void setupListeners() {
        // Toggle visibilidad contraseña
        ivTogglePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            passwordVisible = !passwordVisible;
            // Mover el cursor al final del texto
            etPassword.setSelection(etPassword.getText().length());
        });

        // Botón Registrar
        btnFinalizeRegister.setOnClickListener(v -> registrarUsuario());

        // Volver al Login
        tvBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void registrarUsuario() {
        // 1. Obtener datos
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etName.getText().toString().trim();
        String apellido1 = etApellido1.getText().toString().trim();
        String apellido2 = etApellido2.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etPhone.getText().toString().trim();
        String imagenUrl = etImageUrl.getText().toString().trim();
        String rolSeleccionado = spRol.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido1.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenUrl.isEmpty()){
            imagenUrl= "ic_default_avatar";
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            if (rolSeleccionado.equals("Concursante")) {
                Concursante nuevoConcursante = new Concursante(
                        username,
                        hashedPassword,
                        nombre,
                        apellido1,
                        apellido2,
                        telefono,
                        email,
                        imagenUrl,
                        TipoRol.CONCURSANTE,
                        LocalDate.now()
                );
                concursanteService.insert(nuevoConcursante);

            } else if (rolSeleccionado.equals("Espectador")) {
                Espectador nuevoEspectador = new Espectador(
                        username,
                        hashedPassword,
                        nombre,
                        apellido1,
                        apellido2,
                        telefono,
                        email,
                        imagenUrl,
                        TipoRol.ESPECTADOR,
                        LocalDate.now()
                );

                espectadorService.insertar(nuevoEspectador);
            }

            Toast.makeText(this, "¡Registro completado! Ahora inicia sesión.", Toast.LENGTH_LONG).show();

            //Volver al Login
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error en el registro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}