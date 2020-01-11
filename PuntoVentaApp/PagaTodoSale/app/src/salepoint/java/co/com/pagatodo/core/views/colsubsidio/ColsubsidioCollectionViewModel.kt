package co.com.pagatodo.core.views.colsubsidio

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.interactors.IColsubsidioCollectionInteractor
import co.com.pagatodo.core.data.model.ColsubsidioObligationModel
import co.com.pagatodo.core.di.ColsubsidioModule
import co.com.pagatodo.core.di.DaggerColsubsidioComponent
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import javax.inject.Inject

class ColsubsidioCollectionViewModel: ViewModel() {

    @Inject
    internal lateinit var colsubsidioCollectionInteractor: IColsubsidioCollectionInteractor

    lateinit var obligation: MutableLiveData<ColsubsidioObligationModel>

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
        class ResponseSuccessColsubsidioGetInitialData(val responseDTO: ResponseColsubsidioGetInitialDataDTO?): ViewEvent()
        class ResponseSuccessColsubsidioGetPaymentMethods(val responseDTO: ResponseColsubsidioGetPaymentMethodsDTO?): ViewEvent()
        class ResponseSuccessColsubsidioGetProducts(val responseDTO: ResponseColsubsidioGetProductsDTO?): ViewEvent()
        class ResponseSuccessColsubsidioGetBumperProducts(val responseDTO: ResponseColsubsidioBumperProductsDTO?): ViewEvent()
        class ResponseSuccessColsubsidioCollectObligation(val responseDTO: ResponseColsubsidioCollectObligationDTO?): ViewEvent()

    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerColsubsidioComponent
            .builder()
            .colsubsidioModule(ColsubsidioModule())
            .build()
            .inject(this)

        if (!::obligation.isInitialized){
            obligation = MutableLiveData()
        }
    }

    @SuppressLint("CheckResult")
    fun queryColsubsidioGetInitialData() {
        colsubsidioCollectionInteractor.queryColsubsidioGetInitialData()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessColsubsidioGetInitialData(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })
    }
    @SuppressLint("CheckResult")
    fun queryColsubsidioGetPaymentMethods() {
        colsubsidioCollectionInteractor.queryColsubsidioGetPaymentMethods()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessColsubsidioGetPaymentMethods(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })
    }
    @SuppressLint("CheckResult")
    fun queryColsubsidioGetProducts() {
        colsubsidioCollectionInteractor.queryColsubsidioGetProducts()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessColsubsidioGetProducts(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })
    }
    @SuppressLint("CheckResult")
    fun queryColsubsidioGetBumperProducts() {
        colsubsidioCollectionInteractor.queryColsubsidioGetBumperProducts()?.subscribe({

            if (it.productList?.isNotEmpty()!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessColsubsidioGetBumperProducts(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })
    }
    @SuppressLint("CheckResult")
    fun queryColsubsidioCollectObligation() {
        colsubsidioCollectionInteractor.queryColsubsidioCollectObligation()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessColsubsidioCollectObligation(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })
    }

}