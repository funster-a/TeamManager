package com.example.teammanager.view;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private EditText editTextName, editTextEmail, editTextPassword, editTextRePassword;
    private Button buttonRegister;
    private TextView textViewLoginLink;

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

        editTextName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        buttonRegister = findViewById(R.id.buttonSignup);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        buttonRegister.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String rePassword = editTextRePassword.getText().toString().trim();

            if (name.isEmpty()) {
                editTextName.setError("Имя обязательно");
                return;
            }

            if (email.isEmpty()) {
                editTextEmail.setError("Email обязателен");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Введите корректный email");
                return;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Пароль обязателен");
                return;
            }

            if (password.length() < 6) {
                editTextPassword.setError("Пароль должен быть не менее 6 символов");
                return;
            }

            if (rePassword.isEmpty()) {
                editTextRePassword.setError("Подтверждение пароля обязательно");
                return;
            }

            if (!password.equals(rePassword)) {
                editTextRePassword.setError("Пароли не совпадают");
                return;
            }

            authController.registerUser(name, email, password, new AuthController.OnAuthCompleteListener() {
                @Override
                public void onSuccess(String registeredName) { // Обновленный onSuccess
                    Toast.makeText(registration.this, "Регистрация успешна, " + registeredName + "!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(registration.this, MainActivity.class); // Или MainActivity.class
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(registration.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        textViewLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(registration.this, login.class);
            startActivity(intent);
            finish();
        });
    }
}