package com.stkj.cashier.bean.db;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class CompanyMemberdbEntity {
    /** id */
    @Id(autoincrement = true)
    private Long id;
    @SerializedName("Unique_number")
    private String uniqueNumber;
    @SerializedName("Card_state")
    private Integer cardState;
    @SerializedName("UID")
    private String uid;
    @SerializedName("Full_Name")
    private String fullName;
    @SerializedName("DepNameType")
    private String depNameType;
    @SerializedName("IdentityCard")
    private String identityCard;
    @SerializedName("Phone")
    private String phone;


    @SerializedName("UserNumber")
    @Unique
    private String userNumber;
    @SerializedName("Card_Number")
    private String cardNumber;
    @SerializedName("CardType")
    private String cardType;
    @SerializedName("Balance")
    private double balance;
    @SerializedName("Opening_date")
    private String openingDate;
    @SerializedName("Limit_times1")
    private Integer limitTimes1;
    @SerializedName("Limit_times2")
    private Integer limitTimes2;
    @SerializedName("Limit_times3")
    private Integer limitTimes3;
    @SerializedName("Limit_times4")
    private Integer limitTimes4;
    @SerializedName("Consumption_quota")
    private double consumptionQuota;
    @SerializedName("imgData")
    private String imgData;
    @SerializedName("AccountType")
    private String AccountType;
    @SerializedName("callBack")
    private Boolean callBack;

    private Integer result;
    private String faceToken;

    @Generated(hash = 746439628)
    public CompanyMemberdbEntity(Long id, String uniqueNumber, Integer cardState,
            String uid, String fullName, String depNameType, String identityCard,
            String phone, String userNumber, String cardNumber, String cardType,
            double balance, String openingDate, Integer limitTimes1,
            Integer limitTimes2, Integer limitTimes3, Integer limitTimes4,
            double consumptionQuota, String imgData, String AccountType,
            Boolean callBack, Integer result, String faceToken) {
        this.id = id;
        this.uniqueNumber = uniqueNumber;
        this.cardState = cardState;
        this.uid = uid;
        this.fullName = fullName;
        this.depNameType = depNameType;
        this.identityCard = identityCard;
        this.phone = phone;
        this.userNumber = userNumber;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.balance = balance;
        this.openingDate = openingDate;
        this.limitTimes1 = limitTimes1;
        this.limitTimes2 = limitTimes2;
        this.limitTimes3 = limitTimes3;
        this.limitTimes4 = limitTimes4;
        this.consumptionQuota = consumptionQuota;
        this.imgData = imgData;
        this.AccountType = AccountType;
        this.callBack = callBack;
        this.result = result;
        this.faceToken = faceToken;
    }

    @Generated(hash = 1642368436)
    public CompanyMemberdbEntity() {
    }


    public String getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public Integer getCardState() {
        return cardState;
    }

    public void setCardState(Integer cardState) {
        this.cardState = cardState;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepNameType() {
        return depNameType;
    }

    public void setDepNameType(String depNameType) {
        this.depNameType = depNameType;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public Integer getLimitTimes1() {
        return limitTimes1;
    }

    public void setLimitTimes1(Integer limitTimes1) {
        this.limitTimes1 = limitTimes1;
    }

    public Integer getLimitTimes2() {
        return limitTimes2;
    }

    public void setLimitTimes2(Integer limitTimes2) {
        this.limitTimes2 = limitTimes2;
    }

    public Integer getLimitTimes3() {
        return limitTimes3;
    }

    public void setLimitTimes3(Integer limitTimes3) {
        this.limitTimes3 = limitTimes3;
    }

    public Integer getLimitTimes4() {
        return limitTimes4;
    }

    public void setLimitTimes4(Integer limitTimes4) {
        this.limitTimes4 = limitTimes4;
    }

    public double getConsumptionQuota() {
        return consumptionQuota;
    }

    public void setConsumptionQuota(double consumptionQuota) {
        this.consumptionQuota = consumptionQuota;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

    public String getAccountType() {
        return this.AccountType;
    }

    public void setAccountType(String AccountType) {
        this.AccountType = AccountType;
    }

    public Boolean getCallBack() {
        return this.callBack;
    }

    public void setCallBack(Boolean callBack) {
        this.callBack = callBack;
    }


}
