package co.com.pagatodo.core.views.homemenu.chancemenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.util.lastTransactionStatusObject
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.chance.ChanceActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.maxichance.MaxiChanceActivity
import co.com.pagatodo.core.views.paymillionaire.PayMillionaireActivity
import co.com.pagatodo.core.views.superchance.SuperChanceActivity
import kotlinx.android.synthetic.main.activity_home_menu.*

class ChanceMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chance_menu)
        setupModel()
        setupView()
        hideKeyboard()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadChanceMenu()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.chance_menu_title))
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
                MainMenuEnum.P_JC_PAGA_MILLONARIO -> navigateToOption(PayMillionaireActivity::class.java)
                MainMenuEnum.P_JC_MAXICHANCE -> navigateToOption(MaxiChanceActivity::class.java)
                MainMenuEnum.P_JC_SUPER_CHANCE -> navigateToOption(SuperChanceActivity::class.java)
                MainMenuEnum.P_JC_CHANCE_TRADICIONAL -> navigateToOption(ChanceActivity::class.java)
                MainMenuEnum.CHANCE_REPETIR_APUESTA -> {
                    val intent = Intent(this, ChanceActivity::class.java)
                    intent.putExtra(R_string(R.string.shared_key_repeat_bet), true)
                    startActivity(intent)
                }
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

    private fun <T : Activity> navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }
}
