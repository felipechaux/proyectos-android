package co.com.pagatodo.core.views.homemenu.balotomenu

import android.app.Activity
import android.content.Intent
import co.com.pagatodo.core.R
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.util.lastTransactionStatusObject
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.baloto.BalotoActivity
import co.com.pagatodo.core.views.baloto.BalotoCheckTicketActivity
import co.com.pagatodo.core.views.baloto.BalotoWinnersActivity
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import kotlinx.android.synthetic.main.activity_home_menu.*

class BalotoMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel
    private var isBalotoWinners = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baloto_menu)
        if (intent.extras?.get(R_string(R.string.bundle_is_baloto)) != null) {
            isBalotoWinners = intent.extras?.get(R_string(R.string.bundle_is_baloto)) as Boolean
        }
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        if (isBalotoWinners) {
            viewModel.loadBalotoWinnersMenu()
        } else {
            viewModel.loadBalotoMenu()
        }
    }

    private fun setupView() {
        setupBaseView()
        if (isBalotoWinners) {
            updateTitle(RUtil.R_string(R.string.baloto_winners_menu_title))
        } else {
            updateTitle(RUtil.R_string(R.string.baloto_menu_title))
        }
        rvMenus.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, numberOfColumnsForEnvironment())
        }
    }

    private fun updateMenuList(data: List<MainMenuModel>) {
        val mainMenuAdapter = MenuAdapter(data)
        mainMenuAdapter.setListener(this)
        rvMenus.adapter = mainMenuAdapter
    }

    override fun onClickMenu(type: MainMenuEnum?) {
        val lastTransaction = StorageUtil.getLastTransactionStatus()
        if (lastTransaction == null) {

            when (type) {
                MainMenuEnum.P_B_NUEVA_APUESTA -> navigateToOption(BalotoActivity::class.java)
                MainMenuEnum.CP_BALOTO_GANADORES -> navigateToOption(
                    BalotoWinnersActivity::class.java,
                    Bundle().apply {
                        putBoolean(R_string(R.string.bundle_is_baloto), true)
                    })
                MainMenuEnum.CP_BG_GANADORES_BALOTO_REVANCHA -> navigateToOption(
                    BalotoWinnersActivity::class.java,
                    Bundle().apply {
                        putBoolean(R_string(R.string.bundle_is_baloto), false)
                    })
                MainMenuEnum.P_B_PAGO_PREMIO -> navigateToOption(BalotoCheckTicketActivity::class.java)
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

    private fun <T : Activity> navigateToOption(classType: Class<T>, bundle: Bundle? = null) {
        val intent = Intent(this, classType)
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}
