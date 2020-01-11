package co.com.pagatodo.core.views.homemenu.elderlymenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseElderlyAuthorizedTerminalDTO
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.adapters.MenuAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.elderly.ElderlyEnrollmentActivity
import co.com.pagatodo.core.views.elderly.ElderlyPayActivity
import co.com.pagatodo.core.views.elderly.ElderlyRePrintActivity
import co.com.pagatodo.core.views.elderly.ElderlyViewModel
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import kotlinx.android.synthetic.main.activity_elderly_menu.*

class ElderlyMenuActivity : MenuBaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel

    lateinit var elderlyViewModel: ElderlyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elderly_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })

        elderlyViewModel = ViewModelProviders.of(this).get(ElderlyViewModel::class.java)

        elderlyViewModel.singleLiveEvent.observe(this,Observer{
            when(it){
                is ElderlyViewModel.ViewEvent.Errors -> errorShow(it.errorMessage)
                is ElderlyViewModel.ViewEvent.QueryAuthorizedTerminalSuccess -> authorizedTerminal(it.responseDTO)
                is ElderlyViewModel.ViewEvent.GiroLoginSuccess -> {
                    //not implemented
                }
            }
        })

        elderlyViewModel.elderlyQueryAuthorizedTerminal()

    }

    private fun authorizedTerminal(response: ResponseElderlyAuthorizedTerminalDTO){

        if(response.authorized?:false){

            viewModel.loadElderlyMenu()
            loadSession()

        }else{
            showOkAlertDialog("", getString(R.string.alderly_terminal_no_auth)){
                finish()
            }
        }

    }

    private fun loadSession(){
        elderlyViewModel.girosLogin()
    }

    private fun errorShow(error: String) {

        showOkAlertDialog("", error){
            finish()
        }

    }

    private fun setupView(){
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.elderly_general_title))
        rvMenus.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, StorageUtil.numberOfColumnsForEnvironment())
        }
    }

    private fun updateMenuList(data: List<MainMenuModel>) {
        val menuAdapter = MenuAdapter(data)
        menuAdapter.setListener(this)
        rvMenus.adapter = menuAdapter
    }

    override fun onClickMenu(type: MainMenuEnum?) {
        when(type){
            MainMenuEnum.P_ADULTO_MAYOR_ENROLL -> navigateToOption(ElderlyEnrollmentActivity::class.java)
            MainMenuEnum.P_ADULTO_MAYOR_PAY -> navigateToOption(ElderlyPayActivity::class.java)
            MainMenuEnum.P_ADULTO_REPRINT-> navigateToOption(ElderlyRePrintActivity::class.java)

        }
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>) {
        val intent = Intent(this, classType)
        startActivity(intent)
    }
}
