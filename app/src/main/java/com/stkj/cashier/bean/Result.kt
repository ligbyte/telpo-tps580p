package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class Result<T> {
    @SerializedName("Code")
    var code: Int? = null

    @SerializedName("Message")
    var message: String? = null

    @SerializedName("Data")
    var data: T? = null

    @SerializedName("Heartbeat_time")
    var heartbeatTime: String? = null

    @SerializedName("machine")
    var machine: String? = null

    @SerializedName("equipment_sector")
    var equipmentSector: Int? = null

    @SerializedName("sector_password")
    var sectorPassword: String? = null

    @SerializedName("autoSateTime")
    var autoSateTime: String? = null

    @SerializedName("autoEndTime")
    var autoEndTime: String? = null

    @SerializedName("autoState")
    var autoState: Int? = null
    @SerializedName("company")
    var company: String? = null

}