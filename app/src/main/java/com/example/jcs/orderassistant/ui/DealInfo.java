package com.example.jcs.orderassistant.ui;

/**
 * Created by JCS on 2015/10/9.
 */
public class DealInfo {

    private String dining;
    private String date;
    private int money;

    public DealInfo(String dining,String date,int money){
        this.dining = dining;
        this.date = date;
        this.money = money;
    }

    public String getDining() {
        return dining;
    }

    public String getDate() {
        return date;
    }

    public int getMoney(){
        return money;
    }
}
