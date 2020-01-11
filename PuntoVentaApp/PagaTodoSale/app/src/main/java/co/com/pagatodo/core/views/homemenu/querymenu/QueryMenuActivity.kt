package co.com.pagatodo.core.views.homemenu.querymenu

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
import co.com.pagatodo.core.views.baloto.AccumulatedBalotoActivity
import co.com.pagatodo.core.views.baloto.BalotoResultActivity
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.homemenu.balotomenu.BalotoMenuActivity
import co.com.pagatodo.core.views.lotteryResult.DrawsAndLotteriesResultActivity
import co.com.pagatodo.core.views.lotteryResult.LotteryResultActivity
import co.com.pagatodo.core.views.paymillionaire.QueryPayMillonaireActivity
import co.com.pagatodo.core.views.querywinners.QueryWinnersActivity
import co.com.pagatodo.core.views.recharge.RechargeHistoryActivity
import kotlinx.android.synthetic.main.activity_query_menu.*

class QueryMenuActivity : BaseActivity(), MenuAdapter.OnActionListener {

    lateinit var viewModel: MenuViewModel
    private var balotoWinners = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_menu)
        setupModel()
        setupView()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        viewModel.menusLiveData.observe(this, Observer<List<MainMenuModel>> {
            updateMenuList(it)
        })
        viewModel.loadConsults()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(R_string(R.string.query_menu_title))
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
            MainMenuEnum.CP_SORTEOS_LOTERIAS_RESULTADOS -> navigateToOption(DrawsAndLotteriesResultActivity::class.java)
            MainMenuEnum.CP_LOTERIAS_RESULTADOS -> navigateToOption(LotteryResultActivity::class.java)
            MainMenuEnum.CP_PAGA_MILLONARIO_ACUMULADO -> navigateToOption(QueryPayMillonaireActivity::class.java)
            MainMenuEnum.CP_BALOTO_RESULTADOS -> navigateToOption(BalotoResultActivity::class.java)
            MainMenuEnum.CP_BALOTO_ACUMULADO -> navigateToOption(AccumulatedBalotoActivity::class.java)
            MainMenuEnum.CP_BALOTO_GANADORES -> navigateToOption(BalotoMenuActivity::class.java, Bundle().apply {
                putBoolean(RUtil.R_string(R.string.bundle_is_baloto), true)
            })
            MainMenuEnum.CP_CONSULTAR_GANADORES -> navigateToOption(QueryWinnersActivity::class.java)
            MainMenuEnum.CP_RECAGAS_CONSULTAS -> navigateToOption(RechargeHistoryActivity::class.java)
        }
    }

    private fun <T: Activity>navigateToOption(classType: Class<T>, bundle: Bundle? = null) {
        val intent = Intent(this, classType)
        if(bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}
