package co.com.pagatodo.core.views.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.views.login.LoginActivity
import io.reactivex.disposables.Disposable
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.request.*
import co.com.pagatodo.core.network.retryCount
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.views.baloto.BalotoViewModel
import co.com.pagatodo.core.views.betplay.BetplayViewModel
import co.com.pagatodo.core.views.chance.ChanceViewModel
import co.com.pagatodo.core.views.lottery.LotterySaleViewModel
import co.com.pagatodo.core.views.maxichance.MaxiChanceViewModel
import co.com.pagatodo.core.views.paymillionaire.PayMillionaireViewModel
import co.com.pagatodo.core.views.raffle.RaffleViewModel
import co.com.pagatodo.core.views.recharge.RechargeViewModel
import co.com.pagatodo.core.views.recharge.dispatchRecharge
import co.com.pagatodo.core.views.sporting.SportingViewModel
import co.com.pagatodo.core.views.sporting.sellSportingBets
import co.com.pagatodo.core.views.superastro.SuperAstroViewModel
import co.com.pagatodo.core.views.superastro.paySuperAstro
import co.com.pagatodo.core.views.superchance.SuperChanceViewModel
import co.com.pagatodo.core.views.superchance.paySuperchance
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pay_millonaire.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import java.util.*
import java.util.concurrent.TimeUnit

open class MenuBaseActivity : AppCompatActivity() {

    lateinit var bioFacade: com.datacenter.fingerprintlibrary.BioFacade
    lateinit var bioFacadeQ2: com.datacenter.fingerprintlibraryq2.BioFacade
    lateinit var payMillonaireViewModel: PayMillionaireViewModel
    lateinit var chanceViewModel: ChanceViewModel
    lateinit var maxiChanceViewModel: MaxiChanceViewModel
    lateinit var superChanceViewModel: SuperChanceViewModel
    lateinit var rechargeViewModel: RechargeViewModel
    lateinit var superastroViewModel: SuperAstroViewModel
    lateinit var lotterySaleViewModel: LotterySaleViewModel
    lateinit var balotoViewModel: BalotoViewModel
    lateinit var raffleViewModel: RaffleViewModel
    lateinit var betplayViewModel: BetplayViewModel
    lateinit var sportingViewModel: SportingViewModel

    lateinit var progressDialog: ProgressDialog
    private var subscriptions = arrayListOf<Disposable>()
    private var alertDialogo: AlertDialog? = null

    val timeOut: String = getPreference(R_string(R.string.shared_key_session_time_out))
    var isRunning = false;
    val sessionTimerOut =
        object : CountDownTimer((if (timeOut != "") timeOut.toLong() else 1) * 1000, 5000 * 10) {
            override fun onTick(millisUntilFinished: Long) {
                validateSessionTimeOut()
                clearMemory()
            }

            override fun onFinish() {
                isRunning = false
                validateSessionTimeOut()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBaseView()
        setupViewModel()
        setupBiometria()
    }

    fun setupBiometria() {

        bioFacade = com.datacenter.fingerprintlibrary.BioFacade.getInstance(this@MenuBaseActivity)
        bioFacadeQ2 = com.datacenter.fingerprintlibraryq2.BioFacade.getInstance(
            this@MenuBaseActivity,
            this@MenuBaseActivity
        )

    }


    override fun onResume() {
        super.onResume()

        isRunning = false
        validateSessionTimeOut()

        subscriptions.add(
            BaseObservableViewModel.networkSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        NetworkEvents.UnauthorizedService -> showAlertDialogSessionExpired()
                        NetworkEvents.SocketTimeoutException -> {
                            showOkAlertDialog(
                                R_string(R.string.title_dialog_error),
                                R_string(R.string.message_error_server_not_connection)
                            )
                        }
                        NetworkEvents.MaxNumberRetry -> showAlertDialogMaxNumberRetry()
                    }
                }
        )

        subscriptions.add(
            BaseObservableViewModel.baseSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        BaseEvents.HideProgressDialog -> hideDialogProgress()
                        is BaseEvents.ShowAlertDialogInMenu -> {
                            hideDialogProgress()
                            showOkAlertDialog(it.title, it.message) {
                                removeLastTransaction()
                                hideDialogProgress()
                                if (it.isSuccessTransaction) {
                                    validateNavigate(it.fragment)

                                }
                            }


                        }
                        is BaseEvents.ShowAlertDialogClosure -> {

                        showOkAlertDialogCancel(it.title, it.message, {
                            SharedPreferencesUtil.removePreference(getString(R.string.shared_key_dialog_pending))
                            it.closure()
                        }, {
                            SharedPreferencesUtil.removePreference(getString(R.string.shared_key_dialog_pending))
                            if (it.fragment == null)
                                navigateToMenu(this)
                            else
                                navigateToMenuFragment(it.fragment)
                        })
                    }
                    }
                }
        )
    }

    private fun validateNavigate(baseFragment: BaseFragment?) {
        if (baseFragment == null)
            navigateToMenu(this)
        else
            navigateToMenuFragment(baseFragment)
    }

    override fun onPause() {
        super.onPause()

        unSubscriptions()
        validateSessionTimeOut()
        sessionTimerOut.cancel()
        SecurityUtil.updateSessionTimeOut(getPreference(R_string(R.string.shared_key_session_time_out)))
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        SecurityUtil.updateSessionTimeOut(getPreference(R_string(R.string.shared_key_session_time_out)))

    }

    override fun onStart() {
        super.onStart()

        SecurityUtil.updateSessionTimeOut(getPreference(R_string(R.string.shared_key_session_time_out)))
        validateAlertDialogPending()
    }

    private fun validateAlertDialogPending() {

        val data = getPreference<String>(getString(R.string.shared_key_dialog_pending))

        if (data != "") {
            showOkAlertDialog("", data) {
                removeLastTransaction()
                SharedPreferencesUtil.removePreference(getString(R.string.shared_key_dialog_pending))
            }

        }

    }

    fun validateSessionTimeOut() {

        if (getPreference(R_string(R.string.shared_key_session_time_out_date)) as String != "") {

            val dateSession = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                getPreference(R_string(R.string.shared_key_session_time_out_date))
            )

            if (Date().compareTo(dateSession) > 0) {
                sessionTimerOut.cancel()
                clearSharedPreferences()
                showAlertDialogSessionExpired()
            } else {

                if (!isRunning) {
                    isRunning = true
                    sessionTimerOut.start()
                }
            }
        } else {
            isRunning = false
            sessionTimerOut.cancel()

        }

    }

    private fun clearSharedPreferences() {
        SharedPreferencesUtil.savePreference(
            R_string(R.string.shared_key_session_time_out_date),
            ""
        )
        SharedPreferencesUtil.savePreference(R_string(R.string.shared_key_session_time_out), "")
    }

    protected fun setupViewModel() {
        payMillonaireViewModel =
            ViewModelProviders.of(this).get(PayMillionaireViewModel::class.java)
        chanceViewModel = ViewModelProviders.of(this).get(ChanceViewModel::class.java)
        maxiChanceViewModel = ViewModelProviders.of(this).get(MaxiChanceViewModel::class.java)
        superChanceViewModel = ViewModelProviders.of(this).get(SuperChanceViewModel::class.java)
        rechargeViewModel = ViewModelProviders.of(this).get(RechargeViewModel::class.java)
        superastroViewModel = ViewModelProviders.of(this).get(SuperAstroViewModel::class.java)
        lotterySaleViewModel = ViewModelProviders.of(this).get(LotterySaleViewModel::class.java)
        balotoViewModel = ViewModelProviders.of(this).get(BalotoViewModel::class.java)
        raffleViewModel = ViewModelProviders.of(this).get(RaffleViewModel::class.java)
        betplayViewModel = ViewModelProviders.of(this).get(BetplayViewModel::class.java)
        sportingViewModel = ViewModelProviders.of(this).get(SportingViewModel::class.java)
    }

    protected fun setupBaseView() {
        setSupportActionBar(mainToolbar as Toolbar?)
        progressDialog = ProgressDialog(this)
        mainToolbar?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    protected fun updateTitle(title: String) {
        mainToolbar.title.text = title
    }

    protected fun getStub(): String {
        val stubs = getStubs()
        return "${stubs.serie1?.toUpperCase()}  ${stubs.serie2?.toUpperCase()}"
    }

    protected fun getStubs(): StubModel {
        val serie1 = getPreference<String>(R_string(R.string.shared_key_current_serie1))
        val serie2 = getPreference<String>(R_string(R.string.shared_key_current_serie2))
        return StubModel().apply {
            this.serie1 = serie1
            this.serie2 = serie2
        }
    }

    //TODO: merge with BaseActivity
    fun showAlertDialogSessionExpired() {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            alertDialogo = AlertDialog.Builder(this@MenuBaseActivity).apply {
                setTitle(getString(R.string.text_title_session_not_found))
                setMessage(getString(R.string.text_message_session_not_found))
                setCancelable(false)
                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, _ ->
                    val intent = Intent(this@MenuBaseActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
            }.show()
        }
    }

    //TODO: merge with BaseActivity
    fun showAlertDialogMaxNumberRetry() {
        hideDialogProgress()
        if (alertDialogo == null || alertDialogo?.isShowing == false) {
            alertDialogo = AlertDialog.Builder(this@MenuBaseActivity).apply {
                setTitle(getString(R.string.title_failed_transaction))
                setMessage(getString(R.string.message_error_pending_failed_transaction))
                setCancelable(false)
                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    //TODO: merge with BaseActivity
    fun showOkAlertDialog(title: String, message: String) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@MenuBaseActivity).apply {
                    setTitle(title)
                    setMessage(message)
                    setCancelable(false)
                    setPositiveButton(R_string(R.string.text_btn_accept)) { dialog, which ->
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }

    //TODO: merge with BaseActivity
    fun showOkAlertDialog(title: String, message: String, closure: () -> Unit?) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@MenuBaseActivity).apply {
                    setTitle(title)
                    setMessage(message)
                    setCancelable(false)
                    setPositiveButton(R_string(R.string.text_btn_accept)) { dialog, which ->
                        dialog.dismiss()
                        closure()
                    }
                }.show()
            }
        }
    }

    //TODO: merge with BaseActivity
    fun showAlertDialog(
        title: String,
        message: String,
        positiveClosure: () -> Unit?,
        negativeClosure: () -> Unit?
    ) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@MenuBaseActivity).apply {
                    setTitle(title)
                    setMessage(message)
                    setCancelable(false)
                    setPositiveButton(R_string(R.string.text_btn_accept)) { dialog, which ->
                        dialog.dismiss()
                        positiveClosure()
                    }
                    setNegativeButton(R_string(R.string.text_btn_cancel)) { dialog, which ->
                        dialog.dismiss()
                        negativeClosure()
                    }
                }.show()
            }
        }
    }

    //TODO: merge with BaseActivity
    fun showDialogProgress(message: String) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    //TODO: merge with BaseActivity
    fun hideDialogProgress() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun executePendingTransaction(lastTransaction: LastTransactionStatusModel) {
        showOkAlertDialog(
            getString(R.string.title_pending_transaction),
            getString(R.string.message_error_pending_retry_transaction)
        ) {
            removeLastTransaction()
            retryCount = 0
            showDialogProgress(getString(R.string.message_dialog_request))

            when (lastTransaction.productName) {
                ProductName.PAYMILLIONAIRE.rawValue -> {
                    payMillonaireViewModel.dispatchPayMillionaire(
                        lastTransactionStatusObject as RequestPayMillonaireModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.CHANCE.rawValue -> {
                    chanceViewModel.dispatchPayChance(
                        lastTransactionStatusObject as RequestChanceModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.MAXI_CHANCE.rawValue -> {
                    maxiChanceViewModel.payMaxichance(
                        lastTransactionStatusObject as RequestSuperChanceModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.SUPER_CHANCE.rawValue -> {
                    superChanceViewModel.paySuperchance(
                        lastTransactionStatusObject as RequestSuperChanceModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.RECHARGE.rawValue -> {
                    rechargeViewModel.dispatchRecharge(
                        lastTransactionStatusObject as RequestRechargeModel,
                        false, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.SUPER_ASTRO.rawValue -> {
                    superastroViewModel.paySuperAstro(
                        lastTransactionStatusObject as RequestSuperAstroModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.PHYSICAL_LOTTERY.rawValue -> {
                    lotterySaleViewModel.payPhysicalLottery(
                        lastTransactionStatusObject as RequestSaleOfLotteries,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.VIRTUAL_LOTTERY.rawValue -> {
                    lotterySaleViewModel.payVirtualLottery(
                        lastTransactionStatusObject as RequestSaleOfLotteries,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.BALOTO.rawValue -> {
                    balotoViewModel.dispatchPayBalotoBet(
                        lastTransactionStatusObject as RequestBalotoModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.RAFFLE.rawValue -> {
                    raffleViewModel.sellRaffleNumber(
                        lastTransactionStatusObject as RequestRaffleModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.BETPLAY.rawValue -> {
                    betplayViewModel.isValidDocument(
                        lastTransactionStatusObject as RequestBetplayModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.MEGAGOAL.rawValue -> {
                    sportingViewModel.sellMegaGoaldBet(
                        lastTransactionStatusObject as RequestSportingModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
                ProductName.LEAGUE14.rawValue -> {
                    sportingViewModel.sellSportingBets(
                        lastTransactionStatusObject as RequestSportingModel,
                        true, TransactionType.RETRY.rawValue
                    )
                }
            }
        }
    }

    fun navigateToMenuFragment(baseFragment: BaseFragment) {
        hideDialogProgress()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.containerMainMenu)

        if (currentFragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.containerMainMenu, baseFragment)
                .commit()
        } else
            navigateToMenu(this)


    }

    fun navigateToMenu(activity: Activity) {
        unSubscriptions()
        activity.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        unSubscriptions()
        hideKeyboard()
        clearMemory()
        finish()
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }

    private fun showAuthToken(closure: (data: String) -> Unit?) {

        if (alertDialogo == null || alertDialogo?.isShowing != true) {

            runOnUiThread {


                val inflater = this.layoutInflater

                val dialogView = inflater.inflate(R.layout.dialog_token_validator, null)
                val txtToken = dialogView.findViewById<EditText>(R.id.txtToken)

                val dialog = AlertDialog.Builder(this@MenuBaseActivity).apply {
                    setTitle(R_string(R.string.dialog_token_title))
                    setView(dialogView)
                    setCancelable(false)
                    setNegativeButton(R_string(R.string.text_btn_cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    setPositiveButton(getString(R.string.text_btn_confirm)) { dialog, which ->
                        dialog.dismiss()
                        closure(txtToken.text.toString())

                    }
                }.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false


                RxTextView.textChanges(txtToken)
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
                            txtToken.text.isNotEmpty()
                    }

                dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                    ContextCompat.getColor(
                        this@MenuBaseActivity,
                        R.color.secondaryText
                    )
                )


            }

        }

    }

    private fun showAuthSellerBiometria(closure: (data: String, imgData: String) -> Unit?) {

        runOnUiThread {

            if (DeviceUtil.isSalePoint()) {
                salePointBiometria(closure)
            } else {

                if (DeviceUtil.getDeviceModel() == DatafonoType.Q2.type) {
                    q2Biometria(closure)
                }

            }
        }

    }

    private fun validateType(key: String): AuthMode {

        when (getPreference<String>(key)) {
            "B" -> return AuthMode.BIOMETRY
            "T" -> return AuthMode.TOKEN
            "N" -> return AuthMode.NONE
            else -> return AuthMode.NONE
        }

    }

    fun validateAuthSeller(
        key: String,
        closure: (type: AuthMode, data: String, dataImg: String) -> Unit?
    ) {

        when (validateType(key)) {

            AuthMode.NONE -> {
                closure(AuthMode.NONE, "", "")
            }
            AuthMode.TOKEN -> {
                showAuthToken {
                    closure(AuthMode.TOKEN, it, "")
                }

            }
            AuthMode.BIOMETRY -> {
                showAuthSellerBiometria { data, dataImage ->
                    closure(AuthMode.BIOMETRY, data, dataImage)
                }
            }

        }


    }

    fun showAuthUserBiometria(closure: (data: String, imgData: String) -> Unit?) {
        runOnUiThread {

            if (DeviceUtil.isSalePoint()) {
                salePointBiometria(closure)
            } else {

                if (DeviceUtil.getDeviceModel() == DatafonoType.Q2.type) {
                    q2Biometria(closure)
                }

            }
        }
    }

    private fun salePointBiometria(closure: (data: String, imgData: String) -> Unit?) {

        val call = object : com.datacenter.fingerprintlibrary.IResultListener {
            override fun onResultData(p0: Array<out Array<String>>?) {
                closure(p0?.get(0)?.get(1) ?: "", p0?.get(0)?.get(2) ?: "")
            }

        }
        bioFacade.setListener(call)
        bioFacade.procesar(
            arrayOf(com.datacenter.fingerprintlibrary.HuellaEnum.DERECHA_INDICE),
            true
        )
    }

    private fun q2Biometria(closure: (data: String, imgData: String) -> Unit?) {

        val call = object : com.datacenter.fingerprintlibraryq2.IResultListener {
            override fun onResultData(p0: Array<out Array<String>>?) {
                closure(p0?.get(0)?.get(1) ?: "", p0?.get(0)?.get(2) ?: "")
            }

        }
        bioFacadeQ2.setListener(call)
        bioFacadeQ2.procesar(
            arrayOf(com.datacenter.fingerprintlibraryq2.HuellaEnum.DERECHA_INDICE),
            true
        )
    }

    fun unSubscriptions(){

        subscriptions.forEach {
            it.dispose()
        }
        subscriptions.clear()

    }

    fun showOkAlertDialogCancel(
        title: String,
        message: String,
        closure: () -> Unit?,
        closureCancel: () -> Unit?
    ) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@MenuBaseActivity).apply {
                    setTitle(title)
                    setMessage(message)
                    setCancelable(false)
                    setPositiveButton(R_string(R.string.text_btn_accept)) { dialog, which ->
                        dialog.dismiss()
                        closure()
                    }
                    setNegativeButton(R_string(R.string.text_btn_cancel)) { dialog, which ->
                        dialog.dismiss()
                        closureCancel()
                    }

                }.show()

                alertDialogo?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                    ContextCompat.getColor(
                        this@MenuBaseActivity,
                        R.color.secondaryText
                    )
                )

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMemory()
        unSubscriptions()
    }

    fun clearMemory(){
        cacheDir.deleteRecursively()
        Runtime.getRuntime().gc();
    }

}