package com.rollup.journey.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zq on 2017/1/4.
 */

public class BlueProfile {

    public static String SPLIT = "my_cut_split";

    private List<byte[]> byteArray = new ArrayList<>();

    private BlueProfile() {}
    private static class newInstance{
        private static BlueProfile blueProfile = new BlueProfile();
    }
    public static BlueProfile getInstance(){return newInstance.blueProfile;}

    /**
     * @param bytes 蓝牙请求数据内容
     * @return
     */
    public List<byte[]> requestData(byte[] bytes){
        byteArray.clear();
        int bagLength = bytes.length/16;
        if (bytes.length%16==0){
            fullBagData(bytes, bagLength,1);
        }else{
            fullBagData(bytes, bagLength,2);
            int lastDataLength = (bytes.length%16)+4;
            byte[] bt = new byte[lastDataLength];
            bt[0] = (byte) 0xF8;
            bt[1] = (byte) (bagLength+1);
            bt[2] = (byte) bagLength;
            bt[3] = (byte) (lastDataLength-4);
            int startByte = 16*bagLength;
            for (int i = 0; i < lastDataLength-4; i++) {
                bt[4+i] = bytes[startByte+i];
            }
            byteArray.add(bt);
        }
        return byteArray;
    }

    //整包添加数据
    private void fullBagData(byte[] bytes, int bagLength,int tag) {
        for (int i = 0; i < bagLength; i++) {
            byte[] bt = new byte[20];
            bt[0] = (byte) 0xF8;
            if (tag == 1){
                bt[1] = (byte) bagLength;
            }else{
                bt[1] = (byte) (bagLength+1);
            }
            bt[2] = (byte) i;
            bt[3] = 16;
            for (int j = 0; j < 16; j++) {
                bt[4+j] = bytes[i*16+j];
            }
            byteArray.add(bt);
        }
    }

    //数据内容组拼:单个,data不传null传""
    public byte[] contentReal(int flag,String data){
        byte[] bytes = data.getBytes();
        byte[] bt = new byte[bytes.length+2];
        bt[0] = (byte)flag;
        bt[1] = (byte)(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            bt[2+i] = bytes[i];
        }
        return bt;
    }

    /**
     * @param listDevice 设备序号和设备ID组拼集合
     * @return
     */
    //同时添加多个设备
    public byte[] contentAddMulDevice(List<String> listDevice){
        if (listDevice == null || listDevice.isEmpty()) {
            return null;
        }
        byte[] bt = new byte[6*(listDevice.size())+4];
        bt[0] = 0x03;
        bt[1] = (byte) (bt.length-2);
        String[] str1 = listDevice.get(0).split(SPLIT);
        int deviceIndex1 = Integer.parseInt(str1[0]);
        bt[2] = (byte) deviceIndex1;
        String[] str2 = listDevice.get(listDevice.size()-1).split(SPLIT);
        int deviceIndex2 = Integer.parseInt(str2[0]);
        bt[3] = (byte) deviceIndex2;
        for (int i=0;i < listDevice.size();i++) {
            String[] str = listDevice.get(i).split(SPLIT);
            byte[] b = str[1].getBytes();
            for (int j = 0; j < b.length; j++) {
                bt[4+6*i+j] = b[j];
            }
        }
        return bt;
    }

    /**
     * @param index 设备序号集合
     * @return
     */
    //连接测试多组设备
    public byte[] contentTestMulDevice(List<String> index){
        byte[] bt = new byte[index.size() + 2];
        bt[0] = 0x04;
        bt[1] = (byte) (bt.length-2);
        for (int i = 0; i < index.size(); i++) {
            bt[2+i] = (byte) Integer.parseInt(index.get(i));
        }
        return bt;
    }

    /**
     * @param index 设备序号集合
     * @return
     */
    //请求设备位置信息
    public byte[] contentAddPosition(List<String> index){
        byte[] bt = new byte[index.size() + 2];
        bt[0] = 0x06;
        bt[1] = (byte) (bt.length-2);
        for (int i = 0; i < index.size(); i++) {
            bt[2+i] = (byte) Integer.parseInt(index.get(i));
        }
        return bt;
    }

    /**
     * @param index 要发送消息设备的序号
     * @param message 要发送的消息
     * @return
     */
    //发送消息
    public byte[] contentSendMsg(List<String> index,String message){
        byte[] msg = null;
        try {
            msg = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bt = new byte[4 + index.size() + msg.length];
        bt[0] = 0x05;
        bt[1] = (byte) (index.size()+msg.length+2);
        bt[2] = (byte) index.size();
        bt[3] = (byte) (msg.length);
        for (int i = 0; i < index.size(); i++) {
            bt[4+i] = (byte) Integer.parseInt(index.get(i));
        }
        for (int i = 0; i < msg.length; i++) {
            bt[4+index.size()+i] =  msg[i];
        }
        return bt;
    }

    /**
     * @param index 要校准设备序号集合
     * @return
     */
    //校准时间
    public byte[] contentSendTime(List<String> index){
        byte[] bt = new byte[index.size()+10];
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = format.format(new Date());
//        date = date.replaceAll("-|:| ", "");
//        byte[] bDate = date.getBytes();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String s = Integer.toHexString(year);
        String year1 = (s+"").substring(0,1);
        String year2 = (s+"").substring(1);
        bt[0] = 0x07;
        bt[1] = (byte) (bt.length-2);
        bt[2] = (byte) (Integer.parseInt(year2,16));
        bt[3] = (byte) (Integer.parseInt(year1,16));
        bt[4] = (byte) (month+1);
        bt[5] = (byte) (day);
        bt[6] = (byte) (hour);
        bt[7] = (byte) (minute);
        bt[8] = (byte) (second);
        bt[9] = (byte) (week);
        for (int i = 0; i < index.size(); i++) {
            bt[10+i] = (byte) (Integer.parseInt(index.get(i)));
        }
        return bt;
    }

    //清空设备列表信息
    public byte[] contentClearData(){
        byte[] bt = new byte[3];
        bt[0] = 0x08;
        bt[1] = 0x01;
        bt[2] = (byte) 0xFF;
        return bt;
    }

    public byte[] contentMd5(String teamName) {
        byte[] teamNameMD5 = new byte[18];
        byte[] md5Byte = null;
        if (teamName != null && !teamName.equals("")) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5Byte = md5.digest(teamName.getBytes("UTF8"));

                teamNameMD5[0] = 0x02;
                teamNameMD5[1] = 0x10;
                for (int i = 0; i < 16; i++) {
                    teamNameMD5[2+i] = md5Byte[i];
                }
            } catch (NoSuchAlgorithmException e) {
            } catch (Exception e) {
            }
        }
        return teamNameMD5;
    }

}
