package co.com.pagatodo.core.views.homemenu.colsubsidiomenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.StorageUtil.Companion.numberOfColumnsForEnvironment
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.colsubsidio.ColsubsidioCollectionActivity
import co.com.pagatodo.core.views.colsubsidio.ColsubsidioRetirementActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import kotlinx.android.synthetic.main.activity_query_menu.*

class ColsubsidioMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colsubsidio_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadColsubsidioMenu()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.home_menu_title_colsubsidio))
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
           MainMenuEnum.P_COLSUBSIDIO_RECAUDO -> navigateToOption(ColsubsidioCollectionActivity::class.java)
            MainMenuEnum.P_COLSUBSIDIO_RETIRO -> navigateToOption(ColsubsidioRetirementActivity::class.java)
        }
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }
}
