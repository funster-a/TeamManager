package com.example.teammanager.view;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teammanager.R;
import com.example.teammanager.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messageList;
    private String currentUserId; // ID текущего пользователя для определения отправителя

    public ChatAdapter(List<ChatMessage> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    public void addMessage(ChatMessage message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
        // Прокрутка RecyclerView к последнему сообщению
        // Если RecyclerView существует, вы можете вызвать smoothScrollToPosition
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemView, currentUserId);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSender;
        private TextView textViewMessage;
        private TextView textViewTime;
        private String currentUserId; // Локальное поле для ID текущего пользователя


        public MessageViewHolder(@NonNull View itemView, String currentUserId) {
            super(itemView);
            textViewSender = itemView.findViewById(R.id.textViewSender);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            this.currentUserId = currentUserId; // Инициализация локального поля
        }

        public void bind(ChatMessage message) {
            textViewMessage.setText(message.getMessageText());
            textViewSender.setText(message.getMessageSenderName());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTime = sdf.format(message.getMessageTime());
            textViewTime.setText(formattedTime);

            Log.d("MessageViewHolder", "Message Sender: " + message.getMessageSenderId() + ", Current User ID: " + currentUserId);

            if (message.getMessageSenderId().equals(currentUserId)) {
                // Свое сообщение
                textViewMessage.setBackgroundResource(R.drawable.my_message_background);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textViewMessage.getLayoutParams();
                params.gravity = Gravity.END;
                textViewMessage.setLayoutParams(params);
                LinearLayout.LayoutParams senderParams = (LinearLayout.LayoutParams) textViewSender.getLayoutParams();
                senderParams.gravity = Gravity.END;
                textViewSender.setLayoutParams(senderParams);
                LinearLayout.LayoutParams timeParams = (LinearLayout.LayoutParams) textViewTime.getLayoutParams();
                timeParams.gravity = Gravity.END;
                textViewTime.setLayoutParams(timeParams);
            } else {
                // Чужое сообщение
                textViewMessage.setBackgroundResource(R.drawable.other_message_background);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textViewMessage.getLayoutParams();
                params.gravity = Gravity.START;
                textViewMessage.setLayoutParams(params);
                LinearLayout.LayoutParams senderParams = (LinearLayout.LayoutParams) textViewSender.getLayoutParams();
                senderParams.gravity = Gravity.START;
                textViewSender.setLayoutParams(senderParams);
                LinearLayout.LayoutParams timeParams = (LinearLayout.LayoutParams) textViewTime.getLayoutParams();
                timeParams.gravity = Gravity.START;
                textViewTime.setLayoutParams(timeParams);
            }
        }
    }
}