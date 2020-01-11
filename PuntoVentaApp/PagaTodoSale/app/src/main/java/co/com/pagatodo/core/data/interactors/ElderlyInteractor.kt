package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.*
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.ElderlyPointedFingerModel
import co.com.pagatodo.core.data.model.ElderlyThirdModel
import co.com.pagatodo.core.data.model.print.ElderlyPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IElderlyRepository
import co.com.pagatodo.core.data.repositories.IGiroRepository
import co.com.pagatodo.core.di.DaggerElderlyComponent
import co.com.pagatodo.core.di.ElderlyModule
import co.com.pagatodo.core.util.RUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference as getPreference


interface IElderlyInteractor {

    fun elderlyQueryGetReprint(third: ElderlyThirdModel): Observable<ResponseElderlyGetReprintDTO>?
    fun elderlyQueryAuthorizedTerminal(): Observable<ResponseElderlyAuthorizedTerminalDTO>?
    fun elderlyPointedFingers(third: ElderlyThirdModel): Observable<ResponsePointedFingersDTO>?
    fun elderlyQueryId(third: ElderlyThirdModel): Observable<ResponseElderlyQueryIdDTO>?
    fun elderlySaveFingerPrint(
        third: ElderlyThirdModel,
        pointedFinger: ElderlyPointedFingerModel
    ): Observable<ResponseElderlySaveFingerPrintDTO>?

    fun elderlyQuerySubsidy(third: ElderlyThirdModel): Observable<ResponseElderlyQuerySubsidyDTO>?
    fun elderlyQueryAuthenticateFootprint(third: ElderlyThirdModel): Observable<ResponseElderlyAuthenticateFootprintDTO>?
    fun elderlySaveId(third: ElderlyThirdModel): Observable<ResponseElderlySaveIdDTO>?
    fun elderlyQueryMakeSubsidyPayment(elderlySubsidyPaymentModel: ElderlySubsidyPaymentModel): Observable<ResponseElderlyMakeSubsidyPaymentDTO>?
    fun getCities(cityName: String): Observable<ResponseQueryCitiesDTO>?
    fun getParameterElderly(): Observable<ResponseElderlyParameterDTO>?
    fun printPayment(
        elderlyPrintModel: ElderlyPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    )

    fun createThird(
        thirdDTO: ThirdDTO
    ): Observable<ResponseThirdCreateDTO>?


}

@Singleton
class ElderlyInteractor : IElderlyInteractor {

    @Inject
    lateinit var elderlyRepository: IElderlyRepository

    @Inject
    lateinit var giroRepository: IGiroRepository

    init {
        DaggerElderlyComponent
            .builder()
            .elderlyModule(ElderlyModule())
            .build()
            .inject(this)
    }

    constructor()

    constructor(elderlyRepository: IElderlyRepository) {
        this.elderlyRepository = elderlyRepository
    }

    override fun elderlyQueryGetReprint(third: ElderlyThirdModel): Observable<ResponseElderlyGetReprintDTO>? {
        
        val requestElderlyGetReprintDTO = RequestElderlyGetReprintDTO().apply {

            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.documentType = third.documentType
            this.documentNumber =  third.document

        }

        return elderlyRepository
            .elderlyQueryGetReprint(requestElderlyGetReprintDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlyQueryAuthorizedTerminal(): Observable<ResponseElderlyAuthorizedTerminalDTO>? {
        val requestElderlyAuthorizedTerminalDTO = RequestElderlyAuthorizedTerminalDTO().apply {
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return elderlyRepository
            .elderlyQueryAuthorizedTerminal(requestElderlyAuthorizedTerminalDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlyPointedFingers(third: ElderlyThirdModel): Observable<ResponsePointedFingersDTO>? {

        val requestPointedFingersDTO = RequestPointedFingersDTO().apply {
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.login = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.document = third.document
            this.documentType = third.documentType
        }

        return elderlyRepository
            .elderlyPointedFingers(requestPointedFingersDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlyQueryId(third: ElderlyThirdModel): Observable<ResponseElderlyQueryIdDTO>? {

        val requestElderlyQueryIdDTO = RequestElderlyQueryIdDTO().apply {
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.login = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.document = third.document
            this.documentType = third.documentType
        }

        return elderlyRepository
            .elderlyQueryId(requestElderlyQueryIdDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlySaveFingerPrint(
        third: ElderlyThirdModel,
        pointedFinger: ElderlyPointedFingerModel
    ): Observable<ResponseElderlySaveFingerPrintDTO>? {

        val requestElderlySaveFingerPrintDTO = RequestElderlySaveFingerPrintDTO().apply {
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.login = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.document = third.document
            this.documentType = third.documentType

            this.indexFingerPrintL = pointedFinger.indexFingerL
            this.middleFingerPrintL = pointedFinger.middleFingerL
            this.ringFingerPrintL = pointedFinger.ringFingerL
            this.littleFingerPrintL = pointedFinger.littleFingerL

            this.indexFingerPrintR = pointedFinger.indexFingerR
            this.middleFingerPrintR = pointedFinger.middleFingerR
            this.ringFingerPrintR = pointedFinger.ringFingerR
            this.littleFingerPrintR = pointedFinger.littleFingerR

        }

        return elderlyRepository
            .elderlySaveFingerPrint(requestElderlySaveFingerPrintDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlyQuerySubsidy(third: ElderlyThirdModel): Observable<ResponseElderlyQuerySubsidyDTO>? {

        val requestElderlyQuerySubsidyDTO = RequestElderlyQuerySubsidyDTO().apply {
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.login = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.document = third.document
            this.documentType = third.documentType
        }

        return elderlyRepository
            .elderlyQuerySubsidy(requestElderlyQuerySubsidyDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlyQueryAuthenticateFootprint(third: ElderlyThirdModel): Observable<ResponseElderlyAuthenticateFootprintDTO>? {
        val requestElderlyAuthenticateFootprintDTO =
            RequestElderlyAuthenticateFootprintDTO().apply {
                this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
                this.terminalCode =
                    getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
                this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
                this.productCode = RUtil.R_string(R.string.code_product_elderly)
                this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
                this.login = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
                this.documentNumber = third.document
                this.documentType = third.documentType
                this.indexFingerPrintRight = third.indexFingerPrintRight
            }

        return elderlyRepository
            .elderlyQueryAuthenticateFootprint(requestElderlyAuthenticateFootprintDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlySaveId(third: ElderlyThirdModel): Observable<ResponseElderlySaveIdDTO>? {

        val requestElderlySaveIdDTO = RequestElderlySaveIdDTO().apply {
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.login = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.documentType = third.documentType
            this.documentNumber = third.document
            this.documentUser = third.documentUser

        }

        return elderlyRepository
            .elderlySaveId(requestElderlySaveIdDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun elderlyQueryMakeSubsidyPayment(elderlySubsidyPaymentModel: ElderlySubsidyPaymentModel): Observable<ResponseElderlyMakeSubsidyPaymentDTO>? {

        val requestElderlyMakeSubsidyPaymentDTO = RequestElderlyMakeSubsidyPaymentDTO().apply {
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_elderly)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.login = getPreference(RUtil.R_string(R.string.shared_key_seller_code))

            this.reference = elderlySubsidyPaymentModel.reference
            this.titularDocumentType = elderlySubsidyPaymentModel.titularDocumentType
            this.titularDocumentId = elderlySubsidyPaymentModel.titularDocumentId
            this.titularName = elderlySubsidyPaymentModel.titularName
            this.titularFirstLastName = elderlySubsidyPaymentModel.titularFirstLastName
            this.titularSecondLastName = elderlySubsidyPaymentModel.titularSecondLastName
            this.payValue = elderlySubsidyPaymentModel.payValue
            this.curator = elderlySubsidyPaymentModel.curator
            this.documentTypeAuthorired = elderlySubsidyPaymentModel.documentTypeAuthorired
            this.authorizedName = elderlySubsidyPaymentModel.authorizedName
            this.authorizedFirstLastName = elderlySubsidyPaymentModel.authorizedFirstLastName
            this.authorizedSecondLastName = elderlySubsidyPaymentModel.authorizedSecondLastName
            this.footprintName = elderlySubsidyPaymentModel.footprintName
            this.footprintImage = elderlySubsidyPaymentModel.footprintImage
            this.documentAuthorired = elderlySubsidyPaymentModel.authoriredDocumentId

        }

        return elderlyRepository
            .elderlyQueryMakeSubsidyPayment(requestElderlyMakeSubsidyPaymentDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun getCities(cityName: String): Observable<ResponseQueryCitiesDTO>? {

        val requestQueryCitiesDTO = RequestQueryCitiesDTO().apply {

            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.cityName = cityName
        }

        return elderlyRepository
            .getCities(requestQueryCitiesDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)

            }


    }

    override fun getParameterElderly(): Observable<ResponseElderlyParameterDTO>? {
        val userLocal = giroRepository.getLocalGiroUser()
        val sesionElderlyLocal = giroRepository.getLocalGiroLogin()

        val requestElderlyParameterDTO = RequestElderlyParameterDTO().apply {

            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.user = userLocal
            this.box = GiroCaptureBoxDTO().apply {
                this.boxCode = sesionElderlyLocal.box?.boxCode
            }

        }

        return elderlyRepository
            .getParameterElderly(requestElderlyParameterDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun createThird(
        thirdDTO: ThirdDTO
    ): Observable<ResponseThirdCreateDTO>? {

        val userLocal = giroRepository.getLocalGiroUser()

        val requestThirdCreateDTO = RequestThirdCreateDTO().apply {
            this.canalId = getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.biometrics = "N"
            this.user = GiroUserDTO().apply {
                this.user = userLocal.user
                this.password = userLocal.password
                this.agency = userLocal.agency
            }
            this.third = thirdDTO

        }

        return elderlyRepository
            .createThird(requestThirdCreateDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                Observable.just(it)
            }


    }

    override fun printPayment(
        elderlyPrintModel: ElderlyPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
       elderlyRepository.printPayment(elderlyPrintModel,callback)
    }

}