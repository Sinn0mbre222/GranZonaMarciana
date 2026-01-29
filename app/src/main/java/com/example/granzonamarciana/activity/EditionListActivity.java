package com.example.granzonamarciana.activity;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;
import com.example.granzonamarciana.adapter.EdicionAdapter;
import com.example.granzonamarciana.service.EdicionService;

public class EditionListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editions_list);

        ListView lv = findViewById(R.id.lvEditions);
        EdicionService service = new EdicionService(this);

        service.listarEdiciones().observe(this, ediciones -> {
            if (ediciones != null) {
                // Asume que creaste EdicionAdapter similar al de noticias
                lv.setAdapter(new EdicionAdapter(this, ediciones));
            }
        });
    }
}