package com.rollup.journey.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.database.MyOpenHelper;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.util.Timer;
import java.util.TimerTask;

public class FlashActivity extends AppCompatActivity {

    private String teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        ImageView imgFlash = (ImageView) findViewById(R.id.flash_image);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.flash_img);
        imgFlash.startAnimation(animation);
        teamName = SpUtils.getString(this, ConstantValue.TEAMNAME, null);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (teamName != null) {
                    Intent intent = new Intent(FlashActivity.this,BluetoothActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(FlashActivity.this,GuideActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        };
        timer.schedule(task,1500);
//        ObjectAnimator animator = ObjectAnimator.ofFloat(imgFlash,"alpha",0,2);
//        animator.start();
    }

}
