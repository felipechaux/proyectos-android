package co.com.pagatodo.core.data.interactors

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.DatabaseViewModel
import co.com.pagatodo.core.data.database.entities.MenuEntityRoom
import co.com.pagatodo.core.data.dto.MenuDTO
import co.com.pagatodo.core.data.dto.request.RequestUtilDTO
import co.com.pagatodo.core.data.dto.request.RequestUtilValueDTO
import co.com.pagatodo.core.data.dto.response.ValuesUpdateStubDTO
import co.com.pagatodo.core.data.model.MenuModel
import co.com.pagatodo.core.data.model.request.RequestUtilModel
import co.com.pagatodo.core.data.model.response.ResponseGenericModel
import co.com.pagatodo.core.data.model.response.ResponseMenuModel
import co.com.pagatodo.core.data.model.response.ResponseUpdateStubModel
import co.com.pagatodo.core.data.model.response.ValuesUpdateStubModel
import co.com.pagatodo.core.data.repositories.IUtilRepository
import co.com.pagatodo.core.di.DaggerUtilComponent
import co.com.pagatodo.core.di.UtilModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IUtilInteractor {
    fun getRandomNumbers(quantityNumber: Int, quantityDigits: Int): Observable<List<String>>?
    fun updateStubInServer(requestModel: RequestUtilModel): Observable<ResponseUpdateStubModel>?
    fun getMenus(): Observable<ResponseMenuModel>?
    fun saveMenusInLocal(menus: List<MenuModel>?)
}

@Singleton
class UtilInteractor: IUtilInteractor {

    @Inject
    lateinit var utilRepository: IUtilRepository

    init {
        DaggerUtilComponent.builder()
            .utilModule(UtilModule())
            .build()
            .inject(this)
    }

    constructor(utilRepository: IUtilRepository) {
        this.utilRepository = utilRepository
    }

    constructor()

    override fun getRandomNumbers(quantityNumber: Int, quantityDigits: Int): Observable<List<String>>? {
        return utilRepository
            .getRandomNumbers(quantityNumber, quantityDigits)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = mutableListOf<String>()
                it.numbers?.forEach { item ->
                    response.add(item)
                }
                Observable.just(response)
            }
    }

    override fun updateStubInServer(requestModel: RequestUtilModel): Observable<ResponseUpdateStubModel>? {

        val dto = RequestUtilDTO().apply {
            this.canalId = getPreference(R_string(R.string.shared_key_canal_id))
            this.operationCode = requestModel.operationCode
            this.sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            this.terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            this.userType = getPreference(R_string(R.string.shared_key_user_type))
            this.values = getRequestUtilValueDTO(requestModel)
        }

        return utilRepository.updateStub(dto)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap { responseDTO ->
                val response = ResponseUpdateStubModel().apply {
                    responseCode = responseDTO.responseCode
                    isSuccess = responseDTO.isSuccess
                    transactionDate = responseDTO.transactionDate
                    transactionHour = responseDTO.transactionHour
                    message = responseDTO.message
                    values = utilValueDTOToValueModel(responseDTO.values)
                }
                Observable.just(response)
        }
    }

    override fun getMenus(): Observable<ResponseMenuModel>? {
        return utilRepository.getMenus()
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseMenuModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    menus = converMenusDTOToMenuModel(it.menus)
                }
                Observable.just(response)
            }
    }

    @SuppressLint("CheckResult")
    override fun saveMenusInLocal(menus: List<MenuModel>?) {
        val menussEntity = arrayListOf<MenuEntityRoom>()
        menus?.forEachIndexed { index, item ->
            val entity = MenuEntityRoom(
                index.toString(),
                item.name,
                item.label,
                item.description,
                item.status,
                item.productCode
            )
            menussEntity.add(entity)
        }

        utilRepository.saveMenusInLocalRoom(menussEntity).subscribe {
            DatabaseViewModel.database.onNext(DatabaseViewModel.DatabaseEvents.MENUS_ADDED)
        }
    }

    private fun converMenusDTOToMenuModel(menus: List<MenuDTO>?): List<MenuModel> {
        val menusModel = mutableListOf<MenuModel>()
        menus?.forEach {
            menusModel.add(MenuModel().apply {
                name = it.name
                label = it.label
                description = it.description
                status = it.status
                productCode = it.productCode
            })
        }
        return menusModel
    }

    private fun getRequestUtilValueDTO(requestModel: RequestUtilModel): List<RequestUtilValueDTO> {
        val values = arrayListOf<RequestUtilValueDTO>()
        values.add(
            RequestUtilValueDTO().apply {
                id = "serie1"
                value = requestModel.serie1
            }
        )

        values.add(
            RequestUtilValueDTO().apply {
                id = "serie2"
                value = requestModel.serie2
            }
        )

        values.add(
            RequestUtilValueDTO().apply {
                id = "codVendedor"
                value = requestModel.sellerCode
            }
        )
        return values
    }

    private fun utilValueDTOToValueModel(valuesDTO: List<ValuesUpdateStubDTO>?): List<ValuesUpdateStubModel> {
        val values = arrayListOf<ValuesUpdateStubModel>()
        valuesDTO?.forEach {
            values.add(
                ValuesUpdateStubModel().apply {
                    id = it.id
                    value = it.value
                }
            )
        }
        return values
    }

}