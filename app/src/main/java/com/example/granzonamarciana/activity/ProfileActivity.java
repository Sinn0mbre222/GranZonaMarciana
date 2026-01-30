package com.example.granzonamarciana.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.service.AdministradorService;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;

import org.mindrot.jbcrypt.BCrypt;

public class ProfileActivity extends AppCompatActivity {

    // Vistas
    private EditText etNombre, etApellido1, etApellido2, etEmail, etTelefono;
    private TextView tvStat1, tvStat2;
    private ImageView ivProfileImage;
    private Button btnGuardar, btnCambiarPass;

    // Servicios
    private AdministradorService adminService;
    private ConcursanteService concursanteService;
    private EspectadorService espectadorService;

    // Datos del usuario actual
    private Administrador currentAdmin;
    private Concursante currentConcursante;
    private Espectador currentEspectador;

    private String userRole;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();

        adminService = new AdministradorService(this);
        concursanteService = new ConcursanteService(this);
        espectadorService = new EspectadorService(this);

        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        userId = prefs.getInt("id", -1);
        userRole = prefs.getString("rol", "");

        if (userId == -1) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserData();

        btnGuardar.setOnClickListener(v -> saveChanges());
        btnCambiarPass.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);

        tvStat1 = findViewById(R.id.tvStat1);
        tvStat2 = findViewById(R.id.tvStat2);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCambiarPass = findViewById(R.id.btnCambiarPass);
    }

    private void loadUserData() {
        switch (userRole) {
            case "ADMINISTRADOR":
                adminService.buscarAdministradorPorId(userId).observe(this, admin -> {
                    if (admin != null) {
                        currentAdmin = admin;
                        populateFields(admin.getNombre(), admin.getPrimerApellido(), admin.getSegundoApellido(), admin.getEmail(), admin.getTelefono(), "ic_person");
                        tvStat1.setText("Rol: Administrador");
                        tvStat2.setVisibility(View.GONE);
                    }
                });
                break;

            case "CONCURSANTE":
                concursanteService.obtenerPorId(userId).observe(this, concu -> {
                    if (concu != null) {
                        currentConcursante = concu;
                        populateFields(concu.getNombre(), concu.getPrimerApellido(), concu.getSegundoApellido(), concu.getEmail(), concu.getTelefono(), concu.getImagenUrl());
                        tvStat1.setText("Rol: Concursante");
                        tvStat2.setText("Fecha Inscripción: " + concu.getFechaRegistro().toString());
                    }
                });
                break;

            case "ESPECTADOR":
                espectadorService.obtenerPorId(userId).observe(this, espec -> {
                    if (espec != null) {
                        currentEspectador = espec;
                        populateFields(espec.getNombre(), espec.getPrimerApellido(), espec.getSegundoApellido(), espec.getEmail(), espec.getTelefono(), espec.getImagenUrl());
                        tvStat1.setText("Rol: Espectador");
                        tvStat2.setText("Cuenta activa desde: " + espec.getFechaRegistro().toString());
                    }
                });
                break;
        }
    }

    private void populateFields(String nombre, String ap1, String ap2, String email, String tlf, String imgName) {
        etNombre.setText(nombre);
        etApellido1.setText(ap1);
        etApellido2.setText(ap2);
        etEmail.setText(email);
        etTelefono.setText(tlf);

        if (imgName != null && !imgName.isEmpty()) {
            int resId = getResources().getIdentifier(imgName, "drawable", getPackageName());
            if (resId != 0) {
                ivProfileImage.setImageResource(resId);
            } else {
                ivProfileImage.setImageResource(R.drawable.ic_granzonamarciana);
            }
        }
    }

    private void saveChanges() {
        String nombre = etNombre.getText().toString().trim();
        String ap1 = etApellido1.getText().toString().trim();
        String ap2 = etApellido2.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String tlf = etTelefono.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nombre y Email son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            switch (userRole) {
                case "ADMINISTRADOR":
                    if (currentAdmin != null) {
                        currentAdmin.setNombre(nombre);
                        currentAdmin.setPrimerApellido(ap1);
                        currentAdmin.setSegundoApellido(ap2);
                        currentAdmin.setEmail(email);
                        currentAdmin.setTelefono(tlf);
                        adminService.actualizarAdministrador(currentAdmin);
                    }
                    break;
                case "CONCURSANTE":
                    if (currentConcursante != null) {
                        currentConcursante.setNombre(nombre);
                        currentConcursante.setPrimerApellido(ap1);
                        currentConcursante.setSegundoApellido(ap2);
                        currentConcursante.setEmail(email);
                        currentConcursante.setTelefono(tlf);
                        concursanteService.actualizar(currentConcursante);
                    }
                    break;
                case "ESPECTADOR":
                    if (currentEspectador != null) {
                        currentEspectador.setNombre(nombre);
                        currentEspectador.setPrimerApellido(ap1);
                        currentEspectador.setSegundoApellido(ap2);
                        currentEspectador.setEmail(email);
                        currentEspectador.setTelefono(tlf);
                        espectadorService.actualizar(currentEspectador);
                    }
                    break;
            }
            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Contraseña");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etOldPass = new EditText(this);
        etOldPass.setHint("Contraseña Actual");
        etOldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etOldPass);

        final EditText etNewPass = new EditText(this);
        etNewPass.setHint("Nueva Contraseña");
        etNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass);

        builder.setView(layout);

        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            String oldP = etOldPass.getText().toString();
            String newP = etNewPass.getText().toString();
            verifyAndUpdatePassword(oldP, newP);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void verifyAndUpdatePassword(String oldPass, String newPass) {
        if (newPass.length() < 4) {
            Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_LONG).show();
            return;
        }

        boolean isMatch = false;
        String hashedPassword = BCrypt.hashpw(newPass, BCrypt.gensalt());

        if (userRole.equals("ADMINISTRADOR") && currentAdmin != null) {
            if (BCrypt.checkpw(oldPass, currentAdmin.getPassword())) {
                currentAdmin.setPassword(hashedPassword);
                adminService.actualizarAdministrador(currentAdmin); // Verifica si es 'actualizar'
                isMatch = true;
            }
        } else if (userRole.equals("CONCURSANTE") && currentConcursante != null) {
            if (BCrypt.checkpw(oldPass, currentConcursante.getPassword())) {
                currentConcursante.setPassword(hashedPassword);
                // CORREGIDO: update -> actualizar
                concursanteService.actualizar(currentConcursante);
                isMatch = true;
            }
        } else if (userRole.equals("ESPECTADOR") && currentEspectador != null) {
            if (BCrypt.checkpw(oldPass, currentEspectador.getPassword())) {
                currentEspectador.setPassword(hashedPassword);
                // CORREGIDO: update -> actualizar
                espectadorService.actualizar(currentEspectador);
                isMatch = true;
            }
        }

        if (isMatch) {
            Toast.makeText(this, "¡Contraseña cambiada con éxito!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "La contraseña actual no es correcta", Toast.LENGTH_LONG).show();
        }
    }
}