package com.stkj.cashier.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.stkj.cashier.util.util.LogUtils;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/6/5
 */
public class LoadingView extends View {

    private Paint paint;
    private float progress = 0;

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LogUtils.e("LoadingView init");
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        startAnimation(rotateAnimation);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY);

        paint.setAlpha((int) (255 * progress));
        canvas.drawCircle(centerX, centerY, radius, paint);
        LogUtils.e("LoadingView onDraw");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animateProgress();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 清除动画以避免内存泄漏
        clearAnimation();
    }

    private void animateProgress() {
        progress += 0.01;
        if (progress > 1) {
            progress = 0;
        }
        LogUtils.e("LoadingView animateProgress"+progress);
        invalidate();
    }
}
