package co.com.pagatodo.core.views.homemenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.util.lastTransactionStatusObject
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.base.removeLastTransaction
import co.com.pagatodo.core.views.homemenu.balotomenu.BalotoMenuActivity
import co.com.pagatodo.core.views.homemenu.chancemenu.ChanceMenuActivity
import co.com.pagatodo.core.views.recharge.RechargeActivity
import co.com.pagatodo.core.views.homemenu.sportingmenu.SportingMenuActivity
import co.com.pagatodo.core.views.lottery.LotteryActivity
import co.com.pagatodo.core.views.raffle.RaffleActivity
import co.com.pagatodo.core.views.homemenu.betplaymenu.ItemsBetplayMenuActivity
import co.com.pagatodo.core.views.homemenu.giromenu.GiroMenuActivity
import co.com.pagatodo.core.views.homemenu.lotterymenu.LotteryMenuActivity
import co.com.pagatodo.core.views.soat.SoatActivity
import co.com.pagatodo.core.views.superastro.SuperAstroActivity
import co.com.pagatodo.core.views.virtualwallet.VirtualWalletActivity
import kotlinx.android.synthetic.main.activity_home_menu.*

open class HomeMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener  {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of( this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this , Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadMainMenu()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(getString(R.string.main_menu_title_products))
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
            when(type) {
                MainMenuEnum.P_JUEGOS_CHANCE -> navigateToOption(ChanceMenuActivity::class.java)
                MainMenuEnum.P_RECARGAS -> navigateToOption(RechargeActivity::class.java)
                MainMenuEnum.P_BETPLAY -> navigateToOption(ItemsBetplayMenuActivity::class.java)
                MainMenuEnum.P_SUPER_ASTRO -> navigateToOption(SuperAstroActivity::class.java)
                MainMenuEnum.P_BALOTO -> navigateToOption(BalotoMenuActivity::class.java)
                MainMenuEnum.P_RIFAS -> navigateToOption(RaffleActivity::class.java)
                MainMenuEnum.P_LAS_DEPORTIVAS -> navigateToOption(SportingMenuActivity::class.java)
                MainMenuEnum.P_LOTERIA_VIRTUAL -> navigateToOptionLottery(LotteryActivity::class.java, true)
                MainMenuEnum.P_LOTERIA_FISICA -> navigateToOption(LotteryMenuActivity::class.java)
                MainMenuEnum.P_GIROS -> {

                    if(DeviceUtil.isSalePoint()){
                        navigateToOption(GiroMenuActivity::class.java)
                    }else{
                        showOkAlertDialog("",getString(R.string.requires_white_stationery)){
                            navigateToOption(GiroMenuActivity::class.java)
                        }
                    }


                }
                MainMenuEnum.P_SOAT_VIRTUAL -> navigateToOption(SoatActivity::class.java)
                MainMenuEnum.P_RECARGA_APP_PGT -> navigateToOption(VirtualWalletActivity::class.java)

            }
        }
        else {
            lastTransactionStatusObject = StorageUtil.getLastTransactionObject(lastTransaction)

            if (lastTransactionStatusObject == null) {
                showOkAlertDialog(getString(R.string.title_dialog_current_stub), getString(R.string.message_error_stub_inconsistency)){
                    removeLastTransaction()
                    finish()
                }
            }
            else {
                executePendingTransaction(lastTransaction)
            }
        }
    }

    fun <T: Activity>navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }

    private fun <T: Activity>navigateToOptionLottery(classType: Class<T>, isVirtual: Boolean) {
        val intent = Intent(this, classType)
        intent.putExtra(getString(R.string.bundle_is_virtual), isVirtual)
        startActivity(intent)
    }
}