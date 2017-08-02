package com.rollup.journey.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BlueBaseActivity implements View.OnClickListener {

    private TextView main_text1;
    private TextView main_text2;
    private TextView main_text3;
    private TextView main_text4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initView() {
        main_text1 = (TextView) findViewById(R.id.main_text1);
        main_text2 = (TextView) findViewById(R.id.main_text2);
        main_text3 = (TextView) findViewById(R.id.main_text3);
        main_text4 = (TextView) findViewById(R.id.main_text4);
    }

    private void initEvent() {
        main_text1.setOnClickListener(this);
        main_text2.setOnClickListener(this);
        main_text3.setOnClickListener(this);
        main_text4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_text1:
                Intent intent1 = new Intent(MainActivity.this,Main1Activity.class);
                startActivity(intent1);
                break;
            case R.id.main_text2:
                Intent intent2 = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent2);
                break;
            case R.id.main_text3:
                String map1 = SpUtils.getString(this, ConstantValue.MAPTYPE, "map1");
                Intent intent3;
                //map1高德地图 map2是Google地图
                if(map1.equals("map1")){
                    intent3 = new Intent(MainActivity.this,MapActivity.class);
                }else{
                    intent3 = new Intent(MainActivity.this,GoogleMapActivity.class);
                }
                startActivity(intent3);
                break;
            case R.id.main_text4:
                Intent intent4 = new Intent(MainActivity.this,Main4Activity.class);
                startActivity(intent4);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbEngine.db.close();
        instance.disconnect();
        instance.close();
    }

    private long firstTime;
    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, R.string.str_toast_press_next_out, Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
//            Process.killProcess(Process.myPid());
            instance.disconnect();
            instance.close();
            finish();
        }
    }
}
