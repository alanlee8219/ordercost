package com.example.jcs.orderassistant.ui;

/**
 * Created by JCS on 2015/10/9.
 */
public class DealInfo {

    private String dining;
    private String date;
    private int money;
    private String detail;

    public DealInfo(String dining,String date,int money,String detail){
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

    public int getMoney(){
        return money;
    }
}
