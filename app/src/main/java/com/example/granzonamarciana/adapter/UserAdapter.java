package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.activity.ProfileActivity; // Importar ProfileActivity
import com.example.granzonamarciana.entity.DomainEntity;
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.Concursante;

import java.util.List;

public class UserAdapter extends ArrayAdapter<DomainEntity> {

    private Context context;
    private int resource;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<DomainEntity> objects) {
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

        DomainEntity usuario = getItem(position);

        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvRole = convertView.findViewById(R.id.tvUserRole);
        ImageView ivDelete = convertView.findViewById(R.id.ivDeleteUser);
        ImageView ivIcon = convertView.findViewById(R.id.ivUserIcon);

        if (usuario != null) {
            String nombre = "";
            String rol = "";
            int userId = -1;
            String userRoleStr = "";

            if (usuario instanceof Espectador) {
                nombre = ((Espectador) usuario).getNombre();
                rol = "ESPECTADOR";
                userRoleStr = "ESPECTADOR";
                userId = ((Espectador) usuario).getId();
                ivIcon.setImageResource(R.drawable.ic_person);
            } else if (usuario instanceof Concursante) {
                nombre = ((Concursante) usuario).getNombre();
                rol = "CONCURSANTE";
                userRoleStr = "CONCURSANTE";
                userId = ((Concursante) usuario).getId();
                ivIcon.setImageResource(R.drawable.ic_launcher_foreground);
            }

            tvUsername.setText(nombre);
            tvRole.setText(rol);

            // --- CLICK EN EL ELEMENTO PARA IR AL PERFIL ---
            // Guardamos variables finales para usar en la lambda
            final int finalUserId = userId;
            final String finalUserRoleStr = userRoleStr;

            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("TARGET_USER_ID", finalUserId);
                intent.putExtra("TARGET_USER_ROLE", finalUserRoleStr);
                context.startActivity(intent);
            });

            // Botón Eliminar (Independiente del click en el perfil)
            ivDelete.setOnClickListener(v -> {
                Toast.makeText(context, "Borrar: " + finalUserId, Toast.LENGTH_SHORT).show();

            });
        }

        return convertView;
    }
}