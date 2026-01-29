package com.example.granzonamarciana.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Gala;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GalaAdapter extends RecyclerView.Adapter<GalaAdapter.GalaViewHolder> {

    private List<Gala> galas = new ArrayList<>();
    private final OnGalaClickListener listener;
    // Formateador para que la fecha se vea bonita (ej: 25/10/2026)
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy");

    public interface OnGalaClickListener {
        void onGalaClick(Gala gala);
    }

    public GalaAdapter(OnGalaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GalaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gala, parent, false);
        return new GalaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalaViewHolder holder, int position) {
        Gala actual = galas.get(position);

        holder.tvGalaName.setText("Gala #" + (position + 1));
        if (actual.getFecha() != null) {
            holder.tvGalaDate.setText(actual.getFecha().format(formatter));
        }

        holder.itemView.setOnClickListener(v -> listener.onGalaClick(actual));
    }

    @Override
    public int getItemCount() {
        return galas.size();
    }

    public void setGalas(List<Gala> galas) {
        this.galas = galas;
        notifyDataSetChanged();
    }

    class GalaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGalaName;
        private final TextView tvGalaDate;

        public GalaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGalaName = itemView.findViewById(R.id.tvGalaName);
            tvGalaDate = itemView.findViewById(R.id.tvGalaDate);
        }
    }
}