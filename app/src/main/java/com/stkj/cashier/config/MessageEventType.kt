package com.stkj.cashier.config

interface MessageEventType {
    companion object {
        const val OpenFacePassPay = 900
        const val CloseFacePassPay = 910
        const val ModeMessage = 1010
        const val HideModeMessage = 1020
        const val SettingMessage = 1030
        const val WifiMessage = 1040
        const val BatteryMessage = 2040 //电量
        const val SwitchStatics = 1050
        const val AmountNotice = 1060 //结算-金额消费
        const val AmountNotice2 = 1061//取消金额消费
        const val AmountNotice3 = 1062//定额模式消费
        const val AmountPayingNotice = 1064 //结算-金额消费(支付中)
        const val FaceToken = 1070
        const val AmountToken = 1071
        const val NumberToken = 1072
        const val PickUpToken = 1073 // 取餐模式 识别人脸
        const val PickUpAuto = 1074 // 取餐模式 队列第一个自动取餐
        const val NumberNotice = 1080//可以识别人脸
        const val NumberNotice2 = 1081//关闭识别人脸
        const val PickUpNotice = 1082//开启识别人脸
        const val PickUpNotice2 = 1083//关闭识别人脸
        const val PickUpNotice3 = 1084//已添加够数量的人脸
        const val PickUpNotice4 = 1085//忽略后不够数量的人脸
        const val CompanyName = 1090


        const val HeadBeat = 1120
        const val AmountSuccess = 1130
        const val AmountCancel = 1132 //金额模式 取消支付
        const val AmountError = 1133 //支付失败
        const val NumberSuccess = 1131
        const val ScreenOffTimeout = 1140
        const val InitFaceSDKSuccess = 1150
        const val InitFaceSDKFail = 1151
        const val PickUpFaceNumber = 1160
        const val FaceDBChange = 1170
        const val FaceDBChangeEnd = 1171
        const val CurrentTimeInfo = 1180
        const val CurrentTimeInfoFail = 1181
        const val FaceNumberChange = 1190

        const val MainResume = 1200
        const val WeighToken = 1092  //称重 刷脸

        const val WeighNumber = 1292 //称重 重量
        const val WeighSuccess = 1230 //付款成功

        // 刷卡
        const val AmountCard = 1100
        const val NumberCard = 1101
        const val PickUpPhoneCard = 1102 // 取餐模式  手机号码取餐 读卡/取餐码
        const val PickUpCard = 1112 //取餐码取餐

        const val WeighCard = 1192 //称重 刷卡

        //扫码
        const val AmountScanCode = 1300 //扫码
        const val NumberScanCode = 1301
        const val PickUpScanCode = 1302 // 取餐模式 扫码
        const val WeighScanCode = 1392 //称重 扫码

        //刷卡 弹窗
        const val AmountCardDialog = 1103
        const val NumberCardDialog = 1104
        const val WeighCardDialog = 1106
        const val PickUpCardJump = 1105 //取餐模式 读卡

        // 扫二维码 弹窗
        const val AmountScanCodeDialog = 1203
        const val NumberScanCodeDialog = 1204
        const val WeighScanCodeDialog = 1205
        const val PickUpScanCodeJump = 1206

        // const val WeighCardDialog = 1105
        const val ProgressNumber = 1500 //更新进度条
        const val ProgressError = 1510 //更新进度条失败
        const val KeyEventNumber = 1600 //按键
        const val AmountRefund = 1701 //点击退单按钮按键
        const val AmountRefundToken = 1702 //退款时,刷脸查询
        const val AmountRefundCard = 1703 //退款时,刷卡查询
        const val AmountRefundScanCode = 1704 //退款时,扫码查询
        const val AmountRefundSuccess = 1705//退款成功
        const val AmountRefundList = 1706 //退款列表
        const val AmountRefundCancel = 1707 //点击退单按钮按键,然后再点取消按钮
        const val AmountRefundListSelect = 1708 //退款列表 选择

        const val IntervalCardType = 1800 //刷新时段信息
        const val OpenFixAmountMode = 1900 //开启定额模式
        const val CloseFixAmountMode = 2000 //关闭定额模式
        const val RefreshFixAmountMode = 2100 //刷新定额模式

    }
}