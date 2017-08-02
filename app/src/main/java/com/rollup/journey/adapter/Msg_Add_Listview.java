package com.rollup.journey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.DbEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zq on 2016/12/23.
 */

public class Msg_Add_Listview extends BaseAdapter {

    private Context mContex;
    private List<String> allPerson = null;
    private ArrayList<String> listIndex = new ArrayList<>();
    private boolean isAllChecked = true;
    private boolean noCheck;
    private boolean isHaveClick = false;

    public Msg_Add_Listview(Context context,List<String> allPerson){
        mContex = context;
        this.allPerson = allPerson;
    }

    public Msg_Add_Listview(Context context,List listIndex1,boolean noChecked){
        mContex = context;
        allPerson = listIndex1;
        this.noCheck = noChecked;
    }

    @Override
    public int getCount() {
        return allPerson == null?0:allPerson.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.msg_add_item,null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.main2_msg_add_item_name);
            viewHolder.box = (CheckBox) convertView.findViewById(R.id.main2_msg_add_item_box);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.main2_msg_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final String nameAndIndex = allPerson.get(position);
        String[] split = nameAndIndex.split(BlueProfile.SPLIT);
        final int index = Integer.parseInt(split[1]);
        viewHolder.name.setText(split[0]);
        viewHolder.box.setClickable(false);
        if (!noCheck){
            if (listIndex.contains(nameAndIndex)){
                viewHolder.box.setChecked(true);
            }else{
                viewHolder.box.setChecked(false);
            }
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isHaveClick = true;
                    if(!viewHolder.box.isChecked()){
                        viewHolder.box.setChecked(true);
                        listIndex.add(nameAndIndex);
                    }else{
                        viewHolder.box.setChecked(false);
                        listIndex.remove(nameAndIndex);
                    }
                    Logger.d(listIndex);
                }
            });
            if (!isHaveClick) {
                if (isAllChecked){
                    viewHolder.box.setChecked(true);
                }else{
                    viewHolder.box.setChecked(false);
                }
            }
        }else{
            viewHolder.box.setChecked(true);
        }
        Logger.d("msg add list is have the data:"+isHaveClick);
        return convertView;
    }

    public static class ViewHolder{
        TextView name;
        CheckBox box;
        RelativeLayout layout;
    }

    public void setAllChecked(boolean isAllChecked){
        isHaveClick = false;
       this.isAllChecked = isAllChecked;
    }
    public ArrayList<String> getListIndex(){
        return listIndex;
    }
    public void addListIndex(){
        listIndex.addAll(allPerson);
    }
    public void removetListIndex(){
        listIndex.removeAll(allPerson);
    }
    public void setData(List<String> data){
        allPerson = data;
        notifyDataSetChanged();
    }
}
