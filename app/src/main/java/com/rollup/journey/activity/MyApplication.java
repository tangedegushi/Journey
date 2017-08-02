package com.rollup.journey.activity;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by zq on 2016/12/20.
 */

public class MyApplication extends Application {

    public List<String> nameList;

    private static final String TAG = "journey";
    private Context mContext;
    public static boolean isConnect = false;
    public static String deviceAddress;
    public static int battery;

    private static class newInstance {
        private static MyApplication application = new MyApplication();
    }

    public static MyApplication getInstance(){
        return newInstance.application;
    }

    public Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(TAG);
        mContext = this;
    }
}
