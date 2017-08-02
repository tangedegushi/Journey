package com.rollup.journey.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.rollup.journey.R;

/**
 * Created by zq on 2016/12/16.
 */

public class TitleView extends LinearLayout {
    private static final String TAG = "TitleView";
    private final ImageView left;
    private final TextView title;
    private final ImageView right;

    public TitleView(Context context) {
        this(context,null);}

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.title_bar, this);
        left = (ImageView) view.findViewById(R.id.imageView);
        title = (TextView) view.findViewById(R.id.textView);
        right = (ImageView) view.findViewById(R.id.imageView2);

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.TitleStyle);
        int mLeft = ta.getResourceId(R.styleable.TitleStyle_img_left, 0);
        String mTitle = ta.getString(R.styleable.TitleStyle_text_title);
        int mRight = ta.getResourceId(R.styleable.TitleStyle_img_right,0);
        ta.recycle();

        if (mLeft != 0){
            left.setImageResource(mLeft);
        }
        if (mTitle != null){
            title.setText(mTitle);
        }
        if (mRight != 0){
            right.setImageResource(mRight);
        }
//        Logger.d(mTitle+mLeft+mRight);
    }

    public void setOnClickListenerLeft(OnClickListener listenerLeft){
        left.setOnClickListener(listenerLeft);
    }
    public void setOnClickListenerRight(OnClickListener listenerLeft){
        right.setOnClickListener(listenerLeft);
    }
    public void setTitleText(int titleText){
        title.setText(titleText);
    }
    public void setTitleText(String titleText){
        title.setText(titleText);
    }
}
