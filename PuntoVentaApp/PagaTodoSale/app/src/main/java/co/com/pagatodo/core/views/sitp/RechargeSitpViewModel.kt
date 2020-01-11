package co.com.pagatodo.core.views.sitp

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestQueryCardBlackDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryInventoryDTO
import co.com.pagatodo.core.data.dto.response.ResponseCardRechargeDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryCardBlackDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryInitDateDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryInventoryDTO
import co.com.pagatodo.core.data.interactors.IRechargeSitpInteractor
import co.com.pagatodo.core.di.DaggerRechargeSitpComponent
import co.com.pagatodo.core.di.RechargeSitpModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RechargeSitpViewModel : ViewModel() {

    @Inject
    lateinit var rechargeSitpInteractor: IRechargeSitpInteractor

    sealed class ViewEvent {
        class Errors(val errorMessage: String) : ViewEvent()
        class GetRechargeSitpSuccess(val responseCardRechargeDTO: ResponseCardRechargeDTO) : ViewEvent()
        class GetQueryInitDataSuccess(val responseQueryInitDateDTO: ResponseQueryInitDateDTO) : ViewEvent()
        class GetQueryInventorySuccess(val responseQueryInventoryDTO: ResponseQueryInventoryDTO) : ViewEvent()
        class GetQueryCardBlackSuccess(val responseQueryCardBlackDTO: ResponseQueryCardBlackDTO) : ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerRechargeSitpComponent
            .builder()
            .rechargeSitpModule(RechargeSitpModule())
            .build()
            .inject(this)
    }


    @SuppressLint("CheckResult")
    fun getRechargeSitp(cardIdNumber: String, valueRecharge: Int) {
        rechargeSitpInteractor.getCardRecharge(cardIdNumber, valueRecharge)?.subscribe({
            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GetRechargeSitpSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
            }
        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }

    @SuppressLint("CheckResult")
    fun getQueryInitData() {
        rechargeSitpInteractor.getQueryInitData()?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GetQueryInitDataSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
            }

        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }

    @SuppressLint("CheckResult")
    fun getQueryInventory(requestQueryInventoryDTO: RequestQueryInventoryDTO) {
        rechargeSitpInteractor.getQueryInventory(requestQueryInventoryDTO)?.subscribe({
            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GetQueryInventorySuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
            }
        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }

    @SuppressLint("CheckResult")
    fun getQueryCardBlack(requestQueryCardBlackDTO: RequestQueryCardBlackDTO) {
        rechargeSitpInteractor.getQueryCardBlack(requestQueryCardBlackDTO)?.subscribe({
            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GetQueryCardBlackSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
            }
        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }
}