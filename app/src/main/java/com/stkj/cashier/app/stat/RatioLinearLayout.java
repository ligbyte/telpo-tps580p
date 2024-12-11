package com.stkj.cashier.app.stat;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.stkj.cashier.R;

/**
 * 比例布局工具
 */
public class RatioLinearLayout extends LinearLayout {

    private float mRatioWH = 1.0f;
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    public RatioLinearLayout(Context context) {
        super(context);
        readRatioAttr(context,null);
    }

    public RatioLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readRatioAttr(context,attrs);
    }

    public RatioLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readRatioAttr(context,attrs);
    }

    public void readRatioAttr(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.RatioLinearLayout);
        mRatioWH = a.getFloat(R.styleable.RatioLinearLayout_ratio_w_h, 1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measuredDimension(widthMeasureSpec,heightMeasureSpec);
        super.onMeasure(mWidthMeasureSpec, mHeightMeasureSpec);
    }

    public void measuredDimension(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatioWH <= 0) {
            mRatioWH = 1;
        }
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == View.MeasureSpec.EXACTLY) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            if (widthSize > 0) {
                int heightSize = (int) (widthSize * 1.0f / mRatioWH);
                mHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.EXACTLY);
            }
        } else if (heightMode == View.MeasureSpec.EXACTLY) {
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (heightSize > 0) {
                int widthSize = (int) (heightSize * mRatioWH);
                mWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.EXACTLY);
            }
        }
    }

    public int getWidthMeasureSpec() {
        return mWidthMeasureSpec;
    }

    public int getHeightMeasureSpec() {
        return mHeightMeasureSpec;
    }

    public void setRatioWH(float mRatioWH) {
        this.mRatioWH = mRatioWH;
    }
}