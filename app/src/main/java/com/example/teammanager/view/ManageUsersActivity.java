package com.example.teammanager.view;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teammanager.R;
import com.example.teammanager.view.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<Map<String, Object>> userList = new ArrayList<>();
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        database = FirebaseDatabase.getInstance();

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(userList, this::onUserRoleChanged);
        recyclerViewUsers.setAdapter(userAdapter);

        loadUsers();
    }

    private void loadUsers() {
        database.getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> user = (Map<String, Object>) userSnapshot.getValue();
                    if (user != null) {
                        user.put("uid", userSnapshot.getKey()); // Добавляем UID в данные пользователя
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ManageUsers", "Error getting users: ");
                // TODO: Обработка ошибки загрузки пользователей
            }
        });
    }

    public void onUserRoleChanged(String userId, String newRole) {
        database.getReference("users").child(userId).child("role").setValue(newRole)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ManageUsers", "Роль пользователя " + userId + " успешно изменена на " + newRole);
                    // Можно добавить Toast об успешном обновлении
                })
                .addOnFailureListener(e -> {
                    Log.e("ManageUsers", "Ошибка при обновлении роли пользователя " + userId);
                    // TODO: Обработка ошибки обновления роли
                });
    }
}