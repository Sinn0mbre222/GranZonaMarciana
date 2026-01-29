package com.example.granzonamarciana.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Solicitud;
import java.util.ArrayList;
import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder> {

    private List<Solicitud> solicitudes = new ArrayList<>();
    private final OnSolicitudClickListener listener;

    public interface OnSolicitudClickListener {
        void onSolicitudClick(Solicitud solicitud);
    }

    public SolicitudAdapter(OnSolicitudClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud, parent, false);
        return new SolicitudViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        Solicitud actual = solicitudes.get(position);

        holder.tvEditionLabel.setText("Edición #" + actual.getEditionId());
        holder.tvMessagePreview.setText(actual.getMensaje());
        holder.tvStatus.setText(actual.getEstado().toString());

        switch (actual.getEstado()) {
            case ACEPTADA:
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_light));
                break;
            case RECHAZADA:
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_light));
                break;
            default:
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
                break;
        }

        holder.itemView.setOnClickListener(v -> listener.onSolicitudClick(actual));
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
        notifyDataSetChanged();
    }

    class SolicitudViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEditionLabel;
        private final TextView tvMessagePreview;
        private final TextView tvStatus;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEditionLabel = itemView.findViewById(R.id.tvEditionLabel);
            tvMessagePreview = itemView.findViewById(R.id.tvMessagePreview);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}