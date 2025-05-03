package com.example.teammanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.teammanager.R;

public class Profile extends AppCompatActivity {

    private ImageView profileAvatar;
    private TextView profileName;
    private TextView profileEmail;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileAvatar = findViewById(R.id.profileAvatar);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);

        Button editProfileButton = findViewById(R.id.editProfileButton);

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditProfile.class);
            intent.putExtra("userName", profileName.getText().toString());
            intent.putExtra("userEmail", profileEmail.getText().toString());
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            profileEmail.setText(currentUser.getEmail()); // Отображаем email из Auth

            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);

                        profileName.setText(name != null ? name : "Нет имени");
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(Profile.this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.man) // Ваш placeholder
                                    .error(R.drawable.err) // Иконка ошибки загрузки
                                    .into(profileAvatar);
                        } else {
                            profileAvatar.setImageResource(R.drawable.man); // Placeholder по умолчанию
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Обработка ошибки при чтении данных
                    profileName.setText("Ошибка загрузки");
                }
            });
        }
    }
}