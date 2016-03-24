package com.example.jcs.orderassistant.ui;

/**
 * Created by JCS on 2015/10/9.
 */
public class DealInfo {

    private String dining;
    private String date;
    private float money;
    private String detail;

    public DealInfo(String dining,String date,float money,String detail){
        this.dining = dining;
        this.date = date;
        this.money = money;
        this.detail = detail;
    }

    public String getDining() {
        return dining;
    }

    public String getDate() {
        return date;
    }

    public String getDetail(){
        return  detail;
    }

    public float getMoney(){
        return money;
    }
}
