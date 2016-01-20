/**
 * Created by JCS on 2015/9/30.
 */

package com.example.jcs.orderassistant;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.jcs.orderassistant.DatabaseSchema.MemberEntry;
import com.example.jcs.orderassistant.DatabaseSchema.OrderEntry;
import com.example.jcs.orderassistant.DatabaseSchema.SubOrderEntry;
import com.example.jcs.orderassistant.OrderApplication;

@SuppressWarnings("deprecation")
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "orderAssistant_db";

    private static final String Order_TABLE_CREATE = "create table " + OrderEntry.TABLE_NAME + " ("
            + OrderEntry._ID                      + " integer primary key autoincrement, "
            + OrderEntry.COLUMN_DATE                + " integer default 0, "
            + OrderEntry.COLUMN_return              + " integer default 0 "
            + ");";

    private static final String SubOrder_TABLE_CREATE = "create table " + SubOrderEntry.TABLE_NAME + " ("
            + SubOrderEntry._ID                      + " integer primary key autoincrement, "
            + SubOrderEntry.COLUMN_ORDERID          + " integer default 0, "
            + SubOrderEntry.COLUMN_SUM          + " float default 0, "
            + SubOrderEntry.COLUMN_MEMBER         + " integer default 0,"
            + "FOREIGN KEY (" 	+ SubOrderEntry.COLUMN_ORDERID + ") REFERENCES " + OrderEntry.TABLE_NAME + " (" + OrderEntry._ID + ") ON DELETE SET NULL "
            + ");";

    private static final String Member_TABLE_CREATE = "create table " + MemberEntry.TABLE_NAME + " ("
            + MemberEntry._ID                      + " integer primary key autoincrement, "
            + MemberEntry.COLUMN_NAME          + " varchar(255), "
            + MemberEntry.COLUMN_MONEY          + " float default 0 "
            + ");";

    /**
     * Constructor
     * @param context Application context
     */
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabaseTables(db);
       // insertDefaultMember(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createDatabaseTables(SQLiteDatabase db) {
        //Log.i(LOG_TAG, "Creating database tables");
        db.execSQL(Order_TABLE_CREATE);
        db.execSQL(SubOrder_TABLE_CREATE);
        db.execSQL(Member_TABLE_CREATE);
    }

    /*private void insertDefaultMember(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        OrderApplication app = OrderApplication.getInstance();
        String name = app.getString(R.string.member_han);
        values.put(MemberEntry.COLUMN_NAME,name);
        db.insert(MemberEntry.TABLE_NAME, null, values);
        values.clear();
        name = app.getString(R.string.member_guan);
        values.put(MemberEntry.COLUMN_NAME,name);
        db.insert(MemberEntry.TABLE_NAME, null, values);
        values.clear();
        name = app.getString(R.string.member_li);
        values.put(MemberEntry.COLUMN_NAME,name);
        db.insert(MemberEntry.TABLE_NAME, null, values);
        values.clear();
        name = app.getString(R.string.member_tian);
        values.put(MemberEntry.COLUMN_NAME,name);
        db.insert(MemberEntry.TABLE_NAME, null, values);
        values.clear();
        name = app.getString(R.string.member_zhu);
        values.put(MemberEntry.COLUMN_NAME,name);
        db.insert(MemberEntry.TABLE_NAME,null,values);
    }*/
}

