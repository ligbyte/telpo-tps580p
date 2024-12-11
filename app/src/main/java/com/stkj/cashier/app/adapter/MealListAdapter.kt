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

class MealListAdapter(data: List<TakeMealsListResult.DataDTO>?) :
    BaseQuickAdapter<TakeMealsListResult.DataDTO, BaseViewHolder>(R.layout.item_meal_person, data as MutableList<TakeMealsListResult.DataDTO>?) {
    @SuppressLint("DefaultLocale")
    override fun convert(helper: BaseViewHolder, item: TakeMealsListResult.DataDTO) {
        helper.setText(R.id.tvName,item.fullName)
            .setText(R.id.tvPhone,item.userTel)
            .setText(R.id.takeCode,"(取餐号"+item.takeCode+")")
        Glide.with(context).load(item.userFace).into(helper.getView(R.id.ivHeader))
//        helper.getView<TextView>(R.id.tvTakeMeal).setOnClickListener()
//        addChildClickViewIds(R.id.tvTakeMeal)
        var rvFoodList = helper.getView<RecyclerView>(R.id.rvFoodList)
        var gridLayoutManager = GridLayoutManager(context,2)
        rvFoodList.layoutManager = gridLayoutManager
        var foodList = ArrayList<TakeMealsListResult.DataDTO.FoodListDTO>()
        for ((index ,item) in item.foodList?.withIndex()!!){
            for (i in 0 until item.number!!) {
                var food = TakeMealsListResult.DataDTO.FoodListDTO()
                food.dishName = item.dishName
                food.image = item.image
                foodList.add(food)
                //println(i) // 输出: 0 ~ 99
            }
        }
        rvFoodList.adapter = FoodListAdapter(foodList)
    }

}