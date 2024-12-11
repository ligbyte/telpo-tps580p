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

class StatisticsRecordListAdapter(data: List<ConsumeRecordListBean.ResultsDTO>?) :
    BaseQuickAdapter<ConsumeRecordListBean.ResultsDTO, BaseViewHolder>(R.layout.item_bill, data as MutableList<ConsumeRecordListBean.ResultsDTO>?) {
    @SuppressLint("DefaultLocale")
    override fun convert(helper: BaseViewHolder, item: ConsumeRecordListBean.ResultsDTO) {

        helper.setText(R.id.tvName,item.fullName)
            .setText(R.id.tvPhone,item.userTel)
//            .setText(R.id.tvFeeType,item.feeType)
//            .setText(R.id.tvConsumeMethod,item.consumeMethod)
            .setText(R.id.tvAmount,"￥"+item.bizAmount.toString())
            .setText(R.id.tvPayTime,item.bizDate)
        if ("1" == item.feeType){
            helper.setText(R.id.tvFeeType,"早餐")
        }else if ("2" == item.feeType){
            helper.setText(R.id.tvFeeType,"午餐")
        }else if ("3" == item.feeType){
            helper.setText(R.id.tvFeeType,"晚餐")
        }
        if ("10" == item.consumeMethod){
            helper.setText(R.id.tvConsumeMethod,"刷脸消费")
        }else if ("20" == item.consumeMethod){
            helper.setText(R.id.tvConsumeMethod,"刷卡消费")
        }else if ("30" == item.consumeMethod){
            helper.setText(R.id.tvConsumeMethod,"二维码消费")
        }else if ("40" == item.consumeMethod){
            helper.setText(R.id.tvConsumeMethod,"支付宝消费")
        }else if ("50" == item.consumeMethod){
            helper.setText(R.id.tvConsumeMethod,"微信消费")
        }
    }
}