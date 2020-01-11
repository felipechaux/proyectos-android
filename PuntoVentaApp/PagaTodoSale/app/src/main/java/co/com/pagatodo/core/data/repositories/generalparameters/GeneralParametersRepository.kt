package co.com.pagatodo.core.data.repositories.generalparameters

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GeneralParametersDTO
import co.com.pagatodo.core.data.dto.request.RequestGeneralParametersDTO
import co.com.pagatodo.core.data.dto.request.RequestPaymillionaireParametersDTO
import co.com.pagatodo.core.data.dto.response.ResponsePaymillionaireParametersDTO
import co.com.pagatodo.core.data.repositories.BaseRepository
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import javax.inject.Singleton


interface IGeneralParametersRepository {
    fun getGeneralParameters(): Observable<GeneralParametersDTO>?
    fun getPayMillonaireParameters(): Observable<ResponsePaymillionaireParametersDTO>?
}

@Singleton
class GeneralParametersRepository: BaseRepository(),
    IGeneralParametersRepository {

    override fun getGeneralParameters(): Observable<GeneralParametersDTO>? {
        val request = RequestGeneralParametersDTO().apply {
            this.canalId = getCanalId()
            terminalCode = getTerminalCode()
            sellerCode = getSellerCode()
            userType = getUserType()
        }
        return ApiFactory.build()?.generalParameters(request)
    }

    override fun getPayMillonaireParameters(): Observable<ResponsePaymillionaireParametersDTO>? {
        val request = RequestPaymillionaireParametersDTO().apply {
            userType = getUserType()
            terminalCode = getTerminalCode()
            canalId = getCanalId()
            sellerCode = getSellerCode()
            productCode = R_string(R.string.code_product_paymillionaire)
            transactionTime = DateUtil.getCurrentDateInUnix().toString()
            transactionSequence = StorageUtil.getSequenceTransaction()
        }
        return ApiFactory.build()?.getPaymillionaireParameters(request)
    }
}