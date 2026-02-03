package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
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
    private ListView lvUsers;

    private EspectadorService espectadorService;
    private ConcursanteService concursanteService;

    private UserAdapter adapter;
    private List<DomainEntity> allUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        initViews();
        initServices();
        setupSpinner();

        // Carga inicial de datos desde Room
        cargarTodosLosUsuarios();

        // Buscador reactivo
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarLista();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Click para ver detalle del usuario (solo lectura para el admin)
        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            DomainEntity seleccionado = (DomainEntity) parent.getItemAtPosition(position);
            if (seleccionado != null) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("TARGET_USER_ID", seleccionado.getId());
                intent.putExtra("READ_ONLY", true);

                String rol = (seleccionado instanceof Concursante) ?
                        TipoRol.CONCURSANTE.name() : TipoRol.ESPECTADOR.name();
                intent.putExtra("TARGET_USER_ROLE", rol);

                startActivity(intent);
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerRoleFilter = findViewById(R.id.spinnerRoleFilter);
        etSearch = findViewById(R.id.etSearchUser);
        lvUsers = findViewById(R.id.lvUsers);
    }

    private void initServices() {
        espectadorService = new EspectadorService(this);
        concursanteService = new ConcursanteService(this);
    }

    private void setupSpinner() {
        String[] roles = {"Todos los Usuarios", "Solo Espectadores", "Solo Concursantes"};

        // Cambiamos a android.R.layout.simple_spinner_item temporalmente para probar
        // O usamos el tuyo asegurándonos de que funcione:
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_rol_item, // Tu layout personalizado (texto blanco)
                roles
        );

        // Esta línea es VITAL para que cuando pulses el spinner se vea la lista
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRoleFilter.setAdapter(spinAdapter);

        spinnerRoleFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                // Log de diagnóstico para ver si detecta el cambio
                android.util.Log.d("DEBUG_SPINNER", "Seleccionado pos: " + pos);
                actualizarLista();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void cargarTodosLosUsuarios() {
        // Obtenemos espectadores y actualizamos lista global
        espectadorService.obtenerTodos().observe(this, espectadores -> {
            if (espectadores != null) {
                allUsers.removeIf(u -> u instanceof Espectador);
                allUsers.addAll(espectadores);
                actualizarLista();
            }
        });

        // Obtenemos concursantes y actualizamos lista global
        concursanteService.obtenerTodos().observe(this, concursantes -> {
            if (concursantes != null) {
                allUsers.removeIf(u -> u instanceof Concursante);
                allUsers.addAll(concursantes);
                actualizarLista();
            }
        });
    }

    private void actualizarLista() {
        String busqueda = etSearch.getText().toString().toLowerCase().trim();
        int filtroRol = spinnerRoleFilter.getSelectedItemPosition();

        List<DomainEntity> filtrados = new ArrayList<>();

        for (DomainEntity u : allUsers) {
            String nombre = "";
            boolean esConcu = (u instanceof Concursante);

            // Casting dinámico para obtener el nombre independientemente de la entidad
            if (esConcu) nombre = ((Concursante) u).getNombre();
            else nombre = ((Espectador) u).getNombre();

            // Aplicación de filtros combinados
            boolean coincideNombre = nombre != null && nombre.toLowerCase().contains(busqueda);
            boolean coincideRol = (filtroRol == 0) ||
                    (filtroRol == 1 && !esConcu) ||
                    (filtroRol == 2 && esConcu);

            if (coincideNombre && coincideRol) {
                filtrados.add(u);
            }
        }

        // Refrescamos el adaptador con la lista filtrada
        adapter = new UserAdapter(this, R.layout.item_user, filtrados);
        lvUsers.setAdapter(adapter);
    }
}