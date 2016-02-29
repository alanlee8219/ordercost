package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;
import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;

import java.util.ArrayList;
import java.util.List;

public class AdvanceRecordActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfo> memberList = new ArrayList<MemberInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_banlace);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_header);

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText("入账记录一览");

        listView = (ListView) findViewById(R.id.main_lv);
        getMemberInfo();
        MemberInfoAdapter adapter = new MemberInfoAdapter(AdvanceRecordActivity.this,R.layout.balance_item,memberList);
        listView.setAdapter(adapter);
    }

    private void getMemberInfo()
    {
        memberList.clear();
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "select * from " + DatabaseSchema.AdvanceEntry.TABLE_NAME
                + " order by " + DatabaseSchema.AdvanceEntry.COLUMN_DATE +" desc";

        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            String sub_query = "select " + DatabaseSchema.MemberEntry.COLUMN_NAME
                    +" from " + DatabaseSchema.MemberEntry.TABLE_NAME
                    + " where " + DatabaseSchema.MemberEntry._ID +" = "
                    + cursor.getInt(1);
            Cursor subcursor = db.rawQuery(sub_query,null);
            String memberName="";
            while (subcursor.moveToNext()) {
                memberName =subcursor.getString(0);
            }
            String date = UiUtility.GetDateInfo(cursor.getLong(2));
            date += " " + memberName;
            MemberInfo info = new MemberInfo(date,cursor.getInt(3));
            memberList.add(info);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_advance_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
