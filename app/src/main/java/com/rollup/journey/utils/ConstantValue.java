package com.rollup.journey.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by zq on 2016/12/16.
 */

public class ConstantValue {

    //数据库表的名字
    public static final String TEAM = "team";
    public static final String TEAMINFO = "teamInfo";
    public static final String TEAMINFOHISTORY = "teamInfoHistory";
    public static final String MESSAGEINFO = "messageInfo";
    public static final String PERSONINDEX = "personIndex";
    public static final String LINKRECORD = "linkRecord";

    //BluetoothGatt接收数据时发送广播
    public final static String ACTION_GATT_CONNECTED =
            "com.journey.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.journey.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.journey.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.journey.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.journey.bluetooth.le.EXTRA_DATA";
    public static final int HANDLER_SEND = 100;
    public static final int HANDLER_BACK = 200;
    public static final int HANDLER_DISCONNECT = 400;
    public static final int HANDLER_THREAD = 500;

    //蓝牙地址
    public static final String DEVICEADDRESS = "blueDeviceAddress";
    //通信记录所有人有份
    public static final String ALL = "linkRecordAll";
    //获取团名
    public static String TEAMNAME = "teamName";
    //地图类型
    public static String MAPTYPE = "maptype";
    //返回状态
    public static byte BACK_STATE_SUC = 0x00;
    //功能表示位
    public static byte FUNC_BATTERY = 0x01;
    public static byte FUNC_WRITE_MD5 = 0x02;
    public static byte FUNC_ADD_DEV = 0x03;
    public static byte FUNC_LINK_TEST = 0x04;
    public static byte FUNC_SEND_MSG = 0x05;
    public static byte FUNC_POSITION_MSG = 0x06;
    public static byte FUNC_MODY_TIME = 0x07;
    public static byte FUNC_CLEAR_MSG = 0x08;
    public static byte FUNC_REDD_MD5 = 0x09;
}
