package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.*
import co.com.pagatodo.core.data.dto.GeneralProductDTO
import co.com.pagatodo.core.data.dto.ParameterDTO
import co.com.pagatodo.core.data.dto.response.PayMillionaireModeDTO
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.PrinterConfigurationModel
import co.com.pagatodo.core.data.model.ProductModel
import co.com.pagatodo.core.data.repositories.local.ILocalRepository
import co.com.pagatodo.core.di.DaggerLocalComponent
import co.com.pagatodo.core.di.LocalModule
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

interface ILocalInteractor {
    fun getStringValueParamFromLocal(key: String): String?
    fun saveParameters(paramsDto: List<ParameterDTO>): Boolean
    fun saveProductInfo(products: List<GeneralProductDTO>): Observable<DBHelperResponse>
    fun savePrinterConfiguration(configuration: PrinterConfigurationModel)
    fun getProductInfo(code: String): Observable<ProductModel>
    fun getPayMillionaireModes(): Observable<List<ModeValueModel>>?
    fun savePaymillionaireModesInLocal(modes: List<PayMillionaireModeDTO>?)
}

@Singleton
class LocalInteractor: ILocalInteractor {

    @Inject
    lateinit var localRepository: ILocalRepository

    init {
        DaggerLocalComponent.builder().localModule(LocalModule()).build().inject(this)
    }

    constructor()

    constructor(localRepository: ILocalRepository) {
        this.localRepository = localRepository
    }

    override fun getStringValueParamFromLocal(key: String): String? {
        return localRepository.getStringValueParamFromLocal(key)
    }

    override fun saveParameters(paramsDto: List<ParameterDTO>): Boolean {
        val entities = paramsDto.map {
            KeyValueParameterEntityRoom().apply {
                key = it.key
                value = it.value
            }
        }
        return localRepository.saveParameterInfo(entities)
    }

    override fun saveProductInfo(products: List<GeneralProductDTO>): Observable<DBHelperResponse> {
        val entities = mutableListOf<ProductEntityRoom>()
        products.forEach { productDto ->
            val parameters = arrayListOf<ProductParameterEntityRoom>()
            productDto.productParameters?.forEach {  parameterDto ->
                parameters.add(
                    ProductParameterEntityRoom(
                        "${productDto.code}-${parameterDto.key}",
                        productDto.code ?: "",
                        parameterDto.key,
                        parameterDto.value
                    )
                )
            }

            entities.add(
                ProductEntityRoom(
                    productDto.code ?: "-1",
                    productDto.name,
                    parameters
                )
            )
        }
        return localRepository.saveProductInfoRoom(entities)
    }

    override fun getProductInfo(code: String): Observable<ProductModel> {
        return Observable.create<ProductModel> { emitter ->
            localRepository.getProductInfoRoomAsync(code)?.subscribe {
                val productModel = ProductModel().apply {
                    this.code = it.productCode
                    this.name = it.productName
                }

                val keyValues = arrayListOf<KeyValueModel>()
                it.productParameters?.forEach {
                    keyValues.add(KeyValueModel().apply {
                        this.key = it.key
                        this.value = it.value
                    })
                }

                productModel.parameters = keyValues
                emitter.onNext(productModel)
                emitter.onComplete()
            }
        }
    }

    override fun getPayMillionaireModes(): Observable<List<ModeValueModel>>? {
        return localRepository.getPayMillionaireModes()
    }

    override fun savePaymillionaireModesInLocal(modes: List<PayMillionaireModeDTO>?) {
        val entities = arrayListOf<PayMillionaireEntityRoom>()
        modes?.forEach { mode ->
            entities.add(
                PayMillionaireEntityRoom(
                    mode.code ?: "-1",
                    mode.productCode,
                    mode.name,
                    mode.value,
                    mode.accumulated,
                    mode.numberOfDigits
                )
            )
        }
        localRepository.savePaymillionaireModesRoom(entities).subscribe()
    }

    //TODO: Guardar impresoras local
    override fun savePrinterConfiguration(configuration: PrinterConfigurationModel) {
        val entity = PrinterConfigurationEntity().apply {
            printerCode = configuration.printerCode
            printerType = configuration.printerType
            pathConnection = configuration.pathConnection
        }
        localRepository.savePrinterConfiguration(entity)
    }
}