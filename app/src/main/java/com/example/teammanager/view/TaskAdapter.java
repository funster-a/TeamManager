package com.example.teammanager.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teammanager.R;
import com.example.teammanager.model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private final boolean isAdmin;
    private final OnTaskClickListener clickListener;
    private final OnTaskLongClickListener longClickListener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener clickListener, OnTaskLongClickListener longClickListener, boolean isAdmin) {
        this.taskList = taskList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.isAdmin = isAdmin;
    }

    public void setTasks(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView, clickListener, longClickListener, isAdmin); // Передаем isAdmin в ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.bind(currentTask);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTaskList(List<Task> newTaskList) {
        taskList.clear();
        taskList.addAll(newTaskList);
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewDeadline;
        private TextView textViewStatus;
        private androidx.appcompat.widget.AppCompatCheckBox checkBoxCompleted;
        private final boolean isAdmin;

        public TaskViewHolder(@NonNull View itemView, final OnTaskClickListener clickListener, final OnTaskLongClickListener longClickListener, boolean isAdmin) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTaskTitle);
            textViewDescription = itemView.findViewById(R.id.textViewTaskDescription);
            textViewDeadline = itemView.findViewById(R.id.textViewTaskDeadline);
            textViewStatus = itemView.findViewById(R.id.textViewTaskStatus);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            this.isAdmin = isAdmin;

            itemView.setOnClickListener(v -> {
                if (isAdmin) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onTaskClick(((TaskAdapter) ((RecyclerView) itemView.getParent()).getAdapter()).taskList.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (isAdmin) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                        longClickListener.onTaskLongClick(((TaskAdapter) ((RecyclerView) itemView.getParent()).getAdapter()).taskList.get(position));
                        return true; // Indicate that the long click was handled
                    }
                }
                return false;
            });

            checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                Log.w("TaskAdapter", "Position: " + position);
                View parent = (View) itemView.getParent();
                Log.d("TaskAdapter", "Parent: " + parent);
                RecyclerView recyclerView = (RecyclerView) parent;
                Log.d("TaskAdapter", "RecyclerView: " + recyclerView);
                RecyclerView.Adapter adapter = recyclerView != null ? recyclerView.getAdapter() : null;
                Log.d("TaskAdapter", "Adapter: " + adapter);
                if (adapter instanceof TaskAdapter) {
                    TaskAdapter taskAdapter = (TaskAdapter) adapter;
                    if (position < taskAdapter.taskList.size()) {
                        Task task = taskAdapter.taskList.get(position);
                        Log.d("TaskAdapter", "Task: " + task);
                        if (task != null) {
                            task.setStatus(isChecked ? "Выполнено" : "Отложено");
                            DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(task.getTaskId()).child("status");
                            taskRef.setValue(task.getStatus());
                        }
                    }
                }
            });
        }


        public void bind(Task task) {
            textViewTitle.setText(task.getTitle());
            textViewDescription.setText(task.getDescription());
            textViewDeadline.setText(task.getDeadline());
            textViewStatus.setText(task.getStatus());
            checkBoxCompleted.setChecked(task.getStatus() != null && task.getStatus().equals("Выполнено"));

            String status = task.getStatus();
            if (status != null) {
                // ... (логика установки цвета фона статуса)
            } else {
                textViewStatus.setBackgroundColor(itemView.getContext().getColor(android.R.color.transparent));
            }
        }
    }
}