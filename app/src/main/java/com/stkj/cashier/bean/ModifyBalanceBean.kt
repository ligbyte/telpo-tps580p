package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class ModifyBalanceBean {
    @SerializedName("balance")
    var balance: String? = null

    @SerializedName("consumption_Mone")
    var consumptionMone = 0.0

    @SerializedName("full_name")
    var fullName: String? = null

    @SerializedName("bill_count")
    var billCount: String? = null

    @SerializedName("customerNo")
    var customerNo: String? = null

    @SerializedName("payNo")
    var payNo: String? = null
}