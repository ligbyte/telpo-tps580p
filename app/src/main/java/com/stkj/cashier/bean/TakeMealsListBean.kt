package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class TakeMealsListBean {
    @SerializedName("Card_Number")
    var cardNumber: String? = null

    @SerializedName("orderNumber")
    var orderNumber: String? = null

    @SerializedName("Number")
    var number: Int? = null

    @SerializedName("Time_dining")
    var timeDining: String? = null

    @SerializedName("Dish_name")
    var dishName: String? = null

    @SerializedName("Meal_Amount")
    var mealAmount = 0.0

    @SerializedName("Image")
    var image: String? = null

    @SerializedName("Full_Name")
    var fullName: String? = null

    @SerializedName("User_Tel")
    var userTel: String? = null

    @SerializedName("User_Face")
    var userFace: String? = null
}