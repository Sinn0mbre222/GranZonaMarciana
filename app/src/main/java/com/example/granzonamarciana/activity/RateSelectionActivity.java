package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.ParticipantAdapter;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Edicion;
import com.example.granzonamarciana.entity.Gala;
import com.example.granzonamarciana.service.ConcursanteService;
import com.example.granzonamarciana.service.EdicionService;
import com.example.granzonamarciana.service.GalaService;
import java.util.ArrayList;
import java.util.List;

public class RateSelectionActivity extends AppCompatActivity {

    private Spinner spEdicion, spGala;
    private ListView lvRanking;
    private EdicionService edicionService;
    private GalaService galaService;
    private ConcursanteService concursanteService;

    private List<Edicion> listaEdiciones = new ArrayList<>();
    private List<Gala> listaGalas = new ArrayList<>();
    private ParticipantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. ASEGÚRATE DE QUE ESTE LAYOUT ES EL QUE NO TIENE BUSCADOR
        setContentView(R.layout.activity_rate_selection);

        spEdicion = findViewById(R.id.spinnerEdiciones);
        spGala = findViewById(R.id.spinnerGalas);
        lvRanking = findViewById(R.id.lvParticipantes);

        edicionService = new EdicionService(this);
        galaService = new GalaService(getApplication());
        concursanteService = new ConcursanteService(this);

        setupListeners();
        cargarEdiciones();
    }

    private void setupListeners() {
        lvRanking.setOnItemClickListener((parent, view, position, id) -> {
            // 2. OBTENEMOS EL OBJETO DEL ADAPTADOR DE FORMA SEGURA
            Concursante seleccionado = (Concursante) parent.getAdapter().getItem(position);

            int galaPos = spGala.getSelectedItemPosition();

            if (seleccionado != null && galaPos >= 0 && !listaGalas.isEmpty()) {
                // 3. AQUÍ FORZAMOS EL DESTINO A LA PANTALLA DE VOTAR
                Intent intent = new Intent(RateSelectionActivity.this, RateParticipantActivity.class);
                intent.putExtra("CONCURSANTE_ID", seleccionado.getId());
                intent.putExtra("GALA_ID", listaGalas.get(galaPos).getId());
                intent.putExtra("CONCURSANTE_NOMBRE", seleccionado.getNombre() + " " + seleccionado.getPrimerApellido());
                intent.putExtra("CONCURSANTE_FOTO", seleccionado.getImagenUrl());

                startActivity(intent);
            } else {
                Toast.makeText(this, "Selecciona una gala válida", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    // ... (CargarEdiciones, CargarGalas y CargarParticipantes se mantienen igual)

    private void cargarEdiciones() {
        edicionService.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null && !ediciones.isEmpty()) {
                listaEdiciones = ediciones;
                List<String> labels = new ArrayList<>();
                for (Edicion e : ediciones) labels.add("Edición #" + e.getId());

                ArrayAdapter<String> ad = new ArrayAdapter<>(this, R.layout.spinner_rol_item, labels);
                spEdicion.setAdapter(ad);

                spEdicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                        int idEd = listaEdiciones.get(pos).getId();
                        cargarGalas(idEd);
                        cargarParticipantes(idEd);
                    }
                    @Override public void onNothingSelected(AdapterView<?> p) {}
                });
            }
        });
    }

    private void cargarGalas(int idEd) {
        galaService.getGalasByEdicion(idEd).observe(this, galas -> {
            listaGalas = (galas != null) ? galas : new ArrayList<>();
            List<String> labels = new ArrayList<>();
            for (Gala g : listaGalas) labels.add("Gala: " + g.getFecha());
            if (labels.isEmpty()) labels.add("Sin galas disponibles");

            ArrayAdapter<String> adGa = new ArrayAdapter<>(this, R.layout.spinner_rol_item, labels);
            spGala.setAdapter(adGa);
        });
    }

    private void cargarParticipantes(int idEd) {
        concursanteService.obtenerPorEdicion(idEd).observe(this, list -> {
            if (list != null) {
                adapter = new ParticipantAdapter(this, R.layout.item_participant, list);
                lvRanking.setAdapter(adapter);
            }
        });
    }
}