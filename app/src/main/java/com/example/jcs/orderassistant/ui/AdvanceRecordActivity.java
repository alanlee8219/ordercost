package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;
import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import de.timroes.android.listview.EnhancedListView;

public class AdvanceRecordActivity extends Activity {


    private EnhancedListView listView = null;
    private List<MemberInfo> memberList = new ArrayList<MemberInfo>();
    private List<Integer> recordIdList = new  ArrayList<Integer>();
    private List<Integer> delList = new  ArrayList<Integer>();
    private MemberInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_advance_record);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_header);

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText("入账记录一览");

        listView = (EnhancedListView) findViewById(R.id.advance_Lv);

        getMemberInfo();
        mAdapter = new MemberInfoAdapter(AdvanceRecordActivity.this,R.layout.balance_item,memberList);
        listView.setAdapter(mAdapter);

        // Set the callback that handles dismisses.
        listView.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
                Integer id = recordIdList.get(position);
                delList.add(id);
                final MemberInfo item = mAdapter.getItem(position);
                mAdapter.remove(position);

                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        delList.remove(delList.size()-1);
                        mAdapter.insert(position, item);
                    }

                    @Override public String getTitle() {
                        return "删除入账记录"; // Plz, use the resource system :)
                    }

                    @Override public void discard() {
                        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        String[] args = new String[delList.size()];
                        for (int i=0;i<delList.size();i++){
                            args[i] = Integer.toString(delList.get(i));
                        }
                        db.delete(DatabaseSchema.AdvanceEntry.TABLE_NAME,DatabaseSchema.AdvanceEntry._ID +" =?",
                                args);

                    }
                };

            }
        });

        listView.enableSwipeToDismiss();
        listView.setUndoStyle(EnhancedListView.UndoStyle.SINGLE_POPUP);
    }

    @Override
    protected void onStop() {
        if(listView != null) {
            listView.discardUndo();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(listView != null) {
            listView.discardUndo();
        }
        super.onPause();
    }

    private void getMemberInfo()
    {
        memberList.clear();
        recordIdList.clear();
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "select * from " + DatabaseSchema.AdvanceEntry.TABLE_NAME
                + " order by " + DatabaseSchema.AdvanceEntry.COLUMN_DATE +" desc";

        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            recordIdList.add(cursor.getInt(0));
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
            MemberInfo info = new MemberInfo(date,cursor.getFloat(3));
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
