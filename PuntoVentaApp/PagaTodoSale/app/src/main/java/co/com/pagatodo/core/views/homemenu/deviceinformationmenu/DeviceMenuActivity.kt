package co.com.pagatodo.core.views.homemenu.deviceinformationmenu

import android.app.Activity
import android.content.Intent
import co.com.pagatodo.core.R
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.information.InformationDeviceActivity
import kotlinx.android.synthetic.main.activity_home_menu.*

open class DeviceMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of( this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this , Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadDeviceInfo()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.device_menu_title))
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

    override fun onClickMenu(type: MainMenuEnum?) {}

    fun <T: Activity>navigateToOptionWithExtra(classType: Class<T>, bundle: Bundle? = null) {
        val intent = Intent(this, classType)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}