package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;
import com.example.jcs.orderassistant.db.DatabaseSchema;
import com.example.jcs.orderassistant.db.DatabaseSchema.MemberEntry;
import com.example.jcs.orderassistant.ui.UiUtility;


import java.util.Calendar;
import java.util.regex.Pattern;


public class AddMemberActivity extends Activity {

    private static int year;
    private static int month;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_add_member);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_header);

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText("增加成员");

        Button button = (Button) findViewById(R.id.addM_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText1 = (EditText) findViewById(R.id.addM_name);
                String name = editText1.getText().toString();
                EditText editText2 = (EditText) findViewById(R.id.addM_initBalance);
                String money = editText2.getText().toString();
                addMember(name, money);
                finish();
            }
        });

        Button buttonDel = (Button) findViewById(R.id.addM_del);
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText1 = (EditText) findViewById(R.id.addM_name);
                String name = editText1.getText().toString();
                delMember(name);
                finish();
            }
        });

    }

    private void addMember(String name,String money){
        if (name.isEmpty()) {
            Toast.makeText(AddMemberActivity.this,"请输入姓名",Toast.LENGTH_SHORT).show();
            return;
        }

        if (money.isEmpty()) {
            money = "0";
        }else{
            if (!UiUtility.isInteger(money)){
                Toast.makeText(AddMemberActivity.this,"金额格式不正确",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MemberEntry.COLUMN_NAME,name);
        int m = Integer.parseInt(money);
        values.put(MemberEntry.COLUMN_MONEY, m);
        long memberID = db.insert(MemberEntry.TABLE_NAME, null, values);

        //Todo 这里需要先查询数据库是否已经有这个名字

        Calendar calendar1 = Calendar.getInstance();
        AddMemberActivity.year = calendar1.get(Calendar.YEAR);
        AddMemberActivity.month = calendar1.get(Calendar.MONTH);
        AddMemberActivity.day = calendar1.get(Calendar.DAY_OF_MONTH);;

        values.clear();
        values.put(DatabaseSchema.AdvanceEntry.COLUMN_MEMEBER_ID, memberID);
        values.put(DatabaseSchema.AdvanceEntry.COLUMN_MONEY, m);

        long time = UiUtility.fortmatDateTime(year, month, day);

        values.put(DatabaseSchema.AdvanceEntry.COLUMN_DATE, time);
        db.insert(DatabaseSchema.AdvanceEntry.TABLE_NAME, null, values);

    }

    private void delMember(String name){
        if (name.isEmpty()) {
            Toast.makeText(AddMemberActivity.this,"请输入姓名",Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //String query = "select " +  MemberEntry._ID + " from " + DatabaseSchema.MemberEntry.TABLE_NAME
        //        +" where " + MemberEntry.COLUMN_NAME + " = " + name;
        String query = "select " +  "*"  + " from " + DatabaseSchema.MemberEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        int memberID=-1;
        String searchName = "";
        boolean findFlg = false;
        while (cursor.moveToNext()){
            memberID = cursor.getInt(0);
            searchName = cursor.getString(1);
            if(searchName.equals(name)) {
                findFlg = true;
                break;
            }
        }

        if (findFlg == false) return;

        ContentValues values = new ContentValues();
        values.put(MemberEntry.COLUMN_DEL,1);
        String idquery = ""+ MemberEntry._ID + "= ?";
        String[] arry = new String[1];
        arry[0] = Integer.toString(memberID);
        db.update(MemberEntry.TABLE_NAME,values,idquery,arry);
    }
}
