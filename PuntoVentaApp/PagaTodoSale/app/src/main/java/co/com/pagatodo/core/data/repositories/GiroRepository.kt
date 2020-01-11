package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GiroUserDTO
import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.print.IGiroPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintGiroModule
import co.com.pagatodo.core.di.repository.DaggerGiroRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

interface IGiroRepository {

    fun getParameterGiros(requestGiroParameterDTO: RequestGiroParameterDTO): Observable<ResponseGiroParameterDTO>?
    fun getCities(requestQueryCitiesDTO: RequestQueryCitiesDTO): Observable<ResponseQueryCitiesDTO>?
    fun getAgencies(requestQueryAgencyDTO: RequestQueryAgencyDTO): Observable<ResponseQueryAgencyDTO>?
    fun getThird(requestQueryThirdDTO: RequestQueryThirdDTO): Observable<ResponseQueryThirdDTO>?
    fun calculateConcepts(requestGirosCalculateConceptsDTO: RequestGirosCalculateConceptsDTO): Observable<ResponseGirosCalculateConceptsDTO>?
    fun captureGiro(requestGiroCaptureDTO: RequestGiroCaptureDTO): Observable<ResponseGiroCaptureDTO>?
    fun getGiroUser(requestGiroUserDTO: RequestGiroUserDTO): Observable<ResponseGiroUserDTO>?
    fun girosLogin(requestGiroLoginDTO: RequestGiroLoginDTO): Observable<ResponseGiroLoginDTO>?
    fun printSentGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    )

    fun printPaymentGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    )

    fun getLocalGiroUser(): GiroUserDTO
    fun saveLocalGiroLogin(responseGiroLoginDTO: ResponseGiroLoginDTO?)
    fun getLocalGiroLogin(): ResponseGiroLoginDTO
    fun createThird(requestThirdCreateDTO: RequestThirdCreateDTO): Observable<ResponseThirdCreateDTO>?
    fun updateThird(requestThirdCreateDTO: RequestThirdCreateDTO): Observable<BaseResponseDTO>?
    fun queryGiro(requestQueryGiroDTO: RequestQueryGiroDTO): Observable<ResponseQueryGiroDTO>?
    fun paymentGiro(requestPaymentGiroDTO: RequestPaymentGiroDTO): Observable<ResponsePaymentGiroDTO>?
    fun sendVoucher(requestGiroSendVoucherDTO: RequestGiroSendVoucherDTO): Observable<ResponseGiroSendVoucherDTO>?
    fun giroCriteria(requestGiroCriteriaDTO: RequestGiroCriteriaDTO): Observable<ResponseGiroCriteriaDTO>?
    fun giroConsultMovement(requestGiroMovementDTO: RequestGiroMovementDTO): Observable<ResponseGiroMovementDTO>?
    fun authFingerPrintUser(requestAuthFingerPrintUserDTO: RequestAuthFingerPrintUserDTO): Observable<BaseResponseDTO>?
    fun giroRePrint(requestGiroRePrintDTO: RequestGiroRePrintDTO): Observable<ResponseGiroRePrintDTO>?
    fun exonerateFingerPrint(requestGiroExFingerPrintDTO: RequestGiroExFingerPrintDTO): Observable<BaseResponseDTO>?
    fun giroAuthorization(requestGiroAuthorizationDTO: RequestGiroAuthorizationDTO): Observable<BaseResponseDTO>?
    fun modifyNotes(requestGiroModifyNotesDTO: RequestGiroModifyNotesDTO): Observable<BaseResponseDTO>?
    fun giroCheckRequest(requestGiroCheckRequestDTO: RequestGiroCheckRequestDTO): Observable<ResponseGiroCheckRequestDTO>?
    fun openBox(requestGiroOpenBoxDTO: RequestGiroOpenBoxDTO): Observable<ResponseGiroOpenBoxDTO>?
    fun closeBox(requestGiroCloseBoxDTO: RequestGiroCloseBoxDTO): Observable<ResponseGiroCloseBoxDTO>?
    fun printCheckRequest(
        girorCheckRequestsDTO: GirorCheckRequestsDTO,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    )

    fun printCloseBox(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    )

    fun loadTimeOut(): Long
    fun saveLocalParameterGiros(responseGiroParameterDTO: ResponseGiroParameterDTO)
    fun getLocalParameterGiros(): ResponseGiroParameterDTO

}

@Singleton
class GiroRepository : IGiroRepository {

    @Inject
    lateinit var giroPrint: IGiroPrint

    init {
        DaggerGiroRepositoryComponent
            .builder()
            .printGiroModule(PrintGiroModule())
            .build().inject(this)


    }

    override fun getAgencies(requestQueryAgencyDTO: RequestQueryAgencyDTO): Observable<ResponseQueryAgencyDTO>? {

        return ApiFactory.build(loadTimeOut())?.queryAgencies(requestQueryAgencyDTO)
    }

    override fun getCities(requestQueryCitiesDTO: RequestQueryCitiesDTO): Observable<ResponseQueryCitiesDTO>? {
        return ApiFactory.build(loadTimeOut())?.queryCities(requestQueryCitiesDTO)
    }

    override fun getParameterGiros(requestGiroParameterDTO: RequestGiroParameterDTO): Observable<ResponseGiroParameterDTO>? {
        return ApiFactory.build(loadTimeOut())?.queryParameterGiro(requestGiroParameterDTO)
    }


    override fun getThird(requestQueryThirdDTO: RequestQueryThirdDTO): Observable<ResponseQueryThirdDTO>? {
        return ApiFactory.build(loadTimeOut())?.queryThird(requestQueryThirdDTO)
    }

    override fun calculateConcepts(requestGirosCalculateConceptsDTO: RequestGirosCalculateConceptsDTO): Observable<ResponseGirosCalculateConceptsDTO>? {
        return ApiFactory.build(loadTimeOut())?.calculateConcepts(requestGirosCalculateConceptsDTO)
    }

    override fun getGiroUser(requestGiroUserDTO: RequestGiroUserDTO): Observable<ResponseGiroUserDTO>? {
        return ApiFactory.build(loadTimeOut())?.getGiroUser(requestGiroUserDTO)
    }

    override fun girosLogin(requestGiroLoginDTO: RequestGiroLoginDTO): Observable<ResponseGiroLoginDTO>? {
        return ApiFactory.build(loadTimeOut())?.girosLogin(requestGiroLoginDTO)
    }

    override fun captureGiro(requestGiroCaptureDTO: RequestGiroCaptureDTO): Observable<ResponseGiroCaptureDTO>? {
        return ApiFactory.build(loadTimeOut())?.captureGiro(requestGiroCaptureDTO)
    }

    override fun printSentGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        giroPrint.printCaptureGiro(data, isRetry, callback)
    }

    override fun getLocalGiroUser(): GiroUserDTO {

        val json: String =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_giro))
        return try {
            Gson().fromJson(json, GiroUserDTO::class.java)
        } catch (e: IllegalStateException) {
            GiroUserDTO()
        };

    }

    override fun saveLocalGiroLogin(responseGiroLoginDTO: ResponseGiroLoginDTO?) {

        if (responseGiroLoginDTO != null) {

            responseGiroLoginDTO.apply { isload = true }

            val json = Gson().toJson(responseGiroLoginDTO)
            SharedPreferencesUtil.savePreference(
                RUtil.R_string(R.string.shared_key_giro_login),
                json
            )

            val responseGiroLogin = getLocalGiroLogin()
            val giroUserDTO = GiroUserDTO().apply {
                this.agency = responseGiroLogin.user?.agency
                this.agencyName = responseGiroLogin.user?.agencyName
                this.agencyAddress = responseGiroLogin.user?.agencyName
                this.password = responseGiroLogin.user?.password
                this.user =
                    SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_id))
            }

            val jsonUser = Gson().toJson(giroUserDTO)
            SharedPreferencesUtil.savePreference(
                RUtil.R_string(R.string.shared_key_user_giro),
                jsonUser
            )

        }


    }

    override fun getLocalGiroLogin(): ResponseGiroLoginDTO {
        val json: String =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_giro_login))
        if (json.isNotEmpty())
            return Gson().fromJson(json, ResponseGiroLoginDTO::class.java).apply { isload = true };
        else
            return ResponseGiroLoginDTO().apply { isload = false }
    }

    override fun createThird(requestThirdCreateDTO: RequestThirdCreateDTO): Observable<ResponseThirdCreateDTO>? {

        return ApiFactory.build(loadTimeOut())?.createThird(requestThirdCreateDTO)
    }

    override fun updateThird(requestThirdCreateDTO: RequestThirdCreateDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build(loadTimeOut())?.updateThird(requestThirdCreateDTO)
    }

    override fun queryGiro(requestQueryGiroDTO: RequestQueryGiroDTO): Observable<ResponseQueryGiroDTO>? {
        return ApiFactory.build(loadTimeOut())?.queryGiro(requestQueryGiroDTO)
    }

    override fun printPaymentGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        giroPrint.printPaymentGiro(data, isRetry, callback)
    }

    override fun paymentGiro(requestPaymentGiroDTO: RequestPaymentGiroDTO): Observable<ResponsePaymentGiroDTO>? {
        return ApiFactory.build(loadTimeOut())?.paymentGiro(requestPaymentGiroDTO)
    }

    override fun sendVoucher(requestGiroSendVoucherDTO: RequestGiroSendVoucherDTO): Observable<ResponseGiroSendVoucherDTO>? {
        return ApiFactory.build(loadTimeOut())?.sendVoucher(requestGiroSendVoucherDTO)
    }

    override fun giroCriteria(requestGiroCriteriaDTO: RequestGiroCriteriaDTO): Observable<ResponseGiroCriteriaDTO>? {
        return ApiFactory.build(loadTimeOut())?.giroCriteria(requestGiroCriteriaDTO)
    }

    override fun giroConsultMovement(requestGiroMovementDTO: RequestGiroMovementDTO): Observable<ResponseGiroMovementDTO>? {

        return ApiFactory.build(loadTimeOut())?.giroConsultMovement(requestGiroMovementDTO)
    }

    override fun authFingerPrintUser(requestAuthFingerPrintUserDTO: RequestAuthFingerPrintUserDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build(loadTimeOut())?.authFingerPrintUser(requestAuthFingerPrintUserDTO)
    }

    override fun giroRePrint(requestGiroRePrintDTO: RequestGiroRePrintDTO): Observable<ResponseGiroRePrintDTO>? {
        return ApiFactory.build(loadTimeOut())?.giroRePrint(requestGiroRePrintDTO)
    }

    override fun exonerateFingerPrint(requestGiroExFingerPrintDTO: RequestGiroExFingerPrintDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build(loadTimeOut())?.exonerateFingerPrint(requestGiroExFingerPrintDTO)
    }

    override fun giroAuthorization(requestGiroAuthorizationDTO: RequestGiroAuthorizationDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build(loadTimeOut())?.giroAuthorization(requestGiroAuthorizationDTO)
    }

    override fun modifyNotes(requestGiroModifyNotesDTO: RequestGiroModifyNotesDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build(loadTimeOut())?.modifyNotes(requestGiroModifyNotesDTO)
    }

    override fun giroCheckRequest(requestGiroCheckRequestDTO: RequestGiroCheckRequestDTO): Observable<ResponseGiroCheckRequestDTO>? {
        return ApiFactory.build(loadTimeOut())?.giroCheckRequest(requestGiroCheckRequestDTO)
    }

    override fun openBox(requestGiroOpenBoxDTO: RequestGiroOpenBoxDTO): Observable<ResponseGiroOpenBoxDTO>? {
        return ApiFactory.build(loadTimeOut())?.openBox(requestGiroOpenBoxDTO)
    }

    override fun closeBox(requestGiroCloseBoxDTO: RequestGiroCloseBoxDTO): Observable<ResponseGiroCloseBoxDTO>? {
        return ApiFactory.build(loadTimeOut())?.closeBox(requestGiroCloseBoxDTO)
    }

    override fun printCheckRequest(
        girorCheckRequestsDTO: GirorCheckRequestsDTO,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        giroPrint.printCheckRequest(girorCheckRequestsDTO, isRetry, callback)
    }

    override fun printCloseBox(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        giroPrint.printCloseBox(data, isRetry, callback)
    }

    override fun loadTimeOut(): Long {

        if (SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.giro_parameter_time_out)).isNotEmpty()) {
            return SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.giro_parameter_time_out)).toLong()
        } else {
            return 120
        }
        
    }


    override fun saveLocalParameterGiros(responseGiroParameterDTO: ResponseGiroParameterDTO) {

        val jsonUser = Gson().toJson(responseGiroParameterDTO)
        SharedPreferencesUtil.savePreference(
            RUtil.R_string(R.string.shared_key_giro_parameter),
            jsonUser
        )

    }

    override fun getLocalParameterGiros(): ResponseGiroParameterDTO {

        val json: String =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_giro_parameter))
        if (json.isNotEmpty())
            return Gson().fromJson(json, ResponseGiroParameterDTO::class.java)
                .apply { isload = true };
        else
            return ResponseGiroParameterDTO().apply { isload = false }

    }

}