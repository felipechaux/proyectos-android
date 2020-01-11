package co.com.pagatodo.core.data.interactors

import android.annotation.SuppressLint
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.SessionEntity
import co.com.pagatodo.core.data.database.entities.StubEntity
import co.com.pagatodo.core.data.dto.FingerPrintDTO
import co.com.pagatodo.core.data.dto.StubDTO
import co.com.pagatodo.core.data.dto.request.RequestAuthFingerPrintSellerDTO
import co.com.pagatodo.core.data.dto.request.RequestAuthTokenDTO
import co.com.pagatodo.core.data.dto.response.BaseResponseDTO
import co.com.pagatodo.core.data.model.AuthModel
import co.com.pagatodo.core.data.model.StubModel
import co.com.pagatodo.core.data.model.response.ResponseGenericModel
import co.com.pagatodo.core.data.repositories.IAuthRepository
import co.com.pagatodo.core.di.*
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SecurityUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IAuthInteractor {
    fun auth(sellerCode: String, password: String): Observable<AuthModel>?
    fun logout(): Observable<ResponseGenericModel>?
    fun getGeneralParameters()
    fun getStubs(): Observable<List<StubModel>>?
    fun saveStubsInfo(stubsDto: List<StubDTO>)
    fun authFingerPrintSeller(
        identificationType: String,
        identificationNumber: String,
        fingerPrint: String
    ): Observable<BaseResponseDTO>?

    fun authToken(identificationNumber: String, token: String): Observable<BaseResponseDTO>?
}

@Singleton
class AuthInteractor : IAuthInteractor {

    @Inject
    lateinit var authRepository: IAuthRepository
    @Inject
    lateinit var gralParamInteractor: GeneralParameterInteractor
    @Inject
    lateinit var lotteryInteractor: LotteryInteractor
    @Inject
    lateinit var localInteractor: ILocalInteractor
    @Inject
    lateinit var raffleInteractor: RaffleInteractor
    @Inject
    lateinit var modalityInteractor: ModalityInteractor
    @Inject
    lateinit var rechargeInteractor: RechargeInteractor
    @Inject
    lateinit var promotionInteractor: PromotionInteractor

    init {
        DaggerAuthComponent.builder()
            .authModule(AuthModule())
            .generalParameterModule(GeneralParameterModule())
            .lotteryModule(LotteryModule())
            .localModule(LocalModule())
            .raffleModule(RaffleModule())
            .build()
            .inject(this)
    }

    constructor()

    constructor(authRepository: IAuthRepository) {
        this.authRepository = authRepository
    }

    override fun auth(sellerCode: String, password: String): Observable<AuthModel>? {

        val encryptedPassword = SecurityUtil.convertStringToSH256(password)
        return authRepository
            .login(sellerCode, encryptedPassword)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val authModel = AuthModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    token = it.token
                    sessionId = it.sessionId
                }

                val sessionInfo = SessionEntity(
                    1, it.user?.code, it.user?.name,
                    it.token, it.sessionId,
                    it.terminal?.codePointSale,
                    it.user?.municipalityCode,
                    it.user?.officeCode
                )

                authRepository.saveSessionInfo(sessionInfo)
                authRepository.saveParameterInfo(it.parameters)
                it.stubs?.let { stubs -> saveStubsInfo(stubs) }
                SharedPreferencesUtil.savePreference(
                    RUtil.R_string(R.string.shared_key_user_id),
                    it.user?.identificationDocument ?: ""
                )
                SharedPreferencesUtil.savePreference(
                    RUtil.R_string(R.string.shared_key_user_id_type),
                    it.user?.documentType ?: ""
                )
                Observable.just(authModel)
            }
    }

    override fun logout(): Observable<ResponseGenericModel>? {
        return authRepository
            .logout()
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val genericResponse = ResponseGenericModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess ?: false
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                }

                Observable.just(genericResponse)
            }
    }

    @SuppressLint("CheckResult")
    override fun getGeneralParameters() {

        if (authRepository.validateSessionDate()) {

            authRepository.clearPreference()

            gralParamInteractor
                .getGeneralParameters()
                ?.flatMap { gralParameter ->

                    if(gralParameter.isSuccess){

                        lotteryInteractor.saveLotteriesInLocal(gralParameter.lotteries)
                        rechargeInteractor.saveOperatorsInLocal(gralParameter.operators)
                        raffleInteractor.saveRafflesInLocal(gralParameter.productos)

                        gralParameter.parameters?.let { params ->
                            localInteractor.saveParameters(params)
                        }

                        gralParameter.productos?.let { products ->
                            localInteractor.saveProductInfo(products).subscribe {
                                promotionInteractor.savePromotionalInLocal(gralParameter.productos)
                                lotteryInteractor.saveProductLotteriesInLocal(gralParameter.productos)
                                PagaTodoApplication.getDatabaseInstance()?.productDao()?.getAllAsync()
                                    ?.subscribe {
                                        modalityInteractor.saveModalitiesInLocal(products)
                                    }
                            }
                        }

                    }else{
                        authRepository.clearPreference(isClearParameterDate = true)
                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogSession("", R_string(R.string.auth_error_parameters)))
                    }


                    gralParamInteractor.getPaymillionaireParameters()
                }
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    if (it.isSuccess ?: false) {
                        localInteractor.savePaymillionaireModesInLocal(it.modes)
                    }
                }, {
                    // not implemented
                })

        }
    }

    override fun getStubs(): Observable<List<StubModel>>? {
        return authRepository.getStubsRoomAsync()?.flatMap {
            val models = mutableListOf<StubModel>()
            it.forEach { stub ->
                val stub = StubModel().apply {
                    serie1 = stub.serie1
                    serie2 = stub.serie2
                    uniqueSerie = stub.uniqueSerie
                    inUse = stub.inUse
                    entityCode = stub.entityCode
                    finalRank = stub.finalRank
                    chanceType = stub.chanceType
                }
                models.add(stub)
            }
            Observable.just(models)
        }
    }

    override fun saveStubsInfo(stubsDto: List<StubDTO>) {

        PagaTodoApplication.getDatabaseInstance()?.stubDao()?.deleteAll()
        var index: Long = 1

        val stubs = mutableListOf<StubEntity>()
        stubsDto.forEach { dto ->
            val stubEntity = StubEntity(
                index,
                dto.serie1,
                dto.serie2,
                dto.uniqueSerie,
                dto.inUse,
                dto.entityCode,
                dto.finalRank,
                dto.chanceType
            )
            stubs.add(stubEntity)
            index++
        }
        authRepository.saveStubsInfoRoom(stubs)
    }

    override fun authFingerPrintSeller(
        identificationType: String,
        identificationNumber: String,
        fingerPrint: String
    ): Observable<BaseResponseDTO>? {

        val requestAuthFingerPrintSellerDTO = RequestAuthFingerPrintSellerDTO().apply {

            this.macAdress =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.volMachine =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.user =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.canalId =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.userType =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.thirdType =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.fingerPrint = FingerPrintDTO().apply {
                this.fingerPrint = fingerPrint//"464d52002032300001b80033feff000000fc014400c500c5010000005644405100f94d5e4086002c625d40ba00bd635b406b0056735a409500f85a598014008e825840c400d40a5840500113565840d400709256802300ee4856806500fe4a56402e00df4955804b00d14454402501195854807d00a8ac51400900ad8251405100c7a051400e0105565140c500359e5040210109ac4f40140048764f802c0083854f804400a8334e402300d4984c402800fa584c805b00838d4b40de00968d4b80890090354a80e600b1984a407600e14a4a40bf0021a44940b7009e7147807d00ddb046401400f8ae46407a009c3744406b006889424053003b0b4140b100813240404800286940404100a2954040f100c95e3f804a0030693f404f0047753f40630076913d80db00c7b03d40c501155c3d4063007e903c40c400848b3a800f00ed4d3a40510026683940be007f8739804100211236403600b89b36808c00759b36809b008a3835400400e846350083007a943500a300833e3400b00086733400740069853200e700c04531005500410830004200260b2e005800490b2d002c001b0a2b0076006e8b2b00da00b4732b00d10022a6290000"
                this.id = identificationNumber//"9770418"
                this.idType = identificationType//"CC"
                this.enrolled = false
            }
        }

        return authRepository
            .authFingerPrintSeller(requestAuthFingerPrintSellerDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun authToken(
        identificationNumber: String,
        token: String
    ): Observable<BaseResponseDTO>? {

        val requestAuthTokenDTO = RequestAuthTokenDTO().apply {
            this.macAdress =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.volMachine =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))

            this.canalId =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.userType =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.token = token
            this.id = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
        }

        return authRepository
            .authToken(requestAuthTokenDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }
}