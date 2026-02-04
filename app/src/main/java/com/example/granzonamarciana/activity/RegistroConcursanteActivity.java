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

    // Servicios para interactuar con la base de datos Room
    private ConcursanteService concursanteService;
    private EdicionService edicionService;
    private SolicitudService solicitudService;

    // Componentes de la interfaz
    private EditText etUsername, etPassword, etName, etApellido1, etApellido2, etEmail, etPhone, etImageUrl, etMotivo;
    private Spinner spEdicion;
    private ImageView ivTogglePassword;
    private boolean passwordVisible = false;
    private List<Edicion> listaEdiciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_concursante);

        // Inicialización de los servicios pasando el contexto de la actividad
        concursanteService = new ConcursanteService(this);
        edicionService = new EdicionService(this);
        solicitudService = new SolicitudService(this);

        initViews();
        setupEditionSpinner(); // Carga las ediciones disponibles desde la BD
        setupListeners();
    }

    private void initViews() {
        // Enlace de variables con los IDs del layout XML
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
        // Observamos el LiveData de ediciones para llenar el Spinner dinámicamente
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> nombresEdiciones = new ArrayList<>();
                for (Edicion e : ediciones) {
                    nombresEdiciones.add("Edición #" + e.getId() + " (" + e.getFechaInicio() + ")");
                }
                // Adaptador para mostrar los nombres en el Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, nombresEdiciones);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spEdicion.setAdapter(adapter);
            }
        });
    }

    private void setupListeners() {
        // Lógica para mostrar u ocultar la contraseña mediante el icono del ojo
        ivTogglePassword.setOnClickListener(v -> {
            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            passwordVisible = !passwordVisible;
            etPassword.setSelection(etPassword.getText().length()); // Mantiene el cursor al final
        });

        findViewById(R.id.btnFinalizeRegister).setOnClickListener(v -> registrarUsuario());
        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        // Recogida de datos eliminando espacios en blanco innecesarios
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String ap1 = etApellido1.getText().toString().trim();
        String ap2 = etApellido2.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String motivacion = etMotivo.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Validaciones de seguridad y formato
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || ap1.isEmpty() ||
                ap2.isEmpty() || email.isEmpty() || phone.isEmpty() || motivacion.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 9) {
            etPhone.setError("9 dígitos requeridos");
            return;
        }

        if (!email.contains("@")) {
            etEmail.setError("Email inválido");
            return;
        }

        // Gestión de la imagen: si está vacío usamos un recurso local por defecto
        String finalImageUrl = imageUrl.isEmpty() ? "ic_person" : imageUrl;

        // Creación del objeto Concursante con contraseña encriptada mediante BCrypt
        Concursante c = new Concursante(
                username, BCrypt.hashpw(password, BCrypt.gensalt()),
                name, ap1, ap2, phone, email, finalImageUrl,
                TipoRol.CONCURSANTE, LocalDate.now()
        );

        // 1. LANZAMIENTO DE INSERCIÓN:
        // Se ejecuta en un hilo de fondo para no congelar la interfaz de usuario.
        concursanteService.insert(c);

        // 2. FLUJO REACTIVO (OBSERVER):
        // Como 'insert' es asíncrono, no sabemos exactamente cuándo terminará.
        // Usamos 'observe' para reaccionar en el momento en que el usuario aparezca en la BD.
        concursanteService.buscarConcursantePorUsername(username).observe(this, nuevoCon -> {

            // Este bloque solo se ejecuta cuando Room nos devuelve el objeto recién creado
            if (nuevoCon != null) {
                if (listaEdiciones.isEmpty()) return;

                // 3. VINCULACIÓN DE DATOS:
                // Ahora que tenemos el ID del concursante (generado por la BD),
                // recuperamos el ID de la edición que el usuario eligió en el Spinner.
                int edicionId = listaEdiciones.get(spEdicion.getSelectedItemPosition()).getId();

                // 4. CREACIÓN DE LA SOLICITUD:
                // Creamos el registro que vincula al nuevo Concursante con la Edición elegida.
                // Se guarda con EstadoSolicitud.PENDIENTE para la revisión del administrador.
                Solicitud s = new Solicitud(edicionId, nuevoCon.getId(), motivacion, EstadoSolicitud.PENDIENTE);
                solicitudService.insert(s);

                // 5. FINALIZACIÓN:
                // Notificamos al usuario y cerramos la actividad devolviéndolo al Login.
                Toast.makeText(this, "Registro enviado correctamente", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}