package co.com.pagatodo.core.data.repositories

import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.*
import co.com.pagatodo.core.data.dto.AuthDTO
import co.com.pagatodo.core.data.dto.ParameterDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.BaseResponseDTO
import co.com.pagatodo.core.data.dto.response.ResponseGenericDTO
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.AuthMode
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SecurityUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getSharedPreferenceObject
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.removePreference
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.savePreference
import io.reactivex.Observable
import java.util.*
import javax.inject.Singleton

interface IAuthRepository {
    fun login(sellerCode: String, password: String): Observable<AuthDTO>?
    fun logout(): Observable<ResponseGenericDTO>?
    fun clearAuthEntity()
    fun saveSessionInfo(sessionInfo: SessionEntity)
    fun saveParameterInfo(parameters: List<ParameterDTO>?)
    fun saveStubsInfoRoom(stubs: List<StubEntity>): Observable<DBHelperResponse>
    fun getStubsRoom(): List<StubEntity>?
    fun getStubsRoomAsync(): Observable<List<StubEntity>>?
    fun authFingerPrintSeller(requestAuthFingerPrintSellerDTO: RequestAuthFingerPrintSellerDTO): Observable<BaseResponseDTO>?
    fun authToken(requestAuthTokenDTO: RequestAuthTokenDTO): Observable<BaseResponseDTO>?
    fun validateSessionDate(): Boolean
    fun clearPreference(isClearParameterDate: Boolean? = false)
}

@Singleton
class AuthRepository : IAuthRepository {

    override fun login(sellerCode: String, password: String): Observable<AuthDTO>? {
        val requestLogin = RequestLoginDTO().apply {
            this.canalId = getPreference(R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            this.sellerCode = sellerCode
            this.password = password
            this.userType = getPreference(R_string(R.string.shared_key_user_type))
        }
        return ApiFactory.build()?.login(requestLogin)
    }

    override fun logout(): Observable<ResponseGenericDTO>? {
        val sessionEntity = PagaTodoApplication.getDatabaseInstance()?.sessionDao()?.getById(1)
        val requestLogoutDTO = RequestLogoutDTO().apply {
            this.canalId = getPreference(R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            this.userType = getPreference(R_string(R.string.shared_key_user_type))
            this.sessionId = sessionEntity?.sessionId
        }
        return ApiFactory.build()?.logout(requestLogoutDTO)
    }

    override fun clearAuthEntity() {
        PagaTodoApplication.getDatabaseInstance()?.sessionDao()?.deleteAll()
        PagaTodoApplication.getDatabaseInstance()?.stubDao()?.deleteAll()
        PagaTodoApplication.getDatabaseInstance()?.lotteryDao()?.deleteAll()
        PagaTodoApplication.getDatabaseInstance()?.menuDao()?.deleteAll()
    }

    override fun saveSessionInfo(sessionInfo: SessionEntity) {

        sessionInfo.sellerCode?.let { sellerCode ->
            if(getPreference<String>(R_string(R.string.shared_key_seller_code)) != sellerCode){
                clearPreference(true)
            }

            savePreference(R_string(R.string.shared_key_seller_code), sellerCode)
        }
        sessionInfo.sellerName?.let { sellerName ->
            savePreference(R_string(R.string.shared_key_seller_name), sellerName)
        }
        sessionInfo.codePointSale?.let { code ->
            savePreference(R_string(R.string.shared_key_code_point_sale), code)
        }
        sessionInfo.municipalityCode?.let { code ->
            savePreference(R_string(R.string.shared_key_municipality_code), code)
        }
        sessionInfo.officeCode?.let { code ->
            savePreference(R_string(R.string.shared_key_office_code), code)
        }
        sessionInfo.token?.let {
            savePreference(R_string(R.string.shared_key_token), it)
        }
        sessionInfo.sessionId?.let {
            savePreference(R_string(R.string.shared_key_session_id), it)
        }

        PagaTodoApplication.getDatabaseInstance()?.sessionDao()?.insert(sessionInfo)

    }


    override fun validateSessionDate(): Boolean {

        var isUpdate = false

        val currentDate = DateUtil.getCurrentDate()
        val sessionDate = getPreference<String>(R_string(R.string.shared_key_parameter_date))

        if (sessionDate != "") {


            val dateSession = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                sessionDate
            )

            Log.d("DATA", currentDate.toString())
            Log.d("DATA", dateSession.toString())


            Log.d(
                "DATA", DateUtil.convertDateToStringFormat(
                    DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                    currentDate
                )
            )

            Log.d(
                "DATA", DateUtil.convertDateToStringFormat(
                    DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                    dateSession
                )
            )

            if (currentDate.compareTo(dateSession) > 0) {
                saveSessionDate(currentDate)
                isUpdate = true

            } else {
                isUpdate = false
            }


        } else {
            isUpdate = true
            saveSessionDate(currentDate)

        }

        return isUpdate
    }

    private fun saveSessionDate(currentDate: Date) {

        val dateFinish = Date(currentDate.year, currentDate.month, currentDate.date, 23, 55, 55)

        savePreference(
            R_string(R.string.shared_key_parameter_date),
            DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                dateFinish
            )
        )
    }

    override fun clearPreference(isClearParameterDate: Boolean?) {
        clearPreferenceGiro()
        removePreference(R_string(R.string.shared_key_baloto_parameter))

        if ((isClearParameterDate ?: false)) {
            removePreference(R_string(R.string.shared_key_parameter_date))
            clearAuthEntity()

        }


    }

    private fun clearPreferenceGiro(){
        removePreference(R_string(R.string.shared_key_giro_login))
        removePreference(R_string(R.string.shared_key_user_giro))
        removePreference(R_string(R.string.shared_key_giro_parameter))
    }

    override fun saveParameterInfo(parameters: List<ParameterDTO>?) {


        val versionParameter = parameters?.filter { it.key == R_string(R.string.param_sivemtx_seguridad_version_parametros) }

        if (versionParameter != null &&
            versionParameter.count() != 0 &&
            getPreference<String>(R_string(R.string.shared_key_session_parameter_version)) != versionParameter.last().value.toString()
        ) {
            removePreference(R_string(R.string.shared_key_parameter_date))
            savePreference(
                R_string(R.string.shared_key_session_parameter_version),
                versionParameter.last().value.toString()
            )
        }


        val parameterTimeOut =
            parameters?.filter { it.key == R_string(R.string.param_sivemtx_seguridad_session_time) }

        var timeOutSession: Int = 30 * 60

        if (parameterTimeOut != null && parameterTimeOut.count() != 0) {
            timeOutSession = parameterTimeOut.last().value?.toInt()!! * 60
        }

        savePreference(R_string(R.string.shared_key_session_time_out), timeOutSession.toString())
        SecurityUtil.updateSessionTimeOut(timeOutSession.toString())

        var paramLogin = AuthMode.NONE.type
        var paramGiroCapture = AuthMode.BIOMETRY.type
        var paramGiroPay = AuthMode.BIOMETRY.type

        val parameterAutLogin = parameters?.filter { it.key == R_string(R.string.param_login) }
        val parameterSegGiroCapture =
            parameters?.filter { it.key == R_string(R.string.param_giro_capture) }
        val parameterSegGiroPay = parameters?.filter { it.key == R_string(R.string.param_giro_pay) }

        if (parameterAutLogin != null && parameterAutLogin?.count() != 0) {
            paramLogin = parameterAutLogin?.last()?.value!!
        }

        if (parameterSegGiroCapture != null && parameterSegGiroCapture?.count() != 0) {
            paramGiroCapture = parameterSegGiroCapture?.last()?.value!!
        }

        if (parameterSegGiroPay != null && parameterSegGiroPay?.count() != 0) {
            paramGiroPay = parameterSegGiroPay?.last()?.value!!
        }

        savePreference(R_string(R.string.shared_key_auth_mode), paramLogin)
        savePreference(R_string(R.string.shared_key_auth_mode_giro_capture), paramGiroCapture)
        savePreference(R_string(R.string.shared_key_auth_mode_giro_pay), paramGiroPay)

    }

    override fun saveStubsInfoRoom(stubs: List<StubEntity>): Observable<DBHelperResponse> {
        val response = DBHelperResponse()
        PagaTodoApplication.getDatabaseInstance()?.stubDao()?.insertAll(stubs)
        return Observable.just(response)
    }

    override fun getStubsRoom(): List<StubEntity>? {
        return PagaTodoApplication.getDatabaseInstance()?.stubDao()?.getAll()
    }

    override fun getStubsRoomAsync(): Observable<List<StubEntity>>? {
        return PagaTodoApplication.getDatabaseInstance()?.stubDao()?.getAllAsync()
    }

    override fun authFingerPrintSeller(requestAuthFingerPrintSellerDTO: RequestAuthFingerPrintSellerDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build()?.authFingerPrintSeller(requestAuthFingerPrintSellerDTO)
    }

    override fun authToken(requestAuthTokenDTO: RequestAuthTokenDTO): Observable<BaseResponseDTO>? {
        return ApiFactory.build()?.authToken(requestAuthTokenDTO)
    }

}