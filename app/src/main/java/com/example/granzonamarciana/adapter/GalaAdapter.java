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

    public void setGalas(List<Gala> galas) {
        this.galas = galas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return galas.size(); }

    @Override
    public Object getItem(int position) { return galas.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gala, parent, false);
        }

        Gala actual = galas.get(position);
        TextView tvName = convertView.findViewById(R.id.tvGalaName);
        TextView tvDate = convertView.findViewById(R.id.tvGalaDate);

        tvName.setText("Gala #" + (position + 1));
        tvDate.setText(actual.getFecha().format(formatter));

        return convertView;
    }
}