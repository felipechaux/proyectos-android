package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestColpensionesBepsMakeCollectionDTO
import co.com.pagatodo.core.data.dto.request.RequestColpensionesBepsQueryCollectionDTO
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.print.ColpensionesBepsPrintModel
import co.com.pagatodo.core.data.model.request.RequestColpensionesBepsMakeCollectionModel
import co.com.pagatodo.core.data.model.request.RequestColpensionesBepsQueryCollectionModel
import co.com.pagatodo.core.data.model.response.ResponseColpensionesBepsCollectionModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IColpensionesBepsRepository
import co.com.pagatodo.core.di.DaggerColpensionesBepsComponent
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface IColpensionesBepsInteractor {
    fun getProductParameters(): Observable<List<KeyValueModel>>?
    fun makeCollection(requestColpensionesBeps: RequestColpensionesBepsMakeCollectionModel): Observable<ResponseColpensionesBepsCollectionModel>?
    fun queryCollection(requestQueryCollection: RequestColpensionesBepsQueryCollectionModel): Observable<ResponseColpensionesBepsCollectionModel>?
    fun print(colpensionesBepsPrintModel: ColpensionesBepsPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}

class ColpensionesBepsInteractor: IColpensionesBepsInteractor {

    @Inject
    lateinit var repository: IColpensionesBepsRepository

    init {
        DaggerColpensionesBepsComponent.builder()
            .build().inject(this)
    }

    override fun getProductParameters(): Observable<List<KeyValueModel>>? {
        return repository.getProductParameters()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = arrayListOf<KeyValueModel>()
                it.forEach { ent ->
                    val model = KeyValueModel().apply {
                        key = ent.key
                        value = ent.value
                    }
                    response.add(model)
                }
                Observable.just(response)
            }
    }

    override fun makeCollection(requestColpensionesBeps: RequestColpensionesBepsMakeCollectionModel): Observable<ResponseColpensionesBepsCollectionModel>? {
        val request = RequestColpensionesBepsMakeCollectionDTO().apply {
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            productCode = R_string(R.string.code_product_colpensiones_beps)
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            value = requestColpensionesBeps.value
            documentType = requestColpensionesBeps.documentType
            documentNumber = requestColpensionesBeps.documentNumber
            birthday = requestColpensionesBeps.birthDay
            birthmonth = requestColpensionesBeps.birthMonth
        }

        if (!requestColpensionesBeps.phone.isNullOrEmpty()){
            request.phone = requestColpensionesBeps.phone
        }

        return repository.makeCollection(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseColpensionesBepsCollectionModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    transactionIdResponse = it.transactionId
                    message = it.message
                    authNumber = it.authNumber
                    voucherNumber = it.voucherNumber
                    beneficiaryName = it.beneficiaryName
                    documentType = it.documentType
                    documentNumber = it.documentNumber
                    value = it.value
                    identifier = it.identifier
                    rejectionReason = it.rejectionReason
                    printTransactionMessage = it.printMessageTransaction
                    rentCode = it.rentCode
                    printNumberCopies = it.printNumberCopies
                    serie1 = it.serie1
                    serie2 = it.serie2
                    currentSerie2 = it.currentSerie2
                }
                Observable.just(response)
            }
    }

    override fun queryCollection(requestQueryCollection: RequestColpensionesBepsQueryCollectionModel): Observable<ResponseColpensionesBepsCollectionModel>? {
        val request = RequestColpensionesBepsQueryCollectionDTO().apply {
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = R_string(R.string.code_product_colpensiones_beps)
            userType = getPreference(R_string(R.string.shared_key_user_type))
            transactionNumber = requestQueryCollection.transactionNumber
            queryType = requestQueryCollection.queryType
        }

        return repository.queryCollection(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseColpensionesBepsCollectionModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    authNumber = it.authNumber
                    voucherNumber = it.voucherNumber
                    beneficiaryName = it.beneficiaryName
                    documentType = it.documentType
                    documentNumber = it.documentNumber
                    value = it.value
                    identifier = it.identifier
                    printTransactionMessage = it.printTransactionMessage
                    rentCode = it.rentCode
                    printNumberCopies = it.printNumberCopies
                }
                Observable.just(response)
            }
    }

    override fun print(
        colpensionesBepsPrintModel: ColpensionesBepsPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        colpensionesBepsPrintModel.sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
        colpensionesBepsPrintModel.terminal = getPreference(R_string(R.string.shared_key_terminal_code))
        repository.print(colpensionesBepsPrintModel, callback)
    }
}