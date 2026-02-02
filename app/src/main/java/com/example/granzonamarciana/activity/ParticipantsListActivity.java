package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.ParticipantAdapter;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EdicionService;
import java.util.ArrayList;
import java.util.List;

public class ParticipantsListActivity extends AppCompatActivity {

    private Spinner spinnerEdiciones;
    private ListView lvParticipantes;
    private EditText etBuscar;
    private EdicionService edicionService;
    private ConcursanteService concursanteService;
    private ParticipantAdapter adapter;
    private List<Concursante> listaConcursantesFull = new ArrayList<>();
    private List<Edicion> listaEdiciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants_list);

        initViews();
        edicionService = new EdicionService(this);
        concursanteService = new ConcursanteService(this);

        cargarEdiciones();

        // Buscador en tiempo real
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarListaLocal(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Al pulsar: siempre al Perfil Público
        lvParticipantes.setOnItemClickListener((parent, view, position, id) -> {
            Concursante seleccionado = (Concursante) adapter.getItem(position);
            if (seleccionado != null) {
                Intent intent = new Intent(this, ParticipantPublicActivity.class);
                intent.putExtra("CONCURSANTE_ID", seleccionado.getId());
                startActivity(intent);
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        spinnerEdiciones = findViewById(R.id.spinnerEdiciones);
        lvParticipantes = findViewById(R.id.lvParticipantes);
        etBuscar = findViewById(R.id.etBuscarParticipante);
        // Ocultamos el filtro de galas en este layout si existiera el ID
        View galaLayout = findViewById(R.id.layoutFilterGala);
        if (galaLayout != null) galaLayout.setVisibility(View.GONE);
    }

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                for (Edicion e : ediciones) labels.add("Edición #" + e.getId());

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_rol_item, labels);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEdiciones.setAdapter(spinnerAdapter);

                spinnerEdiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        cargarParticipantes(listaEdiciones.get(pos).getId());
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
            }
        });
    }

    private void cargarParticipantes(int idEdicion) {
        concursanteService.obtenerPorEdicion(idEdicion).observe(this, concursantes -> {
            if (concursantes != null) {
                listaConcursantesFull = new ArrayList<>(concursantes);
                adapter = new ParticipantAdapter(this, R.layout.item_participant, listaConcursantesFull);
                lvParticipantes.setAdapter(adapter);
                filtrarListaLocal(etBuscar.getText().toString());
            }
        });
    }

    private void filtrarListaLocal(String texto) {
        if (adapter == null) return;
        List<Concursante> filtrados = new ArrayList<>();
        String busqueda = texto.toLowerCase().trim();
        if (busqueda.isEmpty()) filtrados.addAll(listaConcursantesFull);
        else {
            for (Concursante c : listaConcursantesFull) {
                if ((c.getNombre() + " " + c.getPrimerApellido()).toLowerCase().contains(busqueda))
                    filtrados.add(c);
            }
        }
        adapter.updateData(filtrados);
    }
}