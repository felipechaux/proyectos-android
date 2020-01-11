package co.com.pagatodo.core.views.homemenu.colpensionesbepsmenu

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
import co.com.pagatodo.core.views.colpensionesbeps.ColpensionesBepsActivity
import co.com.pagatodo.core.views.colpensionesbeps.ColpensionesBepsReprintActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import kotlinx.android.synthetic.main.activity_home_menu.*

class ColpensionesBepsMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {
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
        viewModel.loadColpensionesBepsMenu()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.colpensiones_beps_title))
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
            MainMenuEnum.P_BC_RECAUDO -> navigateToOption(ColpensionesBepsActivity::class.java)
            MainMenuEnum.P_BC_REIMPRESION -> navigateToOption(ColpensionesBepsReprintActivity::class.java)
        }
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }
}