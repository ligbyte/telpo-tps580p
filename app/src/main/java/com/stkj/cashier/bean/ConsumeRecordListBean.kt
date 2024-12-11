package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class ConsumeRecordListBean {
    @SerializedName("Results")
    var results: List<ResultsDTO>? = null

    @SerializedName("totalCount")
    var totalCount: Int? = null

    @SerializedName("totalPage")
    var totalPage: Int? = null

    @SerializedName("pageIndex")
    var pageIndex: Int? = null

    @SerializedName("pageSize")
    var pageSize: Int? = null

    class ResultsDTO {
        @SerializedName("Card_Number")
        var cardNumber: String? = null

        @SerializedName("bizDate")
        var bizDate: String? = null

        @SerializedName("Full_Name")
        var fullName: String? = null

        @SerializedName("bizAmount")
        var bizAmount = 0.0

        @SerializedName("status")
        var status: Int? = null

        @SerializedName("Id")
        var id: String? = null

        @SerializedName("User_Tel")
        var userTel: String? = null
        @SerializedName("feeType")
        var feeType: String? = null
        @SerializedName("consumeMethod")
        var consumeMethod: String? = null
    }
}