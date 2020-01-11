package co.com.pagatodo.core.views.base

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.com.pagatodo.core.BuildConfig
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.StubModel
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.savePreference
import co.com.pagatodo.core.util.print.tmu.EpsonPrinter
import co.com.pagatodo.core.views.homemenu.HomeMenuActivity
import co.com.pagatodo.core.views.login.LoginActivity
import co.com.pagatodo.core.views.sitp.ClienteRecaudoSitp.ClientSitpFacade
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pay_millonaire.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.reactivestreams.Subscription
import java.util.*
import java.util.concurrent.TimeUnit

open class BaseActivity : AppCompatActivity() {

    lateinit var bioFacade: com.datacenter.fingerprintlibrary.BioFacade
    lateinit var bioFacadeQ2: com.datacenter.fingerprintlibraryq2.BioFacade
    var client: ClientSitpFacade? = null
    var initDeviceOk: Boolean = false


    lateinit var progressDialog: ProgressDialog
    private var subscriptions = arrayListOf<Disposable>()
    private var alertDialogo: AlertDialog? = null

    private val REQUEST_ENABLE_BT = 1
    val REQUEST_READ_PHONE = 0
    val REQUEST_READ_EXTERNAL_STORAGEE = 2
    private val REQUEST_PTINER_PERMISSION = 100
    val CODE_PERMISOS = 101

    var isForeground = false


    val timeOut: String = getPreference(R_string(R.string.shared_key_session_time_out))
    var isRunning = false;
    val sessionTimerOut =
        object : CountDownTimer((if (timeOut != "") timeOut.toLong() else 1) * 1000, 500 * 10) {
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
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setupBaseView()
        setupBiometria()
    }

    fun setupBiometria() {

        bioFacade = com.datacenter.fingerprintlibrary.BioFacade.getInstance(this@BaseActivity)
        bioFacadeQ2 = com.datacenter.fingerprintlibraryq2.BioFacade.getInstance(
            this@BaseActivity,
            this@BaseActivity
        )

    }

    override fun onResume() {
        super.onResume()

        isRunning = false
        validateSessionTimeOut()

        initSubscriptionsNetworkSubject()
        initSubscriptionsBaseSubject()

        if (progressDialog.isShowing) {
            timerOutDialog.start()
        }

    }

    private fun initSubscriptionsBaseSubject(){

        subscriptions.add(
            BaseObservableViewModel.baseSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is BaseEvents.ShowAlertDialogInMenu -> showAlertDialogInMenu(it)
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
                        is BaseEvents.HideProgressDialog ->  hideDialogProgress()
                        is BaseEvents.ShowAlertDialogSession -> {
                            showOkAlertDialog(it.title,it.message){
                                showAlertDialogSessionExpired()
                            }
                        }
                    }
                }
        )
    }

    private fun showAlertDialogInMenu(it: BaseEvents.ShowAlertDialogInMenu) {
        hideDialogProgress()
        if (isForeground) {
            savePreference(getString(R.string.shared_key_dialog_pending),it.message)
            validateNavigate(it.fragment)
        } else {
            showOkAlertDialog(it.title, it.message) {
                removeLastTransaction()
                hideDialogProgress()
                if (it.isSuccessTransaction) {
                    validateNavigate(it.fragment)

                }
            }
        }

    }

    private fun initSubscriptionsNetworkSubject(){

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
    }

    private fun validateNavigate(baseFragment: BaseFragment?) {
        if (baseFragment == null)
            navigateToMenu(this)
        else
            navigateToMenuFragment(baseFragment)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        SecurityUtil.updateSessionTimeOut(getPreference(R_string(R.string.shared_key_session_time_out)))

    }

    override fun onStart() {
        super.onStart()
        isForeground = false
        SecurityUtil.updateSessionTimeOut(getPreference(R_string(R.string.shared_key_session_time_out)))
        validateAlertDialogPending()
    }

    private fun validateAlertDialogPending() {

        val data = getPreference<String>(getString(R.string.shared_key_dialog_pending))

       if (data != ""){
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
        savePreference(R_string(R.string.shared_key_session_time_out), "")
    }

    override fun onPause() {
        super.onPause()

        isForeground = true

        validateSessionTimeOut()
        sessionTimerOut.cancel()
        timerOutDialog.cancel()
        SecurityUtil.updateSessionTimeOut(getPreference(R_string(R.string.shared_key_session_time_out)))
    }

    protected fun setupBaseView(view: View? = null) {
        setSupportActionBar(mainToolbar as Toolbar?)
        progressDialog = ProgressDialog(this)
        mainToolbar?.backButton?.setOnClickListener {
            view?.clearFocus()
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

    fun validateBluetoothConnection(success: () -> Unit? = {}, error: () -> Unit? = {}) {
        if (DeviceUtil.isSmartPhone()) {
            PrintBluetoothManager.isBluetoothAdapter({
                PrintBluetoothManager.isBluetoothUUIDSaved({
                    PrintBluetoothManager.findAndConnectDevices(success, {
                        error()
                        showOkAlertDialog(
                            R_string(R.string.title_dialog_error),
                            R_string(R.string.message_error_connection_bluetooth)
                        )
                    })
                }, {
                    showDevicePairedList(success, error)
                })
            }, {
                error()
                showOkAlertDialog(
                    R_string(R.string.title_dialog_error),
                    R_string(R.string.message_error_bluetooth_disabled)
                ) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
            })
        } else {
            success()
        }
    }

    fun validateInfoDevicePrint(success: () -> Unit?) {
        PrintBluetoothManager.isBluetoothAdapter({
            showDevicePairedList(success, {})
        }, {
            showOkAlertDialog(
                R_string(R.string.title_dialog_error),
                R_string(R.string.message_error_bluetooth_disabled)
            ) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        })
    }

    fun validateSocketConnection(success: () -> Unit?, error: () -> Unit?) {
        if (DeviceUtil.isSmartPhone()) {
            PrintBluetoothManager.isBluetoothAdapter({
                PrintBluetoothManager.isBluetoothUUIDSaved({
                    PrintBluetoothManager.findAndConnectDevices({
                        success()
                    }, {
                        showOkAlertDialog(
                            R_string(R.string.title_dialog_error),
                            R_string(R.string.message_error_connection_bluetooth)
                        )
                    })
                }, {
                    showDevicePairedList(success, error)
                })
            }, {
                error()
                showOkAlertDialog(
                    R_string(R.string.title_dialog_error),
                    R_string(R.string.message_error_bluetooth_disabled)
                ) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
            })
        } else {
            success()
        }
    }

    private fun showAlertDialogSessionExpired() {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            alertDialogo = AlertDialog.Builder(this@BaseActivity).apply {
                setTitle(getString(R.string.text_title_session_not_found))
                setMessage(getString(R.string.text_message_session_not_found))
                setCancelable(false)
                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                    goToLogin()
                }
            }.show()
        }
    }

    private fun goToLogin(){
        val intent = Intent(this@BaseActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun showAlertDialogMaxNumberRetry() {

        hideDialogProgress()

        if (alertDialogo == null || alertDialogo?.isShowing == false) {
            alertDialogo = AlertDialog.Builder(this@BaseActivity).apply {
                setTitle(getString(R.string.title_failed_transaction))
                setMessage(getString(R.string.message_error_sale))
                setCancelable(false)
                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, _ ->
                    val intent = Intent(this@BaseActivity, HomeMenuActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
            }.show()
        }
    }

    fun showOkAlertDialog(title: String, message: String) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@BaseActivity).apply {
                    setTitle(title)
                    setMessage(message)
                    setCancelable(false)
                    setPositiveButton(R_string(R.string.text_btn_accept)) { dialog, which ->
                        hideDialogProgress()
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }

    fun showOkAlertDialogCancel(
        title: String,
        message: String,
        closure: () -> Unit?,
        closureCancel: () -> Unit?
    ) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@BaseActivity).apply {
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
                        this@BaseActivity,
                        R.color.secondaryText
                    )
                )

            }
        }
    }

    fun showOkAlertDialog(title: String, message: String, closure: () -> Unit?) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@BaseActivity).apply {
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

    fun showOkAlertDialogOverload(title: String, message: String, closure: () -> Unit?) {

        if (alertDialogo != null) {

            alertDialogo?.dismiss()
        }

        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@BaseActivity).apply {
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

    fun showAlertDialog(
        title: String,
        message: String,
        positiveClosure: () -> Unit?,
        negativeClosure: () -> Unit?
    ) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                alertDialogo = AlertDialog.Builder(this@BaseActivity).apply {
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
                alertDialogo?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                    ContextCompat.getColor(
                        this@BaseActivity,
                        R.color.secondaryText
                    )
                )
            }
        }
    }

    fun showAlertDialogList(
        title: String,
        list: Array<String?>,
        closure: (position: Int) -> Unit?
    ) {
        if (alertDialogo == null || alertDialogo?.isShowing != true) {
            runOnUiThread {
                val alertDialogo = AlertDialog.Builder(this@BaseActivity)
                alertDialogo.setTitle(title)
                alertDialogo.setCancelable(false)
                alertDialogo.setItems(list, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        closure(which)
                    }
                })

                alertDialogo.setPositiveButton(R_string(R.string.text_btn_update)) { dialog, which ->
                    dialog.dismiss()
                    validateBluetoothConnection()
                }

                alertDialogo.setNegativeButton(R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                }

                alertDialogo.create()
                alertDialogo.show()
            }
        }
    }

    private fun showDevicePairedList(success: () -> Unit?, error: () -> Unit?) {
        val deviceList = PrintBluetoothManager.getDevicePaired()
        val arrayName: Array<String?> = arrayOfNulls(deviceList.size)

        deviceList.forEachIndexed { index, bluetoothDevice ->
            arrayName.set(index, bluetoothDevice.name)
        }

        if (deviceList.isNotEmpty()) {
            showAlertDialogList(R_string(R.string.title_select_device), arrayName) {
                PrintBluetoothManager.openConnection(deviceList[it], success, {
                    showOkAlertDialog(
                        R_string(R.string.title_dialog_error),
                        R_string(R.string.message_error_connection_bluetooth)
                    )
                })
            }
        } else {
            error()
            showOkAlertDialog(
                R_string(R.string.title_dialog_error),
                R_string(R.string.message_error_paired_device)
            )
        }
    }

    fun showDialogProgress(message: String) {


        if (!progressDialog.isShowing) {

            timerOutDialog.cancel()
            timerOutDialog.start()

            progressDialog = ProgressDialog(this)
            progressDialog.setMessage(message)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

    }

    fun hideDialogProgress() {

        timerOutDialog.cancel()
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun navigateToMenu(activity: Activity) {
        unSubscriptions()
        activity.finish()
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

    private fun saveTerminalCodeInPreferences() {
        if (BuildConfig.IS_FAKE_TERMINAL_CODE) {
            savePreference(
                R_string(R.string.shared_key_terminal_code),
                R_string(R.string.terminal_code)
            )
        } else {
            savePreference(
                R_string(R.string.shared_key_terminal_code),
                DeviceUtil.getDeviceIMEI() ?: DeviceUtil.getSerialNumber() ?: ""
            )
        }
    }

    private fun showAuthToken(closure: (data: String) -> Unit?) {

        if (alertDialogo == null || alertDialogo?.isShowing != true) {

            runOnUiThread {


                val inflater = this.layoutInflater

                val dialogView = inflater.inflate(R.layout.dialog_token_validator, null)
                val txtToken = dialogView.findViewById<EditText>(R.id.txtToken)

                val dialog = AlertDialog.Builder(this@BaseActivity).apply {
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
                        this@BaseActivity,
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

    fun requestPrinterPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val permissionStorage =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val requestPermissions = ArrayList<String>()

        if (permissionStorage == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (requestPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                requestPermissions.toTypedArray(),
                REQUEST_PTINER_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        var permisosDenegos = false;

        permissions.forEach lit@{

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, it)) {
                //denied
                permisosDenegos = true;
                return@lit
            }

        }

        if (permisosDenegos) {

            showOkAlertDialog(
                R_string(R.string.title_permission_read_phone_state),
                R_string(R.string.message_permission_not_granted_read_phone_state),
                {})

        }

        saveInfoPermission()

    }

    private fun saveInfoPermission(){
        saveTerminalCodeInPreferences()
        discoverEpsonPrinters()
    }

    private fun discoverEpsonPrinters() {
        if (DeviceUtil.isSalePoint()) {
            EpsonPrinter.discoverPrinters(this) {
                savePreference(
                    R_string(R.string.shared_key_jsa_printer),
                    it?.target.toString()
                )
            }
        }
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

    override fun onStop() {
        super.onStop()

        timerOutDialog.cancel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("BASE ACTIVITY","onBackPressed")
        hideKeyboard()
        unSubscriptions()
        clearMemory()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BASE ACTIVITY","onDestroy")
        unSubscriptions()
        clearMemory()
    }


    fun clearMemory(){
        cacheDir.deleteRecursively()
        Runtime.getRuntime().gc()
    }

    fun unSubscriptions(){

       subscriptions.forEach {
           it.dispose()
       }
        subscriptions.clear()

    }

    fun solicitarPermisos( context:Activity,  permissions:Array<String>) {

        //si la API 23 a mas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Habilitar permisos para la version de API 23 a mas

            val permiso1 = ContextCompat.checkSelfPermission(context, permissions[0]);
            val permiso2 = ContextCompat.checkSelfPermission(context, permissions[1]);
            val permiso3 = ContextCompat.checkSelfPermission(context, permissions[2]);


            //Verificamos si el permiso no existe
            if (permiso1 != PackageManager.PERMISSION_GRANTED ||
                permiso2 != PackageManager.PERMISSION_GRANTED ||
                permiso3 != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(context, permissions, CODE_PERMISOS);

            }

        }else{
            if(!DeviceUtil.isSalePoint())
                saveInfoPermission()
        }
    }

    fun validarPermisos( context:Activity,  permissions:Array<String>):Boolean {

        var permisos = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val permiso1= ContextCompat.checkSelfPermission(context, permissions[0]);
            val permiso2 = ContextCompat.checkSelfPermission(context, permissions[1]);
            val permiso3 = ContextCompat.checkSelfPermission(context, permissions[2]);

            if (permiso1 != PackageManager.PERMISSION_GRANTED ||
                permiso2 != PackageManager.PERMISSION_GRANTED ||
                permiso3 != PackageManager.PERMISSION_GRANTED ) {

                permisos = false;

            }else{
                if(!DeviceUtil.isSalePoint())
                    saveInfoPermission()
            }

        }else{
            if(!DeviceUtil.isSalePoint())
                saveInfoPermission()
        }

        return permisos;
    }

    val timerOutDialog =
        object : CountDownTimer((StorageUtil.getDefaultTimeout() * 2) * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("RUN", "timerOutDialog ")
            }

            override fun onFinish() {
                hideDialogProgress()
                showOkAlertDialog("",getString(R.string.msm_time_out)){
                    removeLastTransaction()
                    finish()
                }
            }
        }

    fun setupClientSitp(){
        client = PagaTodoApplication.getClientSitpFacadeInstance()

    }

    fun showError() {
        var lastError = client?.getLastError()
        showOkAlertDialog(
            "Error $lastError",
            client!!.getDescriptionLastError()?.descripcion.toString()
        )
    }
}