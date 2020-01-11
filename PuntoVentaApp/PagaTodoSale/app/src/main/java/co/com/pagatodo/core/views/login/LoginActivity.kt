package co.com.pagatodo.core.views.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.BuildConfig
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.apn.ApnSettingActivity
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuActivity
import co.com.pagatodo.core.views.information.InformationDeviceActivity
import co.com.pagatodo.core.views.stub.StubActivity
import co.com.pagatodo.core.views.stub.StubViewModel
import kotlinx.android.synthetic.main.activity_login.*


class ActionCallBack : ActionMode.Callback {
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        // not implemented
    }
}

class LoginActivity : BaseActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var stubViewModel: StubViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupViewModel()
        setupView()
    }

    private fun setupView() {
        title = R_string(R.string.login_title)
        tvVersion.text = getString(R.string.text_label_parameter_version, "${BuildConfig.BUILD_VARIAN}${BuildConfig.VERSION_NAME}" )

        tvSellerCode.setOnClickListener {
            tvSellerCode.setSelection(tvSellerCode.text.toString().length)
        }

        tvPassword.setOnClickListener {
            tvSellerCode.setSelection(tvSellerCode.text.toString().length)
        }

        tvSellerCode.customSelectionActionModeCallback = ActionCallBack()
        tvPassword.customSelectionActionModeCallback = ActionCallBack()

        btnInfo.setOnClickListener {
            if (validarPermisos(this,PERMISSIONS)) {
                startDeviceInformation()
            } else {
               solicitarPermisos(this,PERMISSIONS)
            }

        }
        btnLogin.setOnClickListener {
            if (validarPermisos(this,PERMISSIONS))
                login()
            else{
                solicitarPermisos(this,PERMISSIONS)
            }
        }

        btnConfig.setOnClickListener {
            startApnSetting()
        }

        solicitarPermisos(this,PERMISSIONS)
    }

    private fun setupViewModel() {
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        stubViewModel = ViewModelProviders.of(this).get(StubViewModel::class.java)

        authViewModel.singleLiveEvent.observe(this, Observer {

            when (it) {
                is AuthViewModel.ViewEvent.AuthSuccessWithSellerCode -> validateAuthSeller(it.sellerCode)
                is AuthViewModel.ViewEvent.AuthError -> {
                    hideDialogProgress()
                    showOkAlertDialog("", it.errorMessage)
                }
                is AuthViewModel.ViewEvent.AuthTokenSuccess -> successAuthSeller()
                is AuthViewModel.ViewEvent.AuthFingerPrintSellerSuccess -> successAuthSeller()
                is AuthViewModel.ViewEvent.AuthTokenError -> failAuthSeller(it.errorMessage)
                is AuthViewModel.ViewEvent.AuthFingerPrintSellerError -> failAuthSeller(it.errorMessage)
            }
        })
    }

    private fun validateAuthSeller(sellerCode: String) {

        hideDialogProgress()

        validateAuthSeller(getString(R.string.shared_key_auth_mode)) { authType, data,dataImg ->

            when (authType) {
                AuthMode.NONE -> navigateToview(sellerCode)
                AuthMode.TOKEN -> {
                    showDialogProgress(getString(R.string.message_dialog_login_auth))
                    authViewModel.authToken(
                        SharedPreferencesUtil.getPreference(getString(R.string.shared_key_user_id)),
                        data
                    )
                }
                AuthMode.BIOMETRY -> {

                    if (data.isNotEmpty()) {
                        showDialogProgress(getString(R.string.message_dialog_login_auth))
                        authViewModel.authFingerPrintSeller(
                            SharedPreferencesUtil.getPreference(getString(R.string.shared_key_user_id_type)),
                            SharedPreferencesUtil.getPreference(getString(R.string.shared_key_user_id)),
                            data
                        )
                    } else {
                        failAuthSeller(getString(R.string.error_biometry))
                    }


                }

            }


        }


    }

    private fun failAuthSeller(message: String) {

        hideDialogProgress()
        showOkAlertDialog("", message)

    }

    private fun successAuthSeller() {

        navigateToview(SharedPreferencesUtil.getPreference(getString(R.string.shared_key_seller_code)))

    }

    private fun startDeviceInformation() {
        val intent = Intent(this, InformationDeviceActivity::class.java)
        intent.putExtra(R_string(R.string.bundle_in_session), false)
        startActivity(intent)
    }

    private fun startApnSetting() {
        val intent = Intent(this, ApnSettingActivity::class.java)
        intent.putExtra(R_string(R.string.bundle_in_session), false)
        startActivity(intent)
    }

    private fun navigateToview(sellerCode: String) {


        StorageUtil.getLastTransactionStatus()?.let { transaction ->

            if (sellerCode != transaction.sellerCode) {
                showOkAlertDialog(
                    R_string(R.string.title_pending_transaction),
                    R_string(R.string.message_error_pending_transaction_other_user)
                ) {
                    StorageUtil.removeLastTransaction()
                    navigationToView(StubActivity::class.java)
                }
            } else {

                val isSavedLastStub = stubViewModel.saveCurrentStubSeries(
                    transaction.serie1 ?: "",
                    transaction.serie2 ?: "",
                    false
                )
                if (isSavedLastStub) {
                    lastTransactionStatusObject = StorageUtil.getLastTransactionObject(transaction)
                    navigationToView(MainMenuActivity::class.java)
                } else {
                    showOkAlertDialog(
                        R_string(R.string.title_dialog_error),
                        R_string(R.string.message_error_save_stub)
                    )
                }
            }
        } ?: run {
            navigationToView(StubActivity::class.java)
        }
    }

    private fun <T : Activity> navigationToView(classType: Class<T>) {
        startActivity(Intent(this, classType))
        finish()
    }

    private fun login() {
        if ((tvSellerCode.text ?: "").isNotEmpty()) {
            if ((tvPassword.text ?: "").isNotEmpty()) {
                val sellerCode = tvSellerCode.text.toString()
                val password = tvPassword.text.toString()
                showDialogProgress(R_string(R.string.message_dialog_login))
                authViewModel.auth(sellerCode, password)
            } else {
                showOkAlertDialog("", R_string(R.string.message_error_password_empty))
            }
        } else {
            showOkAlertDialog("", R_string(R.string.message_error_user_empty))
        }
    }
}
