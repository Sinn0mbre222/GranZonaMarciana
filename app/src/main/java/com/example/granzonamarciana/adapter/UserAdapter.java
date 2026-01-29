package com.example.granzonamarciana.adapter;

import android.content.Context;
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
import com.example.granzonamarciana.entity.DomainEntity; // Usamos la clase padre para manejar ambos (Espectador/Concursante)
import com.example.granzonamarciana.entity.Espectador;
import com.example.granzonamarciana.entity.Concursante;
import com.example.granzonamarciana.entity.TipoRol;

import java.util.List;

public class UserAdapter extends ArrayAdapter<DomainEntity> {

    private Context context;
    private int resource;
    private Runnable onDeleteAction; // Para avisar a la activity que borre a alguien

    // Pasamos una lista genérica de entidades (pueden ser Espectadores o Concursantes)
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
            // Determinar Nombre y Rol
            String nombre = "";
            String rol = "";

            if (usuario instanceof Espectador) {
                nombre = ((Espectador) usuario).getNombre(); // O getUsername si lo tienes
                rol = "ESPECTADOR";
                ivIcon.setImageResource(R.drawable.ic_person); // Asegúrate de tener este icono o usa ic_launcher_foreground
            } else if (usuario instanceof Concursante) {
                nombre = ((Concursante) usuario).getNombre();
                rol = "CONCURSANTE";
                ivIcon.setImageResource(R.drawable.ic_launcher_foreground);
            }

            tvUsername.setText(nombre);
            tvRole.setText(rol);

            // Botón Eliminar (Simplemente ocultamos el ítem o lanzamos acción)
            // En una app real, aquí llamaríamos al servicio para borrar
            ivDelete.setOnClickListener(v -> {
                Toast.makeText(context, "Funcionalidad de borrar pendiente de implementar en servicio", Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;
    }
}