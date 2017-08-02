package com.rollup.journey.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Main1BuildActivity extends BlueBaseActivity implements View.OnClickListener{

    private TitleView main1_build_title;
    private TextView main1_build_name;
    private TextView main1_build_total;
    private TextView main1_build_time;
    private TextView main1_build_destination;
    private TextView main1_build_traffic;
    private TextView main1_build_confirm;
    private ContentValues values;
    private String teamName;
    private boolean isClear = false;
    private boolean isHave = false;
    private boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main1_build;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClearData();
    }

    private void isClearData() {
        String teamName = SpUtils.getString(this, ConstantValue.TEAMNAME, null);
        if (teamName != null){
            showClearDialog();
        }else{isClear = true;}
    }

    private void showClearDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_dialog_title_choose)
                .setMessage(R.string.str_dialog_message_clear_pre_team_data)
                .setCancelable(false)
                .setNegativeButton(R.string.str_dialog_no,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.str_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbEngine.getInstance(Main1BuildActivity.this).showDialog(Main1BuildActivity.this, getResources().getString(R.string.str_toast_data_clearing));
                        clearRemoteDeviceData();
                    }
                })
                .create();
        dialog.show();
        isBack = true;
    }

    private void initView() {
        main1_build_title = (TitleView) findViewById(R.id.main1_build_title);
        main1_build_name = (TextView) findViewById(R.id.main1_build_name);
        main1_build_total = (TextView) findViewById(R.id.main1_build_total);
        main1_build_time = (TextView) findViewById(R.id.main1_build_time);
        main1_build_destination = (TextView) findViewById(R.id.main1_build_destination);
        main1_build_traffic = (TextView) findViewById(R.id.main1_build_traffic);
        main1_build_confirm = (TextView) findViewById(R.id.main1_build_confirm);
    }

    private void initEvent() {
        main1_build_title.setOnClickListenerLeft(this);
        main1_build_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
//                finish();
                showClearDialog();
                break;
            case R.id.main1_build_confirm:
                teamName = main1_build_name.getText().toString().trim();
                String teamTotal = main1_build_total.getText().toString().trim();
                String teamTime = main1_build_time.getText().toString().trim();
                String teamDestination = main1_build_destination.getText().toString().trim();
                String teamTraffic = main1_build_traffic.getText().toString().trim();
                for (String s : DbEngine.getInstance(this).getTeamRecord()) {
                    if (s.equals(teamName)) {
                        isHave = true;
                    }
                }
                if (TextUtils.isEmpty(teamName)){
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_main_pl_team_name));
                }else if (isHave){
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_toast_team_name_had_add));
                }else if (TextUtils.isEmpty(teamTotal)){
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_main_pl_team_people_num));
                }else if (TextUtils.isEmpty(teamTime)){
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_main_pl_team_time));
                }else if (TextUtils.isEmpty(teamDestination)){
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_main_pl_team_destination));
                }else if (TextUtils.isEmpty(teamTraffic)){
                    SpUtils.showToast(getApplicationContext(),getResources().getString(R.string.str_main_pl_team_traffic_info));
                }else {
                    if (isClear){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                        String date = format.format(new Date());
                        values = new ContentValues();
                        values.put("teamName", teamName);
                        values.put("teamSize", teamTotal);
                        values.put("totalTime", teamTime);
                        values.put("place", teamDestination);
                        values.put("trafficInfo", teamTraffic);
                        values.put("buildDate",date);
                        //将团队名生成MD5发送到设备端
                        DbEngine.getInstance(this).showDialog(this, getResources().getString(R.string.str_main_electric_building));
                        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentMd5(teamName));
                        sendMessage();
                    }else{showClearDialog();}
                }
                break;
            default:
                break;
        }
    }

    int number = 0;
    @Override
    public void handlerBackMsg() {
        DbEngine.getInstance(this).dismissDialog();
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        if(0x02 == dataType ){
            if (ConstantValue.BACK_STATE_SUC == data[0]){
                DbEngine.getInstance(this).insertTeam(values);
                SpUtils.putString(this, ConstantValue.TEAMNAME,teamName);
                SpUtils.showToast(this, getResources().getString(R.string.str_main_electric_build_success));
                finish();
            }else{
                SpUtils.showToast(this, getResources().getString(R.string.str_main_electric_build_fail));
            }
        }else if(0x08 == dataType){
            if (ConstantValue.BACK_STATE_SUC == data[0]){
                isClear = true;
                DbEngine.getInstance(Main1BuildActivity.this).dismissDialog();
                DbEngine.getInstance(Main1BuildActivity.this).savaDataToHistory();
                DbEngine.getInstance(Main1BuildActivity.this).clearJourneyData();
                SpUtils.clearString(Main1BuildActivity.this, ConstantValue.TEAMNAME);
                SpUtils.showToast(this,getResources().getString(R.string.str_toast_data_clear_success));
            }else{
                if (number < 3){
                    number++;
                    clearRemoteDeviceData();
                }else {
                    number=0;
                    DbEngine.getInstance(Main1BuildActivity.this).dismissDialog();
                    SpUtils.showToast(this,getResources().getString(R.string.str_toast_data_clear_fail));
                }
            }
        }
    }

    private void clearRemoteDeviceData() {
        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentClearData());
        sendMessage();
    }
}
