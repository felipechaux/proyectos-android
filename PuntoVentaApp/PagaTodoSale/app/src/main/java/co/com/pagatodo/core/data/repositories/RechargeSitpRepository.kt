package co.com.pagatodo.core.data.repositories


import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestCardRechargeDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryCardBlackDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryInitDateDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryInventoryDTO
import co.com.pagatodo.core.data.dto.response.ResponseCardRechargeDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryCardBlackDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryInitDateDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryInventoryDTO
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

interface IRechargeSitpRepository {
    fun getCardRecharge(requestCardRechargeDTO: RequestCardRechargeDTO): Observable<ResponseCardRechargeDTO>?
    fun getQueryInitData(requestQueryInitDateDTO: RequestQueryInitDateDTO): Observable<ResponseQueryInitDateDTO>?
    fun getQueryInventory(requestQueryInventoryDTO: RequestQueryInventoryDTO): Observable<ResponseQueryInventoryDTO>?
    fun getQueryCardBlack(requestQueryCardBlackDTO: RequestQueryCardBlackDTO): Observable<ResponseQueryCardBlackDTO>?
    fun saveLocalSitpRecharge(responseQueryInitDateDTO: ResponseQueryInitDateDTO?)
    fun getLocalSitpRecharge(): ResponseQueryInitDateDTO
}

@Singleton
class RechargeSitpRepository : IRechargeSitpRepository {

    override fun getCardRecharge(requestCardRechargeDTO: RequestCardRechargeDTO): Observable<ResponseCardRechargeDTO>? {
        return ApiFactory.build()?.cardRecharge(requestCardRechargeDTO)
    }

    override fun getQueryInitData(requestQueryInitDateDTO: RequestQueryInitDateDTO): Observable<ResponseQueryInitDateDTO>? {
        return ApiFactory.build()?.queryInitDate(requestQueryInitDateDTO)
    }

    override fun getQueryInventory(requestQueryInventoryDTO: RequestQueryInventoryDTO): Observable<ResponseQueryInventoryDTO>? {
        return ApiFactory.build()?.queryInventory(requestQueryInventoryDTO)
    }

    override fun getQueryCardBlack(requestQueryCardBlackDTO: RequestQueryCardBlackDTO): Observable<ResponseQueryCardBlackDTO>? {
        return ApiFactory.build()?.queryCardBlack(requestQueryCardBlackDTO)
    }

    override fun getLocalSitpRecharge(): ResponseQueryInitDateDTO {
        val json: String = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_sitp_data))
        return Gson().fromJson(json, ResponseQueryInitDateDTO::class.java);
    }

    override fun saveLocalSitpRecharge(responseQueryInitDateDTO: ResponseQueryInitDateDTO?) {

        val json = Gson().toJson(responseQueryInitDateDTO)
        SharedPreferencesUtil.savePreference(RUtil.R_string(R.string.shared_key_sitp_data), json)

    }


}