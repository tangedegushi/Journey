package com.rollup.journey.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.adapter.Map_Gridview;
import com.rollup.journey.bean.MapState;
import com.rollup.journey.customview.TitleView;
import com.rollup.journey.utils.BlueProfile;
import com.rollup.journey.utils.BlueProfileBackData;
import com.rollup.journey.utils.DbEngine;
import com.rollup.journey.utils.SpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoogleMapActivity extends BlueBaseActivity implements OnMapReadyCallback,View.OnClickListener,AdapterView.OnItemClickListener {

    private GoogleMap googleMap;
    private MapFragment mapFragment;

    private List<String> allDeviceIndex;
    private List<String> listIndex = new ArrayList<>();
    private List<MapState> listIndexState = new ArrayList<>();
    private int indexSize;
    private int currentIndex = 0;
    private Map_Gridview adapter;
    private boolean isRefresh = false;
    private String[] split;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_map);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        allDeviceIndex = DbEngine.getInstance(this).getAllDeviceNameAndIndex();
        indexSize = allDeviceIndex.size();
        initView();
        requestLongitudeLatitude();
        DbEngine.getInstance(this).showDialog(this,getResources().getString(R.string.str_position_loading));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_google_map;
    }

    private void initView() {
        TitleView titleView = (TitleView) findViewById(R.id.map1_title);
        titleView.setOnClickListenerLeft(this);
        GridView gridView = (GridView) findViewById(R.id.map1_gridView);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

//        LatLng sydney = new LatLng(-33.867, 151.206);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
//        googleMap.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
//                .position(sydney));
//
//        LatLng sydney1 = new LatLng(-33.567, 151.006);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney1, 13));
//
//        googleMap.addMarker(new MarkerOptions()
//                .title("myself")
//                .snippet("this is in china.")
//                .position(sydney1));
//        addMarkerPoint();

        requestPermision();
    }

    private void requestPermision() {
        if(ContextCompat.checkSelfPermission(GoogleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GoogleMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        }else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    public void addMarkerPoint(double latitude,double longitude,String index,String name){
        LatLng sydney1 = new LatLng(latitude,longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney1, 13));

        googleMap.addMarker(new MarkerOptions()
                .title("zzq")
                .snippet("this is myself.")
                .position(sydney1));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermision();
                } else {
                    SpUtils.showToast(this,getResources().getString(R.string.str_permission_refuse));
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapFragment!=null){
            mapFragment = null;
        }
        if(googleMap != null){
            googleMap = null;
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
        final String index = mapState.getState();
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
                                DbEngine.getInstance(GoogleMapActivity.this).showDialog(GoogleMapActivity.this,"正在刷新位置...");
                                List<String> list = new ArrayList<String>();
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
        Intent intent = new Intent(GoogleMapActivity.this,MapRecordActivity.class);
        intent.putExtra("name",split[1]);
        intent.putExtra("index",split[0]);
        intent.putExtra("peopleName",split[2]);
        startActivity(intent);
    }

    private void requestLongitudeLatitude() {
        listIndex.clear();
        if (currentIndex < indexSize){
            split = allDeviceIndex.get(currentIndex).split(BlueProfile.SPLIT);
            listIndex.add(split[0]);
            currentIndex++;
        }
        if (!listIndex.isEmpty()){
            listBytes = BlueProfile.getInstance().requestData(BlueProfile.getInstance().contentAddPosition(listIndex));
            sendMessage();
        }else{
            DbEngine.getInstance(this).dismissDialog();
        }
    }

    @Override
    public void handlerBackMsg() {
        byte dataType = BlueProfileBackData.getInstance().getDataType();
        for (byte b : data) {
            Log.d("tag",(b&0xff)+"");
        }
        if (dataType == 0x06){
            if (data[1] == 0x01){
                String longitude = "";
                String latitude = "";
                for (int i = 8; 0 < i; i--) {
                    //将byte数值转int再转16进制
                    int intLongi;
                    int intLat;
                    if (data[i+2] < 0){
                        intLongi = 256 + data[i+2];
                    }else{intLongi = data[i+2];}
                    if (data[i+10] < 0) {
                        intLat = 256 + data[i+10];
                    }else{intLat = data[i+10];}
                    String l1 = Integer.toHexString(intLongi);
                    String l2 = Integer.toHexString(intLat);
                    if (intLongi<16){
                        longitude = longitude+"0"+l1;
                    }else{
                        longitude = longitude+l1;
                    }
                    if (intLat<16){
                        latitude = latitude+"0"+l2;
                    }else {
                        latitude = latitude+l2;
                    }
                }
                double lat = Long.parseLong(latitude,16)/1000.0;
                double lgi = Long.parseLong(longitude,16)/1000.0;
                Logger.d(lat/10000000.0+"..."+lgi/10000000.0+"..."+data[0]+"..."+split[1]);
                addMarkerPoint(lgi/10000000.0,lat/10000000.0,data[0]+"", split[1]+"");
                listIndexState.get(currentIndex-1).setState("1");
                adapter.setDataList(listIndexState);
            }else if (data[1] == 0x00){

            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = format.format(new Date());
            ContentValues values = new ContentValues();
            values.put("peopleName",split[2]);
            values.put("type",getResources().getString(R.string.str_main_text3));
            values.put("time",date);
            values.put("state",data[1]);
            DbEngine.getInstance(this).insertLinkRecord(values);
            if (!isRefresh){

                requestLongitudeLatitude();
            }else{
                DbEngine.getInstance(this).dismissDialog();
            }
        }
    }
}
