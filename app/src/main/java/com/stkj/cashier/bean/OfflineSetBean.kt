package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class OfflineSetBean {
    @SerializedName("F_Id")
    private var fId: String? = null

    @SerializedName("limitMoney")
    var limitMoney = 0.0

    @SerializedName("limitCount")
    var limitCount: Int? = null

    @SerializedName("machine_Number")
    var machineNumber: String? = null

    @SerializedName("F_CreatorTime")
    private var fCreatortime: String? = null

    @SerializedName("F_CompanyId")
    private var fCompanyid: String? = null
    fun getfId(): String? {
        return fId
    }

    fun setfId(fId: String?) {
        this.fId = fId
    }

    fun getfCreatortime(): String? {
        return fCreatortime
    }

    fun setfCreatortime(fCreatortime: String?) {
        this.fCreatortime = fCreatortime
    }

    fun getfCompanyid(): String? {
        return fCompanyid
    }

    fun setfCompanyid(fCompanyid: String?) {
        this.fCompanyid = fCompanyid
    }
}