package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Edicion;
import java.util.List;

public class EdicionAdapter extends BaseAdapter {
    private Context context;
    private List<Edicion> ediciones;

    public EdicionAdapter(Context context, List<Edicion> ediciones) {
        this.context = context;
        this.ediciones = ediciones;
    }

    @Override public int getCount() { return ediciones.size(); }
    @Override public Object getItem(int position) { return ediciones.get(position); }
    @Override public long getItemId(int position) { return ediciones.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_edicion, parent, false);
        }
        Edicion edicion = ediciones.get(position);

        TextView nombre = convertView.findViewById(R.id.tvEdicionNombre);
        TextView fechas = convertView.findViewById(R.id.tvEdicionFechas);
        TextView cupo = convertView.findViewById(R.id.tvEdicionCupo);

        nombre.setText("Edición #" + edicion.getId());
        fechas.setText("Inicio: " + edicion.getFechaInicio() + " - Fin: " + edicion.getFechaFinal());
        cupo.setText("Cupo Máximo: " + edicion.getNumeroParticipantesMax() + " participantes");

        return convertView;
    }
}