package com.rollup.journey.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.util.List;

/**
 * 电子团档主入口
 */
public class Main1Activity extends BlueBaseActivity implements View.OnClickListener{

    private RelativeLayout main1_layout1;
    private RelativeLayout main1_layout2;
    private RelativeLayout main1_layout3;
    private TitleView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main1;
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.main1_title);
        main1_layout1 = (RelativeLayout) findViewById(R.id.main1_layout1);
        main1_layout2 = (RelativeLayout) findViewById(R.id.main1_layout2);
        main1_layout3 = (RelativeLayout) findViewById(R.id.main1_layout3);
    }

    private void initEvent() {
        titleView.setOnClickListenerLeft(this);
        main1_layout1.setOnClickListener(this);
        main1_layout2.setOnClickListener(this);
        main1_layout3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            case R.id.main1_layout1:
                Intent intent1 = new Intent(Main1Activity.this,Main1BuildActivity.class);
                startActivity(intent1);
                break;
            case R.id.main1_layout2:
                String teamName = SpUtils.getString(this, ConstantValue.TEAMNAME, null);
                if (teamName == null) {
                    SpUtils.showToast(this, getResources().getString(R.string.str_main_electric_build_first));
                } else {
                    Intent intent2 = new Intent(Main1Activity.this, Main1AddActivity.class);
                    startActivity(intent2);
                }
                break;
            case R.id.main1_layout3:
                List allDeviceIndex = DbEngine.getInstance(this).getAllDeviceIndex(null);
                if (allDeviceIndex.isEmpty()){
                    SpUtils.showToast(this,getResources().getString(R.string.str_main_electric_add_first));
                }else{
                    Intent intent3 = new Intent(Main1Activity.this,Main1TestActivity.class);
                    intent3.putExtra("tag","test");
                    startActivity(intent3);
                }
                break;
            default:
                break;
        }
    }
}
