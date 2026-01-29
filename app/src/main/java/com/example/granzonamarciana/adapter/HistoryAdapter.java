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

        // Referencias a las vistas del layout 'item_history.xml'
        TextView tvMain = convertView.findViewById(R.id.tvHistoryMain);
        TextView tvDate = convertView.findViewById(R.id.tvHistoryDate);
        TextView tvScore = convertView.findViewById(R.id.tvHistoryScore);

        if (puntuacion != null) {
            // Mostramos el ID de la Gala (o podrías buscar el nombre de la gala si hicieras una consulta extra)
            tvMain.setText("Gala " + puntuacion.getGalaId());

            // Fecha del voto
            if (puntuacion.getFechaVoto() != null) {
                tvDate.setText(puntuacion.getFechaVoto().toString());
            }

            // Puntuación con una estrella
            tvScore.setText(puntuacion.getValor() + " ★");
        }

        return convertView;
    }
}