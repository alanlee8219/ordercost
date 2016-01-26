package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema.MemberEntry;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;

public class MainActivity extends Activity {

    private ListView listView = null;
    private List<MemberInfo> memberList = new ArrayList<MemberInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Button aaButton = (Button) findViewById(R.id.AAButton);
        aaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AACountActivity.class);
                startActivity(intent);
            }
        });

        Button sepButton = (Button) findViewById(R.id.IndButton);
        sepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SepcountActivity.class);
                startActivity(intent);
            }
        });

        Button addMButton = (Button) findViewById(R.id.AddMButton);
        addMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddMemberActivity.class);
                startActivity(intent);
            }
        });

        Button rmButton = (Button) findViewById(R.id.RMButton);
        rmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiveMoneyActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.main_lv);
        getMemberInfo();
        MemberInfoAdapter adapter = new MemberInfoAdapter(MainActivity.this,R.layout.balance_item,memberList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMemberInfo();
        MemberInfoAdapter adapter = new MemberInfoAdapter(MainActivity.this,R.layout.balance_item,memberList);
        listView.setAdapter(adapter);
    }

    private void getMemberInfo()
    {
        memberList.clear();
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "select * from " + MemberEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            MemberInfo info = new MemberInfo(cursor.getString(1),cursor.getInt(2));
            memberList.add(info);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
