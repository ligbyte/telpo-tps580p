package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class CurrentTimeInfoBean {
    @SerializedName("total")
    var total: Int? = null

    @SerializedName("endOrder")
    var endOrder: String? = null

    @SerializedName("takeMeal")
    var takeMeal: Int? = null

    @SerializedName("end")
    var end: String? = null

    @SerializedName("feeType")
    var feeType: String? = null

    @SerializedName("begin")
    var begin: String? = null
}