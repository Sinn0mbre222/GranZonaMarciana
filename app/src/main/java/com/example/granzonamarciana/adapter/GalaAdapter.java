package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Gala;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GalaAdapter extends BaseAdapter {
    private Context context;
    private List<Gala> galas = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public GalaAdapter(Context context) {
        this.context = context;
    }

    public void setGalas(List<Gala> nuevasGalas) {
        this.galas.clear();
        if (nuevasGalas != null) {
            this.galas.addAll(nuevasGalas);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return galas.size(); }

    @Override
    public Object getItem(int position) { return galas.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    static class ViewHolder {
        TextView tvName;
        TextView tvDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gala, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvGalaName);
            holder.tvDate = convertView.findViewById(R.id.tvGalaDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Gala actual = galas.get(position);

        // Es mejor usar el ID real de la base de datos o un contador
        holder.tvName.setText("Gala #" + actual.getId());

        if (actual.getFecha() != null) {
            holder.tvDate.setText(actual.getFecha().format(formatter));
        }

        return convertView;
    }
}