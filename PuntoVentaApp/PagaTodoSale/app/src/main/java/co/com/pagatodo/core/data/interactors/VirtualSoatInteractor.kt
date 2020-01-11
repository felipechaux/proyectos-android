package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.InputDataDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.BaseResponseDTO
import co.com.pagatodo.core.data.dto.response.ResponseIssuePolicyDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryVirtualSoatDTO
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IVirtualSoatRepository
import co.com.pagatodo.core.di.DaggerVirtualSoatComponent
import co.com.pagatodo.core.di.VirtualSoatModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface IVirtualSoatInteractor {

    fun getQueryVirtualSoat(inputDataDTO: InputDataDTO) : Observable<ResponseQueryVirtualSoatDTO>?
    fun getIssuePolicy(requestIssuePolicyDTO: RequestIssuePolicyDTO): Observable<ResponseIssuePolicyDTO>?
    fun policyConfirm(requestPolicyConfirmDTO: RequestPolicyConfirmDTO): Observable<BaseResponseDTO>?
    fun printSoat(virtualSoatPrintModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)

}

@Singleton
class VirtualSoatInteractor: IVirtualSoatInteractor {

    @Inject
    lateinit var repository: IVirtualSoatRepository

    init {

        DaggerVirtualSoatComponent
            .builder()
            .virtualSoatModule(VirtualSoatModule())
            .build()
            .inject(this)
    }

    override fun getQueryVirtualSoat(inputDataDTO: InputDataDTO): Observable<ResponseQueryVirtualSoatDTO>? {

        val requestQueryVirtualSoatDTO: RequestQueryVirtualSoatDTO = RequestQueryVirtualSoatDTO().apply {

            val mInputDataDTO = inputDataDTO.apply {
                this.cashierCode = RUtil.R_string(R.string.code_cashier)
            }

            this.mac = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.productCode =RUtil.R_string(R.string.code_product_virtual_soat)
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.entityCode = RUtil.R_string(R.string.code_entity_virtual_soat)
            this.userType = RUtil.R_string(R.string.code_user_type)
            this.inputData = mInputDataDTO
            this.transactionData = TransactionDataDTO().apply {
                this.clientData = ClientDataDTO().apply {
                    this.userName = RUtil.R_string(R.string.code_cashier)
                    this.userType = RUtil.R_string(R.string.code_user_type)
                    this.login = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
                    this.terminal = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
                }
                this.id = "1"
                this.date = "${Date().time}"
            }
        }

        return repository
            .getQueryVirtualSoat(requestQueryVirtualSoatDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun getIssuePolicy(requestIssuePolicyDTO: RequestIssuePolicyDTO): Observable<ResponseIssuePolicyDTO>? {

        val methodPay =PaymentMethodDTO().apply {
            this.paymentMethod = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.code_payment_method))
        }

        val requestIssuePolicyDTO: RequestIssuePolicyDTO = RequestIssuePolicyDTO().apply {
            this.collectingValue = RUtil.R_string(R.string.code_collecting_value)
            this.licensePlate = RUtil.R_string(R.string.code_license_plate)
            this.brand = RUtil.R_string(R.string.code_brand)
            this.transactionId = "1287"
            this.takerDocumentType = RUtil.R_string(R.string.code_document_type)
            this.paymentType = RUtil.R_string(R.string.code_payment_type)
            this.phone = RUtil.R_string(R.string.code_phone)
            this.channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.productCode = RUtil.R_string(R.string.code_product_virtual_soat)
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.paymentMethod = arrayListOf(methodPay)
            this.terminal = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.userType =RUtil.R_string(R.string.code_user_type)
            this.entityCode = RUtil.R_string(R.string.code_entity_virtual_soat)
        }

        return repository
            .getIssuePolicy(requestIssuePolicyDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun policyConfirm(requestPolicyConfirmDTO: RequestPolicyConfirmDTO): Observable<BaseResponseDTO>? {

        requestPolicyConfirmDTO.apply {
            this.channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.productCode = RUtil.R_string(R.string.code_product_virtual_soat)
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = RUtil.R_string(R.string.code_user_type)
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.macAddress = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.state = RUtil.R_string(R.string.code_status_type)
            this.entityCode = RUtil.R_string(R.string.code_entity_virtual_soat)
        }

        return repository
            .policyConfirm(requestPolicyConfirmDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun printSoat(
        virtualSoatPrintModel: VirtualSoatPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        repository.printSoat(virtualSoatPrintModel,callback)
    }

}