package com.example.jcs.orderassistant.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.example.jcs.orderassistant.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JCS on 2015/10/10.
 */
public class MemberWithMAdapter extends ArrayAdapter<MemberInfoWithId> {

    private int resourceId;

    private List<MemberInfoWithId> list;
    public static HashMap<Integer, Boolean> isSelected;

    public MemberWithMAdapter(Context context, int textViewResourceId,
                         List<MemberInfoWithId> objects) {
        super(context, textViewResourceId, objects);
        list = objects;
        resourceId = textViewResourceId;
        init();
    }


    // 初始化 设置所有checkbox都为未选择
    public void init() {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int itemId = position;
        MemberInfoWithId info = getItem(position); // 获取当前项的Member实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        final CheckedTextView name = (CheckedTextView) view.findViewById(R.id.select_member_withm);
        name.setText(info.getName());
        name.setChecked(false);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.toggle();
                if (isSelected.put(itemId, false)) {
                    isSelected.put(itemId, false);
                } else {
                    isSelected.put(itemId, true);
                }
            }
        });
        name.setChecked(isSelected.get(position));

        final EditText edit = (EditText) view.findViewById(R.id.sep_money);

        return view;
    }
}
