package com.example.teammanager.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teammanager.R;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<Map<String, Object>> userList;
    private OnRoleChangeListener roleChangeListener;
    private String[] roles = {"user", "admin"};

    public interface OnRoleChangeListener {
        void onRoleChanged(String userId, String newRole);
    }

    public UserAdapter(List<Map<String, Object>> userList, OnRoleChangeListener listener) {
        this.userList = userList;
        this.roleChangeListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false); // Создайте макет item_user.xml
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Map<String, Object> user = userList.get(position);
        String userId = (String) user.get("uid");
        String name = (String) user.get("name");
        String email = (String) user.get("email");
        String currentRole = (String) user.get("role");

        holder.textViewName.setText(name);
        holder.textViewEmail.setText(email);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.spinnerRole.getContext(),
                android.R.layout.simple_spinner_dropdown_item, roles);
        holder.spinnerRole.setAdapter(adapter);

        // Устанавливаем текущую роль в Spinner (без вызова слушателя)
        if (currentRole != null) {
            int positionInSpinner = -1;
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].equals(currentRole)) {
                    positionInSpinner = i;
                    break;
                }
            }
            holder.spinnerRole.setSelection(positionInSpinner, false); // false чтобы не вызывался listener при установке
        }

        holder.spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newRole = roles[position];
                if (roleChangeListener != null && !newRole.equals(currentRole)) {
                    roleChangeListener.onRoleChanged(userId, newRole);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewEmail;
        public Spinner spinnerRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewUserName); // Создайте TextView с этим ID в item_user.xml
            textViewEmail = itemView.findViewById(R.id.textViewUserEmail); // Создайте TextView с этим ID в item_user.xml
            spinnerRole = itemView.findViewById(R.id.spinnerUserRole);   // Создайте Spinner с этим ID в item_user.xml
        }
    }
}