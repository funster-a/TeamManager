package com.example.teammanager.model;

public class ChatMessage {
    private String messageText;
    private String messageSenderId; // Переименовываем для ясности
    private String messageSenderName; // Добавляем поле для имени
    private long messageTime;
    private String messageId;

    public ChatMessage() {
        // Обязательный пустой конструктор для Firebase
    }

    public ChatMessage(String messageText, String messageSenderId, String messageSenderName, long messageTime) {
        this.messageText = messageText;
        this.messageSenderId = messageSenderId;
        this.messageSenderName = messageSenderName;
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageSenderId() {
        return messageSenderId;
    }

    public void setMessageSenderId(String messageSenderId) {
        this.messageSenderId = messageSenderId;
    }

    public String getMessageSenderName() {
        return messageSenderName;
    }

    public void setMessageSenderName(String messageSenderName) {
        this.messageSenderName = messageSenderName;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}