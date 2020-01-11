package co.com.pagatodo.core.views.lottery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.LotteryAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.lottery.fragment.CheckNumberFragment
import co.com.pagatodo.core.views.lottery.fragment.LotteryListFragment

class LotteryActivity : BaseActivity(), LotteryAdapter.OnListenerAdapter, LotteryListFragment.OnFragmentInteractionListener {

    private lateinit var virtualViewModel: LotterySaleViewModel
    private lateinit var lotteries: List<VirtualLotteryModel>

    private var isVirtual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottery)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        setupGetExtras()
        if(isVirtual)
            updateTitle(RUtil.R_string(R.string.virtual_lottery_title))
        else
            updateTitle(RUtil.R_string(R.string.physical_lottery_title))
        setupViewModel()
        initSubscriptions()
    }

    private fun setupGetExtras() {
        isVirtual = intent.extras?.get(R_string(R.string.bundle_is_virtual)) as Boolean
    }

    private fun setupViewModel() {
        virtualViewModel = ViewModelProviders.of(this).get(LotterySaleViewModel::class.java)
        if(isVirtual)
            virtualViewModel.loadLotteries(R_string(R.string.name_code_product_virtual_lottery))
        else
            virtualViewModel.loadLotteries(R_string(R.string.name_code_product_physical_lottery))
    }

    private fun showLotteryListFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, LotteryListFragment.newInstance(isVirtual, this, this))
        fragmentTransaction.commit()
    }

    private fun showCheckNumberFragment(lottery: VirtualLotteryModel?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if(DeviceUtil.isSalePoint())
            fragmentTransaction.replace(R.id.secondFrameLayout, CheckNumberFragment.newInstance(lottery, this, isVirtual))
        else
            fragmentTransaction.replace(R.id.frameLayout, CheckNumberFragment.newInstance(lottery, this, isVirtual))
        fragmentTransaction.commit()
    }

    @SuppressLint("CheckResult")
    private fun initSubscriptions() {
        virtualViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is LotterySaleViewModel.ViewEvent.ResponseLotteries -> {
                    lotteries = it.virtuaLotteries
                    showLotteryListFragment()
                    if(DeviceUtil.isSalePoint())
                        showCheckNumberFragment(null)
                }
            }
        })
    }

    override fun onClickListener(model: VirtualLotteryModel) {
        showCheckNumberFragment(model)
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

    override fun showMessageDialog(title: String, message: String, backMenu: Boolean) {
        showOkAlertDialog(title,message){
            if (backMenu){
                finish()
            }
        }
    }
}
