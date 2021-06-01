package com.example.todoApplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.todoApplication.db.DbHelper;
import com.example.todoApplication.model.Task;
import com.example.todoApplication.model.User;
import com.example.todoApplication.adapter.ToDoAdapter;

public class MainActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(MainActivity.this);
        recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loginUser();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setPositiveButton("Yes", (dialogue, which) -> {
                            dbHelper.userLogOut();
                            updateUi();
                            loginUser();
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void loginUser(){
        final EditText userEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Login")
                .setMessage("User Name")
                .setView(userEditText)
                .setPositiveButton("Submit", (dialogue, which) -> {
                    String user = String.valueOf(userEditText.getText());
                    dbHelper.addUser(new User(user));
                    updateUi();
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    public void addTask(View view) {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Task")
                .setMessage("Add a new task")
                .setView(taskEditText)
                .setPositiveButton("Add", (dialogue, which) -> {
                    String task = String.valueOf(taskEditText.getText());
                    dbHelper.createTask(new Task(task));
                    updateUi();
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void updateUi(){
        ToDoAdapter adapter = new ToDoAdapter(dbHelper.getTasks());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.TextView_task);
        String task = String.valueOf(taskTextView.getText());
        dbHelper.deleteSelectedTask(task);
        updateUi();
    }
}