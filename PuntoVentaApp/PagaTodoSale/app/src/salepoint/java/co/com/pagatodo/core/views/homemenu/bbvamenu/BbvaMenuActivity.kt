package co.com.pagatodo.core.views.homemenu.bbvamenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.adapters.SalePointMenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.bbva.*
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import kotlinx.android.synthetic.salepoint.activity_bbva_menu.*

class BbvaMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewMenuModel: MenuViewModel
    private var mainMenuAdapter: SalePointMenuAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bbva_menu)
        setupModel()
        setupView()
        loadDefaultFragment()
    }

    private fun setupModel() {
        viewMenuModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewMenuModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewMenuModel.loadBbvaMenu()
    }

    private fun updateMenuList(data: List<MainMenuModel>) {

        if (DeviceUtil.isSalePoint()) {

            mainMenuAdapter = SalePointMenuAdapter(data)
            mainMenuAdapter?.setListener(this)
            rvMenu.adapter = mainMenuAdapter
            mainMenuAdapter?.setSelectedItem(0)

        } else {

            val mainMenuAdapter = MenuAdapter(data)
            mainMenuAdapter.setListener(this)
            rvMenu.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, StorageUtil.numberOfColumnsForEnvironment())
            }
            rvMenu.adapter = mainMenuAdapter
        }

    }

    private fun loadDefaultFragment() {

        supportFragmentManager.beginTransaction().replace(R.id.containerMainMenu, BbvaBillPaymentFragment()).commit()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(getString(R.string.home_menu_title_bbva))
    }

    override fun onClickMenu(type: MainMenuEnum?) {

        when (type) {
            MainMenuEnum.P_BBVA_BILL_PAYMENT -> navigateToOption(BbvaBillPaymentFragment())
            MainMenuEnum.P_BBVA_DEPOSITS_BBVA -> navigateToOption(BbvaAccountDepositFragment())
            MainMenuEnum.P_BBVA_PAYMENT_OF_OBLIGATIONS -> navigateToOption(BbvaPaymentObligationsFragment())
            MainMenuEnum.P_BBVA_WITHDRAWAL_AND_BALANCE_INQUIRY -> navigateToOption(BbvaWithdrawalAndBalanceFragment())
            MainMenuEnum.P_BBVA_REPRINT_LAST_TRANSACTION -> navigateToOption(BbvaReprintTransactionFragment())
            MainMenuEnum.P_BBVA_CLOSE_THE_BOX -> navigateToOption(BbvaCloseBoxFragment())
        }

    }

    fun navigateToOption(view: Fragment) {

        if (DeviceUtil.isSalePoint())
            navigateToOptionSalePoint(view)

    }

    private fun navigateToOptionSalePoint(view: Fragment) {

        supportFragmentManager.beginTransaction().replace(R.id.containerMainMenu, view).commit()

    }

}
