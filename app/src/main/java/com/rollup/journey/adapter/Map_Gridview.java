package com.rollup.journey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rollup.journey.R;
import com.rollup.journey.bean.MapState;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by zq on 2016/12/22.
 */

public class Map_Gridview extends BaseAdapter {

    private Context mContext;
    private List<MapState> dataList;

    public Map_Gridview(Context context, List<MapState> list){
        mContext = context;
        dataList = list;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflate.inflate(R.layout.map_item,null);
            holder.text = (TextView) convertView.findViewById(R.id.map_item_text);
            holder.img = (TextView) convertView.findViewById(R.id.map_item_img);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        MapState mapState = dataList.get(position);
        holder.text.setText(mapState.getIndex());
        String state = mapState.getState();
        if (state.equals("1")){
            holder.img.setBackgroundResource(R.drawable.map_dot_s);
        }else{
            holder.img.setBackgroundResource(R.drawable.map_dot_n);
        }
        return convertView;
    }

    public static class ViewHolder{
        TextView text;
        TextView img;
    }

    public void setDataList(List<MapState> list){
        dataList = list;
        notifyDataSetChanged();
    }
}
