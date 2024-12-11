package com.stkj.cashier.app.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.stkj.cashier.util.util.EncryptUtils
import com.stkj.cashier.util.util.LogUtils
import com.stkj.cashier.charting.components.AxisBase
import com.stkj.cashier.charting.components.Legend
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.R
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.databinding.MenuFragmentBinding
import com.stkj.cashier.databinding.StatisticsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

import com.stkj.cashier.charting.components.YAxis

import com.stkj.cashier.charting.components.YAxis.YAxisLabelPosition


import com.stkj.cashier.charting.components.XAxis.XAxisPosition

import com.stkj.cashier.charting.components.XAxis
import com.stkj.cashier.charting.data.*
import com.stkj.cashier.charting.formatter.DefaultAxisValueFormatter
import com.stkj.cashier.charting.formatter.IAxisValueFormatter
import com.stkj.cashier.charting.formatter.IValueFormatter
import com.stkj.cashier.charting.utils.ViewPortHandler
import com.google.gson.Gson
import com.stkj.cashier.App
import com.stkj.cashier.app.adapter.ConsumeRecordListAdapter
import com.stkj.cashier.app.adapter.StatisticsRecordListAdapter
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class StatisticsFragment : BaseFragment<StatisticsViewModel, StatisticsFragmentBinding>() {



    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        initChartData()

        val layoutManager = LinearLayoutManager(requireActivity());//添加布局管理器
        binding.rvConsume.layoutManager= layoutManager//设置布局管理器
        var mAdapter = StatisticsRecordListAdapter(viewModel.consumeRecord.value?.results)
        binding.rvConsume.adapter = mAdapter

        initRecordData()
        viewModel.consumeRecord.observe(this){
            LogUtils.e("consumeRecord", Gson().toJson(it))
            mAdapter.setList(it.results)
        }
        viewModel.canteenSummary.observe(this){
            LogUtils.e("canteenSummary", Gson().toJson(it))
            initChartNumber()
            initChartAmount()
            var memerNumer = 0
            for (item in viewModel.canteenSummary.value?.feeTypeList!!){
                memerNumer += item.value!!
            }
            binding.tvMemberNumber.text =  "$memerNumer"+"位"
            var totalAmount = 0f
            for (item in viewModel.canteenSummary.value?.consumeMethodList!!){
                totalAmount += item.value!!
            }
            var str = String.format("%.2f",totalAmount)
            binding.tvTotalAmount.text =  "¥$str"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initChartData() {
        var map = hashMapOf<String,Any>()
        map["mode"] = "Canteen_summary"
        map["machine_Number"] = App.serialNumber
        var md5 = EncryptUtils.encryptMD5ToString16(App.serialNumber)
        map["sign"] = md5
        viewModel.canteenSummary(map)
    }

    fun initRecordData(){
        var map = hashMapOf<String,Any>()
        map["mode"] = "ConsumeRecordList"
        map["machine_Number"] = App.serialNumber
//        map["cardNumber"] = "7202143305"
        map["pageIndex"] = 1
        map["pageSize"] = 5000
        var md5 = EncryptUtils.encryptMD5ToString16(App.serialNumber+"&1&5000")
        map["sign"] = md5
        viewModel.consumeRecordList(map)
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            initRecordData()
            initChartData()
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.statistics_fragment
    }
    private fun initChartAmount() {
//        binding.chartAmount.animateXY(1000,1000);
        //设置绘不绘制中间空白
        binding.chartAmount.setDrawHoleEnabled(false);
        //设置手动旋转
        binding.chartAmount.setRotationEnabled(true);
        //设置默认旋转度数
        binding.chartAmount.setRotationAngle(90f);
        //取消右下角描述
        binding.chartAmount.getDescription().setEnabled(false);
//        //设置文字描述为白色
//        binding.chartAmount.setEntryLabelColor(resources.getColor(R.color.text_6));
//        //设置文字描述的大小
//        binding.chartAmount.setEntryLabelTextSize(15f);
//        //设置文字描述的样式
//        binding.chartAmount.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
//        //设置四个方向的偏移
//        binding.chartAmount.setExtraOffsets(5f,5f,5f,5f);
        //设置图标的转动阻力摩擦系数
        binding.chartAmount.setDragDecelerationFrictionCoef(0.2f);

        binding.chartAmount.setDrawEntryLabels(true);
        binding.chartAmount.setEntryLabelColor(resources.getColor(R.color.text_6));
        binding.chartAmount.setEntryLabelTextSize(12f);
//        binding.chartAmount.setExtraOffsets(15f,15f,15f,15f)

        //得到图例
        var legend:Legend = binding.chartAmount.legend;
        legend.isEnabled = false
        //设置图例的样式
        var code = 0f
        var code1 = 0f
        var code2 = 0f
        var code3 = 0f

        var face = 0f
        var face1 = 0f
        var face2 = 0f
        var face3 = 0f

        var card = 0f
        var card1 = 0f
        var card2 = 0f
        var card3 = 0f

        var other = 0f
        var other1 = 0f
        var other2 = 0f
        var other3 = 0f
        for (item in viewModel.canteenSummary.value?.consumeMethodList!!){
            if ("刷脸" == item.key){
                face+= item.value!!
                if ("早餐" ==item.key1){
                    face1 += item.value!!
                }else if ("午餐" ==item.key1){
                    face2 += item.value!!
                }else if ("晚餐" ==item.key1){
                    face3 += item.value!!
                }
            }else if ("扫码" == item.key){
                code+= item.value!!
                if ("早餐" ==item.key1){
                    code1 += item.value!!
                }else if ("午餐" ==item.key1){
                    code2 += item.value!!
                }else if ("晚餐" ==item.key1){
                    code3 += item.value!!
                }
            }else if ("刷卡" == item.key){
                card+= item.value!!
                if ("早餐" ==item.key1){
                    card1 += item.value!!
                }else if ("午餐" ==item.key1){
                    card2 += item.value!!
                }else if ("晚餐" ==item.key1){
                    card3 += item.value!!
                }
            }else if ("其他" == item.key) {
                other += item.value!!
                if ("早餐" == item.key1) {
                    other1 += item.value!!
                } else if ("午餐" == item.key1) {
                    other2 += item.value!!
                } else if ("晚餐" == item.key1) {
                    other3 += item.value!!
                }
            }
        }
        //设置数据集合
        var entries = mutableListOf<PieEntry>()
        var strCode = String.format("%.2f",code)
        var strFace = String.format("%.2f",face)
        var strCard = String.format("%.2f",card)
        var strOther = String.format("%.2f", other)
        if (code != 0f) {
            entries.add(PieEntry(code, "扫码： $strCode\n早餐：$code1\n午餐：$code2\n晚餐：$code3"))
        }
        if (face != 0f) {
            entries.add(PieEntry(face, "刷脸：$strFace\n早餐：$face1\n午餐：$face2\n晚餐：$face3"))
        }
        if (card != 0f) {
            entries.add(PieEntry(card, "刷卡：$strCard\n早餐：$card1\n午餐：$card2\n晚餐：$card3"))
        }
        if (other != 0f) {
            entries.add(PieEntry(other, "其他：$strOther\n早餐：$other1\n午餐：$other2\n晚餐：$other3"))
        }
        //设置显示的颜色
        var colors = mutableListOf<Int>()
        colors.add(Color.parseColor("#14C9C8"));
        colors.add(Color.parseColor("#3489F5"));
        colors.add(Color.parseColor("#99CEFF"));
        //添加数据集合
        var dataSet = PieDataSet(entries,"")
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(resources.getColor(R.color.text_6));
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataSet.setDrawValues(false);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        var pieData = PieData(dataSet)
        binding.chartAmount.setData(pieData)
        binding.chartAmount.setNoDataText("")
        binding.chartAmount.notifyDataSetChanged()
        binding.chartAmount.invalidate()
    }

    private fun initChartNumber() {
        //设置图例的样式
        //UI
        // 不显示图例
        binding.chartNumber.legend.isEnabled = false
        // 不显示描述
        binding.chartNumber.description.isEnabled = false
        // 左右空出barWidth/2，更美观
        binding.chartNumber.setFitBars(true)
        // 不绘制网格
        binding.chartNumber.setDrawGridBackground(false)
        val xAxis: XAxis = binding.chartNumber.xAxis
        // 设置x轴显示在下方
        xAxis.position = XAxisPosition.BOTTOM
        // 设置x轴不画线
        xAxis.setDrawGridLines(false)
        xAxis.labelCount = 3
        // 设置自定义的ValueFormatter
        val labels = arrayOf("早餐", "午餐", "晚餐")
        xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
                val tep = value.toInt() //value的值是从0开始的
                labels[tep]
            }


        // 设置左y轴
        val yAxis: YAxis = binding.chartNumber.axisLeft
        // 设置y-label显示在图表外
        yAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
        // Y轴从0开始，不然会上移一点
        yAxis.axisMinimum = 0f
        // 设置y轴不画线
        yAxis.setDrawGridLines(true)
        yAxis.setLabelCount(6,false);
        yAxis.enableGridDashedLine(4f,2f,2f);
        yAxis.axisLineColor = requireContext().resources.getColor(R.color.transparent)
        // 不显示右y轴
        val rightAxis: YAxis = binding.chartNumber.axisRight
        rightAxis.isEnabled = false
          var meal1 = 0
        var meal2 = 0
        var meal3 = 0
        for (item in viewModel.canteenSummary.value?.feeTypeList!!){
                if ("早餐" ==item.key){
                    meal1 += item.value!!
                }else if ("午餐" ==item.key){
                    meal2 += item.value!!
                }else if ("晚餐" ==item.key){
                    meal3 += item.value!!
                }

        }
        //data
        val barEntries: MutableList<BarEntry> = ArrayList()
        barEntries.add(BarEntry(0f, meal1.toFloat()))
        barEntries.add(BarEntry(1f, meal2.toFloat()))
        barEntries.add(BarEntry(2f, meal3.toFloat()))
//        yAxis.valueFormatter = IAxisValueFormatter { value, axis ->
//            //value的值是从0开始的
//            barEntries[value.toInt()].y.toInt().toString()
//        }
        val barDataSet = BarDataSet(barEntries, "")

        // 设置颜色
        val colors: MutableList<Int> = ArrayList()
//        colors.add(R.color.color_58afff)
//        colors.add(R.color.color_3489f5)
//        colors.add(R.color.color_99ceff)
        colors.add(Color.parseColor("#58afff"))
        colors.add(Color.parseColor("#3489f5"))
        colors.add(Color.parseColor("#99ceff"))
        barDataSet.colors = colors
        val ba = BarData(barDataSet)
        ba.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
            value.toInt().toString()
        };
        ba.setBarWidth(0.5f)
        ba.setValueTextColor(resources.getColor(R.color.color_3489f5))
        binding.chartNumber.data = ba
        binding.chartNumber.setNoDataText("")
        binding.chartNumber.notifyDataSetChanged()
        binding.chartNumber.invalidate()
    }
}