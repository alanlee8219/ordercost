package com.example.jcs.orderassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.List;

/**
 * Created by JCS on 2015/10/10.
 */
public class MemberAdapter  extends ArrayAdapter<MemberInfoWithId> {

    private int resourceId;

    public MemberAdapter(Context context, int textViewResourceId,
                             List<MemberInfoWithId> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemberInfoWithId info = getItem(position); // 获取当前项的Member实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        final CheckedTextView name = (CheckedTextView) view.findViewById(R.id.select_member);
        name.setText(info.getName());
        name.setChecked(true);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.toggle();
            }
        });
        return view;
    }
}