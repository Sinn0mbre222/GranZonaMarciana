package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.NoticiaAdapter;
import com.example.granzonamarciana.entity.Noticia;
import com.example.granzonamarciana.service.NoticiaService;

public class NewsListActivity extends AppCompatActivity {
    private NoticiaService noticiaService;
    private ListView lvNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        lvNews = findViewById(R.id.lvNews);
        noticiaService = new NoticiaService(this);

        noticiaService.listarNoticias().observe(this, noticias -> {
            if (noticias != null) {
                lvNews.setAdapter(new NoticiaAdapter(this, noticias));
            }
        });

        lvNews.setOnItemClickListener((parent, view, position, id) -> {
            Noticia n = (Noticia) parent.getItemAtPosition(position);
            Intent i = new Intent(this, NewsDetailActivity.class);
            i.putExtra("ID", n.getId());
            startActivity(i);
        });
    }
}
