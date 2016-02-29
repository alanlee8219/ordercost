package com.example.jcs.orderassistant.ui;

/**
 * Created by JCS on 2015/10/9.
 */
public class MemberInfoWithId {

    private String name;
    private int id;
    private float sum;

    private boolean selected;
    private int each;

    public MemberInfoWithId(int id,String name,float sum){
        this.name = name;
        this.id = id;
        this.sum = sum;
        selected = false;
        each = 0;
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

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public boolean getSelected()
    {
        return selected;
    }

    public void setEach(int each)
    {
        this.each = each;
    }

    public int getEach()
    {
        return each;
    }

}
