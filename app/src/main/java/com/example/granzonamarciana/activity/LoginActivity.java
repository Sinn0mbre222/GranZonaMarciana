package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.database.PopulateBD;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.AdministradorService;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    // Servicios para acceder a las diferentes tablas de usuarios en Room
    private AdministradorService administradorService;
    private EspectadorService espectadorService;
    private ConcursanteService concursanteService;

    // Elementos de la interfaz de usuario
    private EditText etUsername, etPassword;
    private ImageView ivVisibilidadPassword;
    private Button btnLogin, btnCrearUsuario;
    private TextView tvInvitado;

    // Variable de control para el estado del campo de contraseña
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Verifica si ya existe una sesión activa para saltar directamente al menú
        comprobarSiEstaLogueado();

        // Ejecuta la precarga de datos iniciales si es la primera vez que se abre la app
        populateBD();

        // Inicialización de servicios con el contexto actual
        administradorService = new AdministradorService(this);
        espectadorService = new EspectadorService(this);
        concursanteService = new ConcursanteService(this);

        // Vinculación de los componentes lógicos con las vistas del XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivVisibilidadPassword = findViewById(R.id.ivVisibilidadPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearUsuario = findViewById(R.id.btnCrearUsuario);
        tvInvitado = findViewById(R.id.tvInvitado);

        // Listener para alternar la visibilidad de la contraseña (ojo abierto/cerrado)
        ivVisibilidadPassword.setOnClickListener(v -> cambiarVisibilidadContraseña());

        // Listener para procesar el inicio de sesión
        btnLogin.setOnClickListener(v -> iniciarSesion());

        // Listener para el acceso sin cuenta (Rol Invitado)
        tvInvitado.setOnClickListener(v -> accederComoInvitado());

        // Redirección a la pantalla de selección de tipo de registro
        btnCrearUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroSelectionActivity.class);
            startActivity(intent);
        });
    }

    // Cambia el tipo de entrada del EditText de contraseña y actualiza el icono
    private void cambiarVisibilidadContraseña() {
        if (passwordVisible) {
            // Modo oculto (dots)
            etPassword.setInputType(129);
            ivVisibilidadPassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Modo texto visible
            etPassword.setInputType(144);
            ivVisibilidadPassword.setImageResource(R.drawable.ic_visibility);
        }
        passwordVisible = !passwordVisible;
        // Mueve el cursor al final del texto para mejorar la experiencia de usuario
        etPassword.setSelection(etPassword.getText().length());
    }

    // Valida que los campos no estén vacíos antes de iniciar la búsqueda en la BD
    public void iniciarSesion() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Ingresa el usuario");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Ingresa la contraseña");
            return;
        }

        Toast.makeText(this, "Verificando credenciales...", Toast.LENGTH_SHORT).show();

        // El proceso es secuencial: primero busca en Admins, luego en Concursantes y finalmente en Espectadores
        intentarLoginAdmin(username, password);
    }

    // Intenta encontrar el usuario en la tabla de Administradores
    private void intentarLoginAdmin(String username, String password) {
        administradorService.buscarAdministradorPorUsername(username).observe(this, admin -> {
            // checkpw compara la contraseña plana con el hash almacenado mediante BCrypt
            if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
                guardarUsuarioLogueado(admin.getId(), admin.getUsername(), TipoRol.ADMINISTRADOR);
                redirigirSegunRol();
            } else {
                // Si no es admin, busca en la tabla de concursantes
                intentarLoginConcursante(username, password);
            }
        });
    }

    // Intenta encontrar el usuario en la tabla de Concursantes
    private void intentarLoginConcursante(String username, String password) {
        concursanteService.buscarConcursantePorUsername(username).observe(this, concu -> {
            if (concu != null && BCrypt.checkpw(password, concu.getPassword())) {
                guardarUsuarioLogueado(concu.getId(), concu.getUsername(), TipoRol.CONCURSANTE);
                redirigirSegunRol();
            } else {
                // Si no es concursante, busca en la tabla de espectadores
                intentarLoginEspectador(username, password);
            }
        });
    }

    // Último eslabón de la cadena de autenticación: Espectadores
    private void intentarLoginEspectador(String username, String password) {
        espectadorService.buscarEspectadorPorUsername(username).observe(this, espec -> {
            if (espec != null && BCrypt.checkpw(password, espec.getPassword())) {
                guardarUsuarioLogueado(espec.getId(), espec.getUsername(), TipoRol.ESPECTADOR);
                redirigirSegunRol();
            } else {
                // Si llega aquí, las credenciales no existen en ninguna tabla
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Configura una sesión temporal para el acceso como Invitado
    private void accederComoInvitado() {
        SharedPreferences.Editor editor = getSharedPreferences("granZMUser", MODE_PRIVATE).edit();
        editor.putInt("id", -1); // ID -1 identifica al invitado
        editor.putString("username", "Invitado");
        editor.putString("rol", "INVITADO"); //En caso de que sea invitado, por el rol que se le buscará es el de invitado
        editor.apply();

        Toast.makeText(this, "Accediendo como invitado espacial", Toast.LENGTH_SHORT).show();
        redirigirSegunRol();
    }

    // Almacena los datos del usuario en SharedPreferences para persistencia de sesión
    private void guardarUsuarioLogueado(int id, String username, TipoRol rol) {
        SharedPreferences.Editor editor = getSharedPreferences("granZMUser", MODE_PRIVATE).edit();
        editor.putInt("id", id);
        editor.putString("username", username);
        editor.putString("rol", rol.name());
        editor.apply();

        Toast.makeText(this, "¡Bienvenido " + username + "!", Toast.LENGTH_SHORT).show();
        ejecutarRedireccionInmediata(rol.name());
    }

    // Comprueba si existe un ID guardado al arrancar la actividad para auto-logueo
    private void comprobarSiEstaLogueado() {
        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        if (sharedPreferences.contains("id")) {
            redirigirSegunRol();
        }
    }

    // Determina a qué Activity de menú debe ir el usuario según su rol guardado
    private void redirigirSegunRol() {
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String rol = prefs.getString("rol", ""); //Esto recoge el rol del usuario que está logueado ahora mismo

        Intent intent;

        //Se busca el rol y se le manda directamente al menú que le corresponda
        switch (rol) {
            case "ADMINISTRADOR":
                intent = new Intent(this, MenuAdminActivity.class);
                break;
            case "CONCURSANTE":
                intent = new Intent(this, MenuConcursanteActivity.class);
                break;
            case "ESPECTADOR":
                intent = new Intent(this, MenuEspectadorActivity.class);
                break;
            default:
                intent = new Intent(this, MainMenuActivity.class);
        }

        startActivity(intent);
        finish(); // Finaliza LoginActivity para que el usuario no pueda volver atrás al login
    }

    // Método auxiliar para redirección rápida tras el guardado de datos
    private void ejecutarRedireccionInmediata(String rol) {
        Intent intent;
        switch (rol) {
            case "ADMINISTRADOR": intent = new Intent(this, MenuAdminActivity.class); break;
            case "CONCURSANTE": intent = new Intent(this, MenuConcursanteActivity.class); break;
            case "ESPECTADOR": intent = new Intent(this, MenuEspectadorActivity.class); break;
            default: intent = new Intent(this, MainMenuActivity.class); break;
        }
        startActivity(intent);
        finish();
    }

    // Lógica de precarga de la Base de Datos
    private void populateBD() {
        /*PopulateBD populate = new PopulateBD(this);
        // Limpia cualquier residuo y ejecuta los inserts de las entidades
        populate.deleteBD(this);
        populate.executeFullPopulate();*/

        // Se usa un archivo de preferencias distinto para controlar si la BD ya fue poblada
        SharedPreferences prefs = getSharedPreferences("ConfiguracionApp", MODE_PRIVATE);
        boolean yaPoblada = prefs.getBoolean("db_poblada", false);

        // Si es la primera vez (yaPoblada = false), lanza un hilo para insertar datos
        if (!yaPoblada) {
            new Thread(() -> {
                PopulateBD populate = new PopulateBD(this);
                // Limpia cualquier residuo y ejecuta los inserts de las entidades
                populate.deleteBD(this);
                populate.executeFullPopulate();

                // Marca la bandera como true para que no se repita en el próximo inicio
                prefs.edit().putBoolean("db_poblada", true).apply();
            }).start();
        }
    }
}