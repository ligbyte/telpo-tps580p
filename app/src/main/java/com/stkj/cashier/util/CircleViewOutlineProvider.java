package com.stkj.cashier.util;

import android.graphics.Outline;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;

public class CircleViewOutlineProvider extends ViewOutlineProvider {

    public CircleViewOutlineProvider() {

    }

    @Override
    public void getOutline(View view, Outline outline) {

        //864,648  横屏情况下，宽>高

        Log.d("Circle===","width:"+view.getWidth()+"==height:"+view.getHeight());

        //裁剪成一个圆形

        int left0 = (view.getWidth() - view.getHeight()) / 2;

        int top0 = 0;

        int right0 = left0 + view.getHeight() ;

        int bottom0 =  view.getHeight() ;

        outline.setOval(left0, top0, right0, bottom0);

    }

}
