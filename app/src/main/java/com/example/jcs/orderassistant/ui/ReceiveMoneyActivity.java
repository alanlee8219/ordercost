package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.example.jcs.orderassistant.ui.UiUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class ReceiveMoneyActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfoWithId> memberList = new ArrayList<MemberInfoWithId>();

    private static int year;
    private static int month;
    private static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_receive_money);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_header);

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText("入账");

        listView = (ListView) findViewById(R.id.member_rm_lv);
        memberList = UiUtility.getMemberInfo();
        MemberWithMAdapter adapter = new MemberWithMAdapter(ReceiveMoneyActivity.this,R.layout.select_member_withm_item,memberList);
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

        Calendar calendar1 = Calendar.getInstance();
        ReceiveMoneyActivity.year = calendar1.get(Calendar.YEAR);
        ReceiveMoneyActivity.month = calendar1.get(Calendar.MONTH);
        ReceiveMoneyActivity.day = calendar1.get(Calendar.DAY_OF_MONTH);;


        final Button button_date = (Button) findViewById(R.id.RmDateButton);
        button_date.setText(ReceiveMoneyActivity.year + "年" + (ReceiveMoneyActivity.month + 1) + "月" + ReceiveMoneyActivity.day + "日");
        button_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(ReceiveMoneyActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                button_date.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
                                ReceiveMoneyActivity.year = year;
                                ReceiveMoneyActivity.month = monthOfYear;
                                ReceiveMoneyActivity.day = dayOfMonth;
                            }
                        }
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Button button = (Button) findViewById(R.id.RMSaveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.requestFocus();
                saveRMCount();
                finish();
            }
        });
    }

    private void saveRMCount()
    {
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i=0;i<memberList.size() ;i++)
        {
            if (memberList.get(i).getSelected() == true ){

                Float sep = memberList.get(i).getEach();
                if (sep.compareTo(0.0f) == 0) continue;

                values.clear();
                values.put(DatabaseSchema.MemberEntry.COLUMN_MONEY, memberList.get(i).getSum() + sep.floatValue());
                String idquery = "" + DatabaseSchema.MemberEntry._ID + "= ?";
                String[] arry = new String[1];
                arry[0] = Integer.toString(memberList.get(i).getId());
                db.update(DatabaseSchema.MemberEntry.TABLE_NAME, values, idquery, arry);

                values.clear();
                values.put(DatabaseSchema.AdvanceEntry.COLUMN_MEMEBER_ID, memberList.get(i).getId());
                values.put(DatabaseSchema.AdvanceEntry.COLUMN_MONEY, sep);

                long time = UiUtility.fortmatDateTime(year, month, day);

                values.put(DatabaseSchema.AdvanceEntry.COLUMN_DATE, time);
                db.insert(DatabaseSchema.AdvanceEntry.TABLE_NAME, null, values);

            } else {
                ;
            }
        }
    }

}
