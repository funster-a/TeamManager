package com.example.teammanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity {
    private EditText editTextLoginEmail, editTextLoginPassword;
    private Button buttonLogin;
    private TextView textViewRegisterLink;
    private AuthController authController;
    private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001; // Константа для запроса Intent
    private FirebaseAuth mAuth; // Добавьте экземпляр FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authController = new AuthController();
        mAuth = FirebaseAuth.getInstance(); // Инициализируйте FirebaseAuth

        googleSignInButton = findViewById(R.id.sign_in_button_google);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegisterLink = findViewById(R.id.textViewRegisterLink);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextLoginEmail.setError("Email обязателен");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextLoginEmail.setError("Введите корректный email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextLoginPassword.setError("Пароль обязателен");
            return;
        }

        authController.loginUser(email, password, new AuthController.OnAuthCompleteListener() {
            @Override
            public void onSuccess(String name) {
                Toast.makeText(login.this, "Вход успешен!", Toast.LENGTH_SHORT).show();
                // TODO: Переход на следующий экран (например, экран списка задач)
                Intent intent = new Intent(login.this, TaskListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(login.this, "Ошибка входа: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Ошибка при входе через Google
                Log.w("GoogleSignIn", "Google sign in failed", e);
                Toast.makeText(this, "Ошибка входа через Google.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Вход в Firebase успешен
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user); // Переход на следующий экран
                    } else {
                        // Ошибка при входе в Firebase
                        Log.w("FirebaseAuth", "firebaseAuthWithGoogle failed", task.getException());
                        Toast.makeText(this, "Ошибка аутентификации Firebase.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Перейти на главный экран приложения
            Intent intent = new Intent(this, TaskListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

//package com.example.teammanager.view;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.teammanager.R;
//import com.example.teammanager.controller.AuthController;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.SignInButton;
//
//public class login extends AppCompatActivity {
//    private EditText editTextLoginEmail, editTextLoginPassword;
//    private Button buttonLogin;
//    private TextView textViewRegisterLink;
//    private AuthController authController;
//    private SignInButton googleSignInButton;
//    private GoogleSignInClient mGoogleSignInClient;
//    private static final int RC_SIGN_IN = 9001; // Константа для запроса Intent
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        authController = new AuthController();
//
//        googleSignInButton = findViewById(R.id.sign_in_button_google);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        googleSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signInWithGoogle();
//            }
//        });
//
//        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
//        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
//        buttonLogin = findViewById(R.id.buttonLogin);
//        textViewRegisterLink = findViewById(R.id.textViewRegisterLink);
//
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginUser();
//            }
//        });
//
//        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(login.this, registration.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void loginUser() {
//        String email = editTextLoginEmail.getText().toString().trim();
//        String password = editTextLoginPassword.getText().toString().trim();
//
//        if (TextUtils.isEmpty(email)) {
//            editTextLoginEmail.setError("Email обязателен");
//            return;
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            editTextLoginEmail.setError("Введите корректный email");
//            return;
//        }
//
//        if (TextUtils.isEmpty(password)) {
//            editTextLoginPassword.setError("Пароль обязателен");
//            return;
//        }
//
//        authController.loginUser(email, password, new AuthController.OnAuthCompleteListener() {
//            @Override
//            public void onSuccess(String name) {
//                Toast.makeText(login.this, "Вход успешен!", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(login.this, TaskListActivity.class);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onFailure(String error) {
//                Toast.makeText(login.this, "Ошибка входа: " + error, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void signInWithGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//}