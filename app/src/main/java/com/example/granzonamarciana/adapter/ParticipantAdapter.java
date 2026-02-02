package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.activity.ProfileActivity; // Importar
import com.example.granzonamarciana.entity.Concursante;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ParticipantAdapter extends ArrayAdapter<Concursante> {

    private Context context;
    private int resource;

    static class ViewHolder {
        TextView tvNombre;
        TextView tvInfo;
        ImageView ivFoto;
    }

    public ParticipantAdapter(@NonNull Context context, int resource, @NonNull List<Concursante> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvNombre = convertView.findViewById(R.id.tvItemNombre);
            holder.tvInfo = convertView.findViewById(R.id.tvItemInfo);
            holder.ivFoto = convertView.findViewById(R.id.ivItemFoto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Concursante concursante = getItem(position);

        if (concursante != null) {
            String nombreCompleto = concursante.getNombre() + " " + concursante.getPrimerApellido();
            holder.tvNombre.setText(nombreCompleto);
            holder.tvInfo.setText(concursante.getEmail());

            String url = concursante.getImagenUrl();

            if (url != null && !url.isEmpty() && !url.equals("ic_default_avatar")) {
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .into(holder.ivFoto);
            } else {
                holder.ivFoto.setImageResource(R.drawable.ic_default_avatar);
            }

            // --- CLICK PARA VER EL PERFIL DEL CONCURSANTE ---
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("TARGET_USER_ID", concursante.getId());
                intent.putExtra("TARGET_USER_ROLE", "CONCURSANTE"); // Siempre es concursante en esta lista
                context.startActivity(intent);
            });
        }

        return convertView;
    }

    public void updateData(List<Concursante> nuevaLista) {
        this.clear();
        if (nuevaLista != null) {
            this.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }
}