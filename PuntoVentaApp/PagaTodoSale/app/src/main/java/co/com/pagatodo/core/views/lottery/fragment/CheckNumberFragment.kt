package co.com.pagatodo.core.views.lottery.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.request.RequestSaleOfLotteries
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrintManager
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.PrinterStatusInfo
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.LotteryAdapter
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import co.com.pagatodo.core.views.lottery.LotterySaleViewModel
import com.cloudpos.POSTerminal
import com.cloudpos.printer.PrinterDevice
import com.pos.device.printer.Printer
import kotlinx.android.synthetic.main.fragment_check_number.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_payment_confirm_loteries.view.*

class CheckNumberFragment : Fragment() {

    private lateinit var virtualViewModel: LotterySaleViewModel
    private lateinit var response: ResponseCheckNumberLotteryModel

    private var lotteryFragment: VirtualLotteryModel? = null
    private lateinit var activityFragment: LotteryAdapter.OnListenerAdapter
    private var isVirtualLottery = false

    companion object {
        fun newInstance(lottery: VirtualLotteryModel?, activity: LotteryAdapter.OnListenerAdapter, isVirtual: Boolean) =
                CheckNumberFragment().apply {
                    lotteryFragment = lottery
                    activityFragment = activity
                    isVirtualLottery = isVirtual
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setupViewModel()
        initListenersViews()
        setupVisibilityLabelRandom()
    }

    private fun setupViewModel() {
        virtualViewModel = ViewModelProviders.of(this).get(LotterySaleViewModel::class.java)
        initSubscriptions()
    }

    private fun initSubscriptions() {
        virtualViewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is LotterySaleViewModel.ViewEvent.ResponseSuccess -> {
                    activityFragment.onCloseProgressDialog()
                    it.successMessage?.let { activityFragment.onShowOkAlertDialog("", R_string(R.string.message_success_bet)) { activity?.finish() } }
                }
                is LotterySaleViewModel.ViewEvent.ResponseError -> {
                    activityFragment.onCloseProgressDialog()
                    it.errorMessage?.let { it1 -> activityFragment.onShowOkAlertDialog("", it1) }
                }
                is LotterySaleViewModel.ViewEvent.ResponseNumberLotteryModel -> {
                    response = it.checkNumberLottery
                    activityFragment.onCloseProgressDialog()
                    if (response.message.equals("Exito")) {
                        curtainView.visibility = View.GONE
                        setupSeriesSpinner()
                    } else {
                        curtainView.visibility = View.VISIBLE
                        activityFragment.onShowOkAlertDialog("", response.message.toString())
                    }
                }
                is LotterySaleViewModel.ViewEvent.ResponseNumberLotteryRandom -> {
                    activityFragment.onCloseProgressDialog()
                    labelErrorNumber.visibility = View.GONE
                    txtNumber.setText(it.numberRandom.number?.number)

                    curtainView.visibility = View.VISIBLE
                    spinnerSerie.selectedIndex = 0
                    spinnerFractions.selectedIndex = 0
                    labelErrorFraction.visibility = View.GONE
                    labelErrorSerie.visibility = View.GONE
                    
                }
            }
        })
    }

    private fun setupVisibilityLabelRandom() {
        if (!isVirtualLottery) {
            layoutRandom5.visibility = View.GONE
        }
    }

    private fun initListenersViews() {

        if (!isVirtualLottery) {
            labelErrorNumber.text = R_string(R.string.message_error_number_empty_game_physics_lottery)
        }

        txtNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorNumber.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        layoutRandom5.setOnClickListener {
            validateLottery {
                activityFragment.onShowProgressDialog(R_string(R.string.message_dialog_generate_random))
                virtualViewModel.getRandomNumber(lotteryFragment?.lotteryCode.toString())
            }
        }
        btnCheckNumber.setOnClickListener {
            spinnerSerie.selectedIndex = 0
            spinnerFractions.selectedIndex = 0
            labelErrorFraction.visibility = View.GONE
            labelErrorSerie.visibility = View.GONE
            checkLotteryNumber()

        }
        btnBack.setOnClickListener {
            activityFragment.onBackFragment()
        }
        btnNext.setOnClickListener {
            if (curtainView.visibility == View.GONE)
                showDialogConfirm()
            else
                checkLotteryNumber()
        }
    }

    private fun validateLottery(closure: () -> Unit?) {
        if (lotteryFragment != null) {
            closure()
        } else {
            activityFragment.onShowOkAlertDialog("", R_string(R.string.text_select_lottery))
        }
    }

    private fun checkLotteryNumber() {
        validateLottery {
            if (validateField()) {
                this.context?.let { it1 -> DeviceUtil.hideKeyboard(txtNumber, it1) }
                activityFragment.onShowProgressDialog(R_string(R.string.message_dialog_request))
                lotteryFragment?.lotteryCode?.let { it1 -> virtualViewModel.checkNumberLottery(txtNumber.text.toString(), it1, isVirtualLottery) }
            }
        }
    }

    private fun validateField(): Boolean {
        var isValid = true
        if (txtNumber.text.isEmpty()) {
            labelErrorNumber.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorNumber.visibility = View.GONE
        }
        if (!txtNumber.text.isEmpty() && txtNumber.text.length < 4) {
            labelErrorNumberLimit.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorNumberLimit.visibility = View.GONE
        }
        return isValid
    }

    private fun setupSeriesSpinner() {
        val series = mutableListOf<String>()
        series.add(getString(R.string.placeholder_series))
        val items: List<LotteryNumberModel>? = if (isVirtualLottery) {
            response.numbers
        } else {
            response.tickets
        }
        items?.forEach {
            it.serie?.let { it1 -> series.add(it1) }
        }
        spinnerSerie.setItems(series)
        spinnerSerie.setOnItemSelectedListener { _, position, _, _ ->
            labelErrorSerie.visibility = View.GONE
            var isFullTicket = false
            if (position != 0) {
                if (isVirtualLottery) {
                    if(lotteryFragment?.fractions?.toInt() == response.numbers?.get((position - 1))?.fraction?.toInt()){
                        isFullTicket = true
                    }
                    response.numbers?.get((position - 1))?.fraction?.toInt()?.let { setItemsForFractionSpinner(it, isFullTicket) }
                } else {
                    if(lotteryFragment?.fractions?.toInt() == response.tickets?.get((position - 1))?.fraction?.toInt()){
                        isFullTicket = true
                    }
                    response.tickets?.get((position - 1))?.fraction?.toInt()?.let { setItemsForFractionSpinner(it, isFullTicket) }
                }
            }
        }
    }

    private fun setItemsForFractionSpinner(fraction: Int, isFullTicket: Boolean) {
        val availableFractions = mutableListOf<String>()
        availableFractions.add(getString(R.string.placeholder_fractions))
        if(isFullTicket){
            availableFractions.add("1")
            availableFractions.add("Billete completo")
        }else{
            availableFractions.add("1")
        }
        spinnerFractions.setItems(availableFractions)
        spinnerFractions.setOnItemSelectedListener { _, position, _, _ ->
            labelErrorFraction.visibility = View.GONE
        }
    }

    private fun showDialogConfirm() {
        if (validateField()) {
            if (validateSpinners()) {
                val totalValue = getCalculationValueLottery()
                showConfirmationDialog(totalValue)
            }
        }
    }

    private fun getCalculationValueLottery(): String {
        val value = lotteryFragment?.fractionValue?.toInt()
        val fractions: Int
        if(spinnerFractions.text.toString().contains("Billete")){
            fractions = lotteryFragment?.fractions!!.toInt()
        }else{
            fractions = 1
        }
        var newValue = 0
        if (value != null) newValue = value * fractions
        return newValue.toString()
    }

    private fun validateSpinners(): Boolean {
        var isValid = true
        if (spinnerSerie.selectedIndex == 0) {
            labelErrorSerie.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorSerie.visibility = View.GONE
        }
        if (spinnerFractions.selectedIndex == 0) {
            labelErrorFraction.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorFraction.visibility = View.GONE
        }
        return isValid
    }

    private fun showConfirmationDialog(valueTotal: String) {
        PaymentConfirmationDialog().show(activity, R.layout.layout_payment_confirm_loteries,
                R_string(
                        if (isVirtualLottery) R.string.text_title_virtual_lottery_dialog
                        else R.string.text_title_physical_lottery_dialog), null, { view ->

            if (!isVirtualLottery) {
                view.containerValueLottery.visibility = View.GONE
            } else {
                view.containerValueLottery.visibility = View.VISIBLE
            }

            view.lblLotteryName.text = lotteryFragment?.fullName
            view.lblGameName.text = txtNumber.text.toString()
            view.lblSerie.text = spinnerSerie.text.toString()
            view.lblQuantity.text = spinnerFractions.text.toString()
            view.lblValueTotal.text = getString(R.string.text_label_parameter_coin, formatValue(valueTotal.toDouble()))
        }, {
            //                val status = PrinterStatusInfo().getStatus()
            val status = validatePrinterStatus()
            if (status == PrinterStatus.OK) {
                activityFragment.onShowProgressDialog(R_string(R.string.message_dialog_request))
                if (isVirtualLottery) dispatchPayVirtualLottery() else dispatchPayPhysicalLottery()
            } else if (status == PrinterStatus.PAPER_LACK) {
                activityFragment.onShowOkAlertDialog(R_string(R.string.message_error_title_paper_not_found), R_string(R.string.message_error_paper_not_found))
            } else {
                activityFragment.onShowOkAlertDialog(R_string(R.string.message_error_title_print_device), R_string(R.string.message_error_print_device))
            }
        })
    }

    fun validatePrinterStatus(): Any {


        if (DeviceUtil.getDeviceModel() == DatafonoType.Q2.type) {

            val status: PrinterStatus
            PagaTodoApplication.printerDevice = POSTerminal.getInstance(PagaTodoApplication.getAppContext()).getDevice(
                    "cloudpos.device.printer"
            ) as PrinterDevice

            PagaTodoApplication.printerDevice.open()

            when (PagaTodoApplication.printerDevice.queryStatus()) {
                PrinterDevice.STATUS_PAPER_EXIST -> {
                    status = PrinterStatus.OK
                }
                PrinterDevice.STATUS_OUT_OF_PAPER -> {
                    status = PrinterStatus.PAPER_LACK
                }
                else -> {
                    status = PrinterStatus.ERROR
                }
            }

            PagaTodoApplication.printerDevice.close()

            return status

        } else {
            return PrinterStatusInfo().getStatus()
        }

    }

    private fun dispatchPayPhysicalLottery() {
        val request = RequestSaleOfLotteries().apply {
            this.value = getCalculationValueLottery()
            this.number = txtNumber.text.toString()
            if(spinnerFractions.text.toString().contains("Billete")){
                this.fractions = lotteryFragment?.fractions.toString()
            }else{
                this.fractions = "1"
            }
            this.serie = spinnerSerie.text.toString()
            this.lotteryCode = lotteryFragment?.lotteryCode.toString()
            this.draw = lotteryFragment?.draw.toString()
            this.transactionTime = DateUtil.getCurrentDateInUnix()
            this.sequenceTransaction = StorageUtil.getSequenceTransaction()
        }
        virtualViewModel.payPhysicalLottery(request)
    }

    private fun dispatchPayVirtualLottery() {

        val request = RequestSaleOfLotteries().apply {
            this.fractionValue = lotteryFragment?.fractionValue.toString()
            this.value = getCalculationValueLottery()
            this.number = txtNumber.text.toString()
            if(spinnerFractions.text.toString().contains("Billete")){
                this.fractions = lotteryFragment?.fractions.toString()
            }else{
                this.fractions = "1"
            }
            this.serie = spinnerSerie.text.toString()
            this.drawDate = lotteryFragment?.date.toString()
            this.drawHour = lotteryFragment?.hour.toString()
            this.lotteryCode = lotteryFragment?.lotteryCode.toString()
            this.draw = lotteryFragment?.draw.toString()
            this.award = lotteryFragment?.award
            this.fullname = lotteryFragment?.fullName.toString()
            this.fractionsLottery = lotteryFragment?.fractions
            this.transactionTime = DateUtil.getCurrentDateInUnix()
            this.sequenceTransaction = StorageUtil.getSequenceTransaction()
        }
        virtualViewModel.payVirtualLottery(request)
    }
}