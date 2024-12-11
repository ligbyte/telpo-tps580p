package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName
import com.stkj.cashier.bean.db.CompanyMemberdbEntity

class CompanyMemberBean {
    @SerializedName("Results")
    var results: List<CompanyMemberdbEntity>? = null

    @SerializedName("totalCount")
    var totalCount: Int? = null

    @SerializedName("totalPage")
    var totalPage: Int? = null

    @SerializedName("pageIndex")
    var pageIndex: Int? = null

    @SerializedName("pageSize")
    var pageSize: Int? = null

//    class ResultsDTO {
//        @SerializedName("Unique_number")
//        var uniqueNumber: String? = null
//
//        @SerializedName("Card_state")
//        var cardState: Int? = null
//
//        @SerializedName("UID")
//        var uid: String? = null
//
//        @SerializedName("Full_Name")
//        var fullName: String? = null
//
//        @SerializedName("DepNameType")
//        var depNameType: String? = null
//
//        @SerializedName("IdentityCard")
//        var identityCard: String? = null
//
//        @SerializedName("Phone")
//        var phone: String? = null
//
//        @SerializedName("UserNumber")
//        var userNumber: String? = null
//
//        @SerializedName("Card_Number")
//        var cardNumber: String? = null
//
//        @SerializedName("CardType")
//        var cardType: String? = null
//
//        @SerializedName("Balance")
//        var balance = 0.0
//
//        @SerializedName("Opening_date")
//        var openingDate: String? = null
//
//        @SerializedName("Limit_times1")
//        var limitTimes1: Int? = null
//
//        @SerializedName("Limit_times2")
//        var limitTimes2: Int? = null
//
//        @SerializedName("Limit_times3")
//        var limitTimes3: Int? = null
//
//        @SerializedName("Limit_times4")
//        var limitTimes4: Int? = null
//
//        @SerializedName("Consumption_quota")
//        var consumptionQuota = 0.0
//
//        @SerializedName("imgData")
//        var imgData: String? = null
//    }
}