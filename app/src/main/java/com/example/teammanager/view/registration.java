package com.example.teammanager.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teammanager.R;
import com.example.teammanager.controller.AuthController;

public class registration extends AppCompatActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    };
    authController = new AuthController();

    authController.registerUser(email, password, new AuthController.OnAuthCompleteListener() {
        @Override
        public void onSuccess() {
            Toast.makeText(RegistrationActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
            // переход на Login или MainActivity
        }

        @Override
        public void onFailure(String error) {
            Toast.makeText(RegistrationActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
        }
    });

}