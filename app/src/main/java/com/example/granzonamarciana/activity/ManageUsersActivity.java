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

        // Botón Añadir (Va al registro genérico)
        btnAddUser.setOnClickListener(v -> {
            // ESTO ESTÁ PENDIENTE HASTA QUE CREEN EL ARCHIVO:
            // Intent intent = new Intent(ManageUsersActivity.this, RegistroActivity.class);
            // startActivity(intent);

            // De momento mostramos un aviso para que no falle:
            Toast.makeText(ManageUsersActivity.this, "Falta crear la pantalla de Registro", Toast.LENGTH_SHORT).show();
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
                allUsers.addAll(espectadores);
                actualizarLista();
            }
        });

        // 2. Cargar Concursantes (Asumiendo que tienes obtenerTodos en ConcursanteService, si no, usa el de obtenerPorEdicion o crea uno)
        concursanteService.obtenerTodos().observe(this, concursantes -> {
            if (concursantes != null) {
                allUsers.addAll(concursantes);
                actualizarLista();
            }
        });
    }

    private void actualizarLista() {
        // Refresca la lista con lo que haya en allUsers aplicando el filtro actual
        filtrar(etSearch.getText().toString(), spinnerRoleFilter.getSelectedItemPosition());
    }

    private void filtrar(String texto, int rolPos) {
        List<DomainEntity> filtrados = new ArrayList<>();
        String busqueda = texto.toLowerCase();

        for (DomainEntity u : allUsers) {
            boolean cumpleNombre = false;
            boolean cumpleRol = false;

            // Chequeo de nombre
            if (u instanceof Espectador) {
                cumpleNombre = ((Espectador) u).getNombre().toLowerCase().contains(busqueda);
            } else if (u instanceof Concursante) {
                cumpleNombre = ((Concursante) u).getNombre().toLowerCase().contains(busqueda);
            }

            // Chequeo de Rol (0=Todos, 1=Espectador, 2=Concursante)
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