package co.com.pagatodo.core.views.homemenu.advisormenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.currentbox.CurrentBoxActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.resetpassword.ResetPasswordActivity
import co.com.pagatodo.core.views.wallet.WalletActivity
import kotlinx.android.synthetic.main.activity_query_menu.*

class AdvisorMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advisor_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadAdvisor()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.advisor_menu_title))
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
        when(type){
            MainMenuEnum.AS_CARTERA -> navigateToOption(WalletActivity::class.java)
            MainMenuEnum.AS_CAMBIO_CLAVE -> navigateToOption(ResetPasswordActivity::class.java)
            MainMenuEnum.AS_REPORTE_VENTAS -> navigateToOptionWithExtra(CurrentBoxActivity::class.java, Bundle().apply {
                putBoolean(R_string(R.string.bundle_is_currentbox), false)
            })
            MainMenuEnum.AS_CUADRE_ACTUAL -> navigateToOptionWithExtra(CurrentBoxActivity::class.java, Bundle().apply {
                putBoolean(R_string(R.string.bundle_is_currentbox), true)
            })
        }
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }

    private fun <T: Activity>navigateToOptionWithExtra(classType: Class<T>, bundle: Bundle? = null) {
        val intent = Intent(this, classType)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}