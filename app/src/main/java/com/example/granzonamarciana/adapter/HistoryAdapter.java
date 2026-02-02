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
import com.example.granzonamarciana.entity.Puntuacion;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<Puntuacion> {

    private Context context;
    private int resource;

    public HistoryAdapter(@NonNull Context context, int resource, @NonNull List<Puntuacion> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Puntuacion puntuacion = getItem(position);

        TextView tvMain = convertView.findViewById(R.id.tvHistoryMain);
        TextView tvDate = convertView.findViewById(R.id.tvHistoryDate);
        TextView tvScore = convertView.findViewById(R.id.tvHistoryScore);

        if (puntuacion != null) {
            // Mejoramos el texto para que quede claro que es por GALA
            tvMain.setText("Puntuación en Gala #" + puntuacion.getGalaId());

            // Formateo de fecha (asumiendo que usas LocalDate)
            if (puntuacion.getFechaVoto() != null) {
                tvDate.setText("Realizado el: " + puntuacion.getFechaVoto().toString());
            }

            // Visualización de la puntuación
            tvScore.setText(puntuacion.getValor() + " ★");

            // MEJORA VISUAL: Color según la nota
            if (puntuacion.getValor() >= 4) {
                tvScore.setTextColor(context.getColor(android.R.color.holo_green_dark));
            } else if (puntuacion.getValor() <= 2) {
                tvScore.setTextColor(context.getColor(android.R.color.holo_red_dark));
            } else {
                tvScore.setTextColor(context.getColor(android.R.color.holo_orange_dark));
            }
        }

        return convertView;
    }
}