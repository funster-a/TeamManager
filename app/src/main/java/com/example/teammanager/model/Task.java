package com.example.teammanager.model;

public class Task {
    private String taskId; // Уникальный идентификатор задачи (может быть сгенерирован Firebase)
    private String title;
    private String description;
    private String deadline;
    private String status; // Например: "В процессе", "Выполнено", "К выполнению"
    private String assignedTo; // ID пользователя, которому назначена задача

    // Конструкторы
    public Task() {
        // Обязательный пустой конструктор для Firebase
    }

    public Task(String taskId, String title, String description, String deadline, String status, String assignedTo) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    // Геттеры и сеттеры
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}