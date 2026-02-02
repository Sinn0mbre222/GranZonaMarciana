package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.AdministradorService;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDate;

public class CreateAdminActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etName, etLastName1, etLastName2, etPhone, etEmail;
    private AdministradorService adminService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_admin);

        adminService = new AdministradorService(this);

        // Vincular vistas
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etLastName1 = findViewById(R.id.etLastName1);
        etLastName2 = findViewById(R.id.etLastName2);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        Button btnCreate = findViewById(R.id.btnCreateUser);

        btnCreate.setOnClickListener(v -> crearNuevoAdmin());
    }

    private void crearNuevoAdmin() {
        // REQUISITO: Validar datos (simplificado para el ejemplo)
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Usuario y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto Administrador
        Administrador nuevoAdmin = new Administrador(
                user,
                BCrypt.hashpw(pass, BCrypt.gensalt()), // Encriptar contraseña
                etName.getText().toString().trim(),
                etLastName1.getText().toString().trim(),
                etLastName2.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                "", // Foto vacía por ahora
                TipoRol.ADMINISTRADOR,
                LocalDate.now()
        );

        // Guardar en la base de datos
        adminService.insertarAdministrador(nuevoAdmin);

        Toast.makeText(this, "Administrador creado correctamente", Toast.LENGTH_SHORT).show();
        finish(); // Volver al menú
    }
}