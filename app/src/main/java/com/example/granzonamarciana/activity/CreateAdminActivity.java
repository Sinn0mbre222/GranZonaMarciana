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
        initViews();

        btnCreate.setOnClickListener(v -> crearNuevoAdmin());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
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
    }

    private void crearNuevoAdmin() {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String last1 = etLastName1.getText().toString().trim();
        String last2 = etLastName2.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // VALIDACIONES OBLIGATORIAS
        if (user.isEmpty() || pass.isEmpty() || name.isEmpty() || last1.isEmpty() ||
                last2.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Todos los campos (excepto imagen) son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 9) {
            etPhone.setError("El teléfono debe tener 9 dígitos");
            return;
        }

        if (!email.contains("@")) {
            etEmail.setError("Introduce un email válido");
            return;
        }

        String finalImageUrl;
        if (imageUrl.isEmpty()) {
            finalImageUrl = "ic_person"; // Valor por defecto
        } else {
            finalImageUrl = imageUrl; // URL introducida
        }

        Administrador nuevoAdmin = new Administrador(
                user,
                BCrypt.hashpw(pass, BCrypt.gensalt()),
                name, last1, last2, phone, email,
                finalImageUrl,
                TipoRol.ADMINISTRADOR,
                LocalDate.now()
        );

        adminService.insertarAdministrador(nuevoAdmin);
        Toast.makeText(this, "Administrador creado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}