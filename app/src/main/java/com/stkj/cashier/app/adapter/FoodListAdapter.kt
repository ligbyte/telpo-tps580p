package com.stkj.cashier.app.adapter

import com.stkj.cashier.bean.Bean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import android.annotation.SuppressLint
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stkj.cashier.R
import com.stkj.cashier.bean.TakeMealsListBean
import com.stkj.cashier.bean.TakeMealsListResult

class FoodListAdapter(data: List<TakeMealsListResult.DataDTO.FoodListDTO>?) :
    BaseQuickAdapter<TakeMealsListResult.DataDTO.FoodListDTO, BaseViewHolder>(R.layout.item_meal_food, data as MutableList<TakeMealsListResult.DataDTO.FoodListDTO>?) {
    @SuppressLint("DefaultLocale")
    override fun convert(helper: BaseViewHolder, item: TakeMealsListResult.DataDTO.FoodListDTO) {
        helper.setText(R.id.tvFoodName,item.dishName)
        Glide.with(context).load(item.image)
            .placeholder(R.mipmap.ic_fail)
            .error(R.mipmap.ic_fail)
            .into(helper.getView(R.id.ivFoodImage))
    }

}