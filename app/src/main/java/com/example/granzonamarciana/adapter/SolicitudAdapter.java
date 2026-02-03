package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.pojo.SolicitudConConcursante;
import java.util.ArrayList;
import java.util.List;

public class SolicitudAdapter extends BaseAdapter {
    private Context context;
    // CAMBIO: Ahora usamos el POJO para tener acceso a los datos del concursante
    private List<SolicitudConConcursante> solicitudes = new ArrayList<>();

    public SolicitudAdapter(Context context) {
        this.context = context;
    }

    // Método corregido para aceptar la lista del POJO
    public void setSolicitudes(List<SolicitudConConcursante> nuevasSolicitudes) {
        this.solicitudes.clear();
        if (nuevasSolicitudes != null) {
            this.solicitudes.addAll(nuevasSolicitudes);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return solicitudes.size(); }

    @Override
    public Object getItem(int position) { return solicitudes.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_solicitud, parent, false);
        }

        // Obtenemos el objeto compuesto (Solicitud + Concursante)
        SolicitudConConcursante solicitudConConcursante = solicitudes.get(position);

        TextView tvNombre = convertView.findViewById(R.id.tvEditionLabel); // Reutilizamos para el nombre
        TextView tvMsg = convertView.findViewById(R.id.tvMessagePreview);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);

        if (solicitudConConcursante != null && solicitudConConcursante.solicitud != null) {
            if (solicitudConConcursante.concursante != null) {
                tvNombre.setText(solicitudConConcursante.concursante.getNombre() + " " + solicitudConConcursante.concursante.getPrimerApellido());
            } else {
                tvNombre.setText("Edición #" + solicitudConConcursante.solicitud.getEditionId());
            }

            tvMsg.setText(solicitudConConcursante.solicitud.getMensaje());
            tvStatus.setText(solicitudConConcursante.solicitud.getEstado().toString());

            // Colores según estado
            switch (solicitudConConcursante.solicitud.getEstado()) {
                case ACEPTADA:
                    tvStatus.setTextColor(context.getColor(android.R.color.holo_green_dark));
                    break;
                case RECHAZADA:
                    tvStatus.setTextColor(context.getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    tvStatus.setTextColor(context.getColor(android.R.color.darker_gray));
                    break;
            }
        }

        return convertView;
    }
}