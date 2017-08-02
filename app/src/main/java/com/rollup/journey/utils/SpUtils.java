package com.rollup.journey.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by zq on 2016/12/16.
 */

public class SpUtils {

    private static SharedPreferences journeyTeam;
    private static Toast toast;

    public static boolean putString(Context context,String key,String value){
        journeyTeam = context.getSharedPreferences("journeyTeam", Context.MODE_PRIVATE);
        return journeyTeam.edit().putString(key,value).commit();
    }

    public static String getString(Context context,String key,String def){
        journeyTeam = context.getSharedPreferences("journeyTeam", Context.MODE_PRIVATE);
        return journeyTeam.getString(key, def);
    }

    public static boolean clearString(Context context,String key){
        journeyTeam = context.getSharedPreferences("journeyTeam", Context.MODE_PRIVATE);
        return journeyTeam.edit().remove(key).commit();
    }

    public static void showToast(Context context,String content){
        if (toast == null){
            toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        }else {
            toast.setText(content);
        }
        toast.show();
    }
}
