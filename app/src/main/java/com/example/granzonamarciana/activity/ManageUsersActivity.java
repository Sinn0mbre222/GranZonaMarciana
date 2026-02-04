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

// Pantalla de ADMINISTRACIÓN.
// Permite ver, buscar, filtrar y eliminar a cualquier usuario (Espectador o Concursante).
public class ManageUsersActivity extends AppCompatActivity {

    // Filtros y lista visual
    private Spinner spinnerRoleFilter; // Desplegable "Todos", "Espectadores", "Concursantes"
    private EditText etSearch;         // Buscador por nombre
    private ListView lvUsers;          // Lista visual

    // Servicios de Base de Datos
    private EspectadorService espectadorService;
    private ConcursanteService concursanteService;

    // Adaptador para pintar las filas
    private UserAdapter adapter;

    // Lista maestra que guarda TODOS los usuarios cargados (para filtrar sin volver a consultar la BD)
    private List<DomainEntity> allUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        initViews();
        initServices();

        // Configuramos el desplegable de roles
        setupSpinner();

        // Cargamos los datos de la BD
        cargarTodosLosUsuarios();

        // Listener del Buscador: Se ejecuta cada vez que escribes una letra
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualizarLista(); // Filtramos la lista en tiempo real
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Click en un usuario de la lista -> Ver su Perfil
        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            DomainEntity seleccionado = (DomainEntity) parent.getItemAtPosition(position);
            if (seleccionado != null) {
                Intent intent = new Intent(this, ProfileActivity.class);

                // Pasamos el ID del usuario
                intent.putExtra("TARGET_USER_ID", seleccionado.getId());

                // Activamos el modo "Solo Lectura" para que el Admin no edite los datos personales accidentalmente
                intent.putExtra("READ_ONLY", true);

                // Determinamos qué rol tiene para que ProfileActivity sepa en qué tabla buscar
                String rol = TipoRol.ESPECTADOR.name();
                if (seleccionado instanceof Concursante) {
                    rol = TipoRol.CONCURSANTE.name();
                }
                intent.putExtra("TARGET_USER_ROLE", rol);

                startActivity(intent);
            }
        });

        // Botón volver
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

    // Configura el Spinner (Desplegable) con las opciones de filtro
    private void setupSpinner() {
        String[] roles = {"Todos los Usuarios", "Solo Espectadores", "Solo Concursantes"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, roles);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleFilter.setAdapter(spinAdapter);

        // Cuando cambiamos la selección, actualizamos la lista
        spinnerRoleFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { actualizarLista(); }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    // Carga DATOS DE DOS TABLAS (Espectadores y Concursantes) y los mezcla en una sola lista
    private void cargarTodosLosUsuarios() {
        // 1. Traer espectadores
        espectadorService.obtenerTodos().observe(this, espectadores -> {
            if (espectadores != null) {
                // Borramos los espectadores viejos de la lista y metemos los nuevos
                allUsers.removeIf(u -> u instanceof Espectador);
                allUsers.addAll(espectadores);
                actualizarLista(); // Refrescamos pantalla
            }
        });

        // 2. Traer concursantes
        concursanteService.obtenerTodos().observe(this, concursantes -> {
            if (concursantes != null) {
                // Borramos los concursantes viejos y metemos los nuevos
                allUsers.removeIf(u -> u instanceof Concursante);
                allUsers.addAll(concursantes);
                actualizarLista(); // Refrescamos pantalla
            }
        });
    }

    // Motor de filtrado: Combina el Texto del buscador + la Selección del Spinner
    private void actualizarLista() {
        String busqueda = etSearch.getText().toString().toLowerCase().trim();
        int filtroRol = spinnerRoleFilter.getSelectedItemPosition(); // 0=Todos, 1=Espectadores, 2=Concursantes

        List<DomainEntity> filtrados = new ArrayList<>();

        for (DomainEntity u : allUsers) {
            String nombreCompleto = "";
            boolean esConcu = (u instanceof Concursante);

            // Obtenemos el nombre completo según el tipo de usuario
            if (esConcu) {
                Concursante c = (Concursante) u;
                nombreCompleto = (c.getNombre() + " " + c.getPrimerApellido()).toLowerCase();
            } else {
                Espectador e = (Espectador) u;
                nombreCompleto = (e.getNombre() + " " + e.getPrimerApellido()).toLowerCase();
            }

            // Comprobación 1: ¿El nombre contiene el texto buscado?
            boolean coincideNombre = nombreCompleto.contains(busqueda);

            // Comprobación 2: ¿El rol coincide con el filtro del spinner?
            boolean coincideRol = (filtroRol == 0); // Si es 0 (Todos), siempre es true
            if (filtroRol == 1 && !esConcu) { coincideRol = true; } // Si es 1, debe ser Espectador
            if (filtroRol == 2 && esConcu) { coincideRol = true; }  // Si es 2, debe ser Concursante

            // Si cumple AMBAS condiciones, lo añadimos a la lista final
            if (coincideNombre && coincideRol) {
                filtrados.add(u);
            }
        }

        // Creamos el adaptador pasando la función de eliminar ("this::mostrarDialogoEliminar")
        adapter = new UserAdapter(this, R.layout.item_user, filtrados, this::mostrarDialogoEliminar);
        lvUsers.setAdapter(adapter);
    }

    // Muestra una alerta de confirmación antes de borrar
    private void mostrarDialogoEliminar(DomainEntity usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de que quieres borrar a este usuario?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Borramos de la base de datos según el tipo
                    if (usuario instanceof Concursante) {
                        concursanteService.eliminar((Concursante) usuario);
                    } else {
                        espectadorService.eliminar((Espectador) usuario);
                    }
                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    // Al eliminar, el "observe" de arriba detectará el cambio y recargará la lista solo
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}