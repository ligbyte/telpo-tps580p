package com.stkj.cashier.app.setting

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.stkj.cashier.util.util.LogUtils
import com.stkj.cashier.util.util.SPUtils
import com.stkj.cashier.util.util.VolumeUtil
import com.king.android.ktx.fragment.argument
import com.stkj.cashier.App
import com.stkj.cashier.R
import com.stkj.cashier.app.Constants
import com.stkj.cashier.app.base.BaseFragment
import com.stkj.cashier.app.main.SettingViewModel
import com.stkj.cashier.databinding.*
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@AndroidEntryPoint
class VoiceSettingFragment : BaseFragment<SettingViewModel, VoiceSettingFragmentBinding>() {



    companion object {
        fun newInstance(): VoiceSettingFragment {
            return VoiceSettingFragment()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        var maxVolume = VolumeUtil(context).mediaMaxVolume
        var currentVolume = VolumeUtil(context).mediaVolume
        binding.sbVolume.max = maxVolume
        binding.sbVolume.progress = currentVolume
        binding.sbVolume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                VolumeUtil(context).mediaVolume = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        var voice = SPUtils.getInstance().getFloat(Constants.VOLUME_SPEECH,1f)
        binding.sbSpeak.progress = (voice*10).toInt()

        binding.sbSpeak.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               // App.volumeSpeech = progress/10f
                SPUtils.getInstance().put(Constants.VOLUME_SPEECH,progress/10f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        binding.tvTtsTest.setOnClickListener {
            ttsSpeak("欢迎使用慧餐宝")
        }
        var pickUpSuccess = SPUtils.getInstance().getString(Constants.PICK_UP_SUCCESS,"取餐成功")
        binding.etPickUpSuccess.setText(pickUpSuccess)
        binding.etPickUpSuccess.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                SPUtils.getInstance().put(Constants.PICK_UP_SUCCESS,binding.etPickUpSuccess.text.toString())
            }

        })
        var consumptionSuccess = SPUtils.getInstance().getString(Constants.CONSUMPTION_SUCCESS,"消费成功")
        binding.etConsumptionSuccess.setText(consumptionSuccess)
        binding.etConsumptionSuccess.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                SPUtils.getInstance().put(Constants.CONSUMPTION_SUCCESS,binding.etConsumptionSuccess.text.toString())
            }

        })
    }

    override fun getLayoutId(): Int {
        return R.layout.voice_setting_fragment
    }
}