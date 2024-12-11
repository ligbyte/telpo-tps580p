package com.stkj.cashier.util

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import com.stkj.cashier.util.util.SizeUtils
import com.bumptech.glide.load.data.ExifOrientationStream
import com.stkj.cashier.R
import android.view.Gravity

import android.widget.FrameLayout




object PopupWindowUtil {
    fun showPopupWindow(
        context: Context,
        items: List<String>,
        orientation: Boolean,
        parentView: View
    ) {
        //创建对象

        //创建对象
        val popupWindow: PopupWindow = PopupWindow(context)
        var inflate: View = LayoutInflater.from(context).inflate(R.layout.layout_select_popup, null)
        val rgSelectPopup: RadioGroup = inflate.findViewById(R.id.rgSelectPopup)

        for (str in items) {
//            var radio: RadioButton =
//                LayoutInflater.from(context).inflate(R.layout.layout_select_radiobutton, null) as RadioButton
            var radioButton: RadioButton = RadioButton(context)
            val lytp = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,  SizeUtils.dp2px(48f))
//            lytp.gravity = Gravity.CENTER
            radioButton.layoutParams = lytp
            radioButton.text =str
            radioButton.textSize = 18f
            radioButton.setTextColor(context.resources.getColor(R.color.selector_color_screen_saver_radio))
            radioButton.setPadding(SizeUtils.dp2px(12f),0,0,0)
            radioButton.background = context.getDrawable(R.drawable.selector_screen_saver_radio)
            radioButton.gravity = Gravity.CENTER_VERTICAL
            radioButton.buttonDrawable = null
            rgSelectPopup.addView(radioButton)
        }
        //设置view布局
        //设置view布局
        popupWindow.contentView = inflate

        popupWindow.width = parentView.width
        //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
        //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
        popupWindow.isTouchable = true
        //设置背景透明
        //设置背景透明
        popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
        //点击空白处的时候让PopupWindow消失
        //点击空白处的时候让PopupWindow消失
        popupWindow.isOutsideTouchable = true
        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键，默认是false
        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键，默认是false
        popupWindow.isFocusable = false
        //设置dismiss事件

        //设置dismiss事件
        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {})
        var showing: Boolean = popupWindow.isShowing
        if (!showing) {
            if (orientation) {
                inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                var measuredHeight: Int = inflate.measuredHeight
                popupWindow.showAsDropDown(parentView,0,-(parentView.height+measuredHeight+24))
            } else {
                //show，并且可以设置位置
                popupWindow.showAsDropDown(parentView, 0, 24)
            }
        }

    }
}