package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.BillRequestDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.print.BbvaBillPayPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IBbvaRepository
import co.com.pagatodo.core.di.BbvaModule
import co.com.pagatodo.core.di.DaggerBbvaComponent
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


interface IBbvaInteractor{

    fun accountDeposit (requestBbvaDTO : RequestBbvaDTO): Observable<ResponseBbvaDTO>?
    fun billPayment(request: BillRequestDTO): Observable<ResponseBbvaBillPaymentDTO>?
    fun paymentObligations(requestBbvaDTO : RequestBbvaDTO): Observable<ResponseBbvaDTO>?
    fun withdrawalAndBalance(requestBbvaDTO : RequestBbvaDTO): Observable<ResponseBbvaDTO>?
    fun reprintTransaction(): Observable<ResponseBbvaReprintTransactionDTO>?
    fun closeBox(): Observable<ResponseBbvaCloseBoxDTO>?
    fun getCities(cityName: String): Observable<ResponseQueryCitiesDTO>?
    fun getParameter(): Observable<ResponseQueryParameterDTO>?
    fun validateBill(request: BillRequestDTO): Observable<ResponseBbvaValidateBillDTO>?
    fun print(bbvaBillPayPrintModel: BbvaBillPayPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}

class BbvaInteractor : IBbvaInteractor {

    @Inject
    lateinit var repository: IBbvaRepository

    init {

        DaggerBbvaComponent
            .builder()
            .bbvaModule(BbvaModule())
            .build()
            .inject(this)
    }

    override fun accountDeposit(requestBbvaDTO : RequestBbvaDTO): Observable<ResponseBbvaDTO>? {

        requestBbvaDTO.apply {
            channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            userType =  RUtil.R_string(R.string.code_user_type)
            productCode = RUtil.R_string(R.string.code_product_bbva_deposit)
            terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            macAdress = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            loginClient = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            volMachine = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))

        }

        return repository.accountDeposit(requestBbvaDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }

    }

    override fun billPayment(request: BillRequestDTO): Observable<ResponseBbvaBillPaymentDTO>? {

        val requestPayBillDTO = RequestBbvaBillPaymentDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_bbva_deposit)
            this.value = request.value
            this.bill = request
        }

        return repository.billPayment(requestPayBillDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }

    }

    override fun paymentObligations(requestBbvaDTO : RequestBbvaDTO): Observable<ResponseBbvaDTO>? {

      requestBbvaDTO.apply {
          channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
          userType =  RUtil.R_string(R.string.code_user_type)
          productCode = RUtil.R_string(R.string.code_product_bbva_deposit)
          terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
          sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
          macAdress = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
          loginClient = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
          volMachine = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
        }

        return repository.paymentObligations(requestBbvaDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }

    }

    override fun withdrawalAndBalance(requestBbvaDTO : RequestBbvaDTO): Observable<ResponseBbvaDTO>? {

        requestBbvaDTO.apply {
            channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            userType =  RUtil.R_string(R.string.code_user_type)
            productCode = RUtil.R_string(R.string.code_product_bbva_withdrawal)
            terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            loginClient = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))

        }

        return repository.withdrawalAndBalance(requestBbvaDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }

    }

    override fun reprintTransaction(): Observable<ResponseBbvaReprintTransactionDTO>? {

        val request = RequestBbvaReprintTransactionDTO().apply {

        }

        return repository.reprintTransaction(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }

    }

    override fun closeBox(): Observable<ResponseBbvaCloseBoxDTO>? {

        val request = RequestBbvaCloseBoxDTO().apply {

        }

        return repository.closeBox(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }
    }

    override fun getCities(cityName: String): Observable<ResponseQueryCitiesDTO>? {

        val requestQueryCitiesDTO = RequestQueryCitiesDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.cityName = cityName
        }

        return repository
            .getCities(requestQueryCitiesDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)

            }

    }

    override fun getParameter(): Observable<ResponseQueryParameterDTO>? {
        val requestQueryParameterDTO = RequestQueryParameterDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_bbva_deposit)
        }

        return repository.getParameters(requestQueryParameterDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)

            }
    }

    override fun validateBill(request: BillRequestDTO): Observable<ResponseBbvaValidateBillDTO>? {

        val requestValidateBillDTO = RequestBbvaValidateAndPayBillDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_bbva_deposit)
            this.value = request.value
            this.bill = request
        }

        return repository.validateBill(requestValidateBillDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)

            }
    }

    override fun print(
        bbvaBillPayPrintModel: BbvaBillPayPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        bbvaBillPayPrintModel.terminal =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
        repository.print(bbvaBillPayPrintModel, callback)

    }


}
