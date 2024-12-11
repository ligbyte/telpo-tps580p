package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName
import com.stkj.cashier.bean.TakeMealsListResult.DataDTO
import com.stkj.cashier.bean.TakeMealsListResult.DataDTO.FoodListDTO

class TakeMealsListResult {
    @SerializedName("Code")
    var code: Int? = null

    @SerializedName("Message")
    var message: String? = null

    @SerializedName("Data")
    var data: List<DataDTO>? = null

    @SerializedName("Card_Number")
    var cardNumber: String? = null

    @SerializedName("Full_Name")
    var fullName: String? = null

    @SerializedName("User_Tel")
    var userTel: String? = null

    @SerializedName("User_Face")
    var userFace: String? = null

    @SerializedName("Balance")
    var balance: String? = null

    @SerializedName("orderNumber")
    var orderNumber: Any? = null

    class DataDTO {
        @SerializedName("Card_Number")
        var cardNumber: String? = null

        @SerializedName("orderNumber")
        var orderNumber: String? = null

        @SerializedName("foodList")
        var foodList: List<FoodListDTO>? = null

        @SerializedName("Full_Name")
        var fullName: String? = null

        @SerializedName("User_Tel")
        var userTel: String? = null

        @SerializedName("takeCode")
        var takeCode: String? = null

        @SerializedName("User_Face")
        var userFace: String? = null

        @SerializedName("itemCancel")
        var itemCancel: Boolean = false

        @SerializedName("takeType")
        var takeType: Int = 0

        class FoodListDTO {
            @SerializedName("Number")
            var number: Int? = null

            @SerializedName("Time_dining")
            var timeDining: String? = null

            @SerializedName("Dish_name")
            var dishName: String? = null

            @SerializedName("Meal_Amount")
            var mealAmount: String? = null

            @SerializedName("Image")
            var image: String? = null
        }
    }
}