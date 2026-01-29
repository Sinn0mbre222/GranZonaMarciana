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

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Empezamos la cadena de búsqueda
        intentarLoginAdmin(username, password);
    }

    private void intentarLoginAdmin(String username, String password) {
        administradorService.buscarAdministradorPorUsername(username).observe(this, admin -> {
            if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
                guardarUsuarioLogueado(admin.getId(), admin.getUsername(), TipoRol.ADMINISTRADOR);
                redirigirSegunRol();
            } else {
                // Si no es admin, probamos con Concursante
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
                // Si no es concursante, probamos con Espectador
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
                // Si llegamos aquí y no se ha encontrado en ninguna tabla
                Toast.makeText(this, "Credenciales incorrectas en todos los roles", Toast.LENGTH_SHORT).show();
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
        finish(); // Cerrar LoginActivity
    }

    private void popularteBD() {
        PopulateBD populateBD = new PopulateBD(this);
        populateBD.deleteBD(this);
        populateBD.populateAdministrador();
        populateBD.populateConcursante();
        populateBD.populateEspectador();
    }
}