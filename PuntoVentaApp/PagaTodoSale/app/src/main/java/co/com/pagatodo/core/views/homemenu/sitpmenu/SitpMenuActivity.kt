package co.com.pagatodo.core.views.homemenu.sitpmenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseQueryInitDateDTO
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.sitp.RechargeSitpViewModel
import co.com.pagatodo.core.views.sitp.SitpRechargeActivity
import kotlinx.android.synthetic.main.activity_sitp_menu.*
import kotlin.concurrent.thread

class SitpMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel
    private lateinit var rechargeSitpViewModel: RechargeSitpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitp_menu)
        setupModel()
        setupView()
        initServiceDongle()
    }

    private fun initServiceDongle() {
        setupClientSitp()
        client?.startService()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        rechargeSitpViewModel = ViewModelProviders.of(this).get(RechargeSitpViewModel::class.java)

        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })

        rechargeSitpViewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is RechargeSitpViewModel.ViewEvent.Errors -> errorLoadParameters(it.errorMessage)
                is RechargeSitpViewModel.ViewEvent.GetQueryInitDataSuccess -> getInitData(it.responseQueryInitDateDTO)
            }
        })

        viewModel.loadSitpMenu()

    }

    private fun getInitData(it: ResponseQueryInitDateDTO) {
        hideDialogProgress()
        initializeDevice(it)
    }

    private fun initializeDevice(it: ResponseQueryInitDateDTO) {

        showDialogProgress(getString(R.string.message_dialog_init_device))
        thread {
            client?.stopService()
            client?.createDirectory(it)
            initDeviceOk = client?.init()!!
            hideDialogProgress()
            rechargeProcess()
        }

    }

    private fun rechargeProcess() {
        if (initDeviceOk) {
            navigateToOption(SitpRechargeActivity::class.java)
        } else {
            var lastError = client?.getLastError()
            if (lastError == 51) {
                initDeviceOk = true
                navigateToOption(SitpRechargeActivity::class.java)
            } else {
                showError()
            }
        }
    }

    private fun errorLoadParameters(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.sitp_recharge_title))
        rvMenus.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, StorageUtil.numberOfColumnsForEnvironment())
        }
    }

    private fun updateMenuList(data: List<MainMenuModel>) {
        val menuAdapter = MenuAdapter(data)
        menuAdapter.setListener(this)
        rvMenus.adapter = menuAdapter
        initDeviceOk = false
    }

    override fun onClickMenu(type: MainMenuEnum?) {
        when (type) {
            MainMenuEnum.P_SITP_RECARGAR -> {
                if (!initDeviceOk) {
                    showDialogProgress(getString(R.string.message_dialog_load_data))
                    rechargeSitpViewModel.getQueryInitData()
                } else {
                    navigateToOption(SitpRechargeActivity::class.java)
                }
            }
        }
    }

    private fun <T : Activity> navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }

}
