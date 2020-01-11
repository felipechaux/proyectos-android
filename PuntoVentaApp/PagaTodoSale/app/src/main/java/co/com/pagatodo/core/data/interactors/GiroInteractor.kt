package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.CaptureGiroModel
import co.com.pagatodo.core.data.model.GiroExFingerPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IGiroRepository
import co.com.pagatodo.core.di.DaggerGiroComponent
import co.com.pagatodo.core.di.GiroModule
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface IGiroInteractor {

    fun getParameterGiros(): Observable<ResponseGiroParameterDTO>?
    fun getCities(cityName: String): Observable<ResponseQueryCitiesDTO>?
    fun getAgencies(cityCode: String): Observable<ResponseQueryAgencyDTO>?
    fun getThird(id: String, typeId: String): Observable<ResponseQueryThirdDTO>?
    fun calculateConcepts(
            thirdOrigin: QueryThirdDTO,
            thirdDestination: QueryThirdDTO,
            value: Int,
            includesFreight: Boolean,
            agencyDestination: String
    ): Observable<ResponseGirosCalculateConceptsDTO>?

    fun captureGiro(captureGiroModel: CaptureGiroModel): Observable<ResponseGiroCaptureDTO>?

    fun getGiroUser(): Observable<ResponseGiroUserDTO>?
    fun girosLogin(): Observable<ResponseGiroLoginDTO>?
    fun printSentGiro(data: List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printPaymentGiro(data: List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun getLocalGiroLogin(): ResponseGiroLoginDTO
    fun createThird(thirdDTO: ThirdDTO,fingerPrintL:String,fingerPrintR: String): Observable<ResponseThirdCreateDTO>?
    fun updateThird(thirdDTO: ThirdDTO): Observable<BaseResponseDTO>?
    fun queryGiro(pin: String): Observable<ResponseQueryGiroDTO>?
    fun paymentGiro(
        responseQueryGiroDTO: ResponseQueryGiroDTO,
            reference: String,
            unusualOperations: String,
            unusualOperationsCode: String,
            description: String,
            notes: String
    ): Observable<ResponsePaymentGiroDTO>?

    fun sendVoucher(
        clientGiro: ClientGiroDTO,
        reference: String,
        value: String,
        print: String
    ): Observable<ResponseGiroSendVoucherDTO>?

    fun giroCriteria(
            idClient: String,
            typeIdClient: String,
            reference: String,
            dateStart: String,
            dateEnd: String,
            typeUser:String
    ): Observable<ResponseGiroCriteriaDTO>?

    fun giroConsultMovement(date: String): Observable<ResponseGiroMovementDTO>?

    fun authFingerPrintUser(identificationType: String,
                            identificationNumber: String,
                            fingerPrint: String
    ): Observable<BaseResponseDTO>?

    fun giroRePrint(reference: String,typeBill:String): Observable<ResponseGiroRePrintDTO>?
    fun exonerateFingerPrint(requestGiroExFingerPrintDTO: GiroExFingerPrintModel):Observable<BaseResponseDTO>?
    fun giroAuthorization(authorizationModel: AuthorizationModel):Observable<BaseResponseDTO>?
    fun modifyNotes(reference:String,notes:String): Observable<BaseResponseDTO>?
    fun giroCheckRequest(): Observable<ResponseGiroCheckRequestDTO>?
    fun openBox(): Observable<ResponseGiroOpenBoxDTO>?
    fun closeBox(bannerOpenning: Int,
                 balance: Int,
                 bannerIncome:  Int,
                 bannerExpenses:  Int): Observable<ResponseGiroCloseBoxDTO>?
    fun printCheckRequest(girorCheckRequestsDTO: GirorCheckRequestsDTO, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printCloseBox(data:List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun saveLocalParameterGiros(responseGiroParameterDTO: ResponseGiroParameterDTO)
    fun getLocalParameterGiros(): ResponseGiroParameterDTO
}

@Singleton
class GiroInteractor : IGiroInteractor {


    @Inject
    lateinit var giroRepository: IGiroRepository

    init {

        DaggerGiroComponent
                .builder()
                .giroModule(GiroModule())
                .build()
                .inject(this)

    }

    constructor()

    constructor(giroRepository: IGiroRepository) {
        this.giroRepository = giroRepository
    }

    override fun getParameterGiros(): Observable<ResponseGiroParameterDTO>? {
        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroParameterDTO = RequestGiroParameterDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.user = userLocal
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }

        }

        return giroRepository
                .getParameterGiros(requestGiroParameterDTO)
                ?.subscribeOn(Schedulers.computation())
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

        return giroRepository
                .getCities(requestQueryCitiesDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {

                    Observable.just(it)

                }


    }

    override fun getAgencies(cityCode: String): Observable<ResponseQueryAgencyDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()

        val responseQueryAgencyDTO = RequestQueryAgencyDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.codeCityDestination = cityCode
        }

        return giroRepository
                .getAgencies(responseQueryAgencyDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {

                    Observable.just(it)

                }


    }

    override fun getThird(id: String, typeId: String): Observable<ResponseQueryThirdDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()

        val requestQueryThirdDTO = RequestQueryThirdDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.third = QueryThirdDTO().apply {
                this.id = id
                this.typeId = typeId
            }

        }

        return giroRepository
                .getThird(requestQueryThirdDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {

                    Observable.just(it)

                }
    }

    override fun calculateConcepts(
            thirdOrigin: QueryThirdDTO,
            thirdDestination: QueryThirdDTO,
            value: Int,
            includesFreight: Boolean,
            agencyDestination: String
    ): Observable<ResponseGirosCalculateConceptsDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()

        val requestGirosCalculateConceptsDTO = RequestGirosCalculateConceptsDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.value = value
            this.agencyDestination = agencyDestination
            this.includesFreight = includesFreight
            this.thirdDestination = thirdDestination
            this.thirdOrigin = thirdOrigin

        }

        return giroRepository
                .calculateConcepts(requestGirosCalculateConceptsDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {

                    Observable.just(it)

                }
    }


    override fun getGiroUser(): Observable<ResponseGiroUserDTO>? {

        val requestUserDTO = RequestGiroUserDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
        }

        return giroRepository
                .getGiroUser(requestUserDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {

                    Observable.just(it)
                }

    }

    override fun captureGiro(captureGiroModel: CaptureGiroModel): Observable<ResponseGiroCaptureDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val origin = ClientGiroDTO().apply {
            this.id = captureGiroModel.clientOriginFull?.id
            this.typeId = captureGiroModel.clientOriginFull?.typeId
            this.isExoneratedFootprint = captureGiroModel.clientOriginFull?.isExoneratedFootprint
            this.isEnrolled = captureGiroModel.clientOriginFull?.isEnrolled
            this.firstName =  captureGiroModel.clientOriginFull?.firstName
            this.lastName = captureGiroModel.clientOriginFull?.lastName
            this.secondLastName = captureGiroModel.clientOriginFull?.secondLastName
            this.address = captureGiroModel.clientOriginFull?.address
            this.email = captureGiroModel.clientOriginFull?.email
            this.telephone  = captureGiroModel.clientOriginFull?.telephone
            this.mobile = captureGiroModel.clientOriginFull?.mobile
            this.fingerPrint = captureGiroModel.clientGiro?.fingerPrint
            this.imgFingerPrint = captureGiroModel.clientGiro?.imgFingerPrint
        }

        val destination = ClientGiroDTO().apply {
            this.id = captureGiroModel.clientDestinationFull?.id
            this.typeId = captureGiroModel.clientDestinationFull?.typeId
            this.firstName =  captureGiroModel.clientDestinationFull?.firstName
            this.lastName = captureGiroModel.clientDestinationFull?.lastName
            this.secondLastName = captureGiroModel.clientDestinationFull?.secondLastName
            this.address = captureGiroModel.clientDestinationFull?.address
            this.email = captureGiroModel.clientDestinationFull?.email
            this.telephone  = captureGiroModel.clientDestinationFull?.telephone
            this.mobile = captureGiroModel.clientDestinationFull?.mobile
        }


        val requestGiroCaptureDTO = RequestGiroCaptureDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.value = captureGiroModel.value
            this.includesFreight = captureGiroModel.includesFreight
            this.serie1 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie1))
            this.serie2 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie2))
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
                this.agencyName = userLocal.agencyName
                this.agencyAddress = userLocal.agencyAddress
            }
            this.clientGiro = origin
            this.clientDestination = destination
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }
            this.concepts = captureGiroModel.concepts
            this.agency = captureGiroModel.agency
        }

        if (captureGiroModel.unusualOperations!!.isNotEmpty()) {
            requestGiroCaptureDTO.apply {
                this.unusualOperations = captureGiroModel.unusualOperations
                this.unusualOperationsCode = captureGiroModel.unusualOperationsCode
            }
        }

        if (captureGiroModel.description!!.isNotEmpty()) {
            requestGiroCaptureDTO.apply {
                this.observations = captureGiroModel.description
            }
        }

        return giroRepository
                .captureGiro(requestGiroCaptureDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }

    }

    override fun girosLogin(): Observable<ResponseGiroLoginDTO>? {

        val requestGiroLoginDTO = RequestGiroLoginDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = ""
                this.password = ""
                this.agency = ""
            }
        }

        return giroRepository
                .girosLogin(requestGiroLoginDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    if(it.isSuccess?:false)
                        giroRepository.saveLocalGiroLogin(it)

                    Observable.just(it)
                }

    }

    override fun printSentGiro(
            data: List<String>,
            isRetry: Boolean,
            callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        giroRepository.printSentGiro(data, isRetry, callback)
    }

    override fun getLocalGiroLogin(): ResponseGiroLoginDTO {

        return giroRepository.getLocalGiroLogin()
    }

    override fun createThird(thirdDTO: ThirdDTO,fingerPrintL:String,fingerPrintR: String): Observable<ResponseThirdCreateDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestThirdCreateDTO = RequestThirdCreateDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.biometrics = sesionGiroLocal.employsBiometrics
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.third = thirdDTO
            this.fingerPrintL = fingerPrintL
            this.fingerPrintR = fingerPrintR

        }

        if(thirdDTO.biometrics != null && thirdDTO.biometrics != "" ){
            requestThirdCreateDTO.apply{
                this.biometrics = thirdDTO.biometrics
            }
        }

        return giroRepository
                .createThird(requestThirdCreateDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {

                    Observable.just(it)
                }


    }

    override fun updateThird(thirdDTO: ThirdDTO): Observable<BaseResponseDTO>? {
        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestThirdCreateDTO = RequestThirdCreateDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.third = thirdDTO

        }

        if(thirdDTO.fPrintThirdL != null && thirdDTO.fPrintThirdR != null){

            requestThirdCreateDTO.apply {
                fingerPrintL = thirdDTO.fPrintThirdL
                fingerPrintR = thirdDTO.fPrintThirdR
                third?.sender = false
                biometrics = sesionGiroLocal.employsBiometrics
                isPay = true

            }

        }


        return giroRepository
                .updateThird(requestThirdCreateDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }
    }

    override fun queryGiro(pin: String): Observable<ResponseQueryGiroDTO>? {
        val userLocal = giroRepository.getLocalGiroUser()
        val requestQueryGiroDTO = RequestQueryGiroDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = giroRepository.getLocalGiroLogin().box?.boxCode
            }
            this.reference = pin
        }

        return giroRepository
                .queryGiro(requestQueryGiroDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }
    }

    override fun printPaymentGiro(
            data: List<String>,
            isRetry: Boolean,
            callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        giroRepository.printPaymentGiro(data, isRetry, callback)
    }

    override fun paymentGiro(
            responseQueryGiroDTO: ResponseQueryGiroDTO,
            reference: String,
            unusualOperations: String,
            unusualOperationsCode: String,
            description: String,
            notes: String
    ): Observable<ResponsePaymentGiroDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()

        val clientOrigin = ClientGiroDTO().apply {
            this.id = responseQueryGiroDTO.clientOrigin?.id
            this.typeId = responseQueryGiroDTO.clientOrigin?.typeId
            this.isExoneratedFootprint = responseQueryGiroDTO.clientOrigin?.exoneratedFootprint
            this.isEnrolled = responseQueryGiroDTO.clientOrigin?.isEnrolled
            this.firstName =  responseQueryGiroDTO.clientOrigin?.firstName
            this.lastName = responseQueryGiroDTO.clientOrigin?.lastName
            this.secondLastName = responseQueryGiroDTO.clientOrigin?.secondLastName
            this.address = responseQueryGiroDTO.clientOrigin?.address
            this.email = responseQueryGiroDTO.clientOrigin?.email
            this.telephone  = responseQueryGiroDTO.clientOrigin?.telephone
            this.mobile = responseQueryGiroDTO.clientOrigin?.mobile
        }

        val clientDestination= ClientGiroDTO().apply {
            this.id = responseQueryGiroDTO.clientDestination?.id
            this.typeId = responseQueryGiroDTO.clientDestination?.typeId
            this.firstName =  responseQueryGiroDTO.clientDestination?.firstName
            this.lastName = responseQueryGiroDTO.clientDestination?.lastName
            this.secondLastName = responseQueryGiroDTO.clientDestination?.secondLastName
            this.address = responseQueryGiroDTO.clientDestination?.address
            this.email = responseQueryGiroDTO.clientDestination?.email
            this.telephone  = responseQueryGiroDTO.clientDestination?.telephone
            this.mobile = responseQueryGiroDTO.clientDestination?.mobile
            this.isExoneratedFootprint = responseQueryGiroDTO.clientDestination?.exoneratedFootprint
            this.isEnrolled = responseQueryGiroDTO.clientDestination?.isEnrolled
            this.fingerPrint = responseQueryGiroDTO.clientDestination?.fingerPrint
            this.imgFingerPrint= responseQueryGiroDTO.clientDestination?.imgFingerPrint
        }

        val requestPaymentGiroDTO = RequestPaymentGiroDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro_pago)
            this.concepts = responseQueryGiroDTO.concepts
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = giroRepository.getLocalGiroLogin().box?.boxCode
            }
            this.agencyOrigin = responseQueryGiroDTO.agencyOrigen

            this.agencyDestination = responseQueryGiroDTO.agencyDestination

            this.clientGiro = clientOrigin
            this.clientDestination = clientDestination

            this.serie1 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie1))
            this.serie2 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie2))
            this.reference = reference

            this.value = responseQueryGiroDTO.giroValue
            this.bill = responseQueryGiroDTO.bill
            this.prefBill = responseQueryGiroDTO.prefBill
        }

        if (unusualOperations.isNotEmpty()) {
            requestPaymentGiroDTO.apply {

                this.unusualOperationsCode = unusualOperationsCode
            }
        }

        if (description.isNotEmpty()) {
            requestPaymentGiroDTO.apply {
                this.unusualOperations = unusualOperations
            }
        }

        if (notes.isNotEmpty()) {
            requestPaymentGiroDTO.apply {
                this.notes = notes
            }
        }

        return giroRepository
                .paymentGiro(requestPaymentGiroDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }

    }

    override fun sendVoucher(
        clientGiro: ClientGiroDTO,
        reference: String,
        value: String,
        print: String
    ): Observable<ResponseGiroSendVoucherDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()

        val requestGiroSendVoucherDTO = RequestGiroSendVoucherDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.clientGiro = clientGiro
            this.reference = reference
            this.value = value
            this.serie1 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie1))
            this.serie2 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie2))
            this.date = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, Date())
            this.hour = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.HHMMSS, Date())
            this.typeGiro = RUtil.R_string(R.string.TYPE_GIRO_CAPTURE)
            this.typePrint = "01"
            this.sendMail = true
            this.print = print
        }


        return giroRepository
                .sendVoucher(requestGiroSendVoucherDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }

    }

    override fun giroCriteria(
            idClient: String,
            typeIdClient: String,
            reference: String,
            dateStart: String,
            dateEnd: String,
            typeUser:String
    ): Observable<ResponseGiroCriteriaDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroCriteriaDTO = RequestGiroCriteriaDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)


            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }

            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }

        }

        if (reference.isEmpty()) {

            requestGiroCriteriaDTO.apply {
                this.dateStart = dateStart.replace("/", "")
                this.dateEnd = dateEnd.replace("/", "")
                this.client = ClientSmallDTO().apply {
                    this.typeId = typeIdClient
                    this.id = idClient
                }
                this.typeThird = typeUser
            }


        } else {
            requestGiroCriteriaDTO.apply {
                this.reference = reference
            }
        }


        return giroRepository
                .giroCriteria(requestGiroCriteriaDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }
    }


    override fun giroConsultMovement(date: String): Observable<ResponseGiroMovementDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroMovementDTO = RequestGiroMovementDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }
            this.dateConsult = date

        }

        return giroRepository
                .giroConsultMovement(requestGiroMovementDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }

    }

    override fun authFingerPrintUser(identificationType: String,
                                     identificationNumber: String,
                                     fingerPrint: String): Observable<BaseResponseDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()

        val requestGiroAuthenticateEnrolledUser = RequestAuthFingerPrintUserDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.fingerPrint = fingerPrint
            this.id = identificationNumber
            this.idtype = identificationType
        }

        return giroRepository
                .authFingerPrintUser(requestGiroAuthenticateEnrolledUser)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }

    }

    override fun giroRePrint(reference: String,typeBill:String): Observable<ResponseGiroRePrintDTO>? {

        var productCode = RUtil.R_string(R.string.code_product_giro)

        if(typeBill == RUtil.R_string(R.string.giro_reprint_type_bill_pay)){
            productCode = RUtil.R_string(R.string.code_product_giro_pago)
        }

        val requestGiroRePrintDTO = RequestGiroRePrintDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = productCode
            this.reference = reference

        }

        return giroRepository.giroRePrint(requestGiroRePrintDTO)
                ?.subscribeOn(Schedulers.computation())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    Observable.just(it)
                }

    }

    override fun exonerateFingerPrint(requestGiroExFingerPrintDTO: GiroExFingerPrintModel): Observable<BaseResponseDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroRePrintDTO = RequestGiroExFingerPrintDTO().apply {

            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }
            this.third = requestGiroExFingerPrintDTO.third
            this.notes = requestGiroExFingerPrintDTO.notes
            this.requestsMessages = requestGiroExFingerPrintDTO.requestsMessages
            this.listDocument = requestGiroExFingerPrintDTO.listDocument

        }

        return giroRepository.exonerateFingerPrint(requestGiroRePrintDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }

    }

    override fun giroAuthorization(authorizationModel: AuthorizationModel): Observable<BaseResponseDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroAuthorizationDTO = RequestGiroAuthorizationDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }
            this.reference = authorizationModel.reference
            this.notes = authorizationModel.notes
            this.typeRequests = authorizationModel.typeRequests
            this.third = authorizationModel.third
        }

        return giroRepository.giroAuthorization(requestGiroAuthorizationDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }

    }

    override fun modifyNotes(reference: String, notes: String): Observable<BaseResponseDTO>? {


        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroModifyNotes = RequestGiroModifyNotesDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }
            this.reference = reference
            this.notes = notes
        }


        return giroRepository.modifyNotes(requestGiroModifyNotes)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }

    }

    override fun giroCheckRequest(): Observable<ResponseGiroCheckRequestDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroCheckRequestDTO = RequestGiroCheckRequestDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }

        }

        return giroRepository.giroCheckRequest(requestGiroCheckRequestDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun openBox(): Observable<ResponseGiroOpenBoxDTO>? {

        val requestGiroOpenBoxDTO = RequestGiroOpenBoxDTO().apply {  }

        return giroRepository.openBox(requestGiroOpenBoxDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun closeBox(bannerOpenning: Int,
                          balance: Int,
                          bannerIncome:  Int,
                          bannerExpenses:  Int): Observable<ResponseGiroCloseBoxDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()
        val sesionGiroLocal = giroRepository.getLocalGiroLogin()

        val requestGiroCloseBoxDTO = RequestGiroCloseBoxDTO().apply {
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionGiroLocal.box?.boxCode
            }
            this.bannerOpenning = bannerOpenning
            this.balance = balance
            this.bannerIncome = bannerIncome
            this.bannerExpenses = bannerExpenses
        }

        return giroRepository.closeBox(requestGiroCloseBoxDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun printCheckRequest(
        girorCheckRequestsDTO: GirorCheckRequestsDTO,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        giroRepository.printCheckRequest(girorCheckRequestsDTO, isRetry, callback)
    }

    override fun printCloseBox(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        giroRepository.printCloseBox(data, isRetry, callback)
    }

    override fun saveLocalParameterGiros(responseGiroParameterDTO: ResponseGiroParameterDTO) {
        giroRepository.saveLocalParameterGiros(responseGiroParameterDTO)
    }

    override fun getLocalParameterGiros(): ResponseGiroParameterDTO {
        return  giroRepository.getLocalParameterGiros()
    }

}