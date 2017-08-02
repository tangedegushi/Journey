package com.rollup.journey.activity;

import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rollup.journey.R;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.util.List;

public class Main4HistoryDetailActivity extends BlueBaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private String teamName;
    private String[] split;
    private List<String> teamInfoRecordDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teamName = getIntent().getExtras().getString("teamName");
        initData();
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main4_history_detail;
    }

    private void initData(){
        boolean isCurrent = false;
        String currentTeamName = SpUtils.getString(this, ConstantValue.TEAMNAME, null);
        if (currentTeamName != null) {
            if (currentTeamName.equals(teamName)) {
                isCurrent = true;
            }
        }
        String teamRecordDetail =  DbEngine.getInstance(this).getTeamRecordDetail(teamName);
        teamInfoRecordDetail = DbEngine.getInstance(this).getTeamInfoRecordDetail(isCurrent, teamName);
        split = teamRecordDetail.split(BlueProfile.SPLIT);
    }

    private void initView() {
        TitleView titleView = (TitleView) findViewById(R.id.main4_history_detail_title);
        titleView.setOnClickListenerLeft(this);
        TextView main4_history_detail_name = (TextView) findViewById(R.id.main4_history_detail_name);
        ListView main4_history_detail_people = (ListView) findViewById(R.id.main4_history_detail_people);
        String teamDetail = getResources().getString(R.string.str_main_team_name) + teamName +"\n"+
                getResources().getString(R.string.str_main_team_people_num) + split[0] +"\n"+
                getResources().getString(R.string.str_main_team_true_num) + teamInfoRecordDetail.size() +"\n"+
                getResources().getString(R.string.str_main_team_time) + split[1] +"\n"+
                getResources().getString(R.string.str_main_team_destination) + split[2]+"\n"+
                getResources().getString(R.string.str_main_team_traffic_info) + split[3]+"\n"+
                getResources().getString(R.string.str_out_put_text_show_info);
        main4_history_detail_name.setText(teamDetail);
        main4_history_detail_people.setAdapter(adapter);
        main4_history_detail_people.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                finish();
                break;
            default:
                break;
        }
    }

    public BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return teamInfoRecordDetail.size()+1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(Main4HistoryDetailActivity.this,R.layout.main4_history_detail_item,null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.main4_text_detail_teamName);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.main4_text_detail_phone);
                viewHolder.remark = (TextView) convertView.findViewById(R.id.main4_text_detail_remark);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                viewHolder.name.setText(R.string.str_out_put_text_show_info_name);
                viewHolder.phone.setText(R.string.str_out_put_text_show_info_phone);
                viewHolder.remark.setText(R.string.str_out_put_text_show_info_remark);
            }else{
                String detail = teamInfoRecordDetail.get(position-1);
                String[] split = detail.split(BlueProfile.SPLIT);
                if (split.length == 4){
                    viewHolder.name.setText(split[0]);
                    viewHolder.phone.setText(split[1]);
                    viewHolder.remark.setText(split[2]);
                }

            }
            return convertView;
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }
        String detail = teamInfoRecordDetail.get(position-1);
        String[] split = detail.split(BlueProfile.SPLIT);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(split[0])
                .setMessage(getResources().getString(R.string.str_out_put_text_show_info_phone)+"："+split[1]+"\n"+getResources().getString(R.string.str_out_put_text_show_info_remark)+"："+split[2]+"\n"+getResources().getString(R.string.str_out_put_text_show_info_deviceName)+":"+split[3])
                .create();
        dialog.show();
    }

    public class ViewHolder{
        TextView name;
        TextView phone;
        TextView remark;
    }
}
