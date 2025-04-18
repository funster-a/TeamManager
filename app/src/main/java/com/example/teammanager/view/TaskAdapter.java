package com.example.teammanager.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teammanager.R;
import com.example.teammanager.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
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
        return new TaskViewHolder(itemView, listener);
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

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewDeadline;
        private TextView textViewStatus;

        public TaskViewHolder(@NonNull View itemView, final OnTaskClickListener listener) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTaskTitle);
            textViewDescription = itemView.findViewById(R.id.textViewTaskDescription);
            textViewDeadline = itemView.findViewById(R.id.textViewTaskDeadline);
            textViewStatus = itemView.findViewById(R.id.textViewTaskStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTaskClick(((TaskAdapter) ((RecyclerView) itemView.getParent()).getAdapter()).taskList.get(position));
                }
            });
        }

        public void bind(Task task) {
            textViewTitle.setText(task.getTitle());
            textViewDescription.setText(task.getDescription());
            textViewDeadline.setText(task.getDeadline());
            textViewStatus.setText(task.getStatus());

            String status = task.getStatus();
            if (status != null) {
                switch (status) {
                    case "В процессе":
                        textViewStatus.setBackgroundColor(itemView.getContext().getColor(R.color.status_in_progress));
                        break;
                    case "Выполнено":
                        textViewStatus.setBackgroundColor(itemView.getContext().getColor(R.color.status_completed));
                        break;
                    case "К выполнению":
                        textViewStatus.setBackgroundColor(itemView.getContext().getColor(R.color.status_to_do));
                        break;
                    case "Заблокировано":
                        textViewStatus.setBackgroundColor(itemView.getContext().getColor(R.color.status_blocked));
                        break;
                    default:
                        textViewStatus.setBackgroundColor(itemView.getContext().getColor(android.R.color.transparent)); // По умолчанию прозрачный
                        break;
                }
                // Дополнительно можно настроить цвет текста, если необходимо
                // textViewStatus.setTextColor(Color.WHITE);
            } else {
                textViewStatus.setBackgroundColor(itemView.getContext().getColor(android.R.color.transparent));
            }
        }
    }
}