package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Noticia;
import com.squareup.picasso.Picasso;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NoticiaAdapter extends BaseAdapter {
    private Context context;
    private List<Noticia> noticias;

    public NoticiaAdapter(Context context, List<Noticia> noticias) {
        this.context = context;
        this.noticias = noticias;
    }

    @Override
    public int getCount() {
        return noticias != null ? noticias.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return noticias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return noticias.get(position).getId();
    }

    // Clase interna para optimizar el rendimiento de la lista
    static class ViewHolder {
        TextView title;
        TextView date;
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_noticia, parent, false);
            holder = new ViewHolder();
            // Ajustamos los IDs a los del XML item_noticia.xml
            holder.title = convertView.findViewById(R.id.tvNewsTitleItem);
            holder.date = convertView.findViewById(R.id.tvNewsDateItem);
            holder.img = convertView.findViewById(R.id.ivNewsItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Noticia noticia = noticias.get(position);

        holder.title.setText(noticia.getCabecera());

        // Corregido: LocalDate no tiene horas, usamos solo fecha
        if (noticia.getFechaPublicacion() != null) {
            String fechaFormateada = noticia.getFechaPublicacion().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            holder.date.setText(fechaFormateada);
        }

        // Cargar imagen con Picasso usando iconos del sistema
        if (noticia.getImagen() != null && !noticia.getImagen().trim().isEmpty()) {
            Picasso.get()
                    .load(noticia.getImagen())
                    .placeholder(android.R.drawable.ic_menu_report_image)
                    .error(android.R.drawable.ic_menu_report_image)
                    .fit() // Optimiza la imagen al tamaño del ImageView
                    .centerCrop()
                    .into(holder.img);
        } else {
            holder.img.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        return convertView;
    }

    public void setNoticias(List<Noticia> nuevasNoticias) {
        this.noticias = nuevasNoticias;
        notifyDataSetChanged();
    }
}