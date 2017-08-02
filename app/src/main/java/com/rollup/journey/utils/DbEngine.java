package com.rollup.journey.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orhanobut.logger.Logger;
import com.rollup.journey.bean.LinkState;
import com.rollup.journey.bean.LocationInfo;
import com.rollup.journey.bean.MessageInfo;
import com.rollup.journey.bean.MessageRecord;
import com.rollup.journey.database.MyOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zq on 2016/12/20.
 */

public class DbEngine {

    public static SQLiteDatabase db;
    private String PEOPLENAME = "peopleName";
    private String STATE = "state";
    private List<LinkState> linkStateList;
    public List<String> allPeopleList;
    public List<MessageRecord> messageRecord;
    private List allSendMessageList;
    private List locationInfoList;
    private List personMessageList;
    private List allTeamList;
    private List allTeamListDetail;

    private DbEngine(){};

    private static class newInstance{
        public static final DbEngine engine = new DbEngine();
    }

    public static DbEngine getInstance(Context context){
        MyOpenHelper myOpenHelper = null;
        if (myOpenHelper == null) {
            myOpenHelper = new MyOpenHelper(context);
        }
        db = myOpenHelper.getWritableDatabase();
        return newInstance.engine;
    }

    /**
     * @return 连接测试时显示人名字和状态的集合list
     */
    public List<LinkState> getLinkTest(String peopleName){
        if (linkStateList == null){
            linkStateList = new ArrayList();
        }else{linkStateList.clear();}
        Cursor teamInfo;
        teamInfo = db.query(ConstantValue.TEAMINFO, new String[]{PEOPLENAME, STATE}, null, null, null, null, null);
        if (teamInfo != null && teamInfo.moveToFirst()){
            do {
                LinkState link = new LinkState();
                String name = teamInfo.getString(teamInfo.getColumnIndex(PEOPLENAME));
                String state = teamInfo.getString(teamInfo.getColumnIndex(STATE));
                link.setName(name);
                link.setState(state);
                if (peopleName == null) {
                    linkStateList.add(link);
                }else{
                    if (name.contains(peopleName)) {
                        linkStateList.add(link);
                    }
                }
            }while (teamInfo.moveToNext());
            teamInfo.close();
        }
        return linkStateList;
    }
    /**
     * @return 连接测试时显示人名字和状态的集合list
     */
    public List<LinkState> getTimeTest(String peopleName){
        if (linkStateList == null){
            linkStateList = new ArrayList();
        }else{linkStateList.clear();}
        Cursor teamInfo;
        teamInfo = db.query(ConstantValue.TEAMINFO, new String[]{PEOPLENAME, "timeState"}, null, null, null, null, null);
        if (teamInfo != null && teamInfo.moveToFirst()){
            do {
                LinkState link = new LinkState();
                String name = teamInfo.getString(teamInfo.getColumnIndex(PEOPLENAME));
                String state = teamInfo.getString(teamInfo.getColumnIndex("timeState"));
                link.setName(name);
                link.setState(state);
                if (peopleName == null) {
                    linkStateList.add(link);
                }else{
                    if (name.contains(peopleName)) {
                        linkStateList.add(link);
                    }
                }
            }while (teamInfo.moveToNext());
            teamInfo.close();
        }
        return linkStateList;
    }

    /**
     * @param name 根据人名查找相对应的信息
     * @return
     */
    public List getLinkRecord(String name){
        if (allPeopleList == null) {
            allPeopleList = new ArrayList();
        }else{allPeopleList.clear();}
        Cursor linkRecord = null;
        if (name == null){
            linkRecord = db.query(ConstantValue.LINKRECORD, new String[]{"type", STATE, "time"}, null, null, null, null, null);
        }else {
            linkRecord = db.query(ConstantValue.LINKRECORD, new String[]{"type", STATE, "time"}, PEOPLENAME+"=? ", new String[]{name}, null, null, null);
        }
        if (linkRecord!=null && linkRecord.moveToFirst()){
            do{
                String type = linkRecord.getString(linkRecord.getColumnIndex("type"));
                String time = linkRecord.getString(linkRecord.getColumnIndex("time"));
                String state = linkRecord.getString(linkRecord.getColumnIndex("state"));
                allPeopleList.add(time+BlueProfile.SPLIT+type+BlueProfile.SPLIT+state);
            }while (linkRecord.moveToNext());
        }
//        linkRecord = db.query(ConstantValue.LINKRECORD, new String[]{"type", STATE, "time"}, PEOPLENAME+"=?", new String[]{ConstantValue.ALL}, null, null, null);
//        if (linkRecord!=null && linkRecord.moveToFirst()){
//            do{
//                String type = linkRecord.getString(linkRecord.getColumnIndex("type"));
//                String time = linkRecord.getString(linkRecord.getColumnIndex("time"));
//                String state = linkRecord.getString(linkRecord.getColumnIndex("state"));
//                allPeopleList.add(time+BlueProfile.SPLIT+type+BlueProfile.SPLIT+state);
//            }while (linkRecord.moveToNext());
//        }
        linkRecord.close();
        return allPeopleList;
    }

    /**
     * @return 根据团名返回一个团队的所有人名和索引
     */
    public List getAllPerson(String peopleName){
        if (allPeopleList == null) {
            allPeopleList = new ArrayList();
        }else{allPeopleList.clear();}
        Cursor query;
        query = db.query(ConstantValue.TEAMINFO, new String[]{PEOPLENAME,"deviceIndex"}, null, null, null, null, null);
        if(query != null && query.moveToFirst()){
            do {
                String name = query.getString(query.getColumnIndex(PEOPLENAME));
                String index = query.getString(query.getColumnIndex("deviceIndex"));
                if (peopleName == null) {
                    allPeopleList.add(name+BlueProfile.SPLIT+index);
                } else {
                    if (name.contains(peopleName)) {
                        allPeopleList.add(name+BlueProfile.SPLIT+index);
                    }
                }
            }while (query.moveToNext());
            query.close();
        }
        return allPeopleList;
    }

    /**
     * @return 根据团名和索引查找人名
     */
    public String getPersonName(String index){
        String name = null;
        Cursor query = db.query(ConstantValue.TEAMINFO, new String[]{PEOPLENAME}, "deviceIndex=?", new String[]{index}, null, null, null);
        if(query != null && query.moveToFirst()){
            do {
                name = query.getString(query.getColumnIndex(PEOPLENAME));
            }while (query.moveToNext());
            query.close();
        }
        return name;
    }

    /**
     * @return 根据人名查找信息索引
     */
    public List<MessageRecord> getMessageRecord(String name){
        if (messageRecord == null) {
            messageRecord = new ArrayList<>();
        }else{messageRecord.clear();}
        Cursor query = db.query(ConstantValue.PERSONINDEX, new String[]{"peopleIndex"}, "peopleName=?", new String[]{name}, null, null, null);
        if(query != null && query.moveToFirst()){
            do {
                String peopleIndex = query.getString(query.getColumnIndex("peopleIndex"));
                Cursor cursor = db.query(ConstantValue.MESSAGEINFO,new String[]{"message","time","state"},"peopleIndex=?",new String[]{peopleIndex},null,null,null);
                if (cursor != null && cursor.moveToFirst()) {
                    MessageRecord record = new MessageRecord();
                    String message = cursor.getString(cursor.getColumnIndex("message"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String state = cursor.getString(cursor.getColumnIndex("state"));
                    record.setContent(message);
                    record.setTime(time);
                    record.setState(state);
                    messageRecord.add(record);
                }
            }while (query.moveToNext());
            query.close();
        }
        return messageRecord;
    }

    /**
     * @return 返回一个团队的所有索引
     */
    public List getAllDeviceIndex(String peopleName){
        if (allPeopleList == null) {
            allPeopleList = new ArrayList();
        }else{allPeopleList.clear();}
        Cursor query = null;
        if (peopleName == null){
            query = db.query(ConstantValue.TEAMINFO, new String[]{"deviceIndex"}, null,null, null, null, null);
        }else {
            query = db.query(ConstantValue.TEAMINFO, new String[]{"deviceIndex"}, "peopleName=?", new String[]{peopleName}, null, null, null);
        }
        if(query != null && query.moveToFirst()){
            do {
                String index = query.getString(query.getColumnIndex("deviceIndex"));
                allPeopleList.add(index);
            }while (query.moveToNext());
            query.close();
        }
        return allPeopleList;
    }

    /**
     * @return 返回一个团队的所有设备名和索引
     */
    public List getAllDeviceNameAndIndex(){
        if (allPeopleList == null) {
            allPeopleList = new ArrayList();
        }else{allPeopleList.clear();}
        Cursor query = db.query(ConstantValue.TEAMINFO, new String[]{"deviceName","deviceIndex","peopleName"}, null,null, null, null, null);
        if(query != null && query.moveToFirst()){
            do {
                String name = query.getString(query.getColumnIndex("deviceName"));
                String index = query.getString(query.getColumnIndex("deviceIndex"));
                String peopleName = query.getString(query.getColumnIndex("peopleName"));
                allPeopleList.add(index+BlueProfile.SPLIT+name+BlueProfile.SPLIT+peopleName);
            }while (query.moveToNext());
            query.close();
        }
        return allPeopleList;
    }

    /**
     * @return 返回所有发送的消息
     */
    public List<MessageInfo> getAllSendMessage(){
        if (allSendMessageList == null) {
            allSendMessageList = new ArrayList();
        }else{allSendMessageList.clear();}
        Cursor query = db.query(ConstantValue.MESSAGEINFO, new String[]{"message", "peopleIndex", "count", STATE, "time"}, null, null, null, null, null);
        if(query != null && query.moveToFirst()){
            do{
                String message = query.getString(query.getColumnIndex("message"));
                String index = query.getString(query.getColumnIndex("peopleIndex"));
                String count = query.getString(query.getColumnIndex("count"));
                String state = query.getString(query.getColumnIndex(STATE));
                String time = query.getString(query.getColumnIndex("time"));
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setMessage(message);
                messageInfo.setIndex(index);
                messageInfo.setCount(count);
                messageInfo.setState(state);
                messageInfo.setTime(time);
                allSendMessageList.add(messageInfo);
            }while (query.moveToNext());
            query.close();
        }
        return allSendMessageList;
    }

    /**
     * @param index 根据messageInfo表中拿到的index查询消息发送的人员
     * @return 返回消息发送给那些人员
     */
    public List<LinkState> getSendMessageState(String index){
        if (linkStateList == null) {
            linkStateList = new ArrayList();
        }else{linkStateList.clear();}
        Cursor query = db.query(ConstantValue.PERSONINDEX, new String[]{PEOPLENAME, STATE}, "peopleIndex=?", new String[]{index}, null, null, null);
        if(query != null && query.moveToFirst()){
            do{
                String name = query.getString(query.getColumnIndex(PEOPLENAME));
                String state = query.getString(query.getColumnIndex(STATE));
                LinkState link = new LinkState();
                link.setName(name);
                link.setState(state);
                linkStateList.add(link);
            }while (query.moveToNext());
            query.close();
        }
        return linkStateList;
    }

    /**
     * @return 返回地图位置需要的相关信息
     */
    public List getLocationInfo(){
        if (locationInfoList == null) {
            locationInfoList = new ArrayList();
        }else{locationInfoList.clear();}
        Cursor query = db.query(ConstantValue.TEAMINFO, new String[]{"deviceName", "deviceIndex", PEOPLENAME}, null, null, null, null, null);
        if (query != null && query.moveToFirst()) {
            do {
                String deviceName = query.getString(query.getColumnIndex("deviceName"));
                String index = query.getString(query.getColumnIndex("deviceIndex"));
                String name = query.getString(query.getColumnIndex(PEOPLENAME));
                LocationInfo locationInfo = new LocationInfo();
                locationInfo.setDeviceName(deviceName);
                locationInfo.setDeviceIndex(index);
                locationInfo.setPeopleName(name);
                locationInfoList.add(locationInfo);
            }while (query.moveToNext());
            query.close();
        }
        return locationInfoList;
    }

    /**
     * @param peopleName 发送信息接收人的名字
     * @return 返回发送给peopleName的所有信息
     */
    public List getPersonSendMessage(String peopleName){
        if (personMessageList == null) {
            personMessageList = new ArrayList();
        }else{personMessageList.clear();}
        Cursor query1 = null;
        Cursor query = db.query(ConstantValue.PERSONINDEX, new String[]{"peopleIndex"}, PEOPLENAME+"=?", new String[]{peopleName}, null, null, null);
        if (query != null && query.moveToFirst()) {
            do {
                String index = query.getString(query.getColumnIndex("index"));
                query1 = db.query(ConstantValue.MESSAGEINFO, new String[]{"message", "time", "state"}, "peopleIndex=?", new String[]{index}, null, null, null);
                if (query1 != null && query1.moveToFirst()) {
                    do {
                        String message = query1.getString(query1.getColumnIndex("message"));
                        String time = query1.getString(query1.getColumnIndex("time"));
                        String state = query1.getString(query1.getColumnIndex("state"));
                        MessageInfo info = new MessageInfo();
                        info.setMessage(message);
                        info.setTime(time);
                        info.setState(state);
                    }while (query1.moveToNext());
                }
            }while(query.moveToNext());
            query.close();
            query1.close();
        }
        return personMessageList;
    }

    /**
     * @return 返回所有团队名称
     */
    public List<String> getTeamRecord(){
        if (allTeamList == null) {
            allTeamList = new ArrayList();
        }else {allTeamList.clear();}
        Cursor query = db.query(ConstantValue.TEAM, new String[]{"teamName"}, null, null, null, null, null, null);
        if (query != null && query.moveToFirst()) {
            do {
                String teamName = query.getString(query.getColumnIndex("teamName"));
                allTeamList.add(teamName);
            }while (query.moveToNext());
            query.close();
        }
        return allTeamList;
    }

    /**
     * @return 返回团名信息
     */
    public String getTeamRecordDetail(String teamName){
        Cursor query = db.query(ConstantValue.TEAM, new String[]{"teamSize","buildDate","place","trafficInfo"}, "teamName=?", new String[]{teamName}, null, null, null, null);
        if (query != null && query.moveToFirst()) {
            do {
                String teamSize = query.getString(query.getColumnIndex("teamSize"));
                String buildDate = query.getString(query.getColumnIndex("buildDate"));
                String place = query.getString(query.getColumnIndex("place"));
                String trafficInfo = query.getString(query.getColumnIndex("trafficInfo"));
                query.close();
                return teamSize+BlueProfile.SPLIT+buildDate+BlueProfile.SPLIT+place+BlueProfile.SPLIT+trafficInfo;
            }while (query.moveToNext());
        }
        return null;
    }

    /**
     * @return 返回团名信息
     */
    public List<String> getTeamInfoRecordDetail(boolean isCurrent,String teamName){
        if (allTeamListDetail == null) {
            allTeamListDetail = new ArrayList();
        }else {allTeamListDetail.clear();}
        Cursor query;
        if (isCurrent) {
            query = db.query(ConstantValue.TEAMINFO, new String[]{"deviceName","peopleName","phoneNumber","remark"}, "teamName=?", new String[]{teamName}, null, null, null, null);
        } else {
            query = db.query(ConstantValue.TEAMINFOHISTORY, new String[]{"deviceName","peopleName","phoneNumber","remark"}, "teamName=?", new String[]{teamName}, null, null, null, null);
        }
        if (query != null && query.moveToFirst()) {
            do {
                String deviceName = query.getString(query.getColumnIndex("deviceName"));
                String peopleName = query.getString(query.getColumnIndex("peopleName"));
                String phoneNumber = query.getString(query.getColumnIndex("phoneNumber"));
                String remark = query.getString(query.getColumnIndex("remark"));
                allTeamListDetail.add( peopleName+BlueProfile.SPLIT+phoneNumber+BlueProfile.SPLIT+remark+BlueProfile.SPLIT+deviceName);
            }while (query.moveToNext());
            query.close();
        }
        return allTeamListDetail;
    }

    /**
     * @param values
     * 添加团队
     */
    public void insertTeam(ContentValues values){
        db.insert(ConstantValue.TEAM, null, values);
    }

    /**
     * @param values
     * 添加团队人员信息
     */
    public void insertTeamInfo(ContentValues values){
        db.insert(ConstantValue.TEAMINFO,null,values);
    }

    /**
     * @param values
     * 向设备端请求时记录
     */
    public void insertLinkRecord(ContentValues values){
        db.insert(ConstantValue.LINKRECORD,null,values);
    }

    /**
     * @param index 自定义的设备序号
     */
    public void updataDeviceStatus(ContentValues values,String index){
        db.update(ConstantValue.TEAMINFO,values,"deviceIndex=?",new String[]{index});
    }

    public void savaDataToHistory(){
        Cursor history = db.query(ConstantValue.TEAMINFO, null, null, null, null, null, null);
        ContentValues values = new ContentValues();
        if (history.moveToFirst()){
            do {
                values.put("teamName",history.getString(history.getColumnIndex("teamName")));
                values.put("deviceName",history.getString(history.getColumnIndex("deviceName")));
                values.put("peopleName",history.getString(history.getColumnIndex("peopleName")));
                values.put("phoneNumber",history.getString(history.getColumnIndex("phoneNumber")));
                values.put("remark",history.getString(history.getColumnIndex("remark")));
                db.insert(ConstantValue.TEAMINFOHISTORY,null,values);
            }while (history.moveToNext());
        }
    }

    /**
     * 一次行程结束后删除团档中不需要保存的数据
     */
    public void clearJourneyData(){
        db.delete(ConstantValue.TEAMINFO,null,null);
        db.delete(ConstantValue.MESSAGEINFO,null,null);
        db.delete(ConstantValue.PERSONINDEX,null,null);
        db.delete(ConstantValue.LINKRECORD,null,null);
    }

    /**
     * 清楚一个团档数据
     */
    public int clearJourneyTeamData(boolean isCurrent,String teamName){
        int delNum;
        if (isCurrent) {
            delNum = db.delete(ConstantValue.TEAMINFO, null, null);
            db.delete(ConstantValue.TEAM,"teamName=?",new String[]{teamName});
        }else{
            delNum = db.delete(ConstantValue.TEAMINFOHISTORY, "teamName=?", new String[]{teamName});
            db.delete(ConstantValue.TEAM, "teamName=?", new String[]{teamName});
        }
        return delNum;
    }

    private ProgressDialog progressDialog;
    public void showDialog(Activity activity,String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dialogChangMessage(String message){
        progressDialog.setMessage(message);
    }

    public void dismissDialog() {
        if (progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }
}
