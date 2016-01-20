package com.example.jcs.orderassistant;

import android.util.Log;
import android.app.Application;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.example.jcs.orderassistant.DatabaseHelper;


/**
 * Created by JCS on 2015/10/8.
 */
public class OrderApplication  extends Application{

    private static OrderApplication orderApplication;

    private static DatabaseHelper mDbHelper;

    private static SQLiteDatabase mDb;

    public static OrderApplication getInstance(){
        return orderApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        orderApplication = this;
        mDbHelper = new DatabaseHelper(getApplicationContext());
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(getClass().getName(), "Error getting database: " + e.getMessage());
            mDb = mDbHelper.getReadableDatabase();
        }
    }

    public static DatabaseHelper getDbHelper(){
        return mDbHelper;
    }
}
