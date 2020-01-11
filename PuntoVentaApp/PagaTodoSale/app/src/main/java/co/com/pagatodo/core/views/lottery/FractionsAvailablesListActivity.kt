package co.com.pagatodo.core.views.lottery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.adapters.FractionsListAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_fractions_availables_list.*
import kotlinx.android.synthetic.main.activity_fractions_availables_list.recyclerItems
import kotlinx.android.synthetic.main.fragment_lottery_list.*
import kotlinx.android.synthetic.main.fragment_lottery_list.btnBack
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class FractionsAvailablesListActivity : BaseActivity() {

    private var model: VirtualLotteryModel? = null
    private lateinit var virtualViewModel: LotterySaleViewModel
    private var numberLottery = "0"
    private lateinit var response: ResponseCheckNumberLotteryModel
    private lateinit var listFractions: ResponseCheckNumberLotteryModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fractions_availables_list)
        setupView()
    }

    private fun setupGetExtras() {
        model = intent.extras?.get(RUtil.R_string(R.string.bundle_model_lottery)) as VirtualLotteryModel
    }

    private fun setupView() {
        btnNext.setOnClickListener { showDialogConfirm() }
        btnBack.setOnClickListener{ goBack() }
        setupBaseView()
        setupGetExtras()
        setupViewModel()
        updateTitle(RUtil.R_string(R.string.physical_lottery_title))
        btnNext.setText(RUtil.R_string(R.string.text_btn_print))
        title_lottery_select.setText(this.getString(R.string.text_label_lottery, model?.fullName?.toLowerCase()?.capitalize()))
        initSubscriptions()
        checkLotteryNumber()
    }

    private fun setupViewModel() {
        virtualViewModel = ViewModelProviders.of(this).get(LotterySaleViewModel::class.java)
        initSubscriptions()
    }

    @SuppressLint("CheckResult")
    private fun initSubscriptions() {
        virtualViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is LotterySaleViewModel.ViewEvent.ResponseNumberLotteryModel -> {
                    response = it.checkNumberLottery
                    setupRecyclerViewLotteries(response)
                    listFractions = response!!
                    hideDialogProgress()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerViewLotteries(lotteries: ResponseCheckNumberLotteryModel?) {
        recyclerItems.apply {
            layoutManager = GridLayoutManager(context, if(DeviceUtil.isSalePoint())4 else 1)
            setHasFixedSize(true)
        }
        val lotteryAdapter = FractionsListAdapter(lotteries, model!!)
        recyclerItems.adapter = lotteryAdapter
        val sumFractionSlopes = lotteries!!.tickets!!.map { it.fraction!!.toInt() }!!.sumBy { it!! }
        fraction_slopes.text = getString(R.string.title_fraction_slopes)+" "+sumFractionSlopes
    }

    private fun checkLotteryNumber() {
        showDialogProgress(RUtil.R_string(R.string.message_dialog_request))
        virtualViewModel.checkNumberLottery(numberLottery, model?.lotteryCode.toString(), false)
    }

    private fun showDialogConfirm(){
        showAlertDialog(getString(R.string.title_fractions_available), getString(R.string.message_fraction_available),{
                printFractions()
        },{})
    }

    private fun printFractions(){
        isForeground = false
        showDialogProgress(getString(R.string.text_load_data))
        virtualViewModel.printFractions(listFractions, model!!)
    }

    private fun goBack(){
        onBackPressed()
    }

}
