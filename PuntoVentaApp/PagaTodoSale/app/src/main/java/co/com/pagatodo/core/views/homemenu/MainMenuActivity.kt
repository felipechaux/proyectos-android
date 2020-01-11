package co.com.pagatodo.core.views.homemenu

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import co.com.pagatodo.core.R
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.util.RUtil
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.util.lastTransactionStatusObject
import co.com.pagatodo.core.views.adapters.MainMenuAdapter
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.base.removeLastTransaction
import co.com.pagatodo.core.views.login.LoginActivity
import co.com.pagatodo.core.views.homemenu.advisormenu.AdvisorMenuActivity
import co.com.pagatodo.core.views.homemenu.deviceinformationmenu.ProductDeviceMenuActivity
import co.com.pagatodo.core.views.homemenu.querymenu.QueryMenuActivity
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : MenuBaseActivity(), MainMenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setupModel()
        setupView()
        initSubscriptions()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.informationLiveData.observe(this, Observer<String> { msg ->
            showOkAlertDialog(R_string(R.string.text_title_dialog_message), msg)
        })
        viewModel.loadInformationToShowAfterStubs()
        viewModel.loadMenu()
    }

    private fun initSubscriptions() {
        viewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is MenuViewModel.ViewEvent.ResponseSuccess -> {
                    hideDialogProgress()
                    closeActivity()
                }
                is MenuViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    showOkAlertDialog("", it.errorMessage.toString())
                }
            }
        })
    }

    private fun setupView() {
        lblNameUser.text = getString(
            R.string.text_label_parameter_name_user,
            getPreference(R_string(R.string.shared_key_seller_name))
        )
        rvMenus.isNestedScrollingEnabled = false
        rvMenus.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, numberOfColumnsForEnvironment())
        }
    }

    private fun updateMenuList(data: List<MainMenuModel>) {
        val mainMenuAdapter = MainMenuAdapter(data)
        mainMenuAdapter.setListener(this)
        rvMenus.adapter = mainMenuAdapter
    }

    override fun onClickMenu(type: MainMenuEnum?) {
        val lastTransaction = StorageUtil.getLastTransactionStatus()
        if (lastTransaction == null || type == MainMenuEnum.M_CERRAR_SESSION) {
            when (type) {
                MainMenuEnum.M_PRODUCTOS_SERVICIOS -> navigateToOption(ProductsHomeMenuActivity::class.java)
                MainMenuEnum.M_CONSULTAS_Y_PREMIOS -> navigateToOption(QueryMenuActivity::class.java)
                MainMenuEnum.M_INFO_DISPOSITIVO -> navigateToOption(ProductDeviceMenuActivity::class.java)
                MainMenuEnum.M_CERRAR_SESSION -> onBackPressed()
                MainMenuEnum.M_ASESOR -> navigateToOption(AdvisorMenuActivity::class.java)
            }
        } else {
            lastTransactionStatusObject = StorageUtil.getLastTransactionObject(lastTransaction)

            if (lastTransactionStatusObject == null) {
                showOkAlertDialog(
                    getString(R.string.title_dialog_current_stub),
                    getString(R.string.message_error_stub_inconsistency)
                ){
                    removeLastTransaction()
                    finish()
                }
            } else {
                executePendingTransaction(lastTransaction)
            }
        }
    }

    private fun <T : Activity> navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }

    override fun onBackPressed() {
        showAlertDialog(
            RUtil.R_string(R.string.text_title_close_activity),
            RUtil.R_string(R.string.text_message_close_activity),
            { logout() },
            {}
        )
    }

    private fun logout() {
        showDialogProgress(getString(R.string.message_dialog_request))
        viewModel.logout()
    }

    private fun closeActivity() {
        clearSharedPreferences()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun clearSharedPreferences() {
        SharedPreferencesUtil.savePreference(
            R_string(R.string.shared_key_session_time_out_date),
            ""
        )
        SharedPreferencesUtil.savePreference(R_string(R.string.shared_key_session_time_out), "")
    }
}
