package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
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
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_espectador);

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
    }

    private void setupListeners() {
        ivTogglePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            passwordVisible = !passwordVisible;
            etPassword.setSelection(etPassword.getText().length());
        });

        btnFinalizeRegister.setOnClickListener(v -> registrarEspectador());
        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
    }

    private void registrarEspectador() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etName.getText().toString().trim();
        String ap1 = etApellido1.getText().toString().trim();
        String ap2 = etApellido2.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String tlf = etPhone.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() || ap1.isEmpty() || ap2.isEmpty() || email.isEmpty() || tlf.isEmpty()) {
            Toast.makeText(this, "Campos obligatorios vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tlf.length() != 9) {
            etPhone.setError("9 dígitos");
            return;
        }

        if (!email.contains("@")) {
            etEmail.setError("Email no válido");
            return;
        }

        // IMAGEN OPCIONAL
        String finalImageUrl;
        if (imageUrl.isEmpty()) {
            finalImageUrl = "ic_person"; // Valor por defecto
        } else {
            finalImageUrl = imageUrl; // URL introducida
        }
        Espectador e = new Espectador(
                username, BCrypt.hashpw(password, BCrypt.gensalt()),
                nombre, ap1, ap2, tlf, email, finalImageUrl,
                TipoRol.ESPECTADOR, LocalDate.now()
        );

        espectadorService.insertar(e);
        Toast.makeText(this, "Registro de Espectador completado", Toast.LENGTH_LONG).show();
        finish();
    }
}