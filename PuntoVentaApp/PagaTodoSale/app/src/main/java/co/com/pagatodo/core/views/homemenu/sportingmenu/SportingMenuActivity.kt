package co.com.pagatodo.core.views.homemenu.sportingmenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.util.lastTransactionStatusObject
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.sporting.league14.League14Activity
import co.com.pagatodo.core.views.sporting.megagoals.MegaGoalsActivity
import kotlinx.android.synthetic.main.activity_query_menu.*

class SportingMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sporting_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadSportingMenu()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.sporting_menu_title))
        rvMenus.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, numberOfColumnsForEnvironment())
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
                MainMenuEnum.P_LD_MEGAGOL -> navigateToOption(MegaGoalsActivity::class.java)
                MainMenuEnum.P_LD_LIGA14 -> navigateToOption(League14Activity::class.java)
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
