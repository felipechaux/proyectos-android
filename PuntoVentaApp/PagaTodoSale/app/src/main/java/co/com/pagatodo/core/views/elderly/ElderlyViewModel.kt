package co.com.pagatodo.core.views.elderly

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.interactors.IElderlyInteractor
import co.com.pagatodo.core.data.interactors.IGiroInteractor
import co.com.pagatodo.core.data.model.ElderlyPointedFingerModel
import co.com.pagatodo.core.data.model.ElderlyThirdModel
import co.com.pagatodo.core.data.model.print.ElderlyPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerElderlyComponent
import co.com.pagatodo.core.di.ElderlyModule
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElderlyViewModel : BaseViewModel() {

    @Inject
    internal lateinit var elderlyInteractor: IElderlyInteractor

    @Inject
    internal lateinit var giroInteractor: IGiroInteractor

    sealed class ViewEvent {
        class Errors(val errorMessage: String) : ViewEvent()
        class QueryGetReprintSuccess(val responseDTO: ResponseElderlyGetReprintDTO) : ViewEvent()
        class QueryAuthorizedTerminalSuccess(val responseDTO: ResponseElderlyAuthorizedTerminalDTO) :
            ViewEvent()

        class PointedFingersSuccess(val responseDTO: ResponsePointedFingersDTO?) : ViewEvent()
        class AuthenticateFootprintSuccess(val responseDTO: ResponseElderlyAuthenticateFootprintDTO) :
            ViewEvent()

        class CheckSaveIdSuccess(val responseDTO: ResponseElderlySaveIdDTO) : ViewEvent()
        class MakeSubsidyPaymentSuccess(val responseDTO: ResponseElderlyMakeSubsidyPaymentDTO?) :
            ViewEvent()

        class QueryIdSuccess(val responseDTO: ResponseThirdDTO) : ViewEvent()
        class QueryIdNotFound(val errorMessage: String) : ViewEvent()
        class SaveFingerPrint(val responseDTO: ResponseElderlySaveFingerPrintDTO) : ViewEvent()
        class QuerySubsidy(val responseDTO: ResponseElderlyQuerySubsidyDTO) : ViewEvent()
        class CitiesSuccess(val cities: List<CitiesDTO>?) : ViewEvent()
        class CreateThirdSuccess(val thirdDTO: ThirdDTO) : ViewEvent()
        class GiroLoginSuccess(val responseGiroLoginDTO: ResponseGiroLoginDTO?) : ViewEvent()

        class ParameterSuccess(
            val listDocumentType: List<String>?,
            val listDepartmentDTO: List<DepartmentDTO>?,
            val listCountryDTO: List<CountryDTO>?,
            val listEconomicActivity: List<String>?,
            val listUnusualOperations: List<String>?,
            val listTypePerson: List<String>?,
            val listSocialStratum: List<String>?,
            val typeRequest: List<GiroTypeRequestsDTO>?
        ) : ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerElderlyComponent
            .builder()
            .elderlyModule(ElderlyModule())
            .build()
            .inject(this)
    }

    @SuppressLint("CheckResult")
    fun elderlyQueryGetReprint(third: ElderlyThirdModel) {
        elderlyInteractor.elderlyQueryGetReprint(third)?.subscribe({

            if (it.isSuccess?:false) {
                payPrint(ElderlyPrintModel().apply {
                    this.header = "${it.businessName}\nNIT:${it.businessNit}"
                    this.title = it.title
                    this.dateTransaction = "${it.transactionProductDate} ${it.transactionProductTime}"
                    this.numberTransaction = it.transactionId
                    this.idBeneficiary = it.documentNumber
                    this.nameBeneficiary = "${it.name} ${it.firstLastName} ${it.secondLastName}"
                    this.value = it.subsidioValue
                    this.city = it.cityName
                    this.office = it.officeCode
                    this.sellerCode = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
                    this.sv = it.salePlace
                    this.codeTerminal = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_terminal_code))
                    this.isReprint = true

                })
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.message_no_network))
            }

        }, {
            showErros(it)
        })
    }

    @SuppressLint("CheckResult")
    fun girosLogin() {

        val localGiroLogin = giroInteractor.getLocalGiroLogin()

        if (!(localGiroLogin.isload ?: false)) {

            giroInteractor.girosLogin()?.subscribe({

                if (it.isSuccess!!) {
                    singleLiveEvent.value = ViewEvent.GiroLoginSuccess(it!!)
                } else {
                    singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.giro_user_disabled))
                }
            }, {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.giro_user_disabled))

            })
        } else {
            singleLiveEvent.value = ViewEvent.GiroLoginSuccess(localGiroLogin)
        }

    }

    @SuppressLint("CheckResult")
    fun elderlyQueryAuthorizedTerminal() {
        elderlyInteractor.elderlyQueryAuthorizedTerminal()?.subscribe({
            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.QueryAuthorizedTerminalSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.message_no_network))
            }

        }, {
            showErros(it)
        })
    }

    @SuppressLint("CheckResult")
    fun getParametersElderly() {

        val parameterGiros = giroInteractor.getLocalParameterGiros()

        if (!parameterGiros.isload) {

            giroInteractor.getParameterGiros()?.subscribe({

                if (it.isSuccess!!) {

                    processParameterElderly(it)

                } else {
                    singleLiveEvent.value =
                        ViewEvent.Errors(R_string(R.string.message_no_network))
                }

            }, {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
            })

        }else{
            processParameterElderly(parameterGiros)
        }



    }

    private fun processParameterElderly(it: ResponseGiroParameterDTO) {

        val documentTypes =
            it.parameters?.filter { it.key == RUtil.R_string(R.string.CODE_PARAM_GIRO_DOCUMENT_TYPE) }
                ?.last()
        val economicActivity =
            it.parameters?.filter { it.key == RUtil.R_string(R.string.CODE_PARAM_GIRO_ECONOMIC_ACTIVITY) }
                ?.last()

        val unusualOperations =
            it.parameters?.filter { it.key == RUtil.R_string(R.string.CODE_PARAM_GIRO_UNUSUAL_OPERATIONS) }
                ?.last()

        val typePerson =
            it.parameters?.filter { it.key == RUtil.R_string(R.string.CODE_PARAM_GIRO_TYPE_PERSON) }
                ?.last()

        val socialStratum =
            it.parameters?.filter { it.key == RUtil.R_string(R.string.CODE_PARAM_GIRO_SOCIAL_STRATUM) }
                ?.last()

        val reprintSend =
            it.parameters?.filter { it.key == R_string(R.string.giro_parameter_reprint_send) }
                ?.last()?.value.toString()

        val reprintPay =
            it.parameters?.filter { it.key == R_string(R.string.giro_parameter_reprint_pay) }
                ?.last()?.value.toString()


        val documentTypesSplit = documentTypes?.value?.split(",")
        val economicActivitySplit = economicActivity?.value?.split(",")
        val unusualOperationsSplit = unusualOperations?.value?.split(",")
        val typePersonSplit = typePerson?.value?.split(",")
        val socialStratumSplit = socialStratum?.value?.split(",")

        SharedPreferencesUtil.savePreference(
            R_string(R.string.giro_parameter_reprint_send),
            reprintSend
        )
        SharedPreferencesUtil.savePreference(
            R_string(R.string.giro_parameter_reprint_pay),
            reprintPay
        )

        singleLiveEvent.value = ViewEvent.ParameterSuccess(
            documentTypesSplit,
            it.departaments,
            it.countrys,
            economicActivitySplit,
            unusualOperationsSplit,
            typePersonSplit,
            socialStratumSplit,
            it.typeRequests
        )


    }

    @SuppressLint("CheckResult")
    fun elderlyPointedFingers(third: ElderlyThirdModel) {

        elderlyInteractor.elderlyPointedFingers(third)?.subscribe({

            if (it.isSuccess?:false) {
                singleLiveEvent.value = ViewEvent.PointedFingersSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.message_no_network))
            }


        }, {
            showErros(it)
        })
    }

    @SuppressLint("CheckResult")
    fun elderlyQueryId(third: ElderlyThirdModel) {

        elderlyInteractor.elderlyQueryId(third)?.subscribe({

            if(it.isSuccess?:false)
                queryThird(third)
            else
                singleLiveEvent.value = ViewEvent.QueryIdNotFound(it.message?:R_string(R.string.title_failed_transaction))

        }, {
            showErros(it)
        })

    }

    @SuppressLint("CheckResult")
    fun queryThird(third: ElderlyThirdModel){

        giroInteractor.getThird(third.document?:"",third.documentType?:"")?.subscribe ({

            if(it.isSuccess?:false){
                singleLiveEvent.value = ViewEvent.QueryIdSuccess(it.third?:ResponseThirdDTO())
            }else{
                singleLiveEvent.value = ViewEvent.QueryIdNotFound(it.message?:R_string(R.string.title_failed_transaction))
            }

        },{
            singleLiveEvent.value = ViewEvent.QueryIdNotFound(R_string(R.string.title_failed_transaction))
        })

    }

    @SuppressLint("CheckResult")
    fun createThird(
        thirdDTO: ThirdDTO
    ) {

        elderlyInteractor.createThird(thirdDTO)?.subscribe({


            if (it.isSuccess!!) {

                singleLiveEvent.value = ViewEvent.CreateThirdSuccess(it.third!!)


            } else {

                singleLiveEvent.value = ViewEvent.Errors(it.message ?:R_string(R.string.title_failed_transaction))

            }


        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun elderlySaveFingerPrint(third: ElderlyThirdModel, pointedFinger: ElderlyPointedFingerModel) {

        elderlyInteractor.elderlySaveFingerPrint(third, pointedFinger)?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.SaveFingerPrint(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.title_failed_transaction))
            }


        }, {
            showErros(it)
        })
    }

    @SuppressLint("CheckResult")
    fun elderlyQuerySubsidy(third: ElderlyThirdModel) {

        elderlyInteractor.elderlyQuerySubsidy(third)?.subscribe({

            if (it.isSuccess?:false) {
                singleLiveEvent.value = ViewEvent.QuerySubsidy(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.title_failed_transaction))
            }

        }, {
            showErros(it)
        })
    }

    @SuppressLint("CheckResult")
    fun elderlyQueryAuthenticateFootprint(third: ElderlyThirdModel) {

        elderlyInteractor.elderlyQueryAuthenticateFootprint(third)?.subscribe({

            if (it.isSuccess?:false) {
                singleLiveEvent.value = ViewEvent.AuthenticateFootprintSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.title_failed_transaction))
            }

        }, {
            showErros(it)
        })
    }

    @SuppressLint("CheckResult")
    fun elderlySaveId(third: ElderlyThirdModel) {

        elderlyInteractor.elderlySaveId(third)?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.CheckSaveIdSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.title_failed_transaction))
            }

        }, {
            showErros(it)
        })
    }

    @SuppressLint("CheckResult")
    fun elderlyQueryMakeSubsidyPayment( elderlySubsidyPaymentModel: ElderlySubsidyPaymentModel) {

        elderlyInteractor.elderlyQueryMakeSubsidyPayment(elderlySubsidyPaymentModel)?.subscribe({

            if (it.isSuccess?:false) {
                payPrint(ElderlyPrintModel().apply {
                    this.header = "${it.businessName}\nNIT:${it.businessNit}"
                    this.title = it.title
                    this.dateTransaction = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,Date())
                    this.numberTransaction = it.transactionId
                    this.idBeneficiary = elderlySubsidyPaymentModel.titularDocumentId
                    this.nameBeneficiary = "${elderlySubsidyPaymentModel.titularFirstLastName} ${elderlySubsidyPaymentModel.titularFirstLastName} "
                    this.value = "$${formatValue(elderlySubsidyPaymentModel.payValue?.toDouble()?:0.0)}"
                    this.city = it.cityName
                    this.office = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_office_code))
                    this.sellerCode = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
                    this.sv = it.salePlacement
                    this.codeTerminal = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_terminal_code))
                    this.isReprint = false

                    })
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message?:R_string(R.string.title_failed_transaction))
            }

        }, {
            showErros(it)
        })
    }

    private fun showErros(it: Throwable) {

       if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                ViewEvent.Errors(  R_string(R.string.message_error_transaction))
        } else {
            singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
        }

    }

    @SuppressLint("CheckResult")
    fun getCities(city: String) {


        elderlyInteractor.getCities(city)?.subscribe({

            if (it.isSuccess!!) {

                singleLiveEvent.value = ViewEvent.CitiesSuccess(it.cities)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message!!)
            }

        }, {

            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    fun payPrint(elderlyPrintModel:ElderlyPrintModel){

        elderlyPrintModel.apply {

            this.footer = "Verifique que el valor  impreso en el  recibo  corresponde al valor entregado"
        }

        elderlyInteractor.printPayment(elderlyPrintModel){

            if(it == PrinterStatus.OK){

                BaseObservableViewModel
                    .baseSubject
                    .onNext(BaseEvents
                        .ShowAlertDialogInMenu("", R_string(R.string.text_print_success_transaccion),true))
            }else{
                BaseObservableViewModel
                    .baseSubject
                    .onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_print_device))
                )
            }

        }

    }

}