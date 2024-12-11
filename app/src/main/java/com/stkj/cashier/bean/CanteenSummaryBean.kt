package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName
import com.stkj.cashier.bean.CanteenSummaryBean.FeeTypeListDTO
import com.stkj.cashier.bean.CanteenSummaryBean.ConsumeMethodListDTO

class CanteenSummaryBean {
    @SerializedName("feeTypeList")
    var feeTypeList: List<FeeTypeListDTO>? = null

    @SerializedName("consumeMethodList")
    var consumeMethodList: List<ConsumeMethodListDTO>? = null

    class FeeTypeListDTO {
        @SerializedName("value")
        var value: Int? = null

        @SerializedName("key")
        var key: String? = null
    }

    class ConsumeMethodListDTO {
        @SerializedName("key1")
        var key1: String? = null

        @SerializedName("value")
        var value: Float? = null

        @SerializedName("key")
        var key: String? = null
    }
}