package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
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
        cargarTodosLosUsuarios();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { actualizarLista(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            DomainEntity seleccionado = (DomainEntity) parent.getItemAtPosition(position);
            if (seleccionado != null) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("TARGET_USER_ID", seleccionado.getId());
                intent.putExtra("READ_ONLY", true);
                String rol = TipoRol.ESPECTADOR.name();
                if (seleccionado instanceof Concursante) {
                    rol = TipoRol.CONCURSANTE.name();
                }
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
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, roles);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleFilter.setAdapter(spinAdapter);
        spinnerRoleFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { actualizarLista(); }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void cargarTodosLosUsuarios() {
        espectadorService.obtenerTodos().observe(this, espectadores -> {
            if (espectadores != null) {
                allUsers.removeIf(u -> u instanceof Espectador);
                allUsers.addAll(espectadores);
                actualizarLista();
            }
        });

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
            String nombreCompleto = "";
            boolean esConcu = (u instanceof Concursante);

            if (esConcu) {
                Concursante c = (Concursante) u;
                nombreCompleto = (c.getNombre() + " " + c.getPrimerApellido()).toLowerCase();
            } else {
                Espectador e = (Espectador) u;
                nombreCompleto = (e.getNombre() + " " + e.getPrimerApellido()).toLowerCase();
            }

            boolean coincideNombre = nombreCompleto.contains(busqueda);
            boolean coincideRol = (filtroRol == 0);
            if (filtroRol == 1 && !esConcu) { coincideRol = true; }
            if (filtroRol == 2 && esConcu) { coincideRol = true; }

            if (coincideNombre && coincideRol) {
                filtrados.add(u);
            }
        }

        adapter = new UserAdapter(this, R.layout.item_user, filtrados, this::mostrarDialogoEliminar);
        lvUsers.setAdapter(adapter);
    }

    private void mostrarDialogoEliminar(DomainEntity usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que quieres borrar a este usuario?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (usuario instanceof Concursante) {
                        concursanteService.eliminar((Concursante) usuario);
                    } else {
                        espectadorService.eliminar((Espectador) usuario);
                    }
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}