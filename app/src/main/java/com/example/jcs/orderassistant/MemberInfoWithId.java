package com.example.jcs.orderassistant;

/**
 * Created by JCS on 2015/10/9.
 */
public class MemberInfoWithId {

    private String name;
    private int id;
    private float sum;

    public MemberInfoWithId(int id,String name,float sum){
        this.name = name;
        this.id = id;
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public int getId(){
        return id;
    }

    public float getSum() {
        return sum;
    }
}
