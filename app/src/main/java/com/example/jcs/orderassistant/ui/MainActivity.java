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

import com.example.jcs.orderassistant.ui.PullableListViewActivity;

import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends  Activity {//TitleActivity

    private ListView listView = null;
    private List<MemberInfo> memberList = new ArrayList<MemberInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        TextView titleTextView = (TextView) findViewById(R.id.text_title);

        Button forwardButton = (Button) findViewById(R.id.button_forward);
        forwardButton.setVisibility(View.VISIBLE);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(intent);
            }
        });

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

        Button rmButton = (Button) findViewById(R.id.RMButton);
        rmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiveMoneyActivity.class);
                startActivity(intent);
            }
        });

        Button listOfAccountsButton = (Button) findViewById(R.id.ListOfAccounts);
        listOfAccountsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BanlaceActivity.class);
                startActivity(intent);
            }
        });

       // listView = (ListView) findViewById(R.id.main_lv);
        listView = (ListView) findViewById(R.id.content_view);
        getMemberInfo();
        MemberInfoAdapter adapter = new MemberInfoAdapter(MainActivity.this,R.layout.balance_item,memberList);
        listView.setAdapter(adapter);

        ((PullToRefreshLayout) findViewById(R.id.refresh_view))
                .setOnRefreshListener(new MyListener());
       // listView = (ListView) findViewById(R.id.content_view);
        initListView();
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
            memberList.add(info);//?
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * ListView初始化方法
     */
    private void initListView()
    {
        List<String> items = new ArrayList<String>();
        items.add("可下拉刷新上拉加载的ListView");
        items.add("可下拉刷新上拉加载的GridView");
        items.add("可下拉刷新上拉加载的ExpandableListView");
        items.add("可下拉刷新上拉加载的SrcollView");
        items.add("可下拉刷新上拉加载的WebView");
        items.add("可下拉刷新上拉加载的ImageView");
        items.add("可下拉刷新上拉加载的TextView");
        MyAdapter adapter = new MyAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id)
            {
                Toast.makeText(
                        MainActivity.this,
                        " LongClick on "
                                + parent.getAdapter().getItemId(position),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

              /*  Intent it = new Intent();
                switch (position)
                {
                    case 0:
                        it.setClass(MainActivity.this, PullableListViewActivity.class);
                        break;
                    case 1:
                       // it.setClass(MainActivity.this, PullableGridViewActivity.class);
                        break;
                    case 2:
                       // it.setClass(MainActivity.this, PullableExpandableListViewActivity.class);
                        break;
                    case 3:
                        //it.setClass(MainActivity.this, PullableScrollViewActivity.class);
                        break;
                    case 4:
                       // it.setClass(MainActivity.this, PullableWebViewActivity.class);
                        break;
                    case 5:
                       // it.setClass(MainActivity.this, PullableImageViewActivity.class);
                        break;
                    case 6:
                       // it.setClass(MainActivity.this, PullableTextViewActivity.class);
                        break;

                    default:
                        break;
                }
               // it.setClass(MainActivity.this, PullableListViewActivity.class);
                startActivity(it);*/
            }
        });
    }
}
