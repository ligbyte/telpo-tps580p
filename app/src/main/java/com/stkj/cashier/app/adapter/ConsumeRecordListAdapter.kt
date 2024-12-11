package com.stkj.cashier.app.adapter

import com.stkj.cashier.bean.Bean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.stkj.cashier.util.util.StringUtils
import com.stkj.cashier.util.util.StringUtils.stringToBitmap
import com.stkj.cashier.util.util.Utils
import com.bumptech.glide.Glide
import com.stkj.cashier.R

import com.stkj.cashier.bean.ConsumeRecordListBean

class ConsumeRecordListAdapter(data: List<ConsumeRecordListBean.ResultsDTO>?) :
    BaseQuickAdapter<ConsumeRecordListBean.ResultsDTO, BaseViewHolder>(R.layout.item_main_pay, data as MutableList<ConsumeRecordListBean.ResultsDTO>?) {
    @SuppressLint("DefaultLocale")
    override fun convert(helper: BaseViewHolder, item: ConsumeRecordListBean.ResultsDTO) {
        helper.setText(R.id.tvName,item.fullName)
            .setText(R.id.tvPhone,item.userTel)
            .setText(R.id.tvAmount,"￥"+item.bizAmount.toString())
            .setText(R.id.tvPayTime,item.bizDate)
        if (item.status == 0){
            helper.getView<TextView>(R.id.tvPaySuccess).visibility = View.VISIBLE
            helper.getView<LinearLayout>(R.id.llPayWait).visibility = View.GONE
        }else{
            helper.getView<TextView>(R.id.tvPaySuccess).visibility = View.GONE
            helper.getView<LinearLayout>(R.id.llPayWait).visibility = View.VISIBLE
        }

    }
}