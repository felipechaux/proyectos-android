package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.print.ElderlyPrintModel
import co.com.pagatodo.core.data.print.ElderlyPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Singleton

interface IElderlyRepository {
    fun elderlyQueryGetReprint(requestElderlyGetReprintDTO: RequestElderlyGetReprintDTO
    ): Observable<ResponseElderlyGetReprintDTO>?
    fun elderlyQueryAuthorizedTerminal(requestElderlyAuthorizedTerminalDTO: RequestElderlyAuthorizedTerminalDTO
    ): Observable<ResponseElderlyAuthorizedTerminalDTO>?

    fun elderlyPointedFingers(requestPointedFingersDTO: RequestPointedFingersDTO): Observable<ResponsePointedFingersDTO>?
    fun elderlyQueryId(requestElderlyQueryIdDTO: RequestElderlyQueryIdDTO): Observable<ResponseElderlyQueryIdDTO>?
    fun elderlySaveFingerPrint(requestElderlySaveFingerPrintDTO: RequestElderlySaveFingerPrintDTO): Observable<ResponseElderlySaveFingerPrintDTO>?
    fun elderlyQuerySubsidy(requestElderlyQuerySubsidyDTO: RequestElderlyQuerySubsidyDTO): Observable<ResponseElderlyQuerySubsidyDTO>?
    fun elderlyQueryAuthenticateFootprint(requestElderlyAuthenticateFootprintDTO: RequestElderlyAuthenticateFootprintDTO):
            Observable<ResponseElderlyAuthenticateFootprintDTO>?
    fun elderlySaveId(requestElderlySaveIdDTO: RequestElderlySaveIdDTO):
            Observable<ResponseElderlySaveIdDTO>?
    fun elderlyQueryMakeSubsidyPayment(requestElderlyMakeSubsidyPaymentDTO: RequestElderlyMakeSubsidyPaymentDTO):
            Observable<ResponseElderlyMakeSubsidyPaymentDTO>?
    fun getCities(requestQueryCitiesDTO: RequestQueryCitiesDTO): Observable<ResponseQueryCitiesDTO>?
    fun getParameterElderly(requestElderlyParameterDTO: RequestElderlyParameterDTO): Observable<ResponseElderlyParameterDTO>?
    fun loadTimeOut ():Long
    fun createThird(requestThirdCreateDTO: RequestThirdCreateDTO): Observable<ResponseThirdCreateDTO>?
    fun printPayment (elderlyPrintModel: ElderlyPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}

@Singleton
class ElderlyRepository: BaseRepository(), IElderlyRepository {

    override fun elderlyQueryGetReprint(requestElderlyGetReprintDTO: RequestElderlyGetReprintDTO
    ): Observable<ResponseElderlyGetReprintDTO>? {
        return ApiFactory.build()?.elderlyQueryGetReprint(requestElderlyGetReprintDTO)
    }

    override fun elderlyQueryAuthorizedTerminal(requestElderlyAuthorizedTerminalDTO: RequestElderlyAuthorizedTerminalDTO
    ): Observable<ResponseElderlyAuthorizedTerminalDTO>? {
        return ApiFactory.build()?.elderlyQueryAuthorizedTerminal(requestElderlyAuthorizedTerminalDTO)
    }

    override fun elderlyPointedFingers(requestPointedFingersDTO: RequestPointedFingersDTO): Observable<ResponsePointedFingersDTO>? {
        return ApiFactory.build()?.elderlyPointedFingers(requestPointedFingersDTO)    }

    override fun elderlyQueryId(requestElderlyQueryIdDTO: RequestElderlyQueryIdDTO): Observable<ResponseElderlyQueryIdDTO>? {
        return ApiFactory.build()?.elderlyQueryId(requestElderlyQueryIdDTO)    }

    override fun elderlySaveFingerPrint(requestElderlySaveFingerPrintDTO: RequestElderlySaveFingerPrintDTO): Observable<ResponseElderlySaveFingerPrintDTO>? {
        return ApiFactory.build()?.elderlySaveFingerPrint(requestElderlySaveFingerPrintDTO)    }

    override fun elderlyQuerySubsidy(requestElderlyQuerySubsidyDTO: RequestElderlyQuerySubsidyDTO): Observable<ResponseElderlyQuerySubsidyDTO>? {
        return ApiFactory.build()?.elderlyQuerySubsidy(requestElderlyQuerySubsidyDTO)    }

    override fun elderlyQueryAuthenticateFootprint(requestElderlyAuthenticateFootprintDTO: RequestElderlyAuthenticateFootprintDTO):
            Observable<ResponseElderlyAuthenticateFootprintDTO>? {
        return ApiFactory.build()?.elderlyQueryAuthenticateFootprint(requestElderlyAuthenticateFootprintDTO)
    }

    override fun elderlySaveId(requestElderlySaveIdDTO: RequestElderlySaveIdDTO):
            Observable<ResponseElderlySaveIdDTO>? {
        return ApiFactory.build(180)?.elderlySaveId(requestElderlySaveIdDTO)
    }

    override fun elderlyQueryMakeSubsidyPayment(requestElderlyMakeSubsidyPaymentDTO: RequestElderlyMakeSubsidyPaymentDTO):
            Observable<ResponseElderlyMakeSubsidyPaymentDTO>? {
        return ApiFactory.build()?.elderlyQueryMakeSubsidyPayment(requestElderlyMakeSubsidyPaymentDTO)
    }

    override fun getCities(requestQueryCitiesDTO: RequestQueryCitiesDTO): Observable<ResponseQueryCitiesDTO>? {
        return ApiFactory.build(loadTimeOut())?.queryCities(requestQueryCitiesDTO)
    }

    override fun getParameterElderly(requestElderlyParameterDTO: RequestElderlyParameterDTO): Observable<ResponseElderlyParameterDTO>? {
        return ApiFactory.build(loadTimeOut())?.queryParameterElderly(requestElderlyParameterDTO)
    }

    override fun loadTimeOut(): Long {
        return SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.giro_parameter_time_out)).toLong()
    }

    override fun createThird(requestThirdCreateDTO: RequestThirdCreateDTO): Observable<ResponseThirdCreateDTO>? {

        return ApiFactory.build(loadTimeOut())?.createThird(requestThirdCreateDTO)
    }

    override fun printPayment(
        elderlyPrintModel: ElderlyPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        ElderlyPrint().printPayment(elderlyPrintModel,callback)

    }

}