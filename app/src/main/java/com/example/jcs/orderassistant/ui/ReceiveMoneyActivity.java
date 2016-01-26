package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;

import java.util.ArrayList;
import java.util.List;


public class ReceiveMoneyActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfoWithId> memberList = new ArrayList<MemberInfoWithId>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_money);

        listView = (ListView) findViewById(R.id.member_rm_lv);
        getMemberInfo();
        MemberWithMAdapter adapter = new MemberWithMAdapter(ReceiveMoneyActivity.this,R.layout.select_member_withm_item,memberList);
        listView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.RMSaveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRMCount();
                finish();
            }
        });
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

    private void saveRMCount()
    {
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (int i=0;i<listView.getChildCount();i++) {
            View view = listView.getChildAt(i);
            CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.select_member_withm);
            if (ctv.isChecked()) {
                EditText editText = (EditText) view.findViewById(R.id.sep_money);
                String sep_money = editText.getText().toString();
                int sep = Integer.parseInt(sep_money);

                values.clear();
                values.put(DatabaseSchema.MemberEntry.COLUMN_MONEY, memberList.get(i).getSum() + sep);
                String idquery = "" + DatabaseSchema.MemberEntry._ID + "= ?";
                String[] arry = new String[1];
                arry[0] = Integer.toString(memberList.get(i).getId());
                db.update(DatabaseSchema.MemberEntry.TABLE_NAME, values, idquery, arry);

            } else {
                ;
            }
        }
    }

}
