package co.com.pagatodo.core.views.homemenu.lotterymenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.lastTransactionStatusObject
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.lottery.FractionsAvailablesActivity
import co.com.pagatodo.core.views.lottery.LotteryActivity
import co.com.pagatodo.core.views.sitp.SitpRechargeActivity
import kotlinx.android.synthetic.main.activity_sitp_menu.*

class LotteryMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottery_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadLotteryMenu()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.physical_lottery_title))
        rvMenus.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, StorageUtil.numberOfColumnsForEnvironment())
        }
    }

    private fun updateMenuList(data: List<MainMenuModel>) {
        val menuAdapter = MenuAdapter(data)
        menuAdapter.setListener(this)
        rvMenus.adapter = menuAdapter
    }

    override fun onClickMenu(type: MainMenuEnum?) {

        val lastTransaction = StorageUtil.getLastTransactionStatus()
        if (lastTransaction == null) {
            when (type) {
                MainMenuEnum.P_AVAILABLE_LOTTERY -> {

                    if (!DeviceUtil.isSalePoint()) {

                        showOkAlertDialog("", getString(R.string.requires_white_stationery)) {
                            navigateToOption(FractionsAvailablesActivity::class.java)
                        }

                    } else
                        navigateToOption(FractionsAvailablesActivity::class.java)

                }

                MainMenuEnum.P_SALE_LOTTERY -> navigateToOptionLottery(
                    LotteryActivity::class.java,
                    false
                )
            }
        } else {
            if (lastTransactionStatusObject == null) {
                showOkAlertDialog(
                    getString(R.string.title_dialog_current_stub),
                    getString(R.string.message_error_stub_inconsistency)
                )
            } else {
                executePendingTransaction(lastTransaction)
            }
        }
    }

    private fun <T : Activity> navigateToOptionLottery(classType: Class<T>, isVirtual: Boolean) {
        val intent = Intent(this, classType)
        intent.putExtra(getString(R.string.bundle_is_virtual), isVirtual)
        startActivity(intent)
    }

    private fun <T : Activity> navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }
}
