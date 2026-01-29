package com.example.granzonamarciana.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.entity.Noticia;
import java.util.List;

public class NoticiaAdapter extends BaseAdapter {
    private Context context;
    private List<Noticia> noticias;

    public NoticiaAdapter(Context context, List<Noticia> noticias) {
        this.context = context;
        this.noticias = noticias;
    }

    @Override public int getCount() { return noticias.size(); }
    @Override public Object getItem(int position) { return noticias.get(position); }
    @Override public long getItemId(int position) { return noticias.get(position).getId(); }

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
        date.setText("Publicado: " + noticia.getFechaPublicacion().toString());

        Glide.with(context)
                .load(noticia.getImagen())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(img);

        return convertView;
    }
}