package com.example.jcs.orderassistant.ui;

/**
 * Created by JCS on 2015/10/9.
 */
public class MemberInfo {

    private String name;
    private float money;

    public MemberInfo(String name,float money){
        this.name = name;
        this.money = UiUtility.getMoneyfloat(money);
    }

    public String getName() {
        return name;
    }

    public float getMoney(){
        return money;
    }
}
