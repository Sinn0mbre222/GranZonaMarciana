package com.example.granzonamarciana.activity;

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
import com.example.granzonamarciana.entity.Puntuacion;
import com.example.granzonamarciana.service.AdministradorService;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;
import com.example.granzonamarciana.service.PuntuacionService;
import com.squareup.picasso.Picasso;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Locale;

// Esta Activity sirve para dos cosas:
// 1. Ver y editar "MI PERFIL" (usuario logueado).
// 2. Ver el perfil de OTRO usuario en modo "SOLO LECTURA" (cuando entra un Admin).
public class ProfileActivity extends AppCompatActivity {

    // Variables para los elementos visuales de la pantalla
    private EditText etNombre, etApellido1, etApellido2, etEmail, etTelefono, etImageUrl;
    private TextView tvUsername, tvUserRole, tvJoinDate, tvRatingMedia;
    private ImageView ivProfileImage;
    private Button btnGuardar, btnCambiarPass;
    private LinearLayout layoutEstadisticas;

    // Servicios para conectar con la Base de Datos
    private AdministradorService adminService;
    private ConcursanteService concursanteService;
    private EspectadorService espectadorService;
    private PuntuacionService puntuacionService;

    // Objetos para guardar temporalmente los datos del usuario cargado
    private Administrador currentAdmin;
    private Concursante currentConcursante;
    private Espectador currentEspectador;

    // Datos de control
    private String userRole; // Rol del usuario (ADMINISTRADOR, CONCURSANTE, ESPECTADOR)
    private int userId;      // ID del usuario
    private boolean isReadOnly = false; // Bandera para saber si bloqueamos la edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Inicializamos vistas y servicios de BD
        initViews();
        initServices();

        // 1. Comprobar si venimos desde otra pantalla (ej. Admin pulsando en lista de usuarios)
        // Si el Intent trae datos, significa que estamos en modo "Ver perfil de otro"
        int intentId = getIntent().getIntExtra("TARGET_USER_ID", -1);
        String intentRole = getIntent().getStringExtra("TARGET_USER_ROLE");

        if (intentId != -1 && intentRole != null) {
            // MODO SOLO LECTURA (Admin viendo a otro)
            userId = intentId;
            userRole = intentRole;
            isReadOnly = true; // Activamos el bloqueo
            activarModoLectura(); // Bloqueamos los campos
        } else {
            // 2. Si no hay Intent, cargamos la sesión actual (SharedPreferences)
            // MODO EDICIÓN (Mi propio perfil)
            SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
            userId = prefs.getInt("id", -1);
            userRole = prefs.getString("rol", "");
            isReadOnly = false; // Permitimos editar
        }

        // Si no se encuentra usuario, cerramos la pantalla para evitar errores
        if (userId == -1) {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargamos los datos desde la Base de Datos
        loadUserData();

        // Solo activamos los botones de guardar si NO es modo lectura
        if (!isReadOnly) {
            btnGuardar.setOnClickListener(v -> saveChanges());
            btnCambiarPass.setOnClickListener(v -> showChangePasswordDialog());
        }
    }

    // Vincula las variables con los IDs del XML
    private void initViews() {
        etNombre = findViewById(R.id.etName);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etPhone);
        etImageUrl = findViewById(R.id.etImageUrl);

        tvUsername = findViewById(R.id.tvUsername);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvJoinDate = findViewById(R.id.tvJoinDate);
        tvRatingMedia = findViewById(R.id.tvRatingMedia);
        ivProfileImage = findViewById(R.id.ivProfile);

        layoutEstadisticas = findViewById(R.id.layoutEstadisticas);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCambiarPass = findViewById(R.id.btnCambiarPass);

        // Botón volver (flecha atrás)
        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    // Crea las instancias de los servicios para poder hacer consultas
    private void initServices() {
        adminService = new AdministradorService(this);
        concursanteService = new ConcursanteService(this);
        espectadorService = new EspectadorService(this);
        puntuacionService = new PuntuacionService(this);
    }

    // Oculta botones y bloquea la escritura en los campos de texto
    private void activarModoLectura() {
        if (btnGuardar != null) btnGuardar.setVisibility(View.GONE);
        if (btnCambiarPass != null) btnCambiarPass.setVisibility(View.GONE);

        deshabilitarEditText(etNombre);
        deshabilitarEditText(etApellido1);
        deshabilitarEditText(etApellido2);
        deshabilitarEditText(etEmail);
        deshabilitarEditText(etTelefono);
        deshabilitarEditText(etImageUrl);
    }

    // Metodo auxiliar para quitar el foco y cursor de un campo de texto
    private void deshabilitarEditText(EditText et) {
        if (et != null) {
            et.setFocusable(false);
            et.setClickable(false);
            et.setCursorVisible(false);
            et.setKeyListener(null);
        }
    }

    // Carga la información del usuario dependiendo de su ROL
    private void loadUserData() {
        switch (userRole) {
            case "ADMINISTRADOR":
                adminService.buscarAdministradorPorId(userId).observe(this, admin -> {
                    if (admin != null) {
                        currentAdmin = admin; // Guardamos referencia para luego actualizar
                        tvUsername.setText(admin.getUsername());
                        tvUserRole.setText("Administrador");
                        tvJoinDate.setText(String.valueOf(admin.getFechaRegistro()));
                        populateFields(admin.getNombre(), admin.getPrimerApellido(), admin.getSegundoApellido(), admin.getEmail(), admin.getTelefono(), admin.getImagenUrl());
                    }
                });
                break;

            case "CONCURSANTE":
                concursanteService.obtenerPorId(userId).observe(this, concu -> {
                    if (concu != null) {
                        currentConcursante = concu;
                        tvUsername.setText(concu.getUsername());
                        tvUserRole.setText("Concursante");
                        tvJoinDate.setText(String.valueOf(concu.getFechaRegistro()));
                        populateFields(concu.getNombre(), concu.getPrimerApellido(), concu.getSegundoApellido(), concu.getEmail(), concu.getTelefono(), concu.getImagenUrl());

                        // Si es concursante, mostramos su estadística de nota media
                        layoutEstadisticas.setVisibility(View.VISIBLE);
                        cargarMediaConcursante(concu.getId());
                    }
                });
                break;

            case "ESPECTADOR":
                espectadorService.obtenerPorId(userId).observe(this, espec -> {
                    if (espec != null) {
                        currentEspectador = espec;
                        tvUsername.setText(espec.getUsername());
                        tvUserRole.setText("Espectador");
                        tvJoinDate.setText(String.valueOf(espec.getFechaRegistro()));
                        populateFields(espec.getNombre(), espec.getPrimerApellido(), espec.getSegundoApellido(), espec.getEmail(), espec.getTelefono(), espec.getImagenUrl());
                    }
                });
                break;
        }
    }

    // Calcula la media de estrellas recibidas por un concursante
    private void cargarMediaConcursante(int concursanteId) {
        puntuacionService.obtenerHistorialConcursante(concursanteId).observe(this, puntuaciones -> {
            if (puntuaciones != null && !puntuaciones.isEmpty()) {
                double suma = 0;
                for (Puntuacion p : puntuaciones) {
                    suma += p.getValor();
                }
                double media = suma / puntuaciones.size();
                // Formateamos a 1 decimal (ej. 4.5 ★)
                tvRatingMedia.setText(String.format(Locale.getDefault(), "%.1f ★", media));
            } else {
                tvRatingMedia.setText("Sin votos");
            }
        });
    }

    // Rellena los campos de texto y carga la imagen con Picasso
    private void populateFields(String nombre, String ap1, String ap2, String email, String tlf, String imgUrl) {
        etNombre.setText(nombre);
        etApellido1.setText(ap1);
        etApellido2.setText(ap2);
        etEmail.setText(email);
        etTelefono.setText(tlf);
        etImageUrl.setText(imgUrl);

        // Si hay URL válida, Picasso la descarga y la pone en el ImageView
        if (imgUrl != null && !imgUrl.isEmpty() && (imgUrl.startsWith("http"))) {
            Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_default_avatar) // Imagen mientras carga
                    .error(R.drawable.ic_default_avatar)       // Imagen si falla
                    .into(ivProfileImage);
        } else {
            ivProfileImage.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    // Guarda los cambios realizados en los EditText en la Base de Datos
    private void saveChanges() {
        if (isReadOnly) return; // Seguridad extra

        // Obtenemos los textos escritos
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String url = etImageUrl.getText().toString().trim();

        // Validación básica
        if (nombre.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nombre y Email son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizamos el objeto correspondiente según el rol
        if (userRole.equals("ADMINISTRADOR") && currentAdmin != null) {
            actualizarAdmin(nombre, email, url);
        } else if (userRole.equals("CONCURSANTE") && currentConcursante != null) {
            actualizarConcursante(nombre, email, url);
        } else if (userRole.equals("ESPECTADOR") && currentEspectador != null) {
            actualizarEspectador(nombre, email, url);
        }
        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
    }

    // Métodos específicos para actualizar cada tipo de entidad en la BD
    private void actualizarAdmin(String n, String e, String u) {
        currentAdmin.setNombre(n);
        currentAdmin.setPrimerApellido(etApellido1.getText().toString());
        currentAdmin.setSegundoApellido(etApellido2.getText().toString());
        currentAdmin.setEmail(e);
        currentAdmin.setTelefono(etTelefono.getText().toString());
        currentAdmin.setImagenUrl(u);
        adminService.actualizarAdministrador(currentAdmin);
    }

    private void actualizarConcursante(String n, String e, String u) {
        currentConcursante.setNombre(n);
        currentConcursante.setPrimerApellido(etApellido1.getText().toString());
        currentConcursante.setSegundoApellido(etApellido2.getText().toString());
        currentConcursante.setEmail(e);
        currentConcursante.setTelefono(etTelefono.getText().toString());
        currentConcursante.setImagenUrl(u);
        concursanteService.actualizar(currentConcursante);
    }

    private void actualizarEspectador(String n, String e, String u) {
        currentEspectador.setNombre(n);
        currentEspectador.setPrimerApellido(etApellido1.getText().toString());
        currentEspectador.setSegundoApellido(etApellido2.getText().toString());
        currentEspectador.setEmail(e);
        currentEspectador.setTelefono(etTelefono.getText().toString());
        currentEspectador.setImagenUrl(u);
        espectadorService.actualizar(currentEspectador);
    }

    // Muestra un diálogo emergente (popup) para cambiar la contraseña
    private void showChangePasswordDialog() {
        if (isReadOnly) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seguridad");

        // Creamos el diseño del popup dinámicamente
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etOld = new EditText(this);
        etOld.setHint("Contraseña Actual");
        etOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etOld);

        final EditText etNew = new EditText(this);
        etNew.setHint("Nueva Contraseña");
        etNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNew);

        builder.setView(layout);
        builder.setPositiveButton("Confirmar", (dialog, which) -> verifyAndUpdatePassword(etOld.getText().toString(), etNew.getText().toString()));
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Verifica la contraseña antigua con BCrypt y encripta la nueva antes de guardar
    private void verifyAndUpdatePassword(String oldPass, String newPass) {
        if (newPass.length() < 4) {
            Toast.makeText(this, "La contraseña nueva es muy corta", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generamos el hash de la nueva contraseña
        String hashed = BCrypt.hashpw(newPass, BCrypt.gensalt());
        boolean success = false;

        // Comprobamos la contraseña antigua usando BCrypt.checkpw
        if (userRole.equals("ADMINISTRADOR") && BCrypt.checkpw(oldPass, currentAdmin.getPassword())) {
            currentAdmin.setPassword(hashed);
            adminService.actualizarAdministrador(currentAdmin);
            success = true;
        } else if (userRole.equals("CONCURSANTE") && BCrypt.checkpw(oldPass, currentConcursante.getPassword())) {
            currentConcursante.setPassword(hashed);
            concursanteService.actualizar(currentConcursante);
            success = true;
        } else if (userRole.equals("ESPECTADOR") && BCrypt.checkpw(oldPass, currentEspectador.getPassword())) {
            currentEspectador.setPassword(hashed);
            espectadorService.actualizar(currentEspectador);
            success = true;
        }

        Toast.makeText(this, success ? "Contraseña actualizada" : "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
    }
}