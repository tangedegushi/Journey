package com.rollup.journey.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.rollup.journey.R;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

public class Main4Activity extends BlueBaseActivity implements View.OnClickListener{

    private RelativeLayout main4_layout1;
    private RelativeLayout main4_layout2;
    private RelativeLayout main4_layout3;
    private RelativeLayout main4_layout4;
    private TitleView main4_title;
    private AlertDialog dialog;
    private RadioButton dialog_map_radio1;
    private RadioButton dialog_map_radio2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main4;
    }

    private void initView() {
        main4_title = (TitleView) findViewById(R.id.main4_title);
        main4_layout1 = (RelativeLayout) findViewById(R.id.main4_layout1);
        main4_layout2 = (RelativeLayout) findViewById(R.id.main4_layout2);
        main4_layout3 = (RelativeLayout) findViewById(R.id.main4_layout3);
        main4_layout4 = (RelativeLayout) findViewById(R.id.main4_layout4);
    }

    private void initEvent() {
        main4_title.setOnClickListenerLeft(this);
        main4_layout1.setOnClickListener(this);
        main4_layout2.setOnClickListener(this);
        main4_layout3.setOnClickListener(this);
        main4_layout4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            case R.id.main4_layout1:
                Intent intent1 = new Intent(Main4Activity.this,Main4HistoryActivity.class);
                startActivity(intent1);
                break;
            case R.id.main4_layout2:
                alertMyDialog(2);
                break;
            case R.id.main4_layout3:
                Intent intent3 = new Intent(Main4Activity.this,Main1TestActivity.class);
                intent3.putExtra("tag","time");
                startActivity(intent3);
                break;
            case R.id.main4_layout4:
                String teamName = SpUtils.getString(this, ConstantValue.TEAMNAME, null);
                if (teamName != null) {
                    alertMyDialog(4);
                }else{
                    SpUtils.showToast(this, getResources().getString(R.string.str_main_electric_build_first));
                }
                break;
            case R.id.over_dialog_confirm:
                clearRemoteDeviceData();
                DbEngine.getInstance(this).showDialog(this,getResources().getString(R.string.str_toast_data_clearing));
                dialog.dismiss();
                break;
            case R.id.over_dialog_cancel:
                dialog.dismiss();
                break;
            case R.id.dialog_map_layout1:
                SpUtils.putString(this, ConstantValue.MAPTYPE,"map1");
                dialog.dismiss();
                break;
            case R.id.dialog_map_layout2:
                isSurportGoogleService();
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    private void isSurportGoogleService() {
        boolean googleserviceFlag = true;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
//            if(googleApiAvailability.isUserResolvableError(resultCode))
//            {
//                googleApiAvailability.getErrorDialog(this,resultCode, 2404).show();
//            }
            googleserviceFlag=false;
        }
        if(googleserviceFlag==false){
            //说明不支持google服务
            SpUtils.showToast(this,getResources().getString(R.string.str_toast_device_no_google_service));
        }else{
            SpUtils.putString(this, ConstantValue.MAPTYPE,"map2");
        }
    }

    private void alertMyDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main4Activity.this);
        View view;
        if(index == 2){
            view = getLayoutInflater().inflate(R.layout.dialog_map,null);
        }else{
            view = getLayoutInflater().inflate(R.layout.dialog_over,null);
        }
        builder.setView(view);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if(index == 2){
            //默认是高德地图
            String maptype = SpUtils.getString(this,ConstantValue.MAPTYPE,"map1");
            RadioGroup dialog_map_layout1 = (RadioGroup) view.findViewById(R.id.dialog_map_layout1);
            RadioGroup dialog_map_layout2 = (RadioGroup) view.findViewById(R.id.dialog_map_layout2);
            dialog_map_radio1 = (RadioButton) view.findViewById(R.id.dialog_map_radio1);
            dialog_map_radio2 = (RadioButton) view.findViewById(R.id.dialog_map_radio2);
            dialog_map_radio1.setClickable(false);
            dialog_map_radio2.setClickable(false);
            if (maptype.equals("map1")){
                dialog_map_radio1.setChecked(true);
            }else{
                dialog_map_radio2.setChecked(true);
            }
            dialog_map_layout1.setOnClickListener(this);
            dialog_map_layout2.setOnClickListener(this);
        }else{
            ImageView over_dialog_confirm = (ImageView) view.findViewById(R.id.over_dialog_confirm);
            ImageView over_dialog_cancel = (ImageView) view.findViewById(R.id.over_dialog_cancel);
            over_dialog_confirm.setOnClickListener(this);
            over_dialog_cancel.setOnClickListener(this);
        }
    }

    private void clearRemoteDeviceData() {
        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentClearData());
        sendMessage();
    }

    int number = 0;
    @Override
    public void handlerBackMsg() {
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        if (dataType == 0x08){
            if (ConstantValue.BACK_STATE_SUC == data[0]) {
                DbEngine.getInstance(this).dismissDialog();
                DbEngine.getInstance(this).savaDataToHistory();
                DbEngine.getInstance(this).clearJourneyData();
                SpUtils.clearString(this, ConstantValue.TEAMNAME);
                SpUtils.showToast(this, getResources().getString(R.string.str_toast_data_clear_success));
                Intent intent = new Intent(Main4Activity.this, MainActivity.class);
                startActivity(intent);
            } else {
                if (number < 3){
                    number++;
                    clearRemoteDeviceData();
                }else {
                    number=0;
                    DbEngine.getInstance(this).dismissDialog();
                    SpUtils.showToast(this,getResources().getString(R.string.str_toast_data_clear_fail));
                }
            }
        }
    }
}
