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

    private EditText etUsername, etPassword, etName, etLastName1, etLastName2, etPhone, etEmail, etImageUrl;
    private Button btnCancel, btnCreate;
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
        etImageUrl = findViewById(R.id.etImageUrl);
        btnCreate = findViewById(R.id.btnCreateUser);
        btnCancel = findViewById(R.id.btnCancel);


        btnCreate.setOnClickListener(v -> crearNuevoAdmin());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void crearNuevoAdmin() {
        // REQUISITO: Validar datos (simplificado para el ejemplo)
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String last1 = etLastName1.getText().toString().trim();
        String last2 = etLastName2.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // 2. VALIDACIÓN DE CAMPOS OBLIGATORIOS
        if (user.isEmpty()) {
            etUsername.setError("El usuario es obligatorio");
            etUsername.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            etPassword.setError("La contraseña es obligatoria");
            etPassword.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            etName.setError("El nombre es obligatorio");
            etName.requestFocus();
            return;
        }
        if (last1.isEmpty()) {
            etLastName1.setError("El primer apellido es obligatorio");
            etLastName1.requestFocus();
            return;
        }
        if (last2.isEmpty()) {
            etLastName2.setError("El segundo apellido es obligatorio");
            etLastName2.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            etPhone.setError("El teléfono es obligatorio");
            etPhone.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("El email es obligatorio");
            etEmail.requestFocus();
            return;
        }
        // --- VALIDACIÓN DE TELÉFONO (9 DÍGITOS) ---
        if (phone.length() != 9) {
            etPhone.setError("El teléfono debe tener exactamente 9 dígitos");
            etPhone.requestFocus();
            return;
        }

        // --- VALIDACIÓN DE EMAIL (DEBE CONTENER @) ---
        if (!email.contains("@") || email.length() < 5) {
            etEmail.setError("Introduce un correo electrónico válido");
            etEmail.requestFocus();
            return;
        }

        if (imageUrl.isEmpty()) {
            imageUrl = "ic_person"; // Ponemos el icono por defecto si el admin no pone URL
        }

        // Crear el objeto Administrador
        Administrador nuevoAdmin = new Administrador(
                user,
                BCrypt.hashpw(pass, BCrypt.gensalt()),
                name,
                last1,
                last2,
                phone,
                email,
                imageUrl,
                TipoRol.ADMINISTRADOR,
                LocalDate.now()
        );

        // Guardar en la base de datos
        adminService.insertarAdministrador(nuevoAdmin);

        Toast.makeText(this, "Administrador "+ user +" creado correctamente", Toast.LENGTH_SHORT).show();
        finish(); // Volver al menú
    }
}