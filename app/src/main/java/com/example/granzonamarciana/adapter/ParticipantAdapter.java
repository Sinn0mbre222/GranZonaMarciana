package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Concursante;

import java.util.List;

public class ParticipantAdapter extends ArrayAdapter<Concursante> {

    private Context context;
    private int resource;

    public ParticipantAdapter(@NonNull Context context, int resource, @NonNull List<Concursante> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Patrón básico de ViewHolder para optimizar (opcional pero recomendado)
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Concursante concursante = getItem(position);

        // Asegúrate de que estos IDs coinciden con tu 'item_participant.xml'
        TextView tvNombre = convertView.findViewById(R.id.tvItemNombre);
        TextView tvInfo = convertView.findViewById(R.id.tvItemInfo);
        ImageView ivFoto = convertView.findViewById(R.id.ivItemFoto);

        if (concursante != null) {
            // Nombre y Apellido
            String nombreCompleto = concursante.getNombre() + " " + concursante.getPrimerApellido();
            tvNombre.setText(nombreCompleto);

            // Información extra (luego pondremos la puntuación real)
            tvInfo.setText(concursante.getEmail());

            // Foto por defecto
            ivFoto.setImageResource(R.drawable.ic_default_avatar);
        }

        return convertView;
    }
}