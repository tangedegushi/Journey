package com.rollup.journey.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;
import com.rollup.journey.activity.BluetoothActivity;
import com.rollup.journey.activity.MainActivity;
import com.rollup.journey.activity.MapActivity;
import com.rollup.journey.utils.BlueProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zq on 2016/12/17.
 */

public class Guide_Viewpager extends PagerAdapter {

    private int[] flashPictrue = {R.mipmap.guide_1,R.mipmap.guide_2,R.mipmap.guide_3};
    private Context mContext;
    public Guide_Viewpager(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return flashPictrue.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.guide_item, null);
        ImageView img = (ImageView) inflate.findViewById(R.id.flash_item_img);
        img.setBackgroundResource(flashPictrue[position]);
        TextView btn = (TextView) inflate.findViewById(R.id.flash_item_btn);
        if (position == flashPictrue.length-1) {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(mContext, BluetoothActivity.class);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                }
            });
        }
        container.addView(inflate);

        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
