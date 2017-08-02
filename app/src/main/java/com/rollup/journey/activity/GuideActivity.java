package com.rollup.journey.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Guide_Viewpager;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{


    private ViewPager viewpPager;
    private LinearLayout dot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initEvent();
    }

    private void initView() {
        viewpPager = (ViewPager) findViewById(R.id.activity_flash_viewpager);
        viewpPager.setOnPageChangeListener(this);
        dot = (LinearLayout) findViewById(R.id.activity_flash_dot);
    }

    private void initEvent() {
        viewpPager.setAdapter(new Guide_Viewpager(this));
        dot.getChildAt(0).setSelected(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dot.getChildCount(); i++) {
            if (i == position) {
                dot.getChildAt(i).setSelected(true);
            }else{
                dot.getChildAt(i).setSelected(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
