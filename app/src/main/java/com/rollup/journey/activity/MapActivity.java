package com.rollup.journey.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Map_Gridview;
import com.rollup.journey.bean.MapState;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.ConstantValue;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends BlueBaseActivity implements LocationSource,AMapLocationListener,View.OnClickListener,AdapterView.OnItemClickListener{

    private OnLocationChangedListener mListener;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption clientOption;
    private AMap aMap;
    private List<String> allDeviceIndex;
    private List<String> listIndex = new ArrayList<>();
//    private List<String> deviceNameIndex = new ArrayList<>();
//    private List<String> peopleNameIndex = new ArrayList<>();
    private Map<String,String> index_peopleName = new HashMap<>();
    private List<MapState> listIndexState = new ArrayList<>();
    private int indexSize;
//    private int currentIndex = 0;
    private Map_Gridview adapter;
    private boolean isRefresh = false;
//    private String[] split;
    private SupportMapFragment mapFragment;
    //    private boolean isFirst =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (aMap == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            aMap = mapFragment.getMap();
        }

        //设置定位监听
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        mapSetting();

        allDeviceIndex = DbEngine.getInstance(this).getAllDeviceNameAndIndex();
        indexSize = allDeviceIndex.size();
        initView();
        requestLongitudeLatitude();
        if (indexSize != 0) {
            DbEngine.getInstance(this).showDialog(this,getResources().getString(R.string.str_position_loading));
        }
    }

    private void mapSetting() {
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_map;
    }

    private void initView() {
        TitleView titleView = (TitleView) findViewById(R.id.map_title);
        titleView.setOnClickListenerLeft(this);
        GridView gridView = (GridView) findViewById(R.id.map_gridView);
        for (String s : allDeviceIndex) {
            String[] split = s.split(BlueProfile.SPLIT);
            MapState mapState = new MapState();
            mapState.setIndex(split[0]);
            mapState.setState("0");
            listIndexState.add(mapState);
        }
        adapter = new Map_Gridview(this,listIndexState);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    //传入坐标绘制点
    private void addMarker(double latitude,double longitude,String index,String name){
        LatLng latLng = new LatLng(longitude,latitude);

        CoordinateConverter converter  = new CoordinateConverter();
        // CoordType.GPS 待转换坐标类型
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标点 DPoint类型
        converter.coord(latLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();

        Marker marker = aMap.addMarker(new MarkerOptions().
                position(desLatLng).
                title(index).
                snippet(name));
        marker.showInfoWindow();

    }

    //设置定位初始化及启动定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        Logger.d("activate");
        mListener = listener;

        if(locationClient == null){
            //初始化定位
            locationClient = new AMapLocationClient(this);
            //初始化定位参数
            clientOption = new AMapLocationClientOption();
            //设置定位回掉
            locationClient.setLocationListener(this);
            clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            clientOption.setOnceLocation(true);
            locationClient.setLocationOption(clientOption);
            locationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        Logger.d("deactivate");
        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            } else {
                String errText = getResources().getString(R.string.str_error_get_position_fail) + "," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                SpUtils.showToast(this,errText);
                Logger.e("AmapErr"+errText);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
        MapState mapState = (MapState) parent.getItemAtPosition(position);
        final String index = mapState.getIndex();
//        currentIndex = Integer.parseInt(index);
        String state = mapState.getState();
        if (state.equals("1")){
            openActivity(position);
        }else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.str_dialog_title_choose)
                    .setItems(R.array.map_choose, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //0刷新位置1查看记录
                            if (which == 0){
                                DbEngine.getInstance(MapActivity.this).showDialog(MapActivity.this,"正在刷新位置..."+index);
                                List<String> list = new ArrayList<>();
                                list.add(index);
                                listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentAddPosition(list));
                                sendMessage();
                                isRefresh = true;
                            } else{
                                openActivity(position);
                            }
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    private void openActivity(int position) {
        String s = allDeviceIndex.get(position);
        String[] split = s.split(BlueProfile.SPLIT);
        Intent intent = new Intent(MapActivity.this,MapRecordActivity.class);
        intent.putExtra("index",split[0]);
        intent.putExtra("name",split[1]);
        intent.putExtra("peopleName",split[2]);
        startActivity(intent);
    }

    private void requestLongitudeLatitude() {
//        listIndex.clear();
//        if (currentIndex < indexSize){
//            split = allDeviceIndex.get(currentIndex).split(BlueProfile.SPLIT);
//            listIndex.add(split[0]);
//            currentIndex++;
//        }
//        if (!listIndex.isEmpty()){
//            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentAddPosition(listIndex));
//            sendMessage();
//        }else{
//            DbEngine.getInstance(this).dismissDialog();
//        }
        String[] split;
        for (String s : allDeviceIndex) {
            split = s.split(BlueProfile.SPLIT);
            listIndex.add(split[0]);
//            deviceNameIndex.add(split[1]);
//            peopleNameIndex.add(split[2]);
            index_peopleName.put(split[0],split[2]);
        }
        listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentAddPosition(listIndex));
        sendMessage();
    }

    @Override
    public void handlerBackMsg() {
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        for (byte b : data) {
            Log.d("tag",(b&0xff)+"");
        }
        if (dataType == ConstantValue.FUNC_POSITION_MSG) {
        //item点击处理一个设备，否则处理多个设备
            if (isRefresh){
                if (data.length != 24){
                    Logger.d("返回内容数据长度不对:"+data.length);
                }else{
                    dealOneDevice(data);
                    adapter.setDataList(listIndexState);
                    isRefresh = false;
                }
            }else{
                if ((data.length%24) != 0){
                    Logger.d("返回内容数据长度不对"+data.length);
                }else{
                    int len = data.length/24;
                    byte[] oneData = new byte[24];
                    for (int i = 0; i < len; i++) {
                        for (int j = 0; j < 24; j++) {
                            oneData[j] = data[i*24+j];
                        }
                            dealOneDevice(oneData);
                    }
                    adapter.setDataList(listIndexState);
                }
            }
        }
        DbEngine.getInstance(this).dismissDialog();

            //item点击处理
//        if (!isRefresh){
//            DbEngine.getInstance(MapActivity.this).dialogChangMessage("正在刷新位置..."+(currentIndex+1));
//            requestLongitudeLatitude();
//        }else{
//            DbEngine.getInstance(this).dismissDialog();
//            isRefresh = false;
//        }
    }

    private void dealOneDevice(byte[] data) {
        Logger.d("the back data state"+data[0]+".."+data[1]+".."+data[2]);
        if (data[1] == ConstantValue.BACK_STATE_SUC){
            //命令执行成功
            if (data[2] == 0x01){
                //定位状态成功
                String longitude = "";
                String latitude = "";
                for (int i = 8; 0 < i; i--) {
                    //将byte数值转int再转16进制
                    String l1 = Integer.toHexString(data[i+7]&0xff);
                    String l2 = Integer.toHexString(data[i+15]&0xff);
                    if ((data[i+7]&0xff)<16){
                        longitude = longitude+"0"+l1;
                    }else{
                        longitude = longitude+l1;
                    }
                    if ((data[i+15]&0xff)<16){
                        latitude = latitude+"0"+l2;
                    }else {
                        latitude = latitude+l2;
                    }
                }
                double lat = Long.parseLong(latitude,16)/1000.0;
                double lgi = Long.parseLong(longitude,16)/1000.0;
                Logger.d(lat/10000000.0+"..."+lgi/10000000.0+"..."+data[0]+"..."+index_peopleName.get(data[0]+""));
                addMarker(lgi/10000000.0,lat/10000000.0,data[0]+"", index_peopleName.get(data[0]+""));
                setDeviceState(data[0]);
//                adapter.setDataList(listIndexState);
            }else{
                //定位状态失败
            }

        }else{
            //命令执行失败
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        ContentValues values = new ContentValues();
        values.put("peopleName",index_peopleName.get(data[0]+""));
        values.put("type",getResources().getString(R.string.str_main_text3));
        values.put("time",date);
        values.put("state",data[2]);
        DbEngine.getInstance(this).insertLinkRecord(values);
    }

    public void setDeviceState(int index){
        for (MapState mapState : listIndexState) {
            if (mapState.getIndex().equals(index+"")){
                mapState.setState("1");
                break;
            }
        }
    }
}
