package com.example.teammanager.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.teammanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private EditText editNameEditText;
    private EditText editEmailEditText;
    private Button saveProfileButton;
    private Button cancelEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editNameEditText = findViewById(R.id.editNameEditText);
        editEmailEditText = findViewById(R.id.editEmailEditText);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        cancelEditButton = findViewById(R.id.cancelEditButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("userName");
            String email = extras.getString("userEmail");
            editNameEditText.setText(name);
            editEmailEditText.setText(email);
        }

        saveProfileButton.setOnClickListener(v -> {
            String newName = editNameEditText.getText().toString().trim();
            String newEmail = editEmailEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newName)) {
                editNameEditText.setError("Имя не может быть пустым");
                return;
            }

            if (TextUtils.isEmpty(newEmail) || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                editEmailEditText.setError("Введите корректный email");
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newName);
                updates.put("email", newEmail);

                userRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfile.this, "Профиль обновлен", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditProfile.this, "Ошибка при обновлении профиля: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        cancelEditButton.setOnClickListener(v -> {
            finish();
        });
    }
}