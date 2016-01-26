package com.example.jcs.orderassistant.ui;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by JCS on 2016/1/25.
 * UI用工具类，有点乱
 */
public class UiUtility {

    private static long fortmatDateTime(int year,int month,int day){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_MONTH,day);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

}
