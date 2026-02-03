package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.service.NoticiaService;
import com.squareup.picasso.Picasso;
import java.time.format.DateTimeFormatter;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        int id = getIntent().getIntExtra("ID", -1);
        NoticiaService service = new NoticiaService(this);

        service.buscarPorId(id).observe(this, n -> {
            if (n != null) {
                TextView tvTitle = findViewById(R.id.tvNewsTitle);
                TextView tvBody = findViewById(R.id.tvNewsBody);
                TextView tvDate = findViewById(R.id.tvNewsDate);
                ImageView imageView = findViewById(R.id.ivNewsDetail);

                tvTitle.setText(n.getCabecera());
                tvBody.setText(n.getCuerpo());

                // Formatear fecha
                if (n.getFechaPublicacion() != null) {
                    tvDate.setText(n.getFechaPublicacion().format(DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy")));
                }

                if (n.getImagen() != null && !n.getImagen().isEmpty()) {
                    Picasso.get().load(n.getImagen())
                            .placeholder(android.R.drawable.ic_menu_report_image)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(imageView);
                }
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}