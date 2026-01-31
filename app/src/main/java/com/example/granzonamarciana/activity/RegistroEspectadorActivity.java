package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.EspectadorService;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;

public class RegistroEspectadorActivity extends AppCompatActivity {

    private EspectadorService espectadorService;
    private EditText etUsername, etPassword, etName, etApellido1, etApellido2, etEmail, etPhone, etImageUrl;
    private ImageView ivTogglePassword;
    private Button btnFinalizeRegister;
    private TextView tvBackToLogin;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_espectador);

        // Inicializar el servicio
        espectadorService = new EspectadorService(this);

        initViews();
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
        btnFinalizeRegister = findViewById(R.id.btnFinalizeRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void setupListeners() {
        // Lógica para mostrar/ocultar contraseña
        ivTogglePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            passwordVisible = !passwordVisible;
            // Mantener el cursor al final del texto
            etPassword.setSelection(etPassword.getText().length());
        });

        // Botón Registrar
        btnFinalizeRegister.setOnClickListener(v -> registrarEspectador());

        // Botón Volver (TextView)
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void registrarEspectador() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etName.getText().toString().trim();
        String ap1 = etApellido1.getText().toString().trim();
        String ap2 = etApellido2.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String tlf = etPhone.getText().toString().trim();
        String img = etImageUrl.getText().toString().trim();

        // Validaciones básicas
        if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Imagen por defecto si está vacío
        if (img.isEmpty()) {
            img = "ic_default_avatar";
        }

        // Encriptar contraseña
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            // Crear el objeto Espectador
            Espectador e = new Espectador(
                    username,
                    hashedPassword,
                    nombre,
                    ap1,
                    ap2,
                    tlf,
                    email,
                    img,
                    TipoRol.ESPECTADOR,
                    LocalDate.now()
            );

            // Guardar en base de datos
            espectadorService.insertar(e);

            Toast.makeText(this, "¡Registro de Espectador completado!", Toast.LENGTH_LONG).show();

            // Redirigir al Login y limpiar la pila de actividades
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        } catch (Exception ex) {
            Toast.makeText(this, "Error en el registro: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}