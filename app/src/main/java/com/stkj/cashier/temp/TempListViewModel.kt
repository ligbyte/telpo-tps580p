package com.stkj.cashier.temp

import android.app.Application
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.ListViewModel
import com.stkj.cashier.bean.Bean
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class TempListViewModel @Inject constructor(application: Application, model: BaseModel?) : ListViewModel<Bean>(application, model){

    fun requestData(curPage: Int, pageSize: Int){
        launch {
            val result = apiService.getListBean("")
            if(isSuccess(result)){
                liveData.value = result.data
            }
        }
    }
}