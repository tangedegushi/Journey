package com.rollup.journey.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Main4HistoryActivity extends BlueBaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private List<String> teamRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main4_history;
    }

    private void initView() {
        TitleView main4_history_title = (TitleView) findViewById(R.id.main4_history_title);
        ListView main4_other_history_listView = (ListView) findViewById(R.id.main4_other_history_listView);
        teamRecord = DbEngine.getInstance(this).getTeamRecord();
        main4_other_history_listView.setAdapter(adapter);
        main4_other_history_listView.setOnItemClickListener(this);
        main4_other_history_listView.setOnItemLongClickListener(this);
        main4_history_title.setOnClickListenerLeft(this);
        main4_history_title.setOnClickListenerRight(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            case R.id.imageView2:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setMessage(R.string.str_out_put_text)
                        .setNegativeButton(R.string.str_dialog_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.str_dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveDataToLocal();
                            }
                        })
                        .create();
                dialog.show();
            default:
                break;
        }
    }

    private void saveDataToLocal() {
        Cursor teamInfo = DbEngine.db.query(ConstantValue.TEAMINFO, null, null, null, null, null, null);
        Cursor teamInfoHistory = DbEngine.db.query(ConstantValue.TEAMINFOHISTORY, null, null, null, null, null, null);
        File file = new File(Environment.getExternalStorageDirectory().toString(),"journey.txt");
        String team_name = getResources().getString(R.string.str_out_put_text_team_name);
        String device_name = getResources().getString(R.string.str_out_put_text_device_name);
        String people_name = getResources().getString(R.string.str_out_put_text_people_name);
        String phone_number = getResources().getString(R.string.str_out_put_text_phone_number);
        String remark_1 = getResources().getString(R.string.str_out_put_text_remark);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            if (teamInfoHistory.moveToFirst()){
                do {
                    String teamName = teamInfoHistory.getString(teamInfoHistory.getColumnIndex("teamName"));
                    String deviceName = teamInfoHistory.getString(teamInfoHistory.getColumnIndex("deviceName"));
                    String peopleName = teamInfoHistory.getString(teamInfoHistory.getColumnIndex("peopleName"));
                    String phoneNumber = teamInfoHistory.getString(teamInfoHistory.getColumnIndex("phoneNumber"));
                    String remark = teamInfoHistory.getString(teamInfoHistory.getColumnIndex("remark"));
                    String historyInfo = team_name + teamName + device_name + deviceName + people_name + peopleName + phone_number + phoneNumber + remark_1 + remark+"\n";
                    bos.write(historyInfo.getBytes());
                } while (teamInfoHistory.moveToNext());
            }
            if (teamInfo.moveToFirst()){
                do {
                    String teamName = teamInfo.getString(teamInfo.getColumnIndex("teamName"));
                    String deviceName = teamInfo.getString(teamInfo.getColumnIndex("deviceName"));
                    String peopleName = teamInfo.getString(teamInfo.getColumnIndex("peopleName"));
                    String phoneNumber = teamInfo.getString(teamInfo.getColumnIndex("phoneNumber"));
                    String remark = teamInfo.getString(teamInfo.getColumnIndex("remark"));
                    String historyInfo = team_name + teamName+device_name + deviceName + people_name + peopleName + phone_number + phoneNumber+ remark_1 + remark+"\n";
                    bos.write(historyInfo.getBytes());
                } while (teamInfo.moveToNext());
            }
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String teamName = teamRecord.get(teamRecord.size()-position-1);
        Intent intent = new Intent(Main4HistoryActivity.this,Main4HistoryDetailActivity.class);
        intent.putExtra("teamName",teamName);
        startActivity(intent);
    }

    public BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return teamRecord.size();
        }

        @Override
        public Object getItem(int position) {
            return teamRecord.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = View.inflate(Main4HistoryActivity.this, R.layout.main4_history_item, null);
                holder.textTeamName = (TextView) convertView.findViewById(R.id.main4_text_teamName);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textTeamName.setText(teamRecord.get(teamRecord.size()-position-1));
            return convertView;
        }
    };

    private boolean isCurrent = false;
    private String teamName;
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        teamName = teamRecord.get(teamRecord.size()-position-1);
        String localTeamName = SpUtils.getString(Main4HistoryActivity.this, ConstantValue.TEAMNAME, null);
        Logger.d(localTeamName+"..."+teamName);
        if (localTeamName != null && teamName.equals(localTeamName)) {
            showAlertDialog(getResources().getString(R.string.str_dialog_message_clear_current_team_data));
            isCurrent = true;
        }else{
            showAlertDialog(getResources().getString(R.string.str_dialog_message_clear_team_data)+":"+teamName);
            isCurrent = false;
        }
        teamRecord.remove(teamRecord.size()-position-1);
        adapter.notifyDataSetChanged();
        return true;
    }

    private void showAlertDialog(final String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.str_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除该团所有数据
                        if (isCurrent){
                            DbEngine.getInstance(Main4HistoryActivity.this).showDialog(Main4HistoryActivity.this,getResources().getString(R.string.str_toast_data_clearing));
                            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentClearData());
                            sendMessage();
                        }else{
                            DbEngine.getInstance(Main4HistoryActivity.this).clearJourneyTeamData(isCurrent,teamName);
                        }
                    }
                })
                .create();
        dialog.show();
    }

    public static class ViewHolder{
        TextView textTeamName;
    }

    @Override
    public void handlerBackMsg() {
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        if (dataType == ConstantValue.FUNC_CLEAR_MSG){
            if (data[0] == ConstantValue.BACK_STATE_SUC){
                teamRecord.remove(teamName);
                adapter.notifyDataSetChanged();
                DbEngine.getInstance(Main4HistoryActivity.this).clearJourneyTeamData(isCurrent,teamName);
            }else{
                DbEngine.getInstance(this).dismissDialog();
                SpUtils.showToast(this,getResources().getString(R.string.str_toast_data_clear_fail));
            }
        }
    }
}
