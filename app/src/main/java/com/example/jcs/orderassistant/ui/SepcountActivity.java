package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_sepcount);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_header);

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText("分别记账");

        listView = (ListView) findViewById(R.id.member_sep_lv);
        memberList = UiUtility.getMemberInfo();
        MemberWithMAdapter adapter = new MemberWithMAdapter(SepcountActivity.this,R.layout.select_member_withm_item,memberList);
        listView.setAdapter(adapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                EditText eText = (EditText) view.findViewById(R.id.sep_money);
                eText.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            }
        });

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


        final Button button_date = (Button) findViewById(R.id.SepDateButton);
        button_date.setText(SepcountActivity.year+"年"+(SepcountActivity.month+1)+"月"+SepcountActivity.day+"日");
        button_date.setOnClickListener(new View.OnClickListener()
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
                                button_date.setText(year+"年"+(monthOfYear+1)+"月"+dayOfMonth + "日");
                                SepcountActivity.year = year;
                                SepcountActivity.month = monthOfYear;
                                SepcountActivity.day = dayOfMonth;
                            }
                        }
                        ,calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void saveSepCount()
    {
        EditText cashback = (EditText) findViewById(R.id.sep_return);
        String c = cashback.getText().toString();

        EditText dinning = (EditText) findViewById(R.id.sep_dinning);
        String d = dinning.getText().toString();

        if (c.isEmpty()) c="0";
        if (!UiUtility.isInteger(c)){
            Toast.makeText(SepcountActivity.this,"金额格式不正确",Toast.LENGTH_SHORT).show();
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
        values.put(DatabaseSchema.OrderEntry.COLUMN_DATE, time);
        int m = Integer.parseInt(c);
        values.put(DatabaseSchema.OrderEntry.COLUMN_RETURN, m);
        values.put(DatabaseSchema.OrderEntry.COLUMN_DINING, d);
        values.put(DatabaseSchema.OrderEntry.COLUMN_TYPE, 1);
        long ordid = db.insert(DatabaseSchema.OrderEntry.TABLE_NAME, null, values);
        values.clear();

        //加子订单
        int count = 0;

        for (int i=0;i<memberList.size() ;i++) {
            if (memberList.get(i).getSelected() == true) {
                count++;
            }
        }

        float return_each = m/count;

        for (int i=0;i<memberList.size() ;i++)
        {
            if (memberList.get(i).getSelected() == true ){

                float sep = memberList.get(i).getEach() - return_each;

                //加子订单
                values.clear();
                values.put(DatabaseSchema.SubOrderEntry.COLUMN_ORDERID, ordid);
                values.put(DatabaseSchema.SubOrderEntry.COLUMN_SUM, sep);
                values.put(DatabaseSchema.SubOrderEntry.COLUMN_MEMBER, memberList.get(i).getId());
                db.insert(DatabaseSchema.SubOrderEntry.TABLE_NAME, null, values);
                //设置余额
                values.clear();
                values.put(DatabaseSchema.MemberEntry.COLUMN_MONEY, memberList.get(i).getSum()-sep);
                String idquery = ""+ DatabaseSchema.MemberEntry._ID + "= ?";
                String[] arry = new String[1];
                arry[0] = Integer.toString(memberList.get(i).getId());
                db.update(DatabaseSchema.MemberEntry.TABLE_NAME,values,idquery,arry);
            }else {
                ;
            }
        }
    }
}
