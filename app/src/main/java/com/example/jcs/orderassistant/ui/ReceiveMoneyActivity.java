package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;
import com.example.jcs.orderassistant.ui.UiUtility;

import java.util.ArrayList;
import java.util.List;


public class ReceiveMoneyActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfoWithId> memberList = new ArrayList<MemberInfoWithId>();

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

        Button button = (Button) findViewById(R.id.RMSaveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        MemberWithMAdapter adapter = (MemberWithMAdapter)listView.getAdapter();

        for (int i=0;i<adapter.isSelected.size() ;i++)
        {
            if (adapter.isSelected.get(i) == true ){
                View view = listView.getChildAt(i);
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
