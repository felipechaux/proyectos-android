package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.DBHelperOperations
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.MenuEntityRoom
import co.com.pagatodo.core.data.dto.RandomNumberDTO
import co.com.pagatodo.core.data.dto.request.RequestRandomNumberDTO
import co.com.pagatodo.core.data.dto.request.RequestUtilDTO
import co.com.pagatodo.core.data.dto.response.ResponseGenericDTO
import co.com.pagatodo.core.data.dto.response.ResponseMenuDTO
import co.com.pagatodo.core.data.dto.response.ResponseUpdateStubDTO
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import javax.inject.Singleton

interface IUtilRepository {
    fun getRandomNumbers(quantityNumber: Int, quantityDigits: Int): Observable<RandomNumberDTO>?
    fun updateStub(request: RequestUtilDTO): Observable<ResponseUpdateStubDTO>?
    fun getMenus(): Observable<ResponseMenuDTO>?
    fun saveMenusInLocalRoom(menus: List<MenuEntityRoom>): Observable<DBHelperResponse>
}

@Singleton
class UtilRepository: BaseRepository(),
    IUtilRepository {

    override fun getRandomNumbers(quantityNumber: Int, quantityDigits: Int): Observable<RandomNumberDTO>? {
        val requestRandomNumberDTO = RequestRandomNumberDTO().apply {
            this.canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            this.quantityNumber = "$quantityNumber"
            this.quantityDigits = "$quantityDigits"
        }
       return ApiFactory.build()?.randomNumber(requestRandomNumberDTO)
    }

    override fun updateStub(request: RequestUtilDTO): Observable<ResponseUpdateStubDTO>? {
        if (request.operationCode != R_string(R.string.move_stub)){
            request.operationCode = R_string(R.string.code_stubs_operation_code)
        }
        request.canalId = getPreference(R_string(R.string.shared_key_canal_id))
        request.terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
        request.sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
        request.userType = getPreference(R_string(R.string.shared_key_user_type))
        return ApiFactory.build()?.updateStub(request)
    }

    override fun getMenus(): Observable<ResponseMenuDTO>? {
        val request = RequestUtilDTO().apply {
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
        }
        return ApiFactory.build()?.getMenus(request)
    }

    override fun saveMenusInLocalRoom(menus: List<MenuEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            totalRows = 0
            identifier = null
            operation = DBHelperOperations.insertRows
        }

        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        try {
            dbInstance?.menuDao()?.deleteAll()
            dbInstance?.menuDao()?.insertAll(menus)
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }
}