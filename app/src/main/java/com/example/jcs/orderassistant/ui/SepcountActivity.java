package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import com.example.jcs.orderassistant.ui.UiUtility;


public class SepcountActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfoWithId> memberList = new ArrayList<MemberInfoWithId>();
    private static int year;
    private static int month;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sepcount);
        listView = (ListView) findViewById(R.id.member_sep_lv);
        getMemberInfo();
        MemberWithMAdapter adapter = new MemberWithMAdapter(SepcountActivity.this,R.layout.select_member_withm_item,memberList);
        listView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.SepSaveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSepCount();
                finish();
            }
        });

        Calendar calendar1 = Calendar.getInstance();
        SepcountActivity.year = calendar1.get(Calendar.YEAR);
        SepcountActivity.month = calendar1.get(Calendar.MONTH);
        SepcountActivity.day = calendar1.get(Calendar.DAY_OF_MONTH);;


        final Button button_date1 = (Button) findViewById(R.id.SepDateButton);
        button_date1.setText(Calendar.YEAR+"年"+Calendar.MONTH+"月"+Calendar.DAY_OF_MONTH+"日");
        button_date1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(SepcountActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view,int year, int monthOfYear, int dayOfMonth)
                            {
                                button_date1.setText(year+"年"+monthOfYear+"月"+dayOfMonth + "日");
                                SepcountActivity.year = year;
                                SepcountActivity.month = monthOfYear;
                                SepcountActivity.day = dayOfMonth;
                            }
                        }
                        ,calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)).show();
                saveSepCount();
                finish();
            }
        });
    }

    private void saveSepCount()
    {
        EditText cashback = (EditText) findViewById(R.id.sep_return);
        String c = cashback.getText().toString();

        if (!UiUtility.isInteger(c)){
            Toast.makeText(SepcountActivity.this,"金额格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }

        //加订单
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long time = UiUtility.fortmatDateTime(year,month,day);
        values.put(DatabaseSchema.OrderEntry.COLUMN_DATE, time);
        int m = Integer.parseInt(c);
        values.put(DatabaseSchema.OrderEntry.COLUMN_RETURN, m);
        long ordid = db.insert(DatabaseSchema.OrderEntry.TABLE_NAME, null, values);
        values.clear();

        /*String query = "select last last_insert_rowid() from "+OrderEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        int ordid = 0;
        if (cursor.moveToFirst()) ordid = cursor.getInt(0);*/

        //加子订单

        int count = 0;
        for (int i=0;i<listView.getChildCount();i++)
        {
            View view = listView.getChildAt(i);
            CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.select_member_withm);
            if (ctv.isChecked()) {
                count++;
            }
        }
        float return_each = m/count;

        for (int i=0;i<listView.getChildCount();i++)
        {
            View view = listView.getChildAt(i);
            CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.select_member_withm);
            if (ctv.isChecked()) {
                EditText editText = (EditText) view.findViewById(R.id.sep_money);
                String sep_money = editText.getText().toString();
                int sep = Integer.parseInt(sep_money);

                //加子订单
                values.clear();
                values.put(DatabaseSchema.SubOrderEntry.COLUMN_ORDERID, ordid);
                values.put(DatabaseSchema.SubOrderEntry.COLUMN_SUM, sep);
                values.put(DatabaseSchema.SubOrderEntry.COLUMN_MEMBER, memberList.get(i).getId());
                db.insert(DatabaseSchema.SubOrderEntry.TABLE_NAME, null, values);
                //设置余额
                values.clear();
                values.put(DatabaseSchema.MemberEntry.COLUMN_MONEY, memberList.get(i).getSum()-sep-return_each);
                String idquery = ""+ DatabaseSchema.MemberEntry._ID + "= ?";
                String[] arry = new String[1];
                arry[0] = Integer.toString(memberList.get(i).getId());
                db.update(DatabaseSchema.MemberEntry.TABLE_NAME,values,idquery,arry);
            }else {
                ;
            }
        }
    }

    private void getMemberInfo()
    {
        memberList.clear();
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "select * from " + DatabaseSchema.MemberEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            MemberInfoWithId info = new MemberInfoWithId(cursor.getInt(0),cursor.getString(1),cursor.getFloat(2));
            memberList.add(info);
        }
    }
}
