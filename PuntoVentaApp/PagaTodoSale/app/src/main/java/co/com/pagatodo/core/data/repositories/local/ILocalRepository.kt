package co.com.pagatodo.core.data.repositories.local

import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.*
import co.com.pagatodo.core.data.model.ModeValueModel
import io.reactivex.Observable

interface ILocalRepository {
    fun getStringValueParamFromLocal(key: String): String?
    fun saveCurrentStubSeries(serie1: String, serie2: String, isValidWithStubList: Boolean = true): Boolean
    fun saveParameterInfo(parameters: List<KeyValueParameterEntityRoom>): Boolean
    fun saveProductInfoRoom(products: List<ProductEntityRoom>): Observable<DBHelperResponse>
    fun getProductInfoRoomAsync(productCode: String): Observable<ProductEntityRoom>?
    fun getPayMillionaireModes(): Observable<List<ModeValueModel>>?
    fun savePaymillionaireModesRoom(modes: List<PayMillionaireEntityRoom>): Observable<DBHelperResponse>
    fun savePrinterType(data: String): DBHelperResponse
    fun savePrinterConfiguration(configuration: PrinterConfigurationEntity): Observable<DBHelperResponse>
}