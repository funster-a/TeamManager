package com.example.teammanager.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teammanager.R;
import com.example.teammanager.view.ChatAdapter;
import com.example.teammanager.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSendMessage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatMessagesRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);

        firebaseDatabase = FirebaseDatabase.getInstance();
        chatMessagesRef = firebaseDatabase.getReference("chat_messages"); // Узел для хранения сообщений

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        String currentUserId = (currentUser != null) ? currentUser.getUid() : "Anonymous"; // Получаем ID текущего пользователя
        Log.d("ChatActivity", "Current User ID: " + currentUserId);
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);

        // Слушатель для получения сообщений из Firebase
        chatMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        message.setMessageId(messageSnapshot.getKey()); // Получаем ID сообщения
                        messageList.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerViewChat.scrollToPosition(messageList.size() - 1); // Прокрутка к последнему сообщению
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки при чтении данных
            }
        });

        // Слушатель для кнопки отправки сообщения
        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty() && currentUser != null) {
                    String senderId = currentUser.getUid();
                    String senderName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User"; // Получаем имя пользователя
                    long messageTime = System.currentTimeMillis();

                    ChatMessage message = new ChatMessage(messageText, senderId, senderName, messageTime);
                    chatMessagesRef.push().setValue(message); // Добавляем сообщение в Firebase
                    editTextMessage.setText(""); // Очищаем поле ввода
                }
            }
        });
    }
}