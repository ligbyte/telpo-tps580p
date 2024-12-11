package com.stkj.cashier.app.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.google.gson.Gson
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.adapter.CompanyMemberListAdapter
import com.stkj.cashier.app.adapter.MealListAdapter
import com.stkj.cashier.app.adapter.SelectPopupListAdapter
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.MainActivity
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.app.splash.SplashActivity
import com.stkj.cashier.bean.MessageEventBean
import com.stkj.cashier.bean.db.CompanyMemberdbEntity
import com.stkj.cashier.config.MessageEventType
import com.stkj.cashier.databinding.*
import com.stkj.cashier.greendao.biz.CompanyMemberBiz
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mcv.facepass.FacePassException
import org.greenrobot.eventbus.EventBus
import java.lang.String
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class FaceRecognitionFragment : BaseFragment<SettingViewModel, FaceRecognitionFragmentBinding>(),
    View.OnClickListener {

    private lateinit var memberAdapter: CompanyMemberListAdapter
    var pageIndex = 1
    var pageSize = 5000
    private var depArray = ArrayList<kotlin.String>()
    private var accountArray = ArrayList<kotlin.String>()

    companion object {
        fun newInstance(): FaceRecognitionFragment {
            return FaceRecognitionFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        var index = SPUtils.getInstance().getInt(Constants.AUTH_VALUE)
        when (index) {
            -1 -> {
                binding.tvSelectAuth.text = "文件模式"
            }
            0 -> {
                binding.tvSelectAuth.text = "文件模式"
            }
            1 -> {
                binding.tvSelectAuth.text = "芯片授权"
            }
            2 -> {
                binding.tvSelectAuth.text = "授权码"
            }
        }
        var number = SPUtils.getInstance().getInt(Constants.FACE_MEMBER_NUMBER, 3)
        binding.etFaceNumber.setText(number.toString())
        binding.etFaceNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etFaceNumber.text.isNotEmpty()) {
                    SPUtils.getInstance().put(
                        Constants.FACE_MEMBER_NUMBER,
                        binding.etFaceNumber.text.toString().toInt()
                    )
                } else {
                    SPUtils.getInstance().put(Constants.FACE_MEMBER_NUMBER, 9999)
                }
                EventBus.getDefault().post(MessageEventBean(MessageEventType.PickUpFaceNumber))
            }

        })
        val layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        binding.rvFaceInfo.layoutManager = layoutManager//设置布局管理器
        memberAdapter = CompanyMemberListAdapter(viewModel.companyMember.value?.results)
        binding.rvFaceInfo.adapter = memberAdapter
//        viewModel.companyMember.observe(this){
//            LogUtils.e("companyMember", Gson().toJson(it))
//            mAdapter.setList(it.results)
//            CompanyMemberBiz.addCompanyMembers(it.results)
//        }
        accountArray.add("全部人员")
        depArray.add("全部部门")
        CompanyMemberBiz.getCompanyMemberList {
            LogUtils.e("getCompanyMemberList", "==" + it.size)
            binding.tvMemberNumber.text = "${it.size}"
            for ((index, item) in it.withIndex()) {
                if (!depArray.contains(item.depNameType)) {
                    depArray.add(item.depNameType)
                }
                if (!accountArray.contains(item.accountType)) {
                    accountArray.add(item.accountType)
                }
            }
            memberAdapter.setList(it)
        }
        binding.tvFaceSetting.setOnClickListener(this)
        binding.tvSelectAuth.setOnClickListener(this)
        binding.tvCheckMember.setOnClickListener(this)
        binding.tvSelectMember.setOnClickListener(this)
        binding.tvSelectDep.setOnClickListener(this)
        binding.tvSearch.setOnClickListener(this)
        binding.ivCircleBack.setOnClickListener(this)
        binding.tvClearMember.setOnClickListener(this)
        binding.tvDownLoadMember.setOnClickListener(this)
//        binding.etKeyword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
//            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//                /*判断是否是“NEXT”键*/
//                if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                    /*隐藏软键盘*/
//                   KeyboardUtils.hideSoftInput(binding.etKeyword)
//                    /*隐藏软键盘*/
//                    return true;
//                }
//                return false;
//            }
//
//        })
        binding.etKeyword.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                /*隐藏软键盘*/
                KeyboardUtils.hideSoftInput(binding.etKeyword)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.face_recognition_fragment
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tvFaceSetting -> {
                    faceSetting()
                }
                R.id.tvSelectAuth -> {
                    selectAuth()
                }
                R.id.tvCheckMember -> {
                    //查看人脸详情
                    depArray.clear()
                    accountArray.clear()
                    accountArray.add("全部人员")
                    depArray.add("全部部门")
                    CompanyMemberBiz.getCompanyMemberList {
                        LogUtils.e("getCompanyMemberList", "==" + it.size)
                        binding.tvMemberNumber.text = "${it.size}"
                        for ((index, item) in it.withIndex()) {
                            if (!depArray.contains(item.depNameType)) {
                                depArray.add(item.depNameType)
                            }
                            if (!accountArray.contains(item.accountType)) {
                                accountArray.add(item.accountType)
                            }
                        }
                        memberAdapter.setList(it)

                        binding.tvDownLoadMember.isEnabled=true
                        binding.tvDownLoadMember.background =
                            requireContext().getDrawable(R.drawable.shape_radius_30_bg_0087fa)
                    }
                    binding.llFaceSetting.visibility = View.GONE
                    binding.llCompanyMember.visibility = View.VISIBLE
                }
                R.id.tvSelectMember -> {
                    selectMember()
                }
                R.id.tvSelectDep -> {
                    selectDep()
                }
                R.id.tvSearch -> {
                    KeyboardUtils.hideSoftInput(binding.etKeyword)
                    search()
                }
                R.id.ivCircleBack -> {
                    binding.llFaceSetting.visibility = View.VISIBLE
                    binding.llCompanyMember.visibility = View.GONE
                }
                R.id.tvClearMember -> {
                    clearMemberDB()

                }
                R.id.tvDownLoadMember -> {
//                    var mainActivity = activity as MainActivity
//                    mainActivity.companyMember(0)
                    downMemberDB()
                }
            }
        }
    }
    /**
     * 全量更新弹窗
     * */
    private fun downMemberDB() {
        val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
        val dialog = builder.create()
        dialog.setCancelable(true)
        val view = View.inflate(requireContext(), R.layout.dialog_down_member, null)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        ivClose.setOnClickListener { view1: View? -> dialog.cancel() }
        tvCancel.setOnClickListener { view1: View? -> dialog.cancel() }
        tvConfirm.setOnClickListener { view1: View? ->
//            App.mFacePassHandler?.resetDynamicLocalGroup()
            binding.tvDownLoadMember.isEnabled=false
            binding.tvDownLoadMember.background =
                requireContext().getDrawable(R.drawable.shape_radius_30_bg_f2f1f4)

            try {
                showProgressDialog()
                Thread(Runnable {
                    App.mFacePassHandler?.clearAllGroupsAndFaces()
                    createGroup()
                    CompanyMemberBiz.deleteRelationAll()
                    dismissProgressDialog()

                    CompanyMemberBiz.getCompanyMemberList {
                        LogUtils.e("getCompanyMemberList", "==" + it.size)
                        binding.tvMemberNumber.text = "${it.size}"
                        for ((index, item) in it.withIndex()) {
                            if (!depArray.contains(item.depNameType)) {
                                depArray.add(item.depNameType)
                            }
                            if (!accountArray.contains(item.accountType)) {
                                accountArray.add(item.accountType)
                            }
                        }
                        memberAdapter.setList(it)
                    }

                    // 下载人脸
                    var mainActivity = activity as MainActivity

                    mainActivity.callBack =true
                    mainActivity.companyMember(0)
                    LogUtils.e("全量更新弹窗")
                }).start()

            } catch (e: Exception) {
                e.printStackTrace()
                LogUtils.e("clearMemberDB", "Exception:" + e.message)
            }

            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Objects.requireNonNull(dialog.window)!!.setContentView(view)
    }

    private fun clearMemberDB() {
        val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
        val dialog = builder.create()
        dialog.setCancelable(true)
        val view = View.inflate(requireContext(), R.layout.dialog_clear_member, null)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val tvConfirm = view.findViewById<TextView>(R.id.tvConfirm)
        val tvCancel = view.findViewById<TextView>(R.id.tvCancel)
        ivClose.setOnClickListener { view1: View? -> dialog.cancel() }
        tvCancel.setOnClickListener { view1: View? -> dialog.cancel() }
        tvConfirm.setOnClickListener { view1: View? ->
//            App.mFacePassHandler?.resetDynamicLocalGroup()
            try {
                showProgressDialog()
                Thread(Runnable {
                    App.mFacePassHandler?.clearAllGroupsAndFaces()
                    createGroup()
                    CompanyMemberBiz.deleteRelationAll()
                    dismissProgressDialog()
                    LogUtils.e("底库清楚人脸")

                    CompanyMemberBiz.getCompanyMemberList {
                        LogUtils.e("getCompanyMemberList", "==" + it.size)
                        binding.tvMemberNumber.text = "${it.size}"
                        for ((index, item) in it.withIndex()) {
                            if (!depArray.contains(item.depNameType)) {
                                depArray.add(item.depNameType)
                            }
                            if (!accountArray.contains(item.accountType)) {
                                accountArray.add(item.accountType)
                            }
                        }
                        memberAdapter.setList(it)
                    }
                }).start()

            }catch (e:Exception){
                e.printStackTrace()
                LogUtils.e("底库清楚人脸clearMemberDB","Exception:"+e.message)
            }

            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() * 0.32).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Objects.requireNonNull(dialog.window)!!.setContentView(view)
    }


    private fun search() {
        var accountType = binding.tvSelectMember.text.toString()
        var dep = binding.tvSelectDep.text.toString()
        var keyword = binding.etKeyword.text.toString()
        CompanyMemberBiz.getCompanyMemberList(
            accountType, dep, keyword
        ) {
            memberAdapter.setList(it)

        }

    }

    private fun createGroup() {
        if (App.mFacePassHandler == null) {
            return
        }
        try {
            val localGroups = App.mFacePassHandler?.localGroups
//            isLocalGroupExist = false
            if (localGroups == null || localGroups.isEmpty()) {
                App.mFacePassHandler?.createLocalGroup(Constants.GROUP_NAME)
            }
        } catch (e: FacePassException) {
            e.printStackTrace()
        }
    }

    private fun selectMember() {
        //创建对象
        val popupWindow = PopupWindow(requireActivity())
        val inflate: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.layout_select_popup, null)
        //设置view布局
        popupWindow.contentView = inflate
        popupWindow.width = binding.tvSelectMember.width
        //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
        popupWindow.isTouchable = true
        //设置背景透明
        popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
        //点击空白处的时候让PopupWindow消失
        popupWindow.isOutsideTouchable = true
        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键，默认是false
        popupWindow.isFocusable = false
        //设置dismiss事件
        var rvSelectPopup = inflate.findViewById<RecyclerView>(R.id.rvSelectPopup)
        val layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        rvSelectPopup.layoutManager = layoutManager//设置布局管理器
        var mAdapter = SelectPopupListAdapter(accountArray)
        rvSelectPopup.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.btSelect)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.btSelect -> {
                    binding.tvSelectMember.text = accountArray[position]
                }
            }

            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener { })
        val showing: Boolean = popupWindow.isShowing
        if (!showing) {
            inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val measuredHeight: Int = inflate.measuredHeight
            //show，并且可以设置位置
            popupWindow.showAsDropDown(binding.tvSelectMember, 0, 20)
        }


    }

    private fun selectDep() {
        //创建对象
        val popupWindow = PopupWindow(requireActivity())
        val inflate: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.layout_select_popup, null)
        //设置view布局
        popupWindow.contentView = inflate
        popupWindow.width = binding.tvSelectDep.width
        //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
        popupWindow.isTouchable = true
        //设置背景透明
        popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
        //点击空白处的时候让PopupWindow消失
        popupWindow.isOutsideTouchable = true
        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键，默认是false
        popupWindow.isFocusable = false
        //设置dismiss事件
        var rvSelectPopup = inflate.findViewById<RecyclerView>(R.id.rvSelectPopup)
        val layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        rvSelectPopup.layoutManager = layoutManager//设置布局管理器
        var mAdapter = SelectPopupListAdapter(depArray)
        rvSelectPopup.adapter = mAdapter
        mAdapter.addChildClickViewIds(R.id.btSelect)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.btSelect -> {
                    binding.tvSelectDep.text = depArray.get(position)
                }

            }
            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener { })
        val showing: Boolean = popupWindow.isShowing
        if (!showing) {
            inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val measuredHeight: Int = inflate.measuredHeight
            //show，并且可以设置位置
            popupWindow.showAsDropDown(binding.tvSelectDep, 0, 20)
        }


    }

    private fun selectAuth() {
        //创建对象
        val popupWindow = PopupWindow(requireActivity())
        val inflate: View =
            LayoutInflater.from(requireActivity()).inflate(R.layout.layout_select_auth, null)
        //设置view布局
        popupWindow.contentView = inflate
        popupWindow.width = binding.tvSelectAuth.width
        //设置PopUpWindow的焦点，设置为true之后，PopupWindow内容区域，才可以响应点击事件
        popupWindow.isTouchable = true
        //设置背景透明
        popupWindow.setBackgroundDrawable(ColorDrawable(0x00000000))
        //点击空白处的时候让PopupWindow消失
        popupWindow.isOutsideTouchable = true
        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键，默认是false
        popupWindow.isFocusable = false
        //设置dismiss事件

        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener { })
        val showing: Boolean = popupWindow.isShowing
        if (!showing) {
            inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val measuredHeight: Int = inflate.measuredHeight
            //show，并且可以设置位置
            popupWindow.showAsDropDown(binding.tvSelectAuth, 0, 20)
        }
        var rgSelectAuth = inflate.findViewById<RadioGroup>(R.id.rgSelectAuth)
        var index = SPUtils.getInstance().getInt(Constants.AUTH_VALUE)
        when (index) {
            -1 -> rgSelectAuth.check(R.id.rgSelectFile)
            0 -> rgSelectAuth.check(R.id.rgSelectFile)
            1 -> rgSelectAuth.check(R.id.rgSelectChip)
            2 -> rgSelectAuth.check(R.id.rgSelectCode)

        }
        rgSelectAuth.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rgSelectFile -> {
                    binding.tvSelectAuth.text = "文件模式"
                    SPUtils.getInstance().put(Constants.AUTH_VALUE, 0)
                    if (App.mFacePassHandler == null) {
                        SplashActivity.initFacePassSDKStatic()
                        SplashActivity.initFaceHandlerStatic()
                    }
                }
                R.id.rgSelectChip -> {
                    binding.tvSelectAuth.text = "芯片授权"
                    SPUtils.getInstance().put(Constants.AUTH_VALUE, 1)
                }
                R.id.rgSelectCode -> {
                    binding.tvSelectAuth.text = "授权码"
                    SPUtils.getInstance().put(Constants.AUTH_VALUE, 2)
                }

            }
            popupWindow.dismiss()
        }

    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun faceSetting() {
        val builder = AlertDialog.Builder(requireContext(), R.style.app_dialog)
        val dialog = builder.create()
        dialog.setCancelable(true)
        val view = View.inflate(requireContext(), R.layout.dialog_face_setting, null)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val switchLivenessEnabled = view.findViewById<Switch>(R.id.switchLivenessEnabled)
        val switchRcAttributeAndOcclusionMode =
            view.findViewById<Switch>(R.id.switchRcAttributeAndOcclusionMode)
        val switchlivenessGaThresholdEnabled =
            view.findViewById<Switch>(R.id.switchlivenessGaThresholdEnabled)
        val sbSearchThreshold = view.findViewById<SeekBar>(R.id.sbSearchThreshold)
        val sbLivenessGaThreshold = view.findViewById<SeekBar>(R.id.sbLivenessGaThreshold)
        val sbLivenessThreshold = view.findViewById<SeekBar>(R.id.sbLivenessThreshold)

        val tvSearchThreshold = view.findViewById<TextView>(R.id.tvSearchThreshold)
        val tvLivenessGaThreshold = view.findViewById<TextView>(R.id.tvLivenessGaThreshold)
        val tvLivenessThreshold = view.findViewById<TextView>(R.id.tvLivenessThreshold)
        ivClose.setOnClickListener { view1: View? -> dialog.cancel() }
//            val btnYes = view.findViewById<TextView>(R.id.txt_btn_yes)
//            btnYes.setOnClickListener { view1: View? ->
//            }
        //开启活体
        var livenessEnabled =
            SPUtils.getInstance().getBoolean(Constants.FACE_LIVENESS_ENABLED, true)
        switchLivenessEnabled.isChecked = livenessEnabled
        sbLivenessThreshold.isEnabled = switchLivenessEnabled.isChecked
        switchLivenessEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            SPUtils.getInstance().put(Constants.FACE_LIVENESS_ENABLED, isChecked)
            sbLivenessThreshold.isEnabled = isChecked
        }
        //开启相似度  影响相似度设置
        var livenessGaThresholdEnabled =
            SPUtils.getInstance().getBoolean(Constants.FACE_LIVENESS_GA_THRESHOLD_ENABLED, true)
        switchlivenessGaThresholdEnabled.isChecked = livenessGaThresholdEnabled
        sbLivenessGaThreshold.isEnabled = switchlivenessGaThresholdEnabled.isChecked
        switchlivenessGaThresholdEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            SPUtils.getInstance().put(Constants.FACE_LIVENESS_GA_THRESHOLD_ENABLED, isChecked)
            sbLivenessGaThreshold.isEnabled = isChecked

        }
        //口罩识别
        var rcAttributeAndOcclusionMode =
            SPUtils.getInstance().getInt(Constants.FACE_RCATTRIBUTEANDOCCLUSIONMODE, 0)
        switchRcAttributeAndOcclusionMode.isChecked = rcAttributeAndOcclusionMode == 0
        switchRcAttributeAndOcclusionMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPUtils.getInstance().put(Constants.FACE_RCATTRIBUTEANDOCCLUSIONMODE, 0)
            } else {
                SPUtils.getInstance().put(Constants.FACE_RCATTRIBUTEANDOCCLUSIONMODE, 2)
            }
        }

        var searchThreshold = SPUtils.getInstance().getInt(Constants.FACE_SEARCH_THRESHOLD, 65)
        sbSearchThreshold.progress = searchThreshold
        tvSearchThreshold.text=""+searchThreshold
        sbSearchThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                SPUtils.getInstance().put(Constants.FACE_SEARCH_THRESHOLD, progress)
                tvSearchThreshold.text=""+progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        var livenessGaThreshold =
            SPUtils.getInstance().getInt(Constants.FACE_LIVENESS_GA_THRESHOLD, 85)
        sbLivenessGaThreshold.progress = livenessGaThreshold
        tvLivenessGaThreshold.text=""+livenessGaThreshold
        sbLivenessGaThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                SPUtils.getInstance().put(Constants.FACE_LIVENESS_GA_THRESHOLD, progress)
                tvLivenessGaThreshold.text=""+progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        var livenessThreshold = SPUtils.getInstance().getInt(Constants.FACE_LIVENESS_THRESHOLD, 80)
        sbLivenessThreshold.progress = livenessThreshold
        tvLivenessThreshold.text=""+livenessGaThreshold
        sbLivenessThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                SPUtils.getInstance().put(Constants.FACE_LIVENESS_THRESHOLD, progress)
                tvLivenessThreshold.text=""+progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        var rgSelectDistance = view.findViewById<RadioGroup>(R.id.rgSelectDistance)
        var index = SPUtils.getInstance().getInt(Constants.FACE_MIN_THRESHOLD, 300)

       /* when (index) {
            //150
            400 -> rgSelectDistance.check(R.id.rbSelect0)
            125 -> rgSelectDistance.check(R.id.rbSelect1)
            100 -> rgSelectDistance.check(R.id.rbSelect2)
        }
        rgSelectDistance.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbSelect0 -> {
                    SPUtils.getInstance().put(Constants.FACE_MIN_THRESHOLD, 400)
                }
                R.id.rbSelect1 -> {
                    SPUtils.getInstance().put(Constants.FACE_MIN_THRESHOLD, 125)
                }
                R.id.rbSelect2 -> {
                    SPUtils.getInstance().put(Constants.FACE_MIN_THRESHOLD, 100)
                }

            }
        }*/


        var sbfaceMinThreshold = view.findViewById<SeekBar>(R.id.sbfaceMinThreshold)
        var tvfaceMinThreshold = view.findViewById<TextView>(R.id.tvfaceMinThreshold)
        sbfaceMinThreshold.progress = 500-index
        tvfaceMinThreshold.text=""+(500-index)
        sbfaceMinThreshold.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                SPUtils.getInstance().put(Constants.FACE_MIN_THRESHOLD, 500-progress)
                tvfaceMinThreshold.text=""+progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })



        dialog.show()
        // LinearLayout.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(
            (ScreenUtils.getAppScreenWidth() * 0.52).toInt(),
            (ScreenUtils.getAppScreenHeight() * 0.62).toInt()
        )
        Objects.requireNonNull(dialog.window)!!.setContentView(view)
    }

    override fun onEventReceiveMsg(message: MessageEventBean) {
        super.onEventReceiveMsg(message)
        when (message.type) {
            MessageEventType.InitFaceSDKFail -> {
                ToastUtils.showLong("授权失败")
            }
            MessageEventType.InitFaceSDKSuccess -> {
                ToastUtils.showLong("授权成功")
            }
            MessageEventType.FaceDBChangeEnd -> {
                LogUtils.e("FaceDBChangeEnd"+"==下发完成")
                binding.tvDownLoadMember.isEnabled=true
                binding.tvDownLoadMember.background =
                    requireContext().getDrawable(R.drawable.shape_radius_30_bg_0087fa)
            }
            MessageEventType.FaceDBChange -> {
                CompanyMemberBiz.getCompanyMemberList {
                    LogUtils.e("getCompanyMemberList", "==" + it.size)
                    binding.tvMemberNumber.text = "${it.size}"
                    for ((index, item) in it.withIndex()) {
                        if (!depArray.contains(item.depNameType)) {
                            depArray.add(item.depNameType)
                        }
                        if (!accountArray.contains(item.accountType)) {
                            accountArray.add(item.accountType)
                        }
                    }
                    memberAdapter.setList(it)
                }
            }
        }
    }
}