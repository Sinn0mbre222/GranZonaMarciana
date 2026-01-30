package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

    private AdministradorService administradorService;
    private EspectadorService espectadorService;
    private ConcursanteService concursanteService;

    private EditText etUsername, etPassword;
    private ImageView ivVisibilidadPassword;
    private Button btnLogin, btnCrearUsuario;
    private TextView tvInvitado; // Campo para el acceso de invitado

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Comprobar sesión y poblar BD
        comprobarSiEstaLogueado();

        populateBD();

        //Inicializar Servicios
        administradorService = new AdministradorService(this);
        espectadorService = new EspectadorService(this);
        concursanteService = new ConcursanteService(this);

        // Inicializar Vistas
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivVisibilidadPassword = findViewById(R.id.ivVisibilidadPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearUsuario = findViewById(R.id.btnCrearUsuario);
        tvInvitado = findViewById(R.id.tvInvitado);

        // Listeners
        ivVisibilidadPassword.setOnClickListener(v -> cambiarVisibilidadContraseña());

        btnLogin.setOnClickListener(v -> iniciarSesion());

        tvInvitado.setOnClickListener(v -> accederComoInvitado());

        btnCrearUsuario.setOnClickListener(v -> {
            // Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            // startActivity(intent);
        });
    }

    private void cambiarVisibilidadContraseña() {
        if (passwordVisible) {
            etPassword.setInputType(129); // textPassword
            ivVisibilidadPassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            etPassword.setInputType(144); // textVisiblePassword
            ivVisibilidadPassword.setImageResource(R.drawable.ic_visibility);
        }
        passwordVisible = !passwordVisible;
        etPassword.setSelection(etPassword.getText().length());
    }

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
        // Iniciar cadena de búsqueda: Admin -> Concursante -> Espectador
        intentarLoginAdmin(username, password);
    }

    private void intentarLoginAdmin(String username, String password) {
        administradorService.buscarAdministradorPorUsername(username).observe(this, admin -> {
            if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
                guardarUsuarioLogueado(admin.getId(), admin.getUsername(), TipoRol.ADMINISTRADOR);
                redirigirSegunRol();
            } else {
                intentarLoginConcursante(username, password);
            }
        });
    }

    private void intentarLoginConcursante(String username, String password) {
        concursanteService.buscarConcursantePorUsername(username).observe(this, concu -> {
            if (concu != null && BCrypt.checkpw(password, concu.getPassword())) {
                guardarUsuarioLogueado(concu.getId(), concu.getUsername(), TipoRol.CONCURSANTE);
                redirigirSegunRol();
            } else {
                intentarLoginEspectador(username, password);
            }
        });
    }

    private void intentarLoginEspectador(String username, String password) {
        espectadorService.buscarEspectadorPorUsername(username).observe(this, espec -> {
            if (espec != null && BCrypt.checkpw(password, espec.getPassword())) {
                guardarUsuarioLogueado(espec.getId(), espec.getUsername(), TipoRol.ESPECTADOR);
                redirigirSegunRol();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void accederComoInvitado() {
        // Guardamos datos ficticios para el invitado
        SharedPreferences.Editor editor = getSharedPreferences("granZMUser", MODE_PRIVATE).edit();
        editor.putInt("id", -1); // ID especial para invitados
        editor.putString("username", "Invitado");
        editor.putString("rol", "INVITADO");
        editor.apply();

        Toast.makeText(this, "Accediendo como invitado espacial", Toast.LENGTH_SHORT).show();
        redirigirSegunRol();
    }

    private void guardarUsuarioLogueado(int id, String username, TipoRol rol) {
        SharedPreferences.Editor editor = getSharedPreferences("granZMUser", MODE_PRIVATE).edit();
        editor.putInt("id", id);
        editor.putString("username", username);
        editor.putString("rol", rol.name());
        editor.apply();

        Toast.makeText(this, "¡Bienvenido " + username + "!", Toast.LENGTH_SHORT).show();
    }

    private void comprobarSiEstaLogueado() {
        SharedPreferences sharedPreferences = getSharedPreferences("granZMUser", MODE_PRIVATE);
        if (sharedPreferences.contains("id")) {
            redirigirSegunRol();
        }
    }

    private void redirigirSegunRol() {
        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        String rol = prefs.getString("rol", "");

        Intent intent;
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
                // El caso "INVITADO" cae aquí por defecto
                intent = new Intent(this, MainMenuActivity.class);
        }

        startActivity(intent);
        finish();
    }

    private void populateBD() {
        PopulateBD populateBD = new PopulateBD(this);
        populateBD.deleteBD(this);
        populateBD.populateAdministrador();
        populateBD.populateConcursante();
        populateBD.populateEspectador();
    }
}