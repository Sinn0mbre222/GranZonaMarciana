package com.example.granzonamarciana.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.AdministradorService;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;
import com.example.granzonamarciana.service.PuntuacionService;
import com.example.granzonamarciana.service.SolicitudService;

public class ProfileActivity extends AppCompatActivity {

    private EditText etNombre, etApellido1, etApellido2, etEmail, etTelefono;
    private TextView tvStat1, tvStat2;
    private LinearLayout layoutStats;
    private Button btnGuardar, btnCambiarPass, btnBack;

    private ConcursanteService concursanteService;
    private EspectadorService espectadorService;
    private AdministradorService administradorService;
    private PuntuacionService puntuacionService;
    private SolicitudService solicitudService;

    private int currentUserId;
    private TipoRol currentUserRol;
    private boolean isReadOnly = false; // Bandera para saber si es modo lectura

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initServices();
        initViews();

        // 1. Determinar si mostramos mi perfil o el de otro (Admin)
        if (!cargarDatosDeEntrada()) {
            Toast.makeText(this, "Error al cargar datos de perfil", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Cargar datos desde BD
        loadUserData();

        // 3. Si es modo lectura (Admin mirando usuario), bloquear campos
        if (isReadOnly) {
            activarModoLectura();
        }

        // 4. Configurar botones
        setupButtons();
    }

    private void initServices() {
        concursanteService = new ConcursanteService(this);
        espectadorService = new EspectadorService(this);
        administradorService = new AdministradorService(this);
        puntuacionService = new PuntuacionService(this);
        solicitudService = new SolicitudService(getApplication());
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        layoutStats = findViewById(R.id.layoutStats);
        tvStat1 = findViewById(R.id.tvStat1);
        tvStat2 = findViewById(R.id.tvStat2);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCambiarPass = findViewById(R.id.btnCambiarPass);
        btnBack = findViewById(R.id.btnBack);
    }

    private boolean cargarDatosDeEntrada() {
        // A. Caso ADMIN viendo a otro usuario (viene desde ManageUsersActivity)
        int intentId = getIntent().getIntExtra("TARGET_USER_ID", -1);
        String intentRole = getIntent().getStringExtra("TARGET_USER_ROLE");
        isReadOnly = getIntent().getBooleanExtra("READ_ONLY", false);

        if (intentId != -1 && intentRole != null) {
            currentUserId = intentId;
            currentUserRol = TipoRol.valueOf(intentRole);
            return true;
        }

        // B. Caso MI PERFIL (Usuario logueado)
        SharedPreferences prefs = getSharedPreferences("GranZonaMarcianaPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", -1);
        String rolStr = prefs.getString("USER_ROLE", null);

        if (currentUserId != -1 && rolStr != null) {
            currentUserRol = TipoRol.valueOf(rolStr);
            return true;
        }
        return false;
    }

    private void loadUserData() {
        switch (currentUserRol) {
            case CONCURSANTE:
                loadConcursanteData();
                break;
            case ESPECTADOR:
                loadEspectadorData();
                break;
            case ADMINISTRADOR:
                loadAdministradorData();
                break;
        }
    }

    private void activarModoLectura() {
        // Deshabilitar edición de campos
        etNombre.setEnabled(false);
        etApellido1.setEnabled(false);
        etApellido2.setEnabled(false);
        etEmail.setEnabled(false);
        etTelefono.setEnabled(false);

        // Ocultar botones de acción (Guardar y Cambiar Contraseña)
        btnGuardar.setVisibility(View.GONE);
        btnCambiarPass.setVisibility(View.GONE);

        // El botón Back se queda visible para poder volver
        if(btnBack != null) btnBack.setVisibility(View.VISIBLE);
    }

    private void setupButtons() {
        // Solo activamos guardar si NO es modo lectura
        if (!isReadOnly) {
            btnGuardar.setOnClickListener(v -> saveUserData());
            btnCambiarPass.setOnClickListener(v -> Toast.makeText(this, "Cambiar contraseña pendiente", Toast.LENGTH_SHORT).show());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    // --- CARGA DE DATOS ---

    private void loadConcursanteData() {
        concursanteService.obtenerPorId(currentUserId).observe(this, c -> {
            if (c != null) {
                fillFields(c.getNombre(), c.getPrimerApellido(), c.getSegundoApellido(), c.getEmail(), c.getTelefono());

                // Cargar Estado de Solicitud (Stat 1)
                solicitudService.getMisSolicitudes(currentUserId).observe(this, solicitudes -> {
                    if (solicitudes != null && !solicitudes.isEmpty()) {
                        tvStat1.setText("Estado: " + solicitudes.get(0).getEstado());
                    } else {
                        tvStat1.setText("Sin Solicitud");
                    }
                });
                tvStat2.setVisibility(View.GONE);
            }
        });
    }

    private void loadEspectadorData() {
        espectadorService.obtenerPorId(currentUserId).observe(this, e -> {
            if (e != null) {
                // Ojo: usa getPrimerApellidp o getPrimerApellido según tu entidad corregida
                fillFields(e.getNombre(), e.getPrimerApellido(), e.getSegundoApellido(), e.getEmail(), e.getTelefono());

                // Cargar número de votos (Stat 1)
                puntuacionService.obtenerHistorialEspectador(currentUserId).observe(this, lista -> {
                    tvStat1.setText("Votos realizados: " + (lista != null ? lista.size() : 0));
                    tvStat2.setVisibility(View.GONE);
                });
            }
        });
    }

    private void loadAdministradorData() {
        administradorService.buscarAdministradorPorId(currentUserId).observe(this, a -> {
            if (a != null) {
                fillFields(a.getNombre(), a.getPrimerApellido(), a.getSegundoApellido(), a.getEmail(), a.getTelefono());
                layoutStats.setVisibility(View.GONE);
            }
        });
    }

    private void fillFields(String n, String a1, String a2, String email, String tlf) {
        etNombre.setText(n);
        etApellido1.setText(a1);
        etApellido2.setText(a2);
        etEmail.setText(email);
        etTelefono.setText(tlf);
        // IMPORTANTE: NO cargamos la contraseña para mantenerla oculta.
    }

    // --- GUARDADO ---
    private void saveUserData() {
        // Aquí iría tu lógica de guardado (Update) si quisieras implementarla
        Toast.makeText(this, "Guardando datos...", Toast.LENGTH_SHORT).show();
    }
}