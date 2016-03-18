package com.example.jcs.orderassistant.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jcs.orderassistant.R;

import java.util.List;

/**
 * Created by JCS on 2015/10/9.
 */
public class MemberInfoAdapter extends ArrayAdapter<MemberInfo> {

    private int resourceId;
    private  List<MemberInfo> list;

    public MemberInfoAdapter(Context context, int textViewResourceId,
                        List<MemberInfo> objects) {
        super(context, textViewResourceId, objects);
        list = objects;
        resourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MemberInfo getItem(int position) {
        return list.get(position);
    }

    public void remove(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    public void insert(int position, MemberInfo item) {
        list.add(position, item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemberInfo info = getItem(position); // 获取当前项的MemberInfo实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        final TextView name = (TextView) view.findViewById(R.id.balance_name);
        final TextView money = (TextView) view.findViewById(R.id.balance_money);
        name.setText(info.getName());
        String mm = Float.toString(info.getMoney());
        money.setText(mm);
        return view;
    }
}
