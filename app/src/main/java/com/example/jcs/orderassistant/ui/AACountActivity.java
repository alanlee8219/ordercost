package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;
import com.example.jcs.orderassistant.db.DatabaseSchema.OrderEntry;
import com.example.jcs.orderassistant.db.DatabaseSchema.SubOrderEntry;
import com.example.jcs.orderassistant.db.DatabaseSchema.MemberEntry;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;
import com.example.jcs.orderassistant.ui.UiUtility;

public class AACountActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfoWithId> memberList = new ArrayList<MemberInfoWithId>();
    private static int year;
    private static int month;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_aacount);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_header);
        //setTitle("AA记账");

        listView = (ListView) findViewById(R.id.member_lv);
        memberList = UiUtility.getMemberInfo();
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

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText("AA记账");


        /*Button return_button = (Button) findViewById(R.id.header_return);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        Calendar calendar1 = Calendar.getInstance();
        AACountActivity.year = calendar1.get(Calendar.YEAR);
        AACountActivity.month = calendar1.get(Calendar.MONTH);
        AACountActivity.day = calendar1.get(Calendar.DAY_OF_MONTH);;


        final Button button_date = (Button) findViewById(R.id.AADateButton);
        button_date.setText(AACountActivity.year+"年"+(AACountActivity.month+1)+"月"+AACountActivity.day+"日");
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
                                button_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth + "日");
                                AACountActivity.year = year;
                                AACountActivity.month = monthOfYear;
                                AACountActivity.day = dayOfMonth;
                            }
                        }
                        ,calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    private void saveAACount()
    {
        EditText sum = (EditText) findViewById(R.id.aa_sum);
        String s = sum.getText().toString();
        EditText dinning = (EditText) findViewById(R.id.dinning);
        String d = dinning.getText().toString();

        EditText cashback = (EditText) findViewById(R.id.aa_return);
        String c = cashback.getText().toString();

        if (s.isEmpty()) {
            Toast.makeText(AACountActivity.this, "请输入总金额", Toast.LENGTH_SHORT).show();
            return;
        }

        if (c.isEmpty()) {
            c = "0";
        }

        if ((!UiUtility.isInteger(s)) || !UiUtility.isInteger(c)){
            Toast.makeText(AACountActivity.this,"金额格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }

        if (d.isEmpty()) {
            d = "未知商家";
        }

        //加订单
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long time = UiUtility.fortmatDateTime(year,month,day);
        values.put(OrderEntry.COLUMN_DATE, time);
        int m = Integer.parseInt(c);
        values.put(OrderEntry.COLUMN_RETURN, m);
        values.put(OrderEntry.COLUMN_DINING, d);
        values.put(OrderEntry.COLUMN_TYPE, 0);
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
                values.put(MemberEntry.COLUMN_MONEY, memberList.get(i).getSum()-each+return_each);
                String idquery = ""+ MemberEntry._ID + "= ?";
                String[] arry = new String[1];
                arry[0] = Integer.toString(memberList.get(i).getId());
                db.update(MemberEntry.TABLE_NAME,values,idquery,arry);
            }else {
                ;
            }
        }
    }
}
