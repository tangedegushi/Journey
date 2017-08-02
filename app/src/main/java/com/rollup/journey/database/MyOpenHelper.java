package com.rollup.journey.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zq on 2016/12/19.
 */

public class MyOpenHelper extends SQLiteOpenHelper {

    public MyOpenHelper(Context context) {
        super(context, "journey.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //团队相关信息
        String team = "create table team (_id integer primary key autoincrement not null,teamName varchar(20)," +
                "teamSize int,totalTime varchar(40),place varchar(20),trafficInfo varchar(20),buildDate varchar(20))";
        //团队人员相关历史信息
        String teamInfoHistory = "create table teamInfoHistory (_id integer primary key autoincrement not null,teamName varchar(20)," +
                "deviceName varchar(20),peopleName varchar(10),phoneNumber varchar(15),remark varchar(30))";
        String teamInfo = "create table teamInfo (_id integer primary key autoincrement not null,teamName varchar(20)," +
                "deviceName varchar(20),deviceIndex int,peopleName varchar(10),phoneNumber varchar(15),remark varchar(30),state varchar(5),timeState varchar(5))";
        //保存发送的相关信息
        String messageInfo = "create table messageInfo (_id integer primary key autoincrement not null,message varchar(20),peopleIndex int,count int,time varchar(10),state varchar(5))";
        //发送信息给哪些人
        String personIndex = "create table personIndex (_id integer primary key autoincrement not null,peopleIndex int,peopleName varchar(20),state varchar(5))";
        //发送消息给设备端时记录
        String linkRecord = "create table linkRecord (_id integer primary key autoincrement not null,peopleName varchar(10),type varchar(10),time varchar(20),state varchar(5))";

        db.execSQL(team);
        db.execSQL(teamInfo);
        db.execSQL(teamInfoHistory);
        db.execSQL(messageInfo);
        db.execSQL(personIndex);
        db.execSQL(linkRecord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
