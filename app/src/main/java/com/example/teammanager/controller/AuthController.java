package com.example.teammanager.controller;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AuthController {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final FirebaseDatabase database; // Вот это объявление


    public AuthController() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();  // Инициализация Realtime Database
    }

    public void registerUser(String name, String email, String password, OnAuthCompleteListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // ... (код сохранения данных пользователя с ролью "user" в Realtime Database)
                            Map<String, String> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("name", name);
                            userData.put("role", "user");

                            database.getReference("users").child(user.getUid()).setValue(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        listener.onSuccess(name, "user"); // Передаем роль при регистрации
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("AuthController", "Ошибка сохранения данных пользователя");
                                        listener.onFailure("Ошибка сохранения данных пользователя.");
                                        user.delete();
                                    });
                        } else {
                            listener.onFailure("Ошибка: Не удалось получить текущего пользователя после регистрации.");
                        }
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }


    public void loginUser(String email, String password, OnAuthCompleteListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            database.getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String name = dataSnapshot.child("name").getValue(String.class);
                                        String role = dataSnapshot.child("role").getValue(String.class);
                                        listener.onSuccess(name != null ? name : "", role != null ? role : "user"); // Передаем имя и роль
                                    } else {
                                        listener.onSuccess(user.getDisplayName() != null ? user.getDisplayName() : "", "user"); // Если нет данных в 'users', считаем обычным пользователем
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("AuthController", "Ошибка при чтении данных пользователя");
                                    listener.onSuccess(user.getDisplayName() != null ? user.getDisplayName() : "", "user"); // В случае ошибки считаем обычным пользователем
                                }
                            });
                        } else {
                            listener.onFailure("Ошибка: Не удалось получить текущего пользователя после входа.");
                        }
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }

    public interface OnAuthCompleteListener {
        void onSuccess(String name, String role); // Обновленный интерфейс с ролью
        void onFailure(String error);
    }
//    public void registerUser(String name, String email, String password, OnAuthCompleteListener listener) {
//        auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = auth.getCurrentUser();
//                        if (user != null) {
//                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(name)
//                                    .build();
//
//                            user.updateProfile(profileUpdates)
//                                    .addOnCompleteListener(profileTask -> {
//                                        if (profileTask.isSuccessful()) {
//                                            // Сохраняем данные пользователя в Firestore с ролью "user"
//                                            Map<String, Object> userData = new HashMap<>();
//                                            userData.put("uid", user.getUid());
//                                            userData.put("email", email);
//                                            userData.put("name", name);
//                                            userData.put("role", "user"); // Устанавливаем роль по умолчанию
//
//                                            firestore.collection("users").document(user.getUid())
//                                                    .set(userData)
//                                                    .addOnSuccessListener(documentReference -> {
//                                                        listener.onSuccess(name);
//                                                    })
//                                                    .addOnFailureListener(e -> {
//                                                        Log.e("AuthController", "Ошибка сохранения данных пользователя", e);
//                                                        listener.onFailure("Ошибка сохранения данных пользователя.");
//                                                        // Опционально: можно удалить пользователя Auth при неудаче сохранения в Firestore
//                                                        user.delete();
//                                                    });
//                                        } else {
//                                            listener.onFailure("Ошибка при обновлении профиля пользователя: " + profileTask.getException().getMessage());
//                                            // Опционально: можно удалить пользователя Auth при неудаче обновления профиля
//                                            user.delete();
//                                        }
//                                    });
//                        } else {
//                            listener.onFailure("Ошибка: Не удалось получить текущего пользователя после регистрации.");
//                        }
//                    } else {
//                        listener.onFailure(task.getException().getMessage());
//                    }
//                });
//    }
//    public void loginUser(String email, String password, OnAuthCompleteListener listener) {
//        auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = auth.getCurrentUser();
//                        if (user != null) {
//                            // Здесь вы можете получить имя пользователя из Firestore, если оно вам нужно
//                            firestore.collection("users").document(user.getUid()).get()
//                                    .addOnSuccessListener(documentSnapshot -> {
//                                        if (documentSnapshot.exists() && documentSnapshot.contains("name")) {
//                                            String name = documentSnapshot.getString("name");
//                                            listener.onSuccess(name);
//                                        } else {
//                                            listener.onSuccess(user.getDisplayName() != null ? user.getDisplayName() : ""); // Или другое значение по умолчанию
//                                        }
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        listener.onSuccess(user.getDisplayName() != null ? user.getDisplayName() : ""); // Обработка ошибки получения имени
//                                    });
//                        } else {
//                            listener.onSuccess(""); // Или обработайте случай отсутствия пользователя
//                        }
//                    } else {
//                        listener.onFailure(task.getException().getMessage());
//                    }
//                });
//    }
}