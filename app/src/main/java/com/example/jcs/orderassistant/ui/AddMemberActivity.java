package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;
import com.example.jcs.orderassistant.db.DatabaseSchema.MemberEntry;



import java.util.regex.Pattern;


public class AddMemberActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Button button = (Button) findViewById(R.id.addM_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText1 = (EditText) findViewById(R.id.addM_name);
                String name = editText1.getText().toString();
                EditText editText2 = (EditText) findViewById(R.id.addM_initBalance);
                String money = editText2.getText().toString();
                addMember(name,money);
                finish();
            }
        });

    }

    private void addMember(String name,String money){
        if (name.isEmpty()) {
            Toast.makeText(AddMemberActivity.this,"请输入姓名",Toast.LENGTH_SHORT).show();
            return;
        }
        if (money.isEmpty()) {
            Toast.makeText(AddMemberActivity.this,"请输入金额",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isInteger(money)){
            Toast.makeText(AddMemberActivity.this,"金额格式不正确",Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MemberEntry.COLUMN_NAME,name);
        int m = Integer.parseInt(money);
        values.put(MemberEntry.COLUMN_MONEY,m);
        db.insert(MemberEntry.TABLE_NAME, null, values);
        //Todo 这里需要先查询数据库是否已经有这个名字
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_member, menu);
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
    */
}
