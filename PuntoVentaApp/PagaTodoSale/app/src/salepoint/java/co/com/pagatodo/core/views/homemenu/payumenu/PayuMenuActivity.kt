package co.com.pagatodo.core.views.homemenu.payumenu

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
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.payu.PayuCollectingActivity
import co.com.pagatodo.core.views.payu.PayuReprintActivity
import kotlinx.android.synthetic.main.activity_home_menu.*

class PayuMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {
    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chance_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of( this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this , Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadPayuMenu()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.home_menu_title_pay_u_collecting))
        rvMenus.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, StorageUtil.numberOfColumnsForEnvironment())
        }
    }

    private fun updateMenuList(data: List<MainMenuModel>) {
        val mainMenuAdapter = MenuAdapter(data)
        mainMenuAdapter.setListener(this)
        rvMenus.adapter = mainMenuAdapter
    }

    override fun onClickMenu(type: MainMenuEnum?) {
        when(type) {
            MainMenuEnum.P_PAYU_RECAUDO -> navigateToOption(PayuCollectingActivity::class.java)
            MainMenuEnum.P_PAYU_REIMPRESION -> navigateToOption(PayuReprintActivity::class.java)
        }
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }
}