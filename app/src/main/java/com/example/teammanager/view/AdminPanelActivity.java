package com.example.teammanager.view;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;


import com.example.teammanager.R;

public class AdminPanelActivity extends AppCompatActivity {
    private Button buttonManageUsers;
    private Button buttonManageTasks;
    private Button buttonLogoutAdmin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);
        mAuth = FirebaseAuth.getInstance();
        buttonManageUsers = findViewById(R.id.buttonManageUsers);
        buttonManageTasks = findViewById(R.id.buttonManageTasks);
        buttonLogoutAdmin = findViewById(R.id.buttonLogoutAdmin);

        buttonManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });

        buttonManageTasks.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, TaskListActivity.class);
            intent.putExtra("userRole", "admin"); // Передаем роль администратора
            startActivity(intent);
        });

        buttonLogoutAdmin.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminPanelActivity.this, login.class);
            startActivity(intent);
            finish();
        });

    }
}