package com.stkj.cashier.app.home

import android.app.Application
import com.stkj.cashier.app.base.BaseModel
import com.stkj.cashier.app.base.ListViewModel
import com.stkj.cashier.bean.ImgMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@HiltViewModel
class MenuListViewModel @Inject constructor(application: Application, model: BaseModel?) :
    ListViewModel<ImgMenu>(application, model)