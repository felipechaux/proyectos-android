package co.com.pagatodo.core.data.repositories.local

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.DBHelperOperations
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.*
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class LocalRepository: ILocalRepository {

    override fun saveCurrentStubSeries(serie1: String, serie2: String, isValidWithStubList: Boolean): Boolean {
        var response = false

        try {
            val s1 = serie1.toUpperCase()
            val s2 = serie2.toUpperCase()

            val dbInstance = PagaTodoApplication.getDatabaseInstance()
            if(isValidWithStubList) {
                val stubs = dbInstance?.stubDao()?.getAll()
                stubs?.filter {
                    it.serie1?.toUpperCase() == s1 && it.serie2?.toUpperCase() == s2
                }?.count()?.let { totalFetch ->

                    if (totalFetch > 0) {
                        SharedPreferencesUtil.savePreference(RUtil.R_string(R.string.shared_key_current_serie1), serie1)
                        SharedPreferencesUtil.savePreference(RUtil.R_string(R.string.shared_key_current_serie2), serie2)
                        response = true
                    }
                }
            }
            else {
                SharedPreferencesUtil.savePreference(RUtil.R_string(R.string.shared_key_current_serie1), serie1)
                SharedPreferencesUtil.savePreference(RUtil.R_string(R.string.shared_key_current_serie2), serie2)
                response = true
            }
        }
        catch (e: Exception) {
            response = false
        }
        return response
    }

    override fun getStringValueParamFromLocal(key: String): String? {
        return SharedPreferencesUtil.getPreference(key)
    }

    override fun saveParameterInfo(parameters: List<KeyValueParameterEntityRoom>): Boolean {
        return try {
            parameters.forEach {
                val key = it.key
                val value = it.value
                if (key != null && value != null) {
                    SharedPreferencesUtil.savePreference(key, value)
                    if (key == "PARAM_SERVICIO_PARAMETROS_IMPRESORAS") {
                        savePrinterType(value)
                    }
                }
            }
            true
        }
        catch (e: Exception) { false }
    }

    override fun saveProductInfoRoom(products: List<ProductEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRows
        }

        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        try {
            dbInstance?.productDao()?.saveProduct(products)
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }

    override fun getProductInfoRoomAsync(productCode: String): Observable<ProductEntityRoom>? {
        return PagaTodoApplication.getDatabaseInstance()?.productDao()?.getProductInfoAsync(productCode)
    }

    override fun getPayMillionaireModes(): Observable<List<ModeValueModel>>? {
        return PagaTodoApplication.getDatabaseInstance()?.payMillionaireDao()?.getAll()
            ?.flatMap {
                val modes = arrayListOf<ModeValueModel>()
                it.forEach { entity ->
                    modes.add(
                        ModeValueModel().apply {
                            code = entity.code
                            name = entity.name
                            value = entity.value?.toInt() ?: 0
                            accumulated = entity.accumulated?.toLong() ?: 0
                            numberOfDigits = entity.numberOfDigits
                        }
                    )
                }
                Observable.just(modes)
            }
    }

    override fun savePaymillionaireModesRoom(modes: List<PayMillionaireEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRow
        }
        try {
            PagaTodoApplication.getDatabaseInstance()?.payMillionaireDao()?.insertAll(modes)
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }

    override fun savePrinterType(data: String): DBHelperResponse {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRows
        }

        val items = data.split(",").map {
            val split = it.split("-")
            val id = split.first()
            var name = ""
            if (split.count() > 1) {
                name = split[1]
            }
            PrinterTypeEntity(id, name)
        }

        try {
            PagaTodoApplication.getDatabaseInstance()?.printerTypeDao()?.insertOrReplace(items)
        }
        catch (e: Exception) {
            response.status = false
        }
        return response
    }

    override fun savePrinterConfiguration(configuration: PrinterConfigurationEntity): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRow
        }
        try {
            PagaTodoApplication.getDatabaseInstance()?.printerConfigurationDao()?.insertOrReplace(configuration)
            val a =PagaTodoApplication.getDatabaseInstance()?.printerConfigurationDao()?.getPrinterConfiguration("01")
            val b = a?.pathConnection
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }
}