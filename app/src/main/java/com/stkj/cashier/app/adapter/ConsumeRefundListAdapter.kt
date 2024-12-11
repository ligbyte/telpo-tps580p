package com.stkj.cashier.app.adapter

import com.stkj.cashier.bean.Bean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.stkj.cashier.util.util.LogUtils
import com.stkj.cashier.util.util.StringUtils
import com.stkj.cashier.util.util.StringUtils.stringToBitmap
import com.stkj.cashier.util.util.Utils
import com.bumptech.glide.Glide
import com.stkj.cashier.R

import com.stkj.cashier.bean.ConsumeRecordListBean
import com.stkj.cashier.bean.ConsumeRefundListBean

class ConsumeRefundListAdapter(data: List<ConsumeRefundListBean.ResultsDTO>?) :
    BaseQuickAdapter<ConsumeRefundListBean.ResultsDTO, BaseViewHolder>(
        R.layout.item_refund_bill,
        data as MutableList<ConsumeRefundListBean.ResultsDTO>?
    ) {
    @SuppressLint("DefaultLocale")
    override fun convert(helper: BaseViewHolder, item: ConsumeRefundListBean.ResultsDTO) {
        LogUtils.d("退款订单类别"+item.billDate)

        helper
            .setText(R.id.tvAmount, "￥" + item.billFee.toString())
            .setText(R.id.tvPayTime, item.billDate)
        val view = helper.getView<TextView>(R.id.tvPaySuccess)

        ///** 待退款 */
        //WAIT_REFUND(0),
        ///** 退款成功 */
        //SUCCESS(1),
        ///** 退款失败 */
        //FAIL(2);

        if (item.billStatus == 3) {
            view.text = "支付成功"
            view.setTextColor(Color.parseColor("#00DC82"))
        }else if (item.billStatus == 0) {
            view.text = "退款中"
            view.setTextColor(Color.parseColor("#FFFFFF"))
        }else if (item.billStatus == 1) {
            view.text = "退款成功"
            view.setTextColor(Color.parseColor("#FA5151"))
        } else {
            view.text = "退款失败"
            view.setTextColor(Color.parseColor("#FA5151"))

        }

        var itemPosition = getItemPosition(item)
        if (itemPosition == mSelectedPosition) {
            helper.getView<LinearLayout>(R.id.llItem)
                .setBackgroundColor(Color.parseColor("#24ffffff"))
        } else {
            helper.getView<LinearLayout>(R.id.llItem).setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private var mSelectedPosition = -1 // 初始化为-1表示没有选中项
    // 构造函数和其他方法...

    // 构造函数和其他方法...
    fun setSelectedPosition(position: Int) {
        mSelectedPosition = position
        notifyDataSetChanged() // 通知更新列表
    }

    fun getSelectedPosition(): Int {
        return mSelectedPosition
    }
    fun getSelectedItem(): ConsumeRefundListBean.ResultsDTO {
        return getItem(mSelectedPosition)
    }
}