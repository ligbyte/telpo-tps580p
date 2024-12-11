package com.stkj.cashier.bean

import com.google.gson.annotations.SerializedName

class IntervalCardTypeBean {
    @SerializedName("OneTime")
    var oneTime: String? = null

    @SerializedName("TwoTime")
    var twoTime: String? = null

    @SerializedName("ThreeTime")
    var threeTime: String? = null

    @SerializedName("FourTime")
    var fourTime: String? = null

    @SerializedName("F_OneMoney")
    private var fOnemoney = 0.0

    @SerializedName("F_OneSecondMoney")
    private var fOnesecondmoney = 0.0

    @SerializedName("F_OneThirdMoney")
    private var fOnethirdmoney = 0.0

    @SerializedName("F_TwoMoney")
    private var fTwomoney = 0.0

    @SerializedName("F_TwoSecondMoney")
    private var fTwosecondmoney = 0.0

    @SerializedName("F_TwoThirdMoney")
    private var fTwothirdmoney = 0.0

    @SerializedName("F_ThreeMoney")
    private var fThreemoney = 0.0

    @SerializedName("F_ThreeSecondMoney")
    private var fThreesecondmoney = 0.0

    @SerializedName("F_ThreeThirdMoney")
    private var fThreethirdmoney = 0.0

    @SerializedName("F_FourMoney")
    private var fFourmoney = 0.0

    @SerializedName("F_FourSecondMoney")
    private var fFoursecondmoney = 0.0

    @SerializedName("F_FourThirdMoney")
    private var fFourthirdmoney = 0.0

    @SerializedName("CardType")
    var cardType: String? = null
    fun getfOnemoney(): Double {
        return fOnemoney
    }

    fun setfOnemoney(fOnemoney: Double) {
        this.fOnemoney = fOnemoney
    }

    fun getfOnesecondmoney(): Double {
        return fOnesecondmoney
    }

    fun setfOnesecondmoney(fOnesecondmoney: Double) {
        this.fOnesecondmoney = fOnesecondmoney
    }

    fun getfOnethirdmoney(): Double {
        return fOnethirdmoney
    }

    fun setfOnethirdmoney(fOnethirdmoney: Double) {
        this.fOnethirdmoney = fOnethirdmoney
    }

    fun getfTwomoney(): Double {
        return fTwomoney
    }

    fun setfTwomoney(fTwomoney: Double) {
        this.fTwomoney = fTwomoney
    }

    fun getfTwosecondmoney(): Double {
        return fTwosecondmoney
    }

    fun setfTwosecondmoney(fTwosecondmoney: Double) {
        this.fTwosecondmoney = fTwosecondmoney
    }

    fun getfTwothirdmoney(): Double {
        return fTwothirdmoney
    }

    fun setfTwothirdmoney(fTwothirdmoney: Double) {
        this.fTwothirdmoney = fTwothirdmoney
    }

    fun getfThreemoney(): Double {
        return fThreemoney
    }

    fun setfThreemoney(fThreemoney: Double) {
        this.fThreemoney = fThreemoney
    }

    fun getfThreesecondmoney(): Double {
        return fThreesecondmoney
    }

    fun setfThreesecondmoney(fThreesecondmoney: Double) {
        this.fThreesecondmoney = fThreesecondmoney
    }

    fun getfThreethirdmoney(): Double {
        return fThreethirdmoney
    }

    fun setfThreethirdmoney(fThreethirdmoney: Double) {
        this.fThreethirdmoney = fThreethirdmoney
    }

    fun getfFourmoney(): Double {
        return fFourmoney
    }

    fun setfFourmoney(fFourmoney: Double) {
        this.fFourmoney = fFourmoney
    }

    fun getfFoursecondmoney(): Double {
        return fFoursecondmoney
    }

    fun setfFoursecondmoney(fFoursecondmoney: Double) {
        this.fFoursecondmoney = fFoursecondmoney
    }

    fun getfFourthirdmoney(): Double {
        return fFourthirdmoney
    }

    fun setfFourthirdmoney(fFourthirdmoney: Double) {
        this.fFourthirdmoney = fFourthirdmoney
    }
}