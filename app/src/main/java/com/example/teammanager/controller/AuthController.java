package com.example.teammanager.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthController {
    private final FirebaseAuth auth;

    public AuthController() {
        auth = FirebaseAuth.getInstance();
    }

    public void registerUser(String name, String email, String password, OnAuthCompleteListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            listener.onSuccess(name); // Передайте имя в onSuccess
                                        } else {
                                            listener.onFailure("Ошибка при обновлении профиля пользователя: " + profileTask.getException().getMessage());
                                        }
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
                            listener.onSuccess(user.getDisplayName()); // Передайте имя при входе (если есть)
                        } else {
                            listener.onSuccess(""); // Или обработайте случай отсутствия имени
                        }
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }

    public interface OnAuthCompleteListener {
        void onSuccess(String name); // Обновленный интерфейс
        void onFailure(String error);
    }
}