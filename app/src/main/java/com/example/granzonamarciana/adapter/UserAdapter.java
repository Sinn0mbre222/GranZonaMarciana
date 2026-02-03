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
import com.example.granzonamarciana.entity.DomainEntity;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.Concursante;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UserAdapter extends ArrayAdapter<DomainEntity> {

    private Context context;
    private int resource;
    private OnUserDeleteListener deleteListener;

    public interface OnUserDeleteListener {
        void onDelete(DomainEntity usuario);
    }

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<DomainEntity> objects, OnUserDeleteListener listener) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        DomainEntity usuario = getItem(position);

        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvRole = convertView.findViewById(R.id.tvUserRole);
        ImageView ivDelete = convertView.findViewById(R.id.ivDeleteUser);
        ImageView ivPhoto = convertView.findViewById(R.id.ivUserIcon);

        if (usuario != null) {
            String nombre = "";
            String rol = "";
            String imgUrl = "";

            if (usuario instanceof Espectador) {
                Espectador e = (Espectador) usuario;
                nombre = e.getNombre() + " " + e.getPrimerApellido();
                rol = "ESPECTADOR";
                imgUrl = e.getImagenUrl();
            } else if (usuario instanceof Concursante) {
                Concursante c = (Concursante) usuario;
                nombre = c.getNombre() + " " + c.getPrimerApellido();
                rol = "CONCURSANTE";
                imgUrl = c.getImagenUrl();
            }

            tvUsername.setText(nombre);
            tvRole.setText(rol);

            if (imgUrl != null && imgUrl.startsWith("http")) {
                Picasso.get()
                        .load(imgUrl)
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .into(ivPhoto);
            } else {
                ivPhoto.setImageResource(R.drawable.ic_default_avatar);
            }

            ivDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(usuario);
                }
            });
        }

        return convertView;
    }
}