package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.Puntuacion; // O un objeto DTO si lo tuviéramos

import java.util.List;
import java.util.Map;

// Nota: Para simplificar, este adaptador recibirá una lista de Pares o un Objeto auxiliar.
// Pero para no complicarte con clases nuevas, pasaremos la lista de Concursantes
// y un Map con sus medias.
public class GalaScoreAdapter extends ArrayAdapter<Concursante> {

    private Context context;
    private int resource;
    private Map<Integer, Double> puntuacionesMap; // ID Concursante -> Media Puntos

    public GalaScoreAdapter(@NonNull Context context, int resource,
                            @NonNull List<Concursante> objects,
                            Map<Integer, Double> puntuacionesMap) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.puntuacionesMap = puntuacionesMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Concursante c = getItem(position);

        TextView tvPos = convertView.findViewById(R.id.tvPosition);
        TextView tvName = convertView.findViewById(R.id.tvContestantName);
        TextView tvScore = convertView.findViewById(R.id.tvScoreValue);

        if (c != null) {
            // Posición (empezando por 1)
            tvPos.setText((position + 1) + "º");

            // Nombre
            tvName.setText(c.getNombre() + " " + c.getPrimerApellido());

            // Puntuación (Sacada del mapa)
            Double nota = puntuacionesMap.get(c.getId());
            if (nota != null) {
                tvScore.setText(String.format("%.1f", nota));
            } else {
                tvScore.setText("-");
            }
        }
        return convertView;
    }
}