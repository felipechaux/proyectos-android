package co.com.pagatodo.core.views.giro

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.interactors.IGiroInteractor
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.model.CaptureGiroModel
import co.com.pagatodo.core.data.model.GiroExFingerPrintModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerGiroComponent
import co.com.pagatodo.core.di.GiroModule
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseFragment
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import co.com.pagatodo.core.views.homemenu.giromenu.GiroMenuActivity
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GiroViewModel : BaseViewModel() {

    @Inject
    internal lateinit var giroInteractor: IGiroInteractor

    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    @Inject
    lateinit var localInteractor: ILocalInteractor

    sealed class ViewEvent {

        class Errors(val errorMessage: String) : ViewEvent()
        class Success(val message: String) : ViewEvent()
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

        class CitiesSuccess(val cities: List<CitiesDTO>?) : ViewEvent()
        class AgenciesSuccess(val agencies: List<AgencyDTO>?) : ViewEvent()
        class ThirdSuccess(
            val third: ResponseQueryThirdDTO?,
            val isOrigin: Boolean
        ) : ViewEvent()

        class GiroUserSuccess(val user: GiroUserDTO?) : ViewEvent()
        class GiroLoginSuccess(val responseGiroLoginDTO: ResponseGiroLoginDTO?) : ViewEvent()
        class ThirdNotFound(val isOrigin: Boolean) : ViewEvent()
        class CalculateConceptsSuccess(val concepts: List<GiroConceptDTO>) : ViewEvent()
        class CaptureGiroSuccess() : ViewEvent()
        class CreateThirdSuccess(val thirdDTO: ThirdDTO) : ViewEvent()
        class UpdateThirdSuccess() : ViewEvent()
        class QueryGiroSuccess(val queryGiro: ResponseQueryGiroDTO) : ViewEvent()
        class SendVoucherSuccess() : ViewEvent()
        class GiroCriterialSuccess(val listGiros: List<GiroCriteriaDTO>) : ViewEvent()
        class GiroConsultMovementSuccess(val movement: ResponseGiroMovementDTO) : ViewEvent()
        class GiroAuthUserSuccess() : ViewEvent()
        class GiroAuthUserError(val errorMessage: String) : ViewEvent()
        class GiroCheckRequestSuccess(val checkRequest: ResponseGiroCheckRequestDTO) : ViewEvent()
        class GiroOpenBoxSuccess(val responseGiroOpenBoxDTO: ResponseGiroOpenBoxDTO) : ViewEvent()
        class GiroCloseBoxSuccess(val responseGiroCloseBoxDTO: ResponseGiroCloseBoxDTO) :
            ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerGiroComponent
            .builder()
            .giroModule(GiroModule())
            .build()
            .inject(this)
    }

    @SuppressLint("CheckResult")
    fun getParametersGiros() {

        val parameterGiros = giroInteractor.getLocalParameterGiros()

        if (!parameterGiros.isload) {

            giroInteractor.getParameterGiros()?.subscribe({

                if (it.isSuccess!!) {


                    giroInteractor.saveLocalParameterGiros(it)
                    processParameterGiros(it)

                } else {
                    singleLiveEvent.value =
                        ViewEvent.Errors(it.message?:R_string(R.string.message_no_network))
                }

            }, {
                singleLiveEvent.value =
                    ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
            })

        } else {
            processParameterGiros(parameterGiros)
        }


    }

    private fun processParameterGiros(it: ResponseGiroParameterDTO) {

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
    fun getCities(city: String) {

        giroInteractor.getCities(city)?.subscribe({

            if (it.isSuccess!!) {

                singleLiveEvent.value = ViewEvent.CitiesSuccess(it.cities)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message!!)
            }

        }, {

            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun getAgencies(cityCode: String) {

        giroInteractor.getAgencies(cityCode)?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.AgenciesSuccess(it.agencies)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message!!)
            }

        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun getThird(id: String, typeId: String, isOrigin: Boolean) {

        giroInteractor.getThird(id, typeId)?.subscribe({

            if (it.isSuccess!!) {

                singleLiveEvent.value =
                    ViewEvent.ThirdSuccess(it, isOrigin)
            } else {

                singleLiveEvent.value = ViewEvent.ThirdNotFound(isOrigin)
            }


        }, {

            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))

        })

    }

    @SuppressLint("CheckResult")
    fun getGiroUser() {

        giroInteractor.getGiroUser()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.GiroUserSuccess(it.user)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.giro_user_disabled))
            }

        }, {

            singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.giro_user_disabled))

        })

    }

    @SuppressLint("CheckResult")
    fun calculateConcepts(
        thirdOrigin: QueryThirdDTO,
        thirdDestination: QueryThirdDTO,
        value: Int,
        includesFreight: Boolean,
        agencyDestination: String
    ) {

        giroInteractor.calculateConcepts(
            thirdOrigin,
            thirdDestination,
            value,
            includesFreight,
            agencyDestination
        )?.subscribe({

            if (it.isSuccess!!) {

                singleLiveEvent.value = ViewEvent.CalculateConceptsSuccess(it.concepts!!)

            } else {

                singleLiveEvent.value = ViewEvent.Errors(it.message!!)

            }
        }, {

            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))

        })

    }

    @SuppressLint("CheckResult")
    fun captureGiro(captureGiroModel: CaptureGiroModel) {

        validatePrinterStatus(printerStatusInfo) {


            giroInteractor.captureGiro(captureGiroModel)?.subscribe({

                if (it.isSuccess!!) {


                    val chainImpressionCaptation =
                        giroInteractor.getLocalGiroLogin().chainImpressionCaptation

                    val fareSend = createFareSend(
                        chainImpressionCaptation
                            ?: "",
                        it,
                        captureGiroModel.clientDestinationFull,
                        captureGiroModel.clientOriginFull,
                        captureGiroModel.agency!!,
                        captureGiroModel.value!!
                    )

                    val dataList = fareSend.split(RUtil.R_string(R.string.giro_print_next_line))

                    captureGiroPrint(
                        dataList,
                        baseFragment = if (DeviceUtil.isSalePoint()) GiroSendFragment() else null
                    )


                } else {
                    singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
                }

            }, {
                singleLiveEvent.value =
                    ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
            })

        }

    }

    @SuppressLint("CheckResult")
    fun girosLogin() {

        val localGiroLogin = giroInteractor.getLocalGiroLogin()

        if (!(localGiroLogin.isload ?: false)) {

            giroInteractor.girosLogin()?.subscribe({

                if (it.isSuccess!!) {
                    singleLiveEvent.value = ViewEvent.GiroLoginSuccess(it!!)
                } else {
                    singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.giro_user_disabled))
                }
            }, {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.giro_user_disabled))

            })
        } else {
            singleLiveEvent.value = ViewEvent.GiroLoginSuccess(localGiroLogin)
        }

    }


    @SuppressLint("CheckResult")
    fun loadTimeOutGiros() {

        localInteractor.getProductInfo(R_string(R.string.code_product_giro)).subscribe {

            SharedPreferencesUtil.savePreference(
                R_string(R.string.giro_parameter_time_out),
                it.parameters?.filter { it.key == R_string(R.string.giro_parameter_time_out) }?.map { it.value }?.last()!!
            )
        }

    }


    @SuppressLint("CheckResult")
    fun queryGiro(pin: String) {

        giroInteractor.queryGiro(pin)?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.QueryGiroSuccess(it!!)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message!!)
            }
        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    private fun createFareSend(
        data: String,
        it: ResponseGiroCaptureDTO,
        clientDestinationFull: ResponseThirdDTO?,
        clientOriginFull: ResponseThirdDTO?,
        agency: AgencyDTO, value: Int
    ): String {

        val sessionGiroUser = giroInteractor.getLocalGiroLogin()

        var fareSend = data

        fareSend = fareSend.replace("vPrefijovConsecutivo", it.bill ?: "")
        fareSend = fareSend.replace("vConsecutivo", "")
        fareSend = fareSend.replace("vCodigoAgencia", sessionGiroUser.user?.agency ?: "")
        fareSend = fareSend.replace("vPin", it.pin!!)
        fareSend = fareSend.replace(
            "vFecha",
            DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                DateUtil.getCurrentDate()
            )
        )
        fareSend = fareSend.replace("vNombreAgenciaOrigen", sessionGiroUser.user?.agencyName ?: "")
        fareSend = fareSend.replace("vNombreAsesor", sessionGiroUser.user?.userName ?: "")
        fareSend = fareSend.replace(
            "vNombreRemitente", "${clientOriginFull?.firstName
                ?: ""} ${clientOriginFull?.lastName ?: ""}"
        )
        fareSend =
            fareSend.replace("vEnmIdentificacionRemitente", hideInfo(clientOriginFull?.id ?: ""))
        fareSend =
            fareSend.replace("vEnmTelefonoRemitente", hideInfo(clientOriginFull?.mobile ?: ""))
        fareSend =
            fareSend.replace("vEnmDireccionRemitente", hideInfo(clientOriginFull?.address ?: ""))

        fareSend = fareSend.replace("vNombreAgenciaDestino", agency.name ?: "")

        fareSend = fareSend.replace(
            "vNombreDestinatario", "${clientDestinationFull?.firstName
                ?: ""} ${clientDestinationFull?.lastName ?: ""}"
        )
        fareSend = fareSend.replace(
            "vEnmIdentificacionDestinatario", hideInfo(
                clientDestinationFull?.id
                    ?: ""
            )
        )
        fareSend = fareSend.replace(
            "vEnmTelefonoDestinatario", hideInfo(
                clientDestinationFull?.mobile
                    ?: ""
            )
        )
        fareSend = fareSend.replace(
            "vEnmDireccionDestinatario", hideInfo(
                clientDestinationFull?.address
                    ?: ""
            )
        )

        var dataConcepts = ""
        it.concepts?.forEach {
            dataConcepts =
                "${dataConcepts}LEFT|${it.name}: $${formatValue(it.value?.toDouble() ?: 0.0)}&&"
        }

        fareSend = fareSend.replace("LEFT|vConceptos&&", dataConcepts)

        fareSend = fareSend.replace("vTotalAPagar", "$${formatValue(value.toDouble() ?: 0.0)}")

        fareSend = fareSend.replace("vImprimir", "FISICO")

        fareSend = fareSend.replace("vCorreoDestino", clientDestinationFull?.email?:"")

        fareSend = fareSend.replace("vCorreoOrigen", clientOriginFull?.email?:"")

        fareSend = fareSend.replace("vPruebaEntrega", "FISICO")

        return fareSend

    }

    private fun createFarePay(
        data: String,
        reference: String,
        responseQueryGiroDTO: ResponseQueryGiroDTO
    ): String {


        val sessionGiroUser = giroInteractor.getLocalGiroLogin()

        var farePayment = data
        farePayment = farePayment.replace("vPin", reference)
        farePayment = farePayment.replace("vCodigoAgencia", sessionGiroUser.user?.agency ?: "")

        farePayment = farePayment.replace(
            "vFecha",
            DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                DateUtil.getCurrentDate()
            )
        )

        farePayment = farePayment.replace(
            "vNombreAgenciaPagadora", sessionGiroUser.user?.agencyName
                ?: ""
        )
        farePayment = farePayment.replace("vNombreAsesor", sessionGiroUser.user?.userName ?: "")
        farePayment = farePayment.replace(
            "vNombreRemitente", "${responseQueryGiroDTO.clientOrigin?.firstName
                ?: ""} ${responseQueryGiroDTO.clientOrigin?.lastName ?: ""}"
        )
        farePayment = farePayment.replace(
            "vEnmIdentificacionRemitente", hideInfo(
                responseQueryGiroDTO.clientOrigin?.id
                    ?: ""
            )
        )
        farePayment = farePayment.replace(
            "vEnmTelefonoRemitente", hideInfo(
                responseQueryGiroDTO.clientDestination?.mobile
                    ?: ""
            )
        )
        farePayment = farePayment.replace(
            "vEnmDireccionRemitente", hideInfo(
                responseQueryGiroDTO.clientDestination?.address
                    ?: ""
            )
        )

        farePayment = farePayment.replace(
            "vNombreAgenciaDestino", responseQueryGiroDTO.agencyDestination?.name
                ?: ""
        )
        farePayment = farePayment.replace(
            "vNombreDestinatario", "${responseQueryGiroDTO.clientDestination?.firstName
                ?: ""} ${responseQueryGiroDTO.clientDestination?.lastName ?: ""}"
        )
        farePayment = farePayment.replace(
            "vEnmIdentificacionDestinatario", hideInfo(
                responseQueryGiroDTO.clientDestination?.id
                    ?: ""
            )
        )
        farePayment = farePayment.replace("vMotivoDevolucion", "")
        farePayment = farePayment.replace(
            "vEnmTelefonoDestinatario", hideInfo(
                responseQueryGiroDTO.clientDestination?.mobile
                    ?: ""
            )
        )
        farePayment = farePayment.replace(
            "vEnmDireccionDestinatario", hideInfo(
                responseQueryGiroDTO.clientDestination?.address
                    ?: ""
            )
        )

        farePayment = farePayment.replace(
            "vGiroPostal",
            "$${formatValue(responseQueryGiroDTO.giroValue?.toDouble() ?: 0.0)}"
        )

        farePayment = farePayment.replace(
            "vFlete",
            "$${formatValue(
                responseQueryGiroDTO.concepts?.filter { it.code == R_string(R.string.GIRO_QUERY_CONCEPT_CODE_FREIGHT) }?.last()?.value?.toDouble()
                    ?: 0.0
            )}"
        )
        //farePayment = farePayment.replace("vAdicional", responseQueryGiroDTO.concepts?.filter { it.code != R_string(R.string.GIRO_CONCEPT_CODE_FREIGHT) && it.code != R_string(R.string.GIRO_CONCEPT_CODE_VALUE) }?.last()?.value?.toDouble().toString())
        farePayment = farePayment.replace(
            "vAdicional",
            "$${formatValue(responseQueryGiroDTO.concepts?.filter { concept ->
                concept.code != R_string(
                    R.string.GIRO_QUERY_CONCEPT_CODE_VALUE
                ) && concept.code != R_string(R.string.GIRO_QUERY_CONCEPT_CODE_FREIGHT)
            }?.sumBy { concept -> concept.value!! }?.toDouble() ?: 0.0)}"
        )

        farePayment = farePayment.replace("vCorreoDestino", responseQueryGiroDTO.clientDestination?.email?:"")

        farePayment = farePayment.replace("vCorreoOrigen", responseQueryGiroDTO.clientOrigin?.email?:"")

        farePayment = farePayment.replace("vImprimir", "FISICO")

        farePayment = farePayment.replace("vPruebaEntrega", "FISICO")

        return farePayment
    }

    private fun createFarePayRePrint(
        data: String,
        responseGiroRePrintDTO: ResponseGiroRePrintDTO
    ): String {


        val sessionGiroUser = giroInteractor.getLocalGiroLogin()

        var farePayment = data
        farePayment = farePayment.replace("vPin", responseGiroRePrintDTO.pin ?: "")
        farePayment = farePayment.replace("vCodigoAgencia", sessionGiroUser.user?.agency ?: "")

        farePayment = farePayment.replace(
            "vFecha",
            DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                DateUtil.getCurrentDate()
            )
        )

        farePayment = farePayment.replace(
            "vNombreAgenciaPagadora", sessionGiroUser.user?.agencyName
                ?: ""
        )
        farePayment = farePayment.replace("vNombreAsesor", sessionGiroUser.user?.userName ?: "")
        farePayment = farePayment.replace(
            "vNombreRemitente", "${responseGiroRePrintDTO.clientGiro?.firstName
                ?: ""} ${responseGiroRePrintDTO.clientGiro?.lastName ?: ""}"
        )
        farePayment = farePayment.replace(
            "vEnmIdentificacionRemitente", hideInfo(
                responseGiroRePrintDTO.clientGiro?.id
                    ?: ""
            )
        )
        farePayment = farePayment.replace(
            "vEnmTelefonoRemitente", hideInfo(
                responseGiroRePrintDTO.clientDestination?.mobile
                    ?: ""
            )
        )
        farePayment = farePayment.replace(
            "vEnmDireccionRemitente", hideInfo(
                responseGiroRePrintDTO.clientDestination?.address
                    ?: ""
            )
        )

        farePayment = farePayment.replace(
            "vNombreAgenciaDestino", responseGiroRePrintDTO.agencyDestination?.name
                ?: ""
        )
        farePayment = farePayment.replace(
            "vNombreDestinatario", "${responseGiroRePrintDTO.clientDestination?.firstName
                ?: ""} ${responseGiroRePrintDTO.clientDestination?.lastName ?: ""}"
        )
        farePayment = farePayment.replace(
            "vEnmIdentificacionDestinatario", hideInfo(
                responseGiroRePrintDTO.clientDestination?.id
                    ?: ""
            )
        )
        farePayment = farePayment.replace("vMotivoDevolucion", "")
        farePayment = farePayment.replace(
            "vEnmTelefonoDestinatario", hideInfo(
                responseGiroRePrintDTO.clientDestination?.mobile
                    ?: ""
            )
        )
        farePayment = farePayment.replace(
            "vEnmDireccionDestinatario", hideInfo(
                responseGiroRePrintDTO.clientDestination?.address
                    ?: ""
            )
        )

        farePayment = farePayment.replace(
            "vGiroPostal",
            "$${formatValue(responseGiroRePrintDTO.giroValue?.toDouble() ?: 0.0)}"

        )

        farePayment = farePayment.replace(
            "vFlete",
            "$${formatValue(
                responseGiroRePrintDTO.concepts?.filter { it.code == R_string(R.string.GIRO_QUERY_CONCEPT_CODE_FREIGHT) }?.last()?.value?.toDouble()
                    ?: 0.0
            )}"
        )
        //farePayment = farePayment.replace("vAdicional", responseQueryGiroDTO.concepts?.filter { it.code != R_string(R.string.GIRO_CONCEPT_CODE_FREIGHT) && it.code != R_string(R.string.GIRO_CONCEPT_CODE_VALUE) }?.last()?.value?.toDouble().toString())
        farePayment = farePayment.replace(
            "vAdicional",

            "$${formatValue(responseGiroRePrintDTO.concepts?.filter { concept ->
                concept.code != R_string(R.string.GIRO_QUERY_CONCEPT_CODE_VALUE) &&
                        concept.code != R_string(R.string.GIRO_QUERY_CONCEPT_CODE_FREIGHT)
            }?.sumBy { concept -> concept.value!! }?.toDouble() ?: 0.0)}"
        )


        farePayment = farePayment.replace("vCorreoDestino", responseGiroRePrintDTO.clientDestination?.email?:"")

        farePayment = farePayment.replace("vCorreoOrigen", responseGiroRePrintDTO.clientGiro?.email?:"")

        farePayment = farePayment.replace("vImprimir", "FISICO")

        farePayment = farePayment.replace("vPruebaEntrega", "FISICO")

        return "$farePayment&&CENTER|REIMPRESION"
    }

    private fun createFareSendRePrint(
        data: String,
        responseGiroRePrintDTO: ResponseGiroRePrintDTO
    ): String {

        val sessionGiroUser = giroInteractor.getLocalGiroLogin()

        var fareSend = data

        fareSend = fareSend.replace("vPrefijovConsecutivo", responseGiroRePrintDTO.bill ?: "")
        fareSend = fareSend.replace("vConsecutivo", "")
        fareSend = fareSend.replace("vCodigoAgencia", sessionGiroUser.user?.agency ?: "")
        fareSend = fareSend.replace("vPin", responseGiroRePrintDTO.pin!!)
        fareSend = fareSend.replace(
            "vFecha",
            DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                DateUtil.getCurrentDate()
            )
        )
        fareSend = fareSend.replace("vNombreAgenciaOrigen", sessionGiroUser.user?.agencyName ?: "")
        fareSend = fareSend.replace("vNombreAsesor", sessionGiroUser.user?.userName ?: "")
        fareSend = fareSend.replace(
            "vNombreRemitente", "${responseGiroRePrintDTO.clientGiro?.firstName
                ?: ""} ${responseGiroRePrintDTO.clientGiro?.lastName ?: ""}"
        )
        fareSend = fareSend.replace(
            "vEnmIdentificacionRemitente",
            hideInfo(responseGiroRePrintDTO.clientGiro?.id ?: "")
        )
        fareSend = fareSend.replace(
            "vEnmTelefonoRemitente",
            hideInfo(responseGiroRePrintDTO.clientGiro?.mobile ?: "")
        )
        fareSend = fareSend.replace(
            "vEnmDireccionRemitente",
            hideInfo(responseGiroRePrintDTO.clientGiro?.address ?: "")
        )
        fareSend = fareSend.replace(
            "vNombreAgenciaDestino",
            responseGiroRePrintDTO.agencyDestination?.name ?: ""
        )
        fareSend = fareSend.replace(
            "vNombreDestinatario", "${responseGiroRePrintDTO.clientDestination?.firstName
                ?: ""} ${responseGiroRePrintDTO.clientDestination?.lastName ?: ""}"
        )
        fareSend = fareSend.replace(
            "vEnmIdentificacionDestinatario", hideInfo(
                responseGiroRePrintDTO.clientDestination?.id
                    ?: ""
            )
        )
        fareSend = fareSend.replace(
            "vEnmTelefonoDestinatario", hideInfo(
                responseGiroRePrintDTO.clientDestination?.mobile
                    ?: ""
            )
        )
        fareSend = fareSend.replace(
            "vEnmDireccionDestinatario", hideInfo(
                responseGiroRePrintDTO.clientDestination?.address
                    ?: ""
            )
        )

        var dataConcepts = ""
        responseGiroRePrintDTO.concepts?.forEach {
            dataConcepts =
                "${dataConcepts}LEFT|${it.name}: $${formatValue(it.value?.toDouble() ?: 0.0)}&&"
        }

        fareSend = fareSend.replace("LEFT|vConceptos&&", dataConcepts)

        fareSend = fareSend.replace(
            "vTotalAPagar",
            "$${formatValue(
                responseGiroRePrintDTO.concepts?.sumBy { it.value!! }?.toDouble() ?: 0.0
            )}"
        )

        fareSend = fareSend.replace("vImprimir", "FISICO")

        fareSend = fareSend.replace("vCorreoDestino", responseGiroRePrintDTO.clientDestination?.email?:"")

        fareSend = fareSend.replace("vCorreoOrigen", responseGiroRePrintDTO.clientGiro?.email?:"")

        fareSend = fareSend.replace("vPruebaEntrega", "FISICO")

        return "$fareSend&&CENTER|REIMPRESION"

    }

    @SuppressLint("CheckResult")
    fun createThird(
        thirdDTO: ThirdDTO,
        fingerPrintL: String,
        fingerPrintR: String,
        activity: GiroMenuActivity?,
        giroActivity: GiroActivity?,
        typeCreateUser: TypeCreateUser
    ) {

        giroInteractor.createThird(thirdDTO, fingerPrintL, fingerPrintR)?.subscribe({


            if (it.isSuccess!!) {

                if (typeCreateUser == TypeCreateUser.USER_SEND_CREATE_ORIGEN || typeCreateUser == TypeCreateUser.USER_SEND_CREATE_DESTINATION) {

                    if (DeviceUtil.isSalePoint()) {

                        activity?.hideDialogProgress()
                        activity?.navigateToOptionSalePointReturn(typeCreateUser)

                    } else {
                        giroActivity?.hideDialogProgress()
                        giroActivity?.navigateToOptionReturn(typeCreateUser)
                    }

                } else {
                    singleLiveEvent.value = ViewEvent.CreateThirdSuccess(it.third!!)
                }


            } else {

                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")

            }


        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun updateThird(
        thirdDTO: ThirdDTO,
        activity: GiroMenuActivity?,
        giroActivity: GiroActivity?,
        typeCreateUser: TypeCreateUser
    ) {

        giroInteractor.updateThird(thirdDTO)?.subscribe({

            if (it.isSuccess ?: false) {

                if (DeviceUtil.isSalePoint()) {
                    activity?.hideDialogProgress()
                    activity?.navigateToOptionSalePointReturn(typeCreateUser)

                } else {
                    giroActivity?.hideDialogProgress()
                    giroActivity?.navigateToOptionReturn(typeCreateUser)

                }

            } else {
                if (DeviceUtil.isSalePoint()) activity?.hideDialogProgress() else giroActivity?.hideDialogProgress()

            }


        }, {
            singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun paymentGiro(
        responseQueryGiroDTO: ResponseQueryGiroDTO,
        reference: String,
        unusualOperations: String = "",
        unusualOperationsCode: String = "",
        description: String = "",
        notes: String = "",
        sendMail: Boolean = false
    ) {

        validatePrinterStatus(printerStatusInfo) {
            giroInteractor.paymentGiro(
                responseQueryGiroDTO,
                reference,
                unusualOperations,
                unusualOperationsCode,
                description,
                notes
            )?.subscribe({

                if (it.isSuccess ?: false) {

                    val clientOrigin = ClientGiroDTO().apply {
                        this.id = responseQueryGiroDTO.clientDestination?.id
                        this.typeId = responseQueryGiroDTO.clientDestination?.typeId
                        this.isEnrolled = responseQueryGiroDTO.clientDestination?.isEnrolled
                        this.isExoneratedFootprint =
                            responseQueryGiroDTO.clientDestination?.exoneratedFootprint
                        this.email = responseQueryGiroDTO.clientDestination?.email
                    }

                    val printPayment = giroInteractor.getLocalGiroLogin().stringPrintPayment
                    val farePay = createFarePay(
                        printPayment
                            ?: "", reference, responseQueryGiroDTO
                    )

                    if (sendMail) {

                        sendVoucher(
                            clientOrigin,
                            reference,
                            responseQueryGiroDTO.giroValue.toString(),
                            farePay
                        )

                    } else {

                        val dataList = farePay.split(R_string(R.string.giro_print_next_line))

                        paymentGiroPrint(
                            dataList,
                            baseFragment = if (DeviceUtil.isSalePoint()) GiroConsultFragment() else null
                        )


                    }

                } else {
                    singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
                }

            }, {
                singleLiveEvent.value =
                    ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
            })

        }
    }


    private fun captureGiroPrint(
        dataList: List<String>,
        isReprint: Boolean = false,
        baseFragment: BaseFragment? = null
    ) {

        giroInteractor.printSentGiro(dataList, isReprint) {

            if (it == PrinterStatus.OK) {

                validateRePrintCapture(dataList, isReprint, baseFragment)

            } else
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_print_device)
                    )
                )

        }
    }

    private fun paymentGiroPrint(
        dataList: List<String>,
        isReprint: Boolean = false,
        baseFragment: BaseFragment? = null
    ) {

        giroInteractor.printPaymentGiro(dataList, isReprint) {

            if (it == PrinterStatus.OK) {

                //VALIDAR SI HAY QUE REIMPRIMIR
                validateRePrintPayment(dataList, isReprint, baseFragment)


            } else
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_print_device)

                    )
                )

        }
    }

    private fun validateRePrintPayment(
        dataList: List<String>,
        isReprint: Boolean,
        baseFragment: BaseFragment?
    ) {

        if (SharedPreferencesUtil.getPreference<String>(R_string(R.string.giro_parameter_reprint_pay)) == "SI" && !isReprint) {

            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogClosure(
                    "",
                    R_string(R.string.giro_msm_reprint),
                    baseFragment
                ) {
                    paymentGiroPrint(dataList, true, baseFragment)

                })

        } else {

            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    if (isReprint == false) R_string(R.string.giro_payment_success_title) else R_string(
                        R.string.giro_reprint_dialog_title
                    ),
                    if (isReprint == false) R_string(R.string.giro_payment_success_message) else R_string(
                        R.string.giro_reprint_dialog_msm
                    ),
                    true, baseFragment
                )
            )

        }
    }

    private fun validateRePrintCapture(
        dataList: List<String>,
        isReprint: Boolean,
        baseFragment: BaseFragment?
    ) {

        if (SharedPreferencesUtil.getPreference<String>(R_string(R.string.giro_parameter_reprint_send)) == "SI" && !isReprint) {

            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogClosure(
                    "",
                    R_string(R.string.giro_msm_reprint),
                    baseFragment
                ) {
                    captureGiroPrint(dataList, true, baseFragment)

                })

        } else {

            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    if (isReprint == false) R_string(R.string.giro_send_success_title) else R_string(
                        R.string.giro_reprint_dialog_title
                    ),
                    if (isReprint == false) R_string(R.string.giro_send_success_message) else R_string(
                        R.string.giro_reprint_dialog_msm
                    ),
                    true, baseFragment
                )
            )

        }
    }

    @SuppressLint("CheckResult")
    fun sendVoucher(
        clientGiro: ClientGiroDTO,
        reference: String,
        value: String,
        print: String
    ) {


        giroInteractor.sendVoucher(
            clientGiro,
            reference,
            value,
            print
        )?.subscribe({

            if (it.isSuccess ?: false) {

                singleLiveEvent.value = ViewEvent.SendVoucherSuccess()

            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
            }


        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun authFingerPrintUser(
        identificationType: String,
        identificationNumber: String,
        fingerPrint: String
    ) {


        giroInteractor.authFingerPrintUser(
            identificationType,
            identificationNumber,
            fingerPrint
        )?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GiroAuthUserSuccess()
                BaseObservableViewModel.baseSubject.onNext(BaseEvents.HideProgressDialog)
            } else {
                singleLiveEvent.value = ViewEvent.GiroAuthUserError(it.message ?: "")
            }

        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun giroCriteria(
        idClient: String,
        typeIdClient: String,
        reference: String,
        dateStart: String,
        dateEnd: String,
        typeUser: String
    ) {


        giroInteractor.giroCriteria(
            idClient,
            typeIdClient,
            reference,
            dateStart,
            dateEnd,
            typeUser
        )?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GiroCriterialSuccess(it.giros!!)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message
                        ?: R_string(R.string.message_error_transaction)
                )
            }

        }, {
            if (it is ConnectException) {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.Errors(
                     R_string(R.string.message_error_transaction)
                )
            }
        })

    }

    @SuppressLint("CheckResult")
    fun giroConsultMovement(date: String) {

        giroInteractor.giroConsultMovement(date.replace("/", ""))?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GiroConsultMovementSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message
                        ?: R_string(R.string.message_error_transaction)
                )
            }

        }, {
            if (it is ConnectException) {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.Errors(
                    R_string(R.string.message_error_transaction)
                )
            }
        })


    }

    @SuppressLint("CheckResult")
    fun giroRePrint(reference: String, typeBill: String) {

        validatePrinterStatus(printerStatusInfo) {


            giroInteractor.giroRePrint(reference, typeBill)?.subscribe({

                if (it.isSuccess ?: false) {

                    it.pin = reference

                    if (typeBill == TypeBillGiro.PAY.raw) {

                        giroRePrintPayment(it)

                    } else {

                        giroRePrintCapture(it)

                    }


                } else {
                    singleLiveEvent.value = ViewEvent.Errors(
                        it.message
                            ?: R_string(R.string.message_error_transaction)
                    )
                }

            }, {
                singleLiveEvent.value =
                    ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
            })

        }
    }

    private fun giroRePrintPayment(responseGiroRePrintDTO: ResponseGiroRePrintDTO) {

        // crear tiquete pago
        val printPayment = giroInteractor.getLocalGiroLogin().stringPrintPayment

        val farePay = createFarePayRePrint(printPayment ?: "", responseGiroRePrintDTO)

        val dataList = farePay.split(R_string(R.string.giro_print_next_line))

        paymentGiroPrint(
            dataList,
            true,
            if (DeviceUtil.isSalePoint()) GiroReprintFragment() else null
        )

    }

    private fun giroRePrintCapture(responseGiroRePrintDTO: ResponseGiroRePrintDTO) {

        val chainImpressionCaptation =
            giroInteractor.getLocalGiroLogin().chainImpressionCaptation

        val fareSend = createFareSendRePrint(chainImpressionCaptation ?: "", responseGiroRePrintDTO)

        val dataList = fareSend.split(RUtil.R_string(R.string.giro_print_next_line))

        captureGiroPrint(
            dataList,
            true,
            if (DeviceUtil.isSalePoint()) GiroReprintFragment() else null
        )
    }

    @SuppressLint("CheckResult")
    fun exonerateFingerPrint(requestGiroExFingerPrintDTO: GiroExFingerPrintModel) {

        giroInteractor.exonerateFingerPrint(requestGiroExFingerPrintDTO)?.subscribe({

            if (it.isSuccess!!)
                singleLiveEvent.value = ViewEvent.Success(it.message ?: "")
            else
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")

        }, {
            singleLiveEvent.value =
                ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun giroAuthorization(authorizationModel: AuthorizationModel) {

        giroInteractor.giroAuthorization(authorizationModel)?.subscribe({
            if (it.isSuccess!!)
                singleLiveEvent.value = ViewEvent.Success(it.message ?: "")
            else
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
        }, {
            singleLiveEvent.value =
                ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun modifyNotes(reference: String, notes: String) {

        giroInteractor.modifyNotes(reference, notes)?.subscribe({
            if (it.isSuccess!!)
                singleLiveEvent.value = ViewEvent.Success(it.message ?: "")
            else
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
        }, {
            singleLiveEvent.value =
                ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }

    @SuppressLint("CheckResult")
    fun giroCheckRequest() {

        giroInteractor.giroCheckRequest()?.subscribe({
            if (it.isSuccess!! && it.responseCode != R_string(R.string.GIRO_ERROR_GR_020))
                singleLiveEvent.value = ViewEvent.GiroCheckRequestSuccess(it)
            else
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
        }, {
            singleLiveEvent.value =
                ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }

    @SuppressLint("CheckResult")
    fun openBox() {

        giroInteractor.openBox()?.subscribe({

            if (it.isSuccess!!)
                singleLiveEvent.value = ViewEvent.GiroOpenBoxSuccess(it)
            else
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
        }, {
            singleLiveEvent.value =
                ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }

    @SuppressLint("CheckResult")
    fun closeBox(
        bannerOpenning: Int,
        balance: Int,
        bannerIncome: Int,
        bannerExpenses: Int
    ) {
        validatePrinterStatus(printerStatusInfo) {

            giroInteractor.closeBox(
                bannerOpenning,
                balance,
                bannerIncome,
                bannerExpenses
            )?.subscribe({

                if (it.isSuccess!!)
                    printCloseBox(it.listPrint!!, it.message ?: "")
                else
                    singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")


            }, {
                singleLiveEvent.value =
                    ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
            })

        }


    }

    fun printCheckRequest(girorCheckRequestsDTO: GirorCheckRequestsDTO) {

        validatePrinterStatus(printerStatusInfo) {


            giroInteractor.printCheckRequest(girorCheckRequestsDTO, false) {

                if (it != PrinterStatus.OK) {
                    BaseObservableViewModel.baseSubject.onNext(
                        BaseEvents.ShowAlertDialogInMenu(
                            "",
                            R_string(R.string.message_error_print_device)

                        )
                    )
                }

            }

        }

    }

    private fun printCloseBox(data: List<String>, message: String) {


        giroInteractor.printCloseBox(data, false) {

            if (it == PrinterStatus.OK) {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        message

                    )
                )
            } else {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_print_device)

                    )
                )
            }


        }

    }


    private fun hideInfo(data: String): String {

        var dataInfo = ""

        if (data.length > 4) {
            dataInfo = "****${data.substring(data.length - 4, data.length)} "
        } else {

            dataInfo = "****$data"
        }

        return dataInfo
    }


}


