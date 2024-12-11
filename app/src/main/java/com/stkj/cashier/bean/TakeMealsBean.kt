package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class TakeMealsBean {
    @SerializedName("Card_Number")
    var cardNumber: String? = null

    @SerializedName("Full_Name")
    var fullName: String? = null

    @SerializedName("Balance")
    var balance: String? = null

    @SerializedName("orderNumber")
    var orderNumber: String? = null


}