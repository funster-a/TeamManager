package com.example.teammanager.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teammanager.R;
import com.example.teammanager.model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText editTextTaskTitle;
    private EditText editTextTaskDescription;
    private EditText editTextTaskDeadline;
    private Spinner spinnerTaskStatus;
    private Button buttonSaveTask;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tasksReference;
    private String taskIdToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        editTextTaskDeadline = findViewById(R.id.editTextTaskDeadline);
        spinnerTaskStatus = findViewById(R.id.spinnerTaskStatus);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);

        firebaseDatabase = FirebaseDatabase.getInstance();
        tasksReference = firebaseDatabase.getReference("tasks");

        // Настройка Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.task_statuses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskStatus.setAdapter(adapter);

        // Проверка, были ли переданы данные для редактирования
        if (getIntent().hasExtra("taskId")) {
            taskIdToEdit = getIntent().getStringExtra("taskId");
            String title = getIntent().getStringExtra("taskTitle");
            String description = getIntent().getStringExtra("taskDescription");
            String deadline = getIntent().getStringExtra("taskDeadline");
            String status = getIntent().getStringExtra("taskStatus");

            editTextTaskTitle.setText(title);
            editTextTaskDescription.setText(description);
            editTextTaskDeadline.setText(deadline);
            // Находим позицию статуса в Spinner
            if (status != null) {
                int position = adapter.getPosition(status);
                spinnerTaskStatus.setSelection(position);
            }
            buttonSaveTask.setText("Обновить задачу");
        } else {
            buttonSaveTask.setText("Сохранить задачу");
        }

        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        String title = editTextTaskTitle.getText().toString().trim();
        String description = editTextTaskDescription.getText().toString().trim();
        String deadline = editTextTaskDeadline.getText().toString().trim();
        String status = spinnerTaskStatus.getSelectedItem().toString();
        String assignedTo = null; // Временно присваиваем null

        if (title.isEmpty()) {
            editTextTaskTitle.setError("Заголовок обязателен");
            editTextTaskTitle.requestFocus();
            return;
        }

        if (taskIdToEdit != null) {
            // Режим редактирования
            Task updatedTask = new Task(taskIdToEdit, title, description, deadline, status, assignedTo);
            tasksReference.child(taskIdToEdit).setValue(updatedTask)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Задача успешно обновлена!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка при обновлении задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            // Режим добавления
            String taskId = tasksReference.push().getKey();
            Task newTask = new Task(taskId, title, description, deadline, status, assignedTo);
            tasksReference.child(taskId).setValue(newTask)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Задача успешно сохранена!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Ошибка при сохранении задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}