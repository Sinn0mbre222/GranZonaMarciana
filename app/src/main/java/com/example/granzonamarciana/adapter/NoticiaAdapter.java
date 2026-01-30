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
        return noticias.size();
    }

    @Override
    public Object getItem(int position) {
        return noticias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return noticias.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_noticia, parent, false);
        }

        Noticia noticia = noticias.get(position);

        TextView title = convertView.findViewById(R.id.tvItemTitulo);
        TextView date = convertView.findViewById(R.id.tvItemFecha);
        ImageView img = convertView.findViewById(R.id.ivItemNoticia);

        title.setText(noticia.getCabecera());

        String fechaFormateada = noticia.getFechaPublicacion().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        date.setText("Publicado: " + fechaFormateada);

        // Cargar imagen con Picasso
        Picasso.get()
                .load(noticia.getImagen())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(img);

        return convertView;
    }
}