package com.example.granzonamarciana.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.granzonamarciana.R;

public class MenuAdminActivity extends AppCompatActivity {

    private Button btnLogout, btnManageUsers, btnManageNews, btnManageEditions,
            btnManageApplications, btnManageGalas, btnCreateAdmin, btnMyProfile;
    private TextView tvWelcomeAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        tvWelcomeAdmin = findViewById(R.id.tvWelcomeAdmin);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageNews = findViewById(R.id.btnManageNews);
        btnManageEditions = findViewById(R.id.btnManageEditions);
        btnManageApplications = findViewById(R.id.btnManageApplications);
        btnManageGalas = findViewById(R.id.btnManageGalas);
        btnCreateAdmin = findViewById(R.id.btnCreateAdmin);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs = getSharedPreferences("granZMUser", MODE_PRIVATE);
        tvWelcomeAdmin.setText("Panel Admin: " + prefs.getString("username", "Admin"));

        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        btnManageNews.setOnClickListener(v -> startActivity(new Intent(this, NewsListActivity.class)));
        btnManageEditions.setOnClickListener(v -> startActivity(new Intent(this, CreateEditionActivity.class)));
        btnManageApplications.setOnClickListener(v -> startActivity(new Intent(this, ManageApplicationsActivity.class)));
        btnManageGalas.setOnClickListener(v -> startActivity(new Intent(this, GalasListActivity.class)));
        btnMyProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        btnCreateAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAdminActivity.class);
            intent.putExtra("isAdminCreation", true);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("granZMUser", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}