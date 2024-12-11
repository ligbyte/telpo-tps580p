package com.stkj.cashier.app.weigh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.cashier.util.util.LogUtils;
import com.google.gson.Gson;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/6/17
 */
public class KeyEventView extends EditText {
    public KeyEventView(Context context) {
        super(context);
    }

    public KeyEventView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyEventView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        LogUtils.e("按键" + new Gson().toJson(event));


        return super.dispatchKeyEventPreIme(event);
    }
}
