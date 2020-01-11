package co.com.pagatodo.core.views.raffle

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.RaffleModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import co.com.pagatodo.core.views.homemenu.HomeMenuActivity
import co.com.pagatodo.core.views.raffle.fragments.PayRaffleFragment
import co.com.pagatodo.core.views.raffle.fragments.RaffleListFragment
import co.com.pagatodo.core.views.raffle.fragments.RangeRaffleFragment
import kotlinx.android.synthetic.main.dialog_pay_raffle.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.salepoint.fragment_range_raffle.*
import kotlin.properties.Delegates

class RaffleActivity : BaseActivity(),
    RaffleListFragment.OnFragmentInteractionListener,
    PayRaffleFragment.OnFragmentInteractionListener, RangeRaffleFragment.OnFragmentInteractionListener {

    lateinit var viewModel: RaffleViewModel
    private var raffleListFragment: RaffleListFragment by Delegates.notNull()
    private var payRaffleFragment: PayRaffleFragment by Delegates.notNull()
    private var rangeRaffleFragment: RangeRaffleFragment by Delegates.notNull()
    private var isRange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raffle)
        setupView()
        setupModel()
    }

    private fun setupModel() {
        viewModel = ViewModelProviders.of(this).get(RaffleViewModel::class.java)
        viewModel.rafflesListLiveData.observe(this, Observer<List<RaffleModel>> {
            raffleListFragment.updateRaffleList(it)
        })
        viewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is RaffleViewModel.ViewEvent.ResponseMessage -> {
                    if (it.isSuccess) {
                        payRaffleFragment.numberAvailable()
                    } else {
                        showOkAlertDialog("", it.message)
                        hideDialogProgress()
                    }
                }
                is RaffleViewModel.ViewEvent.ResponseRandomNumber -> {
                    hideDialogProgress()
                    payRaffleFragment.generateRandomNumber(it.number)
                }
                is RaffleViewModel.ViewEvent.ResponseNumberSale -> {
                    hideDialogProgress()
                    showDialogResponseSale(it.message, it.isSuccess)
                }
                is RaffleViewModel.ViewEvent.AvailableRangeSuccess -> {

                    val numbers = (it.startNum.toInt()..it.endNum.toInt()).map { it } as MutableList<Int>
                    numbers.removeAll(it.response.numbers!!.map { it.toInt() })
                    hideDialogProgress()
                    rangeRaffleFragment.updateRaffleList(numbers!!.map { it.toString() })
                }
            }
        })
        viewModel.fetchRaffles()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.title_view_raffles))

        raffleListFragment = RaffleListFragment()
        payRaffleFragment = PayRaffleFragment()
        rangeRaffleFragment = RangeRaffleFragment()
        raffleListFragment.setOnFragmentListener(this)
        payRaffleFragment.setOnFragmentListener(this)
        rangeRaffleFragment.setOnFragmentListener(this)

        if (DeviceUtil.isSalePoint()) {
            initFragment(true)
        } else {
            initFragment()
        }

        btnBack.setOnClickListener {
        if(isRange){

            supportFragmentManager
                .beginTransaction()
                .show(payRaffleFragment)
                .commit()

            supportFragmentManager
                .beginTransaction()
                .remove( rangeRaffleFragment.apply { clearForm() })
                .commit()

            isRange = false


        }else{

            backButtonRaffle()
        }

        }
        btnNext.setOnClickListener {
            if(isRange){


                if(rangeRaffleFragment.etNumberInitRaffle.text.toString().isEmpty() ||
                    rangeRaffleFragment.etNumberFinalRaffle.text.toString().isEmpty()){

                    showDialogResponseSale(R_string(R.string.summary_ram_raffle),false)

                    return@setOnClickListener
                }

                isRange = false

                val selectNumber = rangeRaffleFragment.selectNumber
                supportFragmentManager
                    .beginTransaction()
                    .show(payRaffleFragment.apply {
                        setNumberRaffleValue(selectNumber)
                        fetchAvailable()

                    })
                    .commit()

                supportFragmentManager
                    .beginTransaction()
                    .hide(rangeRaffleFragment)
                    .commit()




            } else{
                nextViewRaffle()
            }


        }
    }

    private fun initFragment(isSalePoint: Boolean = false) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.frameLayoutRaffles, raffleListFragment)
            .commit()

        if (isSalePoint)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frameLayoutPayRaffles, payRaffleFragment)
                .commit()
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutRaffles, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun nextViewRaffle() {
        validatePayFragmentFields()
    }

    private fun validatePayFragmentFields() {


        if(isRange){

            showDialogToPay()

        }else{
            if (payRaffleFragment.validateData() && viewModel.validateNumber(viewModel.currentRaffle?.raffleNumber ?: "")) {
                showDialogToPay()
            } else {
                payRaffleFragment.fetchAvailable()
            }
        }


    }

    private fun backButtonRaffle() {
        onBackPressed()
    }

    private fun showDialogToPay() {
        val hourFormated = DateUtil.convertMilitaryHourToNormal(
            DateUtil.StringFormat.HHMMA,
            viewModel.getCurrentRaffle()?.drawTime ?: ""
        ).toLowerCase()
        PaymentConfirmationDialog().show(this, R.layout.dialog_pay_raffle,
            R_string(R.string.raffle_dialog_raffle_ticket), null, { view ->
                view.text_dialog_raffle_name.text = viewModel.getCurrentRaffle()?.raffleName
                view.dialog_raffle_number.text = viewModel.currentRaffle?.raffleNumber
                view.dialog_raffle_lottery.text = viewModel.getCurrentRaffle()?.lotteryName
                view.dialog_raffle_value.text =
                    "$ ${formatValue(viewModel.getCurrentRaffle()?.price?.toDouble() ?: 0.0)}"
                view.dialog_raffle_date_draw.text = "${viewModel.getCurrentRaffle()?.dateDraw} ${hourFormated}"
            }, {
                showDialogProgress(R_string(R.string.message_dialog_request))
                viewModel.sellRaffleNumber(viewModel.getRequestRaffleModel())
            })
    }

    private fun showDialogResponseSale(message: String, isSuccess: Boolean) {
        androidx.appcompat.app.AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(R_string(R.string.text_btn_accept)) { _, _ ->
                if (isSuccess) {
                    val menuIntent = Intent(context, HomeMenuActivity::class.java)
                    navigateUpTo(menuIntent)
                }
            }
        }.show()
    }

    fun onClickMenu(currentRaffle: RaffleModel?) {
        viewModel.setCurrentRaffle(currentRaffle)
    }

    fun showNextButton(show: Boolean) {
        if (show) {
            btnNext.visibility = View.VISIBLE
        } else {
            btnNext.visibility = View.GONE
        }
    }

    override fun selectRaffle(raffleModel: RaffleModel) {
        viewModel.setCurrentRaffle(raffleModel)
        if (!DeviceUtil.isSalePoint()) {
            payRaffleFragment = PayRaffleFragment()
            payRaffleFragment.setOnFragmentListener(this)
            showFragment(payRaffleFragment)
        } else {



            if(isRange)
                rangeRaffleFragment.setEnableEditText(true)
            else{
                payRaffleFragment.setEnableEditText(true)
                payRaffleFragment.apply {
                    hideSummary()
                }

            }

        }
    }

    override fun getCurrentRaffle(): RaffleModel? {
        return viewModel.getCurrentRaffle()
    }

    override fun getListRaffle(): List<RaffleModel>? {
        return viewModel.rafflesListLiveData.value
    }

    override fun generateRandomNumber() {
        showDialogProgress(R_string(R.string.message_dialog_generate_random))
        viewModel.fetchRandomNumber()
    }

    override fun setupViewModelNumber(number: String) {
        viewModel.currentRaffle?.raffleNumber = number
    }

    override fun validateNumberRaffle(number: String) {
        if (!viewModel.isSelectedRaffle()) {
            showOkAlertDialog("", R_string(R.string.text_raffles_validation_id))
        } else {
            viewModel.fetchAvailableRaffles(number)
        }
    }

    override fun showMessage(message: String) {
        showOkAlertDialog("", message)
    }

    override fun showNextButton() {
        showNextButton(true)
    }

    override fun hideNextButton() {
        showNextButton(false)
    }

    override fun getNumbers(numberInit: String, numberFinal: String) {
        hideKeyboard()
        showDialogProgress(getString(R.string.text_load_data))
        viewModel.availableRange(viewModel.getCurrentRaffle()?.raffleId.toString(),numberInit,numberFinal)
    }

    override fun selectNumber(numberSelect: String) {
        btnNext.visibility = View.VISIBLE
        viewModel.currentRaffle?.raffleNumber = numberSelect
    }

    override fun showRange() {

        isRange = true
        if (DeviceUtil.isSalePoint()) {

            supportFragmentManager
                .beginTransaction()
                .hide(payRaffleFragment)
                .commit()


            supportFragmentManager
                .beginTransaction()
                .add(R.id.frameLayoutPayRaffles, rangeRaffleFragment.apply { clearForm() })
                .commit()




        }
    }
}
