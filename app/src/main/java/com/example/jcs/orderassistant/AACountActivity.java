package com.example.jcs.orderassistant;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.example.jcs.orderassistant.DatabaseSchema.OrderEntry;
import com.example.jcs.orderassistant.DatabaseSchema.SubOrderEntry;
import com.example.jcs.orderassistant.DatabaseSchema.MemberEntry;

public class AACountActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfoWithId> memberList = new ArrayList<MemberInfoWithId>();
    private static int year;
    private static int month;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aacount);

        listView = (ListView) findViewById(R.id.member_lv);
        getMemberInfo();
        MemberAdapter adapter = new MemberAdapter(AACountActivity.this,R.layout.select_member_item,memberList);
        listView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.AASaveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAACount();
                finish();
            }
        });

        Calendar calendar1 = Calendar.getInstance();
        AACountActivity.year = calendar1.get(Calendar.YEAR);
        AACountActivity.month = calendar1.get(Calendar.MONTH);
        AACountActivity.day = calendar1.get(Calendar.DAY_OF_MONTH);;


        final Button button_date = (Button) findViewById(R.id.AADateButton);
        button_date.setText(Calendar.YEAR+"年"+Calendar.MONTH+"月"+Calendar.DAY_OF_MONTH+"日");
        button_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(AACountActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view,int year, int monthOfYear, int dayOfMonth)
                            {
                                button_date.setText(year+"年"+monthOfYear+"月"+dayOfMonth + "日");
                                AACountActivity.year = year;
                                AACountActivity.month = monthOfYear;
                                AACountActivity.day = dayOfMonth;
                            }
                        }
                        ,calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)).show();
                saveAACount();
                finish();
            }
        });
    }


    private void saveAACount()
    {
        EditText sum = (EditText) findViewById(R.id.aa_sum);
        String s = sum.getText().toString();

        EditText cashback = (EditText) findViewById(R.id.aa_return);
        String c = cashback.getText().toString();

        if (s.isEmpty() || c.isEmpty()) {
            Toast.makeText(AACountActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((!isInteger(s)) || !isInteger(c)){
            Toast.makeText(AACountActivity.this,"金额格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }

        //加订单
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long time = fortmatDateTime();
        values.put(OrderEntry.COLUMN_DATE, time);
        int m = Integer.parseInt(c);
        values.put(OrderEntry.COLUMN_return, m);
        long ordid = db.insert(OrderEntry.TABLE_NAME, null, values);
        values.clear();

        /*String query = "select last last_insert_rowid() from "+OrderEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        int ordid = 0;
        if (cursor.moveToFirst()) ordid = cursor.getInt(0);*/

        //加子订单

        int sum_money = Integer.parseInt(s);

        int count = 0;
        for (int i=0;i<listView.getChildCount();i++)
        {
            View view = listView.getChildAt(i);
            CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.select_member);
            if (ctv.isChecked()) {
                count++;
            }
        }
        float each = (float)sum_money/count;
        float return_each = (float)m/count;

        for (int i=0;i<listView.getChildCount();i++)
        {
            View view = listView.getChildAt(i);
            CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.select_member);
            if (ctv.isChecked()) {
                //加子订单
                values.clear();
                values.put(SubOrderEntry.COLUMN_ORDERID, ordid);
                values.put(SubOrderEntry.COLUMN_SUM, each);
                values.put(SubOrderEntry.COLUMN_MEMBER, memberList.get(i).getId());
                db.insert(SubOrderEntry.TABLE_NAME, null, values);
                //设置余额
                values.clear();
                values.put(MemberEntry.COLUMN_MONEY, memberList.get(i).getSum()-each-return_each);
                String idquery = ""+ MemberEntry._ID + "= ?";
                String[] arry = new String[1];
                arry[0] = Integer.toString(memberList.get(i).getId());
                db.update(MemberEntry.TABLE_NAME,values,idquery,arry);
            }else {
                ;
            }
        }
    }

    private static long fortmatDateTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR,AACountActivity.year);
        cal.set(Calendar.MONTH,AACountActivity.month);
        cal.set(Calendar.DAY_OF_MONTH,AACountActivity.day);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
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
