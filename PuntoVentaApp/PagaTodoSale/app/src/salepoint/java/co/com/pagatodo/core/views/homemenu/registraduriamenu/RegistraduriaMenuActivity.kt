package co.com.pagatodo.core.views.homemenu.registraduriamenu

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
import co.com.pagatodo.core.views.registraduria.RegistraduriaCollectionActivity
import co.com.pagatodo.core.views.registraduria.RegistraduriaReprintCollectionActivity
import kotlinx.android.synthetic.main.activity_home_menu.*

class RegistraduriaMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {
    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registraduria_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of( this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this , Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadRegistraduriaMenu()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.home_menu_title_registraduria_collection))
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
            MainMenuEnum.P_REGISTRADURIA_RECAUDO -> navigateToOption(RegistraduriaCollectionActivity::class.java)
            MainMenuEnum.P_REGISTRADURIA_REIMPRESION -> navigateToOption(RegistraduriaReprintCollectionActivity::class.java)
        }
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }
}