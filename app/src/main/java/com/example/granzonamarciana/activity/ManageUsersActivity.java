package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.UserAdapter;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.DomainEntity;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.TipoRol;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EspectadorService;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private Spinner spinnerRoleFilter;
    private EditText etSearch;
    private Button btnAddUser;
    private ListView lvUsers;

    private EspectadorService espectadorService;
    private ConcursanteService concursanteService;

    private UserAdapter adapter;
    private List<DomainEntity> allUsers; // Lista maestra con todos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        initViews();
        initServices();
        setupSpinner();

        // Cargar usuarios
        cargarTodosLosUsuarios();

        // Botón Añadir (Pendiente de que creen RegistroActivity tus compañeros)
        btnAddUser.setOnClickListener(v -> {
            // Intent intent = new Intent(ManageUsersActivity.this, RegistroActivity.class);
            // startActivity(intent);
            Toast.makeText(ManageUsersActivity.this, "RegistroActivity aún no creada", Toast.LENGTH_SHORT).show();
        });

        // Buscador
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString(), spinnerRoleFilter.getSelectedItemPosition());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // --- NUEVO: Click en la lista para ver detalle (READ ONLY) ---
        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            DomainEntity usuarioSeleccionado = adapter.getItem(position);

            if (usuarioSeleccionado != null) {
                Intent intent = new Intent(ManageUsersActivity.this, ProfileActivity.class);

                // Pasamos ID y activamos modo lectura
                intent.putExtra("TARGET_USER_ID", usuarioSeleccionado.getId());
                intent.putExtra("READ_ONLY", true);

                // Pasamos el Rol correcto
                if (usuarioSeleccionado instanceof Concursante) {
                    intent.putExtra("TARGET_USER_ROLE", TipoRol.CONCURSANTE.toString());
                } else {
                    intent.putExtra("TARGET_USER_ROLE", TipoRol.ESPECTADOR.toString());
                }

                startActivity(intent);
            }
        });
    }

    private void initViews() {
        spinnerRoleFilter = findViewById(R.id.spinnerRoleFilter);
        etSearch = findViewById(R.id.etSearchUser);
        btnAddUser = findViewById(R.id.btnAddUser);
        lvUsers = findViewById(R.id.lvUsers);
    }

    private void initServices() {
        espectadorService = new EspectadorService(this);
        concursanteService = new ConcursanteService(this);
        allUsers = new ArrayList<>();
    }

    private void setupSpinner() {
        String[] roles = {"Todos", "Espectadores", "Concursantes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleFilter.setAdapter(adapter);

        spinnerRoleFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrar(etSearch.getText().toString(), position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarTodosLosUsuarios() {
        allUsers.clear();

        // 1. Cargar Espectadores
        espectadorService.obtenerTodos().observe(this, espectadores -> {
            if (espectadores != null) {
                // Limpiamos previos de espectadores para no duplicar si se llama varias veces
                allUsers.removeIf(u -> u instanceof Espectador);
                allUsers.addAll(espectadores);
                actualizarLista();
            }
        });

        // 2. Cargar Concursantes
        concursanteService.obtenerTodos().observe(this, concursantes -> {
            if (concursantes != null) {
                allUsers.removeIf(u -> u instanceof Concursante);
                allUsers.addAll(concursantes);
                actualizarLista();
            }
        });
    }

    private void actualizarLista() {
        filtrar(etSearch.getText().toString(), spinnerRoleFilter.getSelectedItemPosition());
    }

    private void filtrar(String texto, int rolPos) {
        List<DomainEntity> filtrados = new ArrayList<>();
        String busqueda = texto.toLowerCase();

        for (DomainEntity u : allUsers) {
            boolean cumpleNombre = false;
            boolean cumpleRol = false;

            String nombre = "";
            if (u instanceof Espectador) {
                nombre = ((Espectador) u).getNombre();
            } else if (u instanceof Concursante) {
                nombre = ((Concursante) u).getNombre();
            }

            if (nombre != null && nombre.toLowerCase().contains(busqueda)) {
                cumpleNombre = true;
            }

            if (rolPos == 0) cumpleRol = true;
            else if (rolPos == 1 && u instanceof Espectador) cumpleRol = true;
            else if (rolPos == 2 && u instanceof Concursante) cumpleRol = true;

            if (cumpleNombre && cumpleRol) {
                filtrados.add(u);
            }
        }

        adapter = new UserAdapter(this, R.layout.item_user, filtrados);
        lvUsers.setAdapter(adapter);
    }
}