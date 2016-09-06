package com.example.jcs.orderassistant.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jcs.orderassistant.app.OrderApplication;
import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by JCS on 2016/1/25.
 * UI用工具类，有点乱
 */
public class UiUtility {

    public static long fortmatDateTime(int year,int month,int day){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH,day);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    public static String GetDateInfo(long millis){
        String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

        Date date=new Date(millis);
        java.text.SimpleDateFormat format=new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dateinfo = format.format(date);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        dateinfo += " " + weeks[week_index];
        return dateinfo;
    }

    public static float getMoneyfloat(float money)
    {
        BigDecimal dec = new BigDecimal(money);
        dec = dec.setScale(2,BigDecimal.ROUND_HALF_UP);
        return dec.floatValue();
    }

    public static String getMoneyStr(float money)
    {
        BigDecimal dec = new BigDecimal((double)money);
        dec = dec.setScale(2,BigDecimal.ROUND_HALF_UP);
        return dec.toString();
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static List<MemberInfoWithId> getMemberInfo()
    {
        List<MemberInfoWithId> memberList = new ArrayList<MemberInfoWithId>();
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "select * from " + DatabaseSchema.MemberEntry.TABLE_NAME
                + " order by " + DatabaseSchema.MemberEntry._ID ;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            if (cursor.getInt(3) == 1) continue;
            MemberInfoWithId info = new MemberInfoWithId(cursor.getInt(0),cursor.getString(1),cursor.getFloat(2));
            memberList.add(info);
        }

        return memberList;
    }

}
