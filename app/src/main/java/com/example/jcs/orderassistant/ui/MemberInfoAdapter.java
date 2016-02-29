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

    public MemberInfoAdapter(Context context, int textViewResourceId,
                        List<MemberInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
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
