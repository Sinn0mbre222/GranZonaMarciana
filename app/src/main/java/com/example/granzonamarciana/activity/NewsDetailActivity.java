package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.service.NoticiaService;

public class NewsDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        int id = getIntent().getIntExtra("ID", -1);
        NoticiaService service = new NoticiaService(this);

        service.listarNoticiaePorid(id).observe(this, n -> {
            if (n != null) {
                ((TextView)findViewById(R.id.tvNewsTitle)).setText(n.getCabecera());
                ((TextView)findViewById(R.id.tvNewsBody)).setText(n.getCuerpo());
                Glide.with(this).load(n.getImagen()).into((ImageView)findViewById(R.id.ivNewsDetail));
            }
        });
    }
}