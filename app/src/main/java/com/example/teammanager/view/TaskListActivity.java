package com.example.teammanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar; // Импортируйте правильный класс Toolbar

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teammanager.R;
import com.example.teammanager.view.TaskAdapter;
import com.example.teammanager.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private List<Task> originalTaskList; // Копия исходного списка

    private FloatingActionButton fabAddTask;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tasksReference;
    private EditText editTextSearchTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String userRole = getIntent().getStringExtra("userRole");
        final boolean isAdmin = "admin".equals(userRole);

        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        if (isAdmin) {
            fabAddTask.setVisibility(View.VISIBLE);
            fabAddTask.setOnClickListener(v -> {
                Intent intent = new Intent(TaskListActivity.this, AddEditTaskActivity.class);
                startActivity(intent);
            });
        } else {
            fabAddTask.setVisibility(View.GONE);
        }

        editTextSearchTask = findViewById(R.id.editTextSearchTask);
        editTextSearchTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не используется
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не используется
            }
        });
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        // Инициализация адаптера с слушателем кликов
        taskAdapter = new TaskAdapter(taskList, task -> {
            // Обработка клика (редактирование)
            if (isAdmin) {
                Intent intent = new Intent(TaskListActivity.this, AddEditTaskActivity.class);
                intent.putExtra("taskId", task.getTaskId());
                intent.putExtra("task0Title", task.getTitle());
                intent.putExtra("taskDescription", task.getDescription());
                intent.putExtra("taskDeadline", task.getDeadline());
                intent.putExtra("taskStatus", task.getStatus());
                startActivity(intent);
            }
        }, task -> {
            // Обработка долгого нажатия (удаление)
            if (isAdmin) {
                deleteTask(task.getTaskId());
            }
        }, isAdmin);
        recyclerViewTasks.setAdapter(taskAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        tasksReference = firebaseDatabase.getReference("tasks");

        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        ImageView chatIconImageView = findViewById(R.id.imageViewChat);
        chatIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskListActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });


        tasksReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                originalTaskList = new ArrayList<>(taskList);
                taskAdapter.setTasks(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskListActivity.this, "Ошибка при загрузке задач: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void loadTasks() {
        // ...
    }

    private void filterTasks(String query) {
        List<Task> filteredList = new ArrayList<>();
        query = query.toLowerCase().trim(); // Приводим запрос к нижнему регистру и убираем пробелы

        if (query.isEmpty()) {
            // Если поисковый запрос пуст, показываем весь исходный список
            filteredList.addAll(originalTaskList); // Используем originalTaskList
        } else {
            for (Task task : originalTaskList) { // Итерируемся по originalTaskList
                if (task.getTitle().toLowerCase().contains(query)) {
                    filteredList.add(task);
                } else if (task.getDescription() != null && task.getDescription().toLowerCase().contains(query)) {
                    filteredList.add(task);
                }
            }
        }

        // Обновляем RecyclerView отфильтрованным списком
        taskAdapter.updateTaskList(filteredList);
    }

    private void deleteTask(String taskIdToDelete) {
        DatabaseReference taskToDeleteReference = tasksReference.child(taskIdToDelete);
        taskToDeleteReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TaskListActivity.this, "Задача успешно удалена!", Toast.LENGTH_SHORT).show();
                    // Firebase автоматически обновит данные, и ValueEventListener сработает,
                    // обновив taskList и RecyclerView.
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskListActivity.this, "Ошибка при удалении задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}