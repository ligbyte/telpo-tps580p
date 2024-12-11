package com.stkj.cashier.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import android.annotation.SuppressLint
import android.widget.Button
import android.widget.ExpandableListView
import com.stkj.cashier.util.util.StringUtils
import com.stkj.cashier.R
import com.stkj.cashier.bean.db.CompanyMemberdbEntity

class SelectPopupListAdapter(data: List<String>?) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_select_popup, data as MutableList<String>?) {
    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.btSelect,item)
    }
}