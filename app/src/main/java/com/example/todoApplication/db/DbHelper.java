package com.example.todoApplication.db;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoApplication.model.User;
import com.example.todoApplication.model.Task;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "todoApp";

    private static final String TABLE_USER = "users";
    private static final String USER_ID = "id";
    private static final String USER_NAME = "name";

    private static final String TASK_USER_ID = "user_id";

    private static final String TABLE_TASK = "tasks";
    private static final String TASK_ID = "id";
    private static final String TASK_NAME = "name";


    private int currentUser;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + USER_NAME + " TEXT UNIQUE NOT NULL);";
        db.execSQL(CREATE_USERS_TABLE);
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASK + "("
                + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TASK_NAME + " TEXT NOT NULL,"
                + TASK_USER_ID + " INTEGER NOT NULL);";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select * from " + TABLE_USER + " where "+ USER_NAME + " = '" + user.getUserName() + "'";
        Cursor checkUserCursor = db.rawQuery(selectQuery, null);
        if(checkUserCursor.getCount()<1){
            ContentValues values = new ContentValues();
            values.put(USER_NAME, user.getUserName());
            db.insert(TABLE_USER, null, values);
        }
        checkUserCursor.close();
        Cursor setUserCursor = db.rawQuery(selectQuery, null);
        if(setUserCursor.getCount()>0){
            setUserCursor.moveToFirst();
            currentUser = setUserCursor.getInt(0);
        }
        setUserCursor.close();
    }

    public void createTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select * from " + TABLE_TASK + " where "+ TASK_USER_ID + " = '" + currentUser + "' and "+ TASK_NAME + " = '" + task.getTaskName() + "'";
        Cursor setTaskCursor = db.rawQuery(selectQuery, null);
        if(setTaskCursor.getCount()<1){
            ContentValues values = new ContentValues();
            values.put(TASK_NAME, task.getTaskName());
            values.put(TASK_USER_ID, currentUser);
            db.insert(TABLE_TASK, null, values);
        }
        setTaskCursor.close();
    }

    public ArrayList<String> getTasks() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "select * from " + TABLE_TASK + " where "+ TASK_USER_ID + " = '" + currentUser + "'";
        Cursor checkTaskCursor = db.rawQuery(selectQuery, null);
        while (checkTaskCursor.moveToNext()) {
            taskList.add(checkTaskCursor.getString(1));
        }
        checkTaskCursor.close();
        return taskList;
    }

    public void deleteSelectedTask(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK, TASK_NAME + " = ? AND " + TASK_USER_ID + "= ?", new String[] {task, String.valueOf(currentUser)});

    }

    public void userLogOut(){
        currentUser = 0;
    }
}

