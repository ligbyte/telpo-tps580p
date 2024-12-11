package com.stkj.cashier.app.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import android.annotation.SuppressLint
import com.stkj.cashier.util.util.StringUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.bean.db.CompanyMemberdbEntity
import com.stkj.cashier.glide.GlideApp
import com.stkj.cashier.util.DesensitizedUtil

class CompanyMemberListAdapter(data: List<CompanyMemberdbEntity>?) :
    BaseQuickAdapter<CompanyMemberdbEntity, BaseViewHolder>(R.layout.item_face_info, data as MutableList<CompanyMemberdbEntity>?) {
    @SuppressLint("DefaultLocale")
    override fun convert(helper: BaseViewHolder, item: CompanyMemberdbEntity) {
        helper.setText(R.id.tvName, DesensitizedUtil.desensitizeName(item.fullName,1))
            .setText(R.id.tvCardNumber, DesensitizedUtil.desensitizePhoneNumber(item.phone,4))
            .setText(R.id.tvCardType,item.accountType)
            .setText(R.id.tvDepNameType,item.depNameType)
            .setText(R.id.tvOpeningDate,item.openingDate)
       // helper.setImageBitmap(R.id.ivHeader,StringUtils.stringToBitmap(item.imgData))

        GlideApp.with(App.applicationContext)
            .load(item.imgData)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.mipmap.ic_fail)
            .into(helper.getView(R.id.ivHeader))

        if (item.result==20){
            helper.setText(R.id.tvResult,"入库成功")
            helper.setTextColorRes(R.id.tvResult,R.color.text_3)
        }else{
            helper.setText(R.id.tvResult,"入库失败("+(item.result?:"-")+")")
            helper.setTextColorRes(R.id.tvResult,R.color.color_ff3c30)
        }
    }
}