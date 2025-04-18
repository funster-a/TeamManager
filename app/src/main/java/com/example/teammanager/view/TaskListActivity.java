package com.example.teammanager.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
    private FloatingActionButton fabAddTask;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference tasksReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        // Инициализация адаптера с слушателем кликов
        taskAdapter = new TaskAdapter(taskList, task -> {
            Intent intent = new Intent(TaskListActivity.this, AddEditTaskActivity.class);
            intent.putExtra("taskId", task.getTaskId());
            intent.putExtra("taskTitle", task.getTitle());
            intent.putExtra("taskDescription", task.getDescription());
            intent.putExtra("taskDeadline", task.getDeadline());
            intent.putExtra("taskStatus", task.getStatus());
            startActivity(intent);
        });
        recyclerViewTasks.setAdapter(taskAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        tasksReference = firebaseDatabase.getReference("tasks");

        fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(TaskListActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
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
                taskAdapter.setTasks(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskListActivity.this, "Ошибка при загрузке задач: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}