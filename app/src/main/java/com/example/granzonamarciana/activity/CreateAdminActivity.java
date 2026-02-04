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

        // Iniciamos el servicio administrativo
        adminService = new AdministradorService(this);
        initViews();

        // Configuración de los botones para crear o cancelar la operación
        btnCreate.setOnClickListener(v -> crearNuevoAdmin());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        // Enlace de los campos de texto del formulario de administración
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
        // Captura de datos
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String last1 = etLastName1.getText().toString().trim();
        String last2 = etLastName2.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Los administradores requieren todos los campos para contacto y seguridad
        if (user.isEmpty() || pass.isEmpty() || name.isEmpty() || last1.isEmpty() ||
                last2.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Todos los campos (excepto imagen) son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validaciones de formato obligatorias
        if (phone.length() != 9) {
            etPhone.setError("El teléfono debe tener 9 dígitos");
            return;
        }

        if (!email.contains("@")) {
            etEmail.setError("Introduce un email válido");
            return;
        }

        String finalImageUrl;

        // Comprobamos si el campo de texto de la URL está vacío
        if (imageUrl.isEmpty()) {
            // Si está vacío, asignamos el nombre del recurso por defecto (avatar gris)
            finalImageUrl = "ic_person";
        } else {
            // Si el usuario ha escrito algo, usamos esa URL o nombre de archivo
            finalImageUrl = imageUrl;
        }
        // Construimos el objeto Administrador. Importante: se usa BCrypt para la password
        Administrador nuevoAdmin = new Administrador(
                user,
                BCrypt.hashpw(pass, BCrypt.gensalt()),
                name, last1, last2, phone, email,
                finalImageUrl,
                TipoRol.ADMINISTRADOR,
                LocalDate.now()
        );

        // Ejecutamos la inserción y cerramos la pantalla para volver al menú anterior
        adminService.insertarAdministrador(nuevoAdmin);
        Toast.makeText(this, "Administrador creado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}