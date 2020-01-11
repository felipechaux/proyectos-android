package co.com.pagatodo.core.views.lottery

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.adapters.LotteryAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.MenuBaseActivity
import co.com.pagatodo.core.views.lottery.fragment.LotteryListAvailableFragment

class FractionsAvailablesActivity : MenuBaseActivity(), LotteryAdapter.OnListenerAdapter, LotteryListAvailableFragment.OnFragmentInteractionListener  {

    private lateinit var virtualViewModel: LotterySaleViewModel
    private lateinit var lotteries: List<VirtualLotteryModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fractions_availables)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.physical_lottery_title))
        setupViewModelFractions()
        initSubscriptions()
    }

    private fun setupViewModelFractions() {
        virtualViewModel = ViewModelProviders.of(this).get(LotterySaleViewModel::class.java)
        virtualViewModel.loadLotteries(RUtil.R_string(R.string.name_code_product_physical_lottery))
    }

    private fun showLotteryListFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutAvailable, LotteryListAvailableFragment.newInstance(this, this))
        fragmentTransaction.commit()
    }

    @SuppressLint("CheckResult")
    private fun initSubscriptions() {
        virtualViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is LotterySaleViewModel.ViewEvent.ResponseLotteries -> {
                    lotteries = it.virtuaLotteries
                    showLotteryListFragment()
                }
            }
        })
    }

    override fun onClickListener(model: VirtualLotteryModel) {
        navigateToOptionLottery(FractionsAvailablesListActivity::class.java, model)
    }

    private fun <T: Activity>navigateToOptionLottery(classType: Class<T>, model: VirtualLotteryModel) {
        val intent = Intent(this, classType)
        intent.putExtra(getString(R.string.bundle_model_lottery), model)
        startActivity(intent)
    }

    override fun onShowProgressDialog(message: String) {
        showDialogProgress(message)
    }

    override fun onCloseProgressDialog() {
        hideDialogProgress()
    }

    override fun onShowOkAlertDialog(title: String, message: String, closure: () -> Unit?) {
        showOkAlertDialog(title, message, closure)
    }

    override fun onShowOkAlertDialog(title: String, message: String) {
        showOkAlertDialog(title, message)
    }

    override fun onBackFragment() {
        if(DeviceUtil.isSalePoint()){
            finish()
        } else {
            showLotteryListFragment()
        }
    }

    override fun onStart() {
        super.onStart()
        hideKeyboard()
    }

    override fun showMessageDialog(title: String, message: String, backMenu: Boolean) {
        showOkAlertDialog(title,message){
            if (backMenu){
                finish()
            }
        }
    }
}
