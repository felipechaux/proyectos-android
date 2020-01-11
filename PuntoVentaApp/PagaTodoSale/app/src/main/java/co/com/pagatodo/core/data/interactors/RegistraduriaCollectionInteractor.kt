package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.request.RequestRegistraduriaCollectionDTO
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.RegistraduriaCollectionModel
import co.com.pagatodo.core.data.model.print.RegistraduriaCollectionPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IRegistraduriaCollectionRepository
import co.com.pagatodo.core.di.DaggerRegistraduriaCollectionComponent
import co.com.pagatodo.core.di.RegistraduriaCollectionModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IRegistraduriaCollectionInteractor{

    fun queryRegistraduriaGetCollection(pinNumber: String?): Observable<ResponseRegistraduriaGetCollectionDTO>?
    fun queryRegistraduriaGetServices(): Observable<ResponseRegistraduriaGetServiceDTO>?
    fun queryRegistraduriaCollection(registraduriaModel: RegistraduriaCollectionModel): Observable<ResponseRegistraduriaCollectionDTO>?
    fun registraduriaCollectionPrint(printModel: RegistraduriaCollectionPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)

}

@Singleton
class RegistraduriaCollectionInteractor : IRegistraduriaCollectionInteractor {

    @Inject
    lateinit var registraduriaCollectionRepository: IRegistraduriaCollectionRepository

    init {
        DaggerRegistraduriaCollectionComponent
            .builder()
            .registraduriaCollectionModule(RegistraduriaCollectionModule())
            .build()
            .inject(this)
    }

    constructor()

    constructor(registraduriaCollectionRepository: IRegistraduriaCollectionRepository) {
        this.registraduriaCollectionRepository = registraduriaCollectionRepository
    }

    override fun queryRegistraduriaGetCollection(pinNumber: String?): Observable<ResponseRegistraduriaGetCollectionDTO>? {
        val requestRegistraduriaGetCollectionDTO = RequestRegistraduriaGetCollectionDTO().apply {
            this.macAddress = RUtil.R_string(R.string.registraduria_mac_address)
            this.channelId = RUtil.R_string(R.string.canalId_dataphone)
            this.terminalCode = RUtil.R_string(R.string.registraduria_mac_address)
            this.productCode = RUtil.R_string(R.string.registraduria_product_code)
            this.sellerCode = RUtil.R_string(R.string.registraduria_seller_code)
            this.userType = RUtil.R_string(R.string.registraduria_user_type)
            this.salePin = pinNumber
        }

        return registraduriaCollectionRepository
            .queryRegistraduriaGetCollection(requestRegistraduriaGetCollectionDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun queryRegistraduriaGetServices(): Observable<ResponseRegistraduriaGetServiceDTO>? {

        val requestRegistraduriaGetServiceDTO = RequestRegistraduriaGetServiceDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.macAddress = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.productCode = RUtil.R_string(R.string.registraduria_product_code)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))


        }

        return registraduriaCollectionRepository
            .queryRegistraduriaGetServices(requestRegistraduriaGetServiceDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun queryRegistraduriaCollection(registraduriaModel: RegistraduriaCollectionModel): Observable<ResponseRegistraduriaCollectionDTO>? {

        val requestRegistraduriaCollectionDTO = RequestRegistraduriaCollectionDTO().apply {

            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.macAddress = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.deviceVol = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.productCode = RUtil.R_string(R.string.registraduria_product_code)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.documentType = registraduriaModel.documentType
            this.documentNumber = registraduriaModel.documentNumber
            this.firstName = registraduriaModel.firstName
            this.secondName = registraduriaModel.secondName
            this.lastName = registraduriaModel.lastName
            this.secondLastName = registraduriaModel.secondLastName
            this.particle = registraduriaModel.particle
            this.movile = registraduriaModel.movile
            this.docTypeProcess = registraduriaModel.docTypeProcess
            this.serviceName = registraduriaModel.serviceName
            this.registraduriaOffice = registraduriaModel.registraduriaOffice
            this.email = registraduriaModel.email
            this.value = registraduriaModel.value


        }

        return registraduriaCollectionRepository
            .queryRegistraduriaCollection(requestRegistraduriaCollectionDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun registraduriaCollectionPrint(printModel: RegistraduriaCollectionPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        registraduriaCollectionRepository.registraduriaCollectionPrint(printModel, callback)
    }

}