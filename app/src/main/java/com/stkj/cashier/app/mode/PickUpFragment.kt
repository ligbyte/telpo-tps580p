package com.stkj.cashier.app.mode

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.adapter.MealListAdapter
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.DifferentDisplay
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.bean.TakeMealsListResult
import com.stkj.cashier.bean.db.CompanyMemberdbEntity
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.PickUpFragmentBinding
import com.stkj.cashier.greendao.biz.CompanyMemberBiz
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class PickUpFragment : BaseFragment<ModeViewModel, PickUpFragmentBinding>() {


    private var currentPosition: Int = -1
    private var currentType: Int = 0  //10 刷脸  20刷卡
    var curPage = 1
    var faceNumber = 3
    private lateinit var mAdapter: MealListAdapter
    private var companyMember: CompanyMemberdbEntity? = null
    private var cardNumberList = ArrayList<String>()
    private var autoPickUp = false //自动取餐,默认否

    companion object {
        fun newInstance(): PickUpFragment {
            return PickUpFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.pick_up_fragment
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        EventBus.getDefault().post(MessageEventBean(MessageEventType.PickUpNotice))
        faceNumber = SPUtils.getInstance().getInt(Constants.FACE_MEMBER_NUMBER, 3)
        val layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvMealList.layoutManager = layoutManager//设置布局管理器
        mAdapter = MealListAdapter(viewModel.takeMealsList.value?.data)
        binding.rvMealList.adapter = mAdapter

        /**
         * 通过人脸或者卡查询出来的新的订单,刷新页面
         * */
        viewModel.takeMealsList.observe(this) {
            LogUtils.e("takeMealsList", Gson().toJson(it))
            if (it.code == 10000 && it.data != null) {
                var cardNumber = ""
                for (item in it.data!!) {
                    item.fullName = it.fullName
                    item.userTel = it.userTel
                    item.userFace = it.userFace
                    item.takeType = currentType
                    cardNumber = item.cardNumber?.toString().toString()
                }
                //从后往前迭代  reversed 反
                /*for (index in mAdapter.data.indices.reversed()) {
                    if (mAdapter.data[index].cardNumber == cardNumber) {
                        mAdapter.removeAt(index)
                    }
                }*/
                mAdapter.addData(it.data!!)
                // 改为查订单后,就出餐,
                takeMeal(it.data!!)
                //刷新排队超出的 头部
                takeMealsListAutoRemoveByfirst()
                if (mAdapter.data.size == 0) {
                    binding.ivListEmpty.visibility = View.VISIBLE
                } else {
                    binding.ivListEmpty.visibility = View.GONE
                }

            } else {
                it.message?.let { it1 -> ttsSpeak(it1) }
            }
            currentType = 0
        }
//        binding.srlMealList.setEnableLoadMore(false)
//        binding.srlMealList.setOnRefreshListener{requestData(1)}
//        binding.srlMealList.setOnLoadMoreListener {requestData(curPage)}
//        binding.srlMealList.autoRefresh()
        mAdapter.addChildClickViewIds(R.id.tvTakeMeal, R.id.tvCancelMeal)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.tvTakeMeal -> {
                    //出餐
                    autoPickUp = false
                    currentPosition = position
                    // takeMeals(position)

                    // 2.出餐只是删除订单
                    var voice = SPUtils.getInstance().getString(Constants.PICK_UP_SUCCESS, "取餐成功")
                    ttsSpeak(voice)
                    if (currentPosition != -1) {
                        var cardNumber = mAdapter.data[currentPosition].cardNumber
                        mAdapter.removeAt(currentPosition)
                        var cardNumbers = ArrayList<String>();
                        for (item in mAdapter.data) {
                            if (!cardNumbers.contains(item.cardNumber)) {
                                cardNumbers.add(item.cardNumber!!)
                            }
                        }
                        var faceNumber =
                            SPUtils.getInstance().getInt(Constants.FACE_MEMBER_NUMBER, 3)
                        if (!cardNumbers.contains(cardNumber)) {
                            //当前这个人的订单已全部取餐
                            if (cardNumbers.size < faceNumber) {
                                EventBus.getDefault()
                                    .post(
                                        MessageEventBean(
                                            MessageEventType.FaceNumberChange,
                                            cardNumber
                                        )
                                    )
                            }
                        }
                        currentPosition = -1
                    }
                    if (mAdapter.data.size == 0) {
                        binding.ivListEmpty.visibility = View.VISIBLE
                    } else {
                        binding.ivListEmpty.visibility = View.GONE
                    }

                }
                R.id.tvCancelMeal -> {
                    //忽略
//                    mAdapter.data[position].itemCancel = true
//                    var cardNumbers = ArrayList<String>();
//                    for (item in mAdapter.data){
//                        if (!cardNumbers.contains(item.cardNumber)&&item.itemCancel){
//                            cardNumbers.add(item.cardNumber!!)
//                        }
//                    }
//                    if (cardNumbers.size<faceNumber&&cardNumberList.size == faceNumber){
//                        var intersect = cardNumberList.intersect(cardNumbers.toSet())
//                        if (intersect.isNotEmpty()){
//                            cardNumberList = intersect.toList() as ArrayList<String>
//                        }
//                    }
//                    EventBus.getDefault().post(MessageEventBean(MessageEventType.PickUpNotice4))
                    var cardNumber = mAdapter.data[position].cardNumber
                    mAdapter.removeAt(position)
                    var cardNumbers = ArrayList<String>();
                    for (item in mAdapter.data) {
                        if (!cardNumbers.contains(item.cardNumber)) {
                            cardNumbers.add(item.cardNumber!!)
                        }
                    }
                    var faceNumber = SPUtils.getInstance().getInt(Constants.FACE_MEMBER_NUMBER, 3)
                    if (cardNumbers.size < faceNumber) {
                        EventBus.getDefault()
                            .post(MessageEventBean(MessageEventType.FaceNumberChange, cardNumber))
                    }
                    if (mAdapter.data.size == 0) {
                        binding.ivListEmpty.visibility = View.VISIBLE
                    } else {
                        binding.ivListEmpty.visibility = View.GONE
                    }
                }
            }
        }
        //出餐之后的接口返回的数据 监听
        viewModel.takeMeals.observe(this) {
            LogUtils.e("出餐之后takeMeals", Gson().toJson(it))
            if (it.code == 10000) {
                if (!autoPickUp) {
                    var voice = SPUtils.getInstance().getString(Constants.PICK_UP_SUCCESS, "取餐成功")
                    ttsSpeak(voice)
                }
                LogUtils.e("出餐之后takeMeals", currentPosition)
                //刷新顶部信息
                requestData()
                if (currentPosition != -1) {
                    var cardNumber = mAdapter.data[currentPosition].cardNumber
                    mAdapter.removeAt(currentPosition)
                    var cardNumbers = ArrayList<String>();
                    for (item in mAdapter.data) {
                        if (!cardNumbers.contains(item.cardNumber)) {
                            cardNumbers.add(item.cardNumber!!)
                        }
                    }
                    var faceNumber = SPUtils.getInstance().getInt(Constants.FACE_MEMBER_NUMBER, 3)
                    if (!cardNumbers.contains(cardNumber)) {
                        //当前这个人的订单已全部取餐
                        if (cardNumbers.size < faceNumber) {
                            EventBus.getDefault()
                                .post(
                                    MessageEventBean(
                                        MessageEventType.FaceNumberChange,
                                        cardNumber
                                    )
                                )
                        }
                    }
                    currentPosition = -1
                }
                if (mAdapter.data.size == 0) {
                    binding.ivListEmpty.visibility = View.VISIBLE
                } else {
                    binding.ivListEmpty.visibility = View.GONE
                }
//                takeMealsListFresh()
            } else {
                if (!it.message.isNullOrEmpty()) {
                    ttsSpeak(it.message!!)
                }
            }
        }

        viewModel.currentTimeInfo.observe(this) {
            LogUtils.e("currentTimeInfo", Gson().toJson(it))
            if (it.code == 10000) {
                if ("1" == it.data?.feeType) {
                    binding.tvFeeType.text = "早餐"
                } else if ("2" == it.data?.feeType) {
                    binding.tvFeeType.text = "午餐"
                } else if ("3" == it.data?.feeType) {
                    binding.tvFeeType.text = "晚餐"
                }
                binding.tvStartTime.text = it.data?.begin
                binding.tvEndTime.text = it.data?.endOrder
                binding.tvTotal.text = it.data?.total.toString()
                binding.tvTakeMeal.text = it.data?.takeMeal.toString()
                App.currentTimeInfo = it.data
                EventBus.getDefault().post(MessageEventBean(MessageEventType.CurrentTimeInfo))
            } else {
                binding.tvFeeType.text = "暂无"
                binding.tvTotal.text = "0"
                binding.tvTakeMeal.text = "0"
                binding.tvStartTime.text = "暂无时段设置"
                binding.tvEndTime.text = "未设置截止时间"
            }
        }
        //初始化取餐界面-上部信息
        requestData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            EventBus.getDefault().post(MessageEventBean(MessageEventType.PickUpNotice2))
        } else {
            EventBus.getDefault().post(MessageEventBean(MessageEventType.PickUpNotice))
        }
    }


    /**
     * 取餐 当前时段信息查询接口-更新 当前餐别  供应时间 截止订餐时间
     */
    private fun requestData() {
        var timeMap = hashMapOf<String, Any>()
        timeMap["mode"] = "Current_Time_Info"
        timeMap["machine_Number"] = App.serialNumber
        var timeMd5 = EncryptUtils.encryptMD5ToString16(App.serialNumber)
        timeMap["sign"] = timeMd5
        viewModel.currentTimeInfo(timeMap)
    }

    /**
     * // 取餐模式 识别人脸
     * */
    private fun takeMealsList(faceToken: String) {
        companyMember = CompanyMemberBiz.getCompanyMember(faceToken)
        if (companyMember != null) {
            currentType = 10
            var map = hashMapOf<String, Any>()
            map["mode"] = "Take_Meals_List"
            map["cardNumber"] = companyMember?.cardNumber.toString()
            map["machine_Number"] = App.serialNumber
            map["consumption_type"] = currentType
            var md5 =
                EncryptUtils.encryptMD5ToString16(companyMember?.cardNumber + "&" + App.serialNumber)
            map["sign"] = md5
            takeMealsList(map)

        }

    }

    /**
     *  // 取餐模式 读卡/取餐码
     * */
    private fun takeMealsListByCard(card: String) {
        companyMember = CompanyMemberBiz.getCompanyMemberByCard(card)
        if (companyMember != null && !TextUtils.isEmpty(companyMember?.cardNumber)) {
            currentType = 20
            var map = hashMapOf<String, Any>()
            map["mode"] = "Take_Meals_List"
            map["cardNumber"] = companyMember?.cardNumber.toString()
            map["machine_Number"] = App.serialNumber

            map["consumption_type"] = currentType
            var md5 =
                EncryptUtils.encryptMD5ToString16(companyMember?.cardNumber + "&" + App.serialNumber)
            map["sign"] = md5
            takeMealsList(map)

        } else if (card != null) {
            currentType = 20
            var map = hashMapOf<String, Any>()
            map["mode"] = "Take_Meals_List"
            map["cardNumber"] = card
            map["machine_Number"] = App.serialNumber

            map["consumption_type"] = currentType
            var md5 =
                EncryptUtils.encryptMD5ToString16(card + "&" + App.serialNumber)
            map["sign"] = md5
            takeMealsList(map)
        } else {
            ttsSpeak("无信息")
            DifferentDisplay.isStartFaceScan.set(true)
        }
    }

    /**
     * 取餐码取餐
     * */
    private fun takeMealsListByCode(card: String) {
        if (card != null) {
            LogUtils.e("取餐模式 2取餐码" + card)
            currentType = 20
            var map = hashMapOf<String, Any>()
            map["mode"] = "Take_Code_Query"
            map["takeCode"] = card
            map["machine_Number"] = App.serialNumber
            map["consumption_type"] = currentType
            var md5 =
                EncryptUtils.encryptMD5ToString16(App.serialNumber + "&" + card)
            map["sign"] = md5
            viewModel.takeMealsList(map)
        }
    }

    /**
     *  // 取餐模式 读卡/取餐码
     * */
    private fun takeMealsListByScanCode(card: String) {
        if (!TextUtils.isEmpty(card)) {
            currentType = 30
            var map = hashMapOf<String, Any>()
            map["mode"] = "Take_Meals_List"
            map["cardNumber"] = card
            map["machine_Number"] = App.serialNumber

            map["consumption_type"] = currentType
            var md5 =
                EncryptUtils.encryptMD5ToString16(card + "&" + App.serialNumber)
            map["sign"] = md5
            takeMealsList(map)

        } else {
            ttsSpeak("无信息")

        }
    }

    /**
     * 出餐--设备订单取餐接口
     * */
    private fun takeMeals(position: Int) {
        var map = hashMapOf<String, Any>()
        map["mode"] = "Take_Meals"
        map["machine_Number"] = App.serialNumber
        map["consumption_type"] = mAdapter.data[position].takeType
        map["cardNumber"] = mAdapter.data[position].cardNumber.toString()
        map["orderNumber"] = mAdapter.data[position].orderNumber as String
        var md5 =
            EncryptUtils.encryptMD5ToString16(mAdapter.data[position].cardNumber + "&" + App.serialNumber + "&" + mAdapter.data[position].orderNumber as String)
        map["sign"] = md5
        LogUtils.e("出餐" + Gson().toJson(map))
        viewModel.takeMeals(map)
    }

    /**
     * 2. 出餐--设备订单取餐接口--默认就是出餐的
     * */
    private fun takeMeal(data: List<TakeMealsListResult.DataDTO>) {
        for (item in data!!) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "Take_Meals"
            map["machine_Number"] = App.serialNumber
            map["consumption_type"] = item.takeType
            map["cardNumber"] = item.cardNumber.toString()
            map["orderNumber"] = item.orderNumber as String
            var md5 =
                EncryptUtils.encryptMD5ToString16(item.cardNumber + "&" + App.serialNumber + "&" + item.orderNumber as String)
            map["sign"] = md5
            LogUtils.e("出餐" + Gson().toJson(map))
            // viewModel.takeMeal(map)
        }
    }

    /**
     * 进来新的单子后,计算是否超出排队人数
     * */
    private fun takeMealsListAutoRemoveByfirst() {
        var faceNumber = SPUtils.getInstance().getInt(Constants.FACE_MEMBER_NUMBER, 3)
        val dataList = mAdapter.data
        val nowdataList = ArrayList<TakeMealsListResult.DataDTO>()
        LogUtils.e("全部订单数量" + dataList.size)
        if (dataList != null && dataList.size > 0) {
            var cardNumbers = ArrayList<String>();
            for (item in dataList) {
                if (!cardNumbers.contains(item.cardNumber)) {
                    cardNumbers.add(item.cardNumber!!)
                }
            }
            LogUtils.e("全部人员数量" + cardNumbers.size)
            if (cardNumbers.size > faceNumber) {
                // 删除超出的部分
                val itemsToRemove: Int = cardNumbers.size - faceNumber
                LogUtils.e("删除超出的部分itemsToRemove-" + itemsToRemove)
                for (i in 0 until itemsToRemove) {
                    LogUtils.e("删除超出的部分cardNumbers-" + cardNumbers[i])
                    LogUtils.e("删除超出的部分cardNumbers-" + mAdapter.data.size)
                    //for (indices in mAdapter.data.indices){
                    for (indices in mAdapter.data.indices) {

                        val item = dataList[indices]
                        LogUtils.e("删除indices-" + indices + item.fullName + "/" + item.cardNumber + "/" + item.orderNumber)
                        if (item.cardNumber.equals(cardNumbers[i])) {
                            EventBus.getDefault()
                                .post(
                                    MessageEventBean(
                                        MessageEventType.FaceNumberChange,
                                        dataList[0].cardNumber
                                    )
                                )
                            //dataList.removeAt(indices) // 从列表末尾开始删除
//                            mAdapter.removeAt(indices) // 从列表末尾开始删除
                            // mAdapter.notifyItemRemoved(indices)
                            LogUtils.e("删除超出的部分indices-" + indices + item.fullName + item.orderNumber)
                        } else {
                            nowdataList.add(item)
                        }
                    }
                }
                autoPickUp = true
                LogUtils.e("全部订单数量-" + dataList.size + "/" + nowdataList.size)
                mAdapter.setList(nowdataList)
                // 通知 RecyclerView 数据已经改变
                //mAdapter.notifyItemRangeRemoved(0, itemsToRemove)
            }
        }
    }

    /**
     * 移除订单里面的第一个人的全部订单(做出餐处理)
     * */
    private fun takeMealsListRemoveByfirst() {
        //获取第一个订单里面的人员信息
        //遍历第一个人的全部订单
        //做出餐处理,并刷新页面
        if (mAdapter.data != null && mAdapter.data.size > 0) {
            val cardNumber = mAdapter.data[0].cardNumber
            val data = mAdapter.data

            for (index in data.indices) {
                if (data[index].cardNumber == cardNumber) {
                    LogUtils.e("取餐模式 自动取餐" + cardNumber + "//" + index)
                    /* var map = hashMapOf<String, Any>()
                     map["mode"] = "Take_Meals"
                     map["machine_Number"] = App.serialNumber
                     map["consumption_type"] = data[index].takeType
                     map["cardNumber"] = data[index].cardNumber.toString()
                     map["orderNumber"] = data[index].orderNumber as String
                     var md5 =
                         EncryptUtils.encryptMD5ToString16(data[index].cardNumber + "&" + App.serialNumber + "&" + data[index].orderNumber as String)
                     map["sign"] = md5
                     LogUtils.e("自动出餐" + Gson().toJson(map))
                     currentPosition = index
                     viewModel.takeMeals(map)*/

                    mAdapter.removeAt(index)

                    var cardNumbers = ArrayList<String>();
                    for (item in mAdapter.data) {
                        if (!cardNumbers.contains(item.cardNumber)) {
                            cardNumbers.add(item.cardNumber!!)
                        }
                    }
                    var faceNumber = SPUtils.getInstance().getInt(Constants.FACE_MEMBER_NUMBER, 3)
                    if (!cardNumbers.contains(cardNumber)) {
                        //当前这个人的订单已全部取餐
                        if (cardNumbers.size < faceNumber) {
                            EventBus.getDefault()
                                .post(
                                    MessageEventBean(
                                        MessageEventType.FaceNumberChange,
                                        cardNumber
                                    )
                                )
                        }
                    }
                }
            }
        }


    }

    private fun takeMealsListFresh() {
        if (companyMember != null) {
            var map = hashMapOf<String, Any>()
            map["mode"] = "Take_Meals_List"
            map["cardNumber"] = companyMember?.cardNumber.toString()
            map["machine_Number"] = App.serialNumber
            var md5 =
                EncryptUtils.encryptMD5ToString16(companyMember?.cardNumber + "&" + App.serialNumber)
            map["sign"] = md5
            takeMealsList(map)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    fun takeMealsList(params: Map<String, Any>) {
        viewModel.launch(false) {
            // TODO Http请求
            val it = viewModel.apiService.takeMealsList(params)
            // TODO 只需处理成功的场景，失败的场景都已统一处理
            LogUtils.e("取餐takeMealsList", Gson().toJson(it))

            DifferentDisplay.isStartFaceScan.set(true)
            if (it.code == 10000 && it.data != null) {
                var cardNumber = ""
                for (item in it.data!!) {
                    item.fullName = it.fullName
                    item.userTel = it.userTel
                    item.userFace = it.userFace
                    item.takeType = currentType
                    cardNumber = item.cardNumber?.toString().toString()
                }
                //从后往前迭代  reversed 反
                /*for (index in mAdapter.data.indices.reversed()) {
                    if (mAdapter.data[index].cardNumber == cardNumber) {
                        mAdapter.removeAt(index)
                    }
                }*/
                mAdapter.addData(it.data!!)
                // 改为查订单后,就出餐,
                takeMeal(it.data!!)
                //刷新排队超出的 头部
                takeMealsListAutoRemoveByfirst()
                if (mAdapter.data.size == 0) {
                    binding.ivListEmpty.visibility = View.VISIBLE
                } else {
                    binding.ivListEmpty.visibility = View.GONE
                }

            } else {

                it.message?.let { it1 -> ttsSpeak(it1) }
            }
            currentType = 0
        }
    }

    /**
     * 收到人脸识别界面发过来的人脸信息ID和
     * */
    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        when (message.type) {
            MessageEventType.PickUpToken -> {
                message.content?.let {
                    /// 取餐模式 识别人脸
                    LogUtils.e("取餐模式 识别人脸" + Gson().toJson(it))
                    autoPickUp = false
                    takeMealsList(it)
                }
            }
            MessageEventType.PickUpPhoneCard -> {
                message.content?.let {
                    // 取餐模式 读卡/取餐码
                    LogUtils.e("取餐模式 读卡/取餐码" + Gson().toJson(it))
                    autoPickUp = false
                    ttsSpeak("识别成功")
                    takeMealsListByCard(it)
                }
            }
            MessageEventType.PickUpCard -> {
                //取餐码取餐
                message.content?.let {
                    autoPickUp = false
                    LogUtils.e("取餐模式 取餐码" + Gson().toJson(it))
                    ttsSpeak("识别成功")
                    takeMealsListByCode(it)
                }
            }
            MessageEventType.PickUpScanCode -> {
                message.content?.let {
                    // 取餐模式 扫码/取餐码
                    LogUtils.e("取餐模式 扫码/取餐码" + Gson().toJson(it))
                    autoPickUp = false
                    ttsSpeak("识别成功")
                    takeMealsListByScanCode(it)
                }
            }
            MessageEventType.PickUpAuto -> {
                message.content?.let {
                    // 取餐模式 第一个人 自动取餐
                    LogUtils.e("取餐模式 自动取餐" + Gson().toJson(it))
                    autoPickUp = true
                    //takeMealsListRemoveByfirst()
                }
            }
            MessageEventType.CurrentTimeInfo -> {
                LogUtils.e("取餐模式 时段" + Gson().toJson(message.content) + App.currentTimeInfo?.feeType)
                if (App.currentTimeInfo?.feeType == "1") {
                    binding.tvFeeType.text = "早餐"
                } else if (App.currentTimeInfo?.feeType == "2") {
                    binding.tvFeeType.text = "午餐"
                } else if (App.currentTimeInfo?.feeType == "3") {
                    binding.tvFeeType.text = "晚餐"
                }
                binding.tvStartTime.text = App.currentTimeInfo?.begin
                binding.tvEndTime.text = App.currentTimeInfo?.endOrder
                binding.tvTotal.text = App.currentTimeInfo?.total.toString()
                binding.tvTakeMeal.text = App.currentTimeInfo?.takeMeal.toString()
            }
        }
    }


}