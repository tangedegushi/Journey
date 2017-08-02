package com.rollup.journey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rollup.journey.R;
import com.rollup.journey.activity.Main1TestActivity;
import com.rollup.journey.activity.Main1TestRecordActivity;
import com.rollup.journey.bean.LinkState;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BluetoothLeClass;
import com.rollup.journey.utils.DbEngine;

import java.util.List;

/**
 * Created by zq on 2016/12/23.
 */

public class Test_Listview extends BaseAdapter {

    private Context mContext;
    private List<LinkState> data;
    private final String success;
    private final String fail;
    private String tag = null;
    private String msg = null;

    public Test_Listview(Context context, List data,String tag){
        success = context.getResources().getString(R.string.str_main_success);
        fail = context.getResources().getString(R.string.str_main_fail);
        mContext = context;
        this.tag = tag;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Test_Listview.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.test_link_item,null);
            viewHolder.item = (LinearLayout) convertView.findViewById(R.id.main1_test_item_all);
            viewHolder.name = (TextView) convertView.findViewById(R.id.main1_test_item_name);
            viewHolder.state = (TextView) convertView.findViewById(R.id.main1_test_item_state);
            viewHolder.refresh = (ImageView) convertView.findViewById(R.id.main1_test_item_refresh);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LinkState linkState = data.get(position);
        final String name = linkState.getName();
        String state = linkState.getState();
        viewHolder.name.setText(name);
        if (state.equals("0")){
            viewHolder.state.setText(fail);
            viewHolder.name.setTextColor(Color.RED);
            viewHolder.state.setTextColor(Color.RED);
            viewHolder.refresh.setVisibility(View.VISIBLE);
        }else{
            viewHolder.name.setTextColor(Color.GRAY);
            viewHolder.state.setTextColor(Color.GRAY);
            viewHolder.refresh.setVisibility(View.INVISIBLE);
            viewHolder.state.setText(success);
        }

        if (state.equals("1")) {
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Main1TestRecordActivity.class);
                    intent.putExtra("name", name);
                    mContext.startActivity(intent);
                }
            });
            viewHolder.state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Main1TestRecordActivity.class);
                    intent.putExtra("name", name);
                    mContext.startActivity(intent);
                }
            });
        }else {
            viewHolder.state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Main1TestRecordActivity.class);
                    intent.putExtra("name", name);
                    mContext.startActivity(intent);
                }
            });
            viewHolder.refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tag.equals("test")) {
                        //重新去连接测试设备
                        DbEngine.getInstance(mContext).showDialog((Main1TestActivity) mContext, "设备测试中...");
                        List allDeviceIndex = DbEngine.getInstance(mContext).getAllDeviceIndex(name);
                        ((Main1TestActivity)mContext).listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentTestMulDevice(allDeviceIndex));
                        ((Main1TestActivity)mContext).sendMessage();
                    } else if (tag.equals("message")) {
                        //重新发送消息
                        DbEngine.getInstance(mContext).showDialog((Main1TestActivity) mContext, "正在重新发送消息...");
                        List allDeviceIndex = DbEngine.getInstance(mContext).getAllDeviceIndex(name);
                        ((Main1TestActivity)mContext).listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentSendMsg(allDeviceIndex, msg));
                        ((Main1TestActivity)mContext).sendMessage();
                    } else if (tag.equals("time")) {
                        //重新校准时间
                        DbEngine.getInstance(mContext).showDialog((Main1TestActivity) mContext, "重新校准时间...");
                        List allDeviceIndex = DbEngine.getInstance(mContext).getAllDeviceIndex(name);
                        ((Main1TestActivity)mContext).listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentTestMulDevice(allDeviceIndex));
                        ((Main1TestActivity)mContext).sendMessage();
                    }
                }
            });
        }
        return convertView;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class ViewHolder{
        LinearLayout item;
        TextView name;
        TextView state;
        ImageView refresh;
    }

    public void setData(List data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void setState(int position){
        data.get(position).setState("1");
        notifyDataSetChanged();
    }
}
