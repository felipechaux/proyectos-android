package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.ProductBoxDTO
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.data.dto.request.RequestCurrentBoxDTO
import co.com.pagatodo.core.data.model.request.RequestCurrentBoxModel
import co.com.pagatodo.core.data.model.response.ResponseCurrentBoxModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.ICurrentBoxRepository
import co.com.pagatodo.core.di.CurrentBoxModule
import co.com.pagatodo.core.di.DaggerCurrentBoxComponent
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentBoxInteractor {

    @Inject
    lateinit var currentBoxRepository: ICurrentBoxRepository

    init {
        DaggerCurrentBoxComponent
            .builder()
            .currentBoxModule(CurrentBoxModule())
            .build().inject(this)
    }

    fun checkCurrentBox(currentDate: String? = null): Observable<ResponseCurrentBoxModel>? {
        val model = createRequestCurrentBoxModel(currentDate)
        val dto = convertRequestCurrentBoxModelToDTO(model)
        return currentBoxRepository
            .checkCurrentBox(dto)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseCurrentBoxModel().apply {
                    responseCode = it.responseCode
                    success = it.isSuccess?:false
                    transactionDate = it.transactionDate
                    transactionTime = it.transactionHour
                    message = it.message
                    sale = it.sale
                    lottery = it.lottery
                    services = it.services
                    others = it.others
                    totalSale = it.totalSale
                    productsBox  = convertResponseProductsBoxDtoToModel(it.productsBox)
                }
                Observable.just(response)
            }
    }

    private fun createRequestCurrentBoxModel(currentDate: String? = null): RequestCurrentBoxModel {
        return RequestCurrentBoxModel().apply {
            canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            queryType = getPreference<String>(RUtil.R_string(R.string.shared_key_query_type)).toUpperCase()
            dateConsultation = currentDate
        }
    }

    private fun convertResponseProductsBoxDtoToModel(listDto: List<ProductBoxDTO>?): List<ProductBoxModel> {
        val listModel = arrayListOf<ProductBoxModel>()
        listDto?.forEach {
            val entity = ProductBoxModel().apply {
                productCode = it.productCode
                name = it.name
                saleValue = it.saleValue
            }
            listModel.add(entity)
        }
        return listModel
    }

    private fun convertRequestCurrentBoxModelToDTO(model: RequestCurrentBoxModel): RequestCurrentBoxDTO {
        return RequestCurrentBoxDTO().apply {
            canalId = model.canalId
            sellerCode = model.sellerCode
            terminalCode = model.terminalCode
            userType = model.userType
            queryType = model.queryType
            dateConsultation = model.dateConsultation
        }
    }

    fun print(responseCurrentBox: ResponseCurrentBoxModel,stubs:String, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val sellerName = getPreference<String>(R_string(R.string.shared_key_seller_code))
        val value = responseCurrentBox.totalSale
        responseCurrentBox.productsBox?.let { value?.let { it1 -> currentBoxRepository.print(it, it1, stubs, sellerName, callback) } }
    }
}