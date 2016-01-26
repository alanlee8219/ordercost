package com.example.jcs.orderassistant.ui;

/**
 * Created by JCS on 2015/10/9.
 */
public class MemberInfo {

    private String name;
    private int money;

    public MemberInfo(String name,int money){
        this.name = name;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public int getMoney(){
        return money;
    }
}
