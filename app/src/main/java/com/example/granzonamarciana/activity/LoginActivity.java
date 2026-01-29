package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.database.PopulateBD;
import com.example.granzonamarciana.entity.Administrador;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.AdministradorService;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private AdministradorService administradorService;
    private EspectadorService espectadorService;
    private ConcursanteService concursanteService;

    //Declarar los campos
    private EditText etUsername, etPassword;

    //Imagen para ocultar contraseña
    private ImageView ivVisibilidadPassword;

    //Contraseña visible o no visible
    private boolean passwordVisible = false;

    private Button btnLogin, btnCrearUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        comprobarSiEstaLogueado();
        popularteBD();

        administradorService = new AdministradorService(this);
        espectadorService = new EspectadorService(this);
        concursanteService = new ConcursanteService(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivVisibilidadPassword = findViewById(R.id.ivVisibilidadPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearUsuario = findViewById(R.id.btnCrearUsuario);

        //Pulsar en el icono del ojo de la contraseña
        ivVisibilidadPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidadContraseña();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a RegisterActivity
                //Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //startActivity(intent);
            }
        });
    }

    //Metodo para cambiar la visibilidad de la contraseña
    private void cambiarVisibilidadContraseña() {
        //Si está visible, se oculta, si no, se muestra la contraseña
        if (passwordVisible) {
            // Ocultar contraseña
            etPassword.setInputType(129);
            ivVisibilidadPassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Mostrar contraseña
            etPassword.setInputType(144);
            ivVisibilidadPassword.setImageResource(R.drawable.ic_visibility);
        }
        passwordVisible = !passwordVisible;
        // Mover cursor al final
        etPassword.setSelection(etPassword.getText().length());
    }

    public void iniciarSesion() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        //Validar usuario y contraseña
        if (username.isEmpty()) {
            etUsername.setError("Ingresa el usuario");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingresa la contaseña");
            etPassword.requestFocus();
            return;
        }

        Toast.makeText(this, "Iniciando sesión en el sistema...", Toast.LENGTH_SHORT).show();

        // 1. Primero intentar como ADMIN
        loginAdmin(username, password);
    }

    private void loginAdmin(final String username, final String password) {
        administradorService.buscarAdministradorPorUsername(username).observe(this, administrador -> {
            if (administrador != null && BCrypt.checkpw(password, administrador.getPassword())) {
                // Login exitoso como ADMIN
                guardarUsuarioLogueado(administrador.getId(), administrador.getUsername(), TipoRol.ADMINISTRADOR);
                redirigirSegunRol();
            } else {
                // Credenciales incorrectas
                Toast.makeText(LoginActivity.this,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginConcursante(final String username, final String password) {
        concursanteService.buscarConcursantePorUsername(username).observe(this, concursante -> {
            if (concursante != null && BCrypt.checkpw(password, concursante.getPassword())) {
                // Login exitoso como CONCURSANTE
                guardarUsuarioLogueado(concursante.getId(), concursante.getUsername(), TipoRol.CONCURSANTE);
                redirigirSegunRol();
            } else {
                // Credenciales incorrectas
                Toast.makeText(LoginActivity.this,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginEspectador(final String username, final String password) {
        espectadorService.buscarEspectadorPorUsername(username).observe(this, espectador -> {
            if (espectador != null && BCrypt.checkpw(password, espectador.getPassword())) {
                // Login exitoso como ESPECTADOR
                guardarUsuarioLogueado(espectador.getId(), espectador.getUsername(), TipoRol.ESPECTADOR);
                redirigirSegunRol();
            } else {
                // Credenciales incorrectas
                Toast.makeText(LoginActivity.this,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_LONG).show();
            }
        });
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
                intent = new Intent(this, MainMenuActivity.class);
                break;
            /*case "CONCURSANTE":
                intent = new Intent(this, MainMenuActivity.class);
                break;
            case "ESPECTADOR":
                intent = new Intent(this, MainMenuActivity.class);
                break;*/
            default:
                intent = new Intent(this, MainMenuActivity.class);
        }

        startActivity(intent);
        finish(); // Cerrar LoginActivity
    }

    private void popularteBD() {
        PopulateBD populateBD = new PopulateBD(this);
        populateBD.populateAdministrador();
        // populateBD.populateConcursante();
        // populateBD.populateEspectador();
    }
}