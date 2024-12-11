package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class ConsumeRefundListBean {
    @SerializedName("billList")
    var results: List<ResultsDTO>? = null

    @SerializedName("customerImg")
    var customerImg: String? = null

    @SerializedName("customerName")
    var customerName: String? = null

    @SerializedName("customerNo")
    var customerNo: String? = null

    @SerializedName("customerTel")
    var customerTel: String? = null

    class ResultsDTO {
        @SerializedName("billFee")
        var billFee = 0.0

        @SerializedName("billDate")
        var billDate: String? = null


        @SerializedName("billId")
        var billId: String? = null

        @SerializedName("billStatus")
        var billStatus: Int? = null

        @SerializedName("billType")
        var billType: Int? = null
    }
}