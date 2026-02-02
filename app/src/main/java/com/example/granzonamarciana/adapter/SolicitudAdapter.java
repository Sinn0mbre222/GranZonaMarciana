package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Solicitud;
import java.util.ArrayList;
import java.util.List;

public class SolicitudAdapter extends BaseAdapter {
    private Context context;
    private List<Solicitud> solicitudes = new ArrayList<>();

    public SolicitudAdapter(Context context) {
        this.context = context;
    }

    // Mertodo para actualizar los datos desde la Activity
    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
        notifyDataSetChanged(); // Esto refresca la lista visualmente
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

        Solicitud actual = solicitudes.get(position);

        TextView tvEdition = convertView.findViewById(R.id.tvEditionLabel);
        TextView tvMsg = convertView.findViewById(R.id.tvMessagePreview);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);

        // Asegúrate de que los IDs (tvEditionLabel, etc.) existan en item_solicitud.xml
        if (actual != null) {
            tvEdition.setText("Edición #" + actual.getEditionId());
            tvMsg.setText(actual.getMensaje());
            tvStatus.setText(actual.getEstado().toString());

            // Colores según estado para mejor feedback visual
            switch (actual.getEstado()) {
                case ACEPTADA:
                    tvStatus.setTextColor(context.getColor(android.R.color.holo_green_dark));
                    break;
                case RECHAZADA:
                    tvStatus.setTextColor(context.getColor(android.R.color.holo_red_dark));
                    break;
                case PENDIENTE:
                default:
                    tvStatus.setTextColor(context.getColor(android.R.color.darker_gray));
                    break;
            }
        }

        return convertView;
    }
}