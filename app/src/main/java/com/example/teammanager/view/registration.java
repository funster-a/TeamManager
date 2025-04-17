package com.example.teammanager.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        authController = new AuthController();

        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextRePassword = findViewById(R.id.editTextRePassword);
        Button buttonRegister = findViewById(R.id.buttonSignup);

        buttonRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                authController.registerUser(email, password, new AuthController.OnAuthCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(registration.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        // переход на Login или MainActivity
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(registration.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(registration.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });


        String email="", password = "";
        authController.registerUser(email, password, new AuthController.OnAuthCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(registration.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                // переход на Login или MainActivity
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(registration.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    };


}