package co.com.pagatodo.core.views.baloto

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.JumpToNextEditTextWatcher
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.activity_baloto_ticket.*
import kotlinx.android.synthetic.main.dialog_pay_prize.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class BalotoCheckTicketActivity : BaseActivity() {

    lateinit var viewModel: BalotoViewModel
    var valueSerie = ""
    var numberSerie = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baloto_ticket)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.text_title_baloto))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BalotoViewModel::class.java)
        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModel.singleLiveEvent.observe(this, Observer { itl ->
            when(itl) {
                is BalotoViewModel.ViewEvent.ResponsTicketSerie -> {
                    hideDialogProgress()
                    setupInformationSuccess(itl.checkTicket.netAmount.toString())
                    btnNext.visibility = View.VISIBLE
                    numberSerie = itl.checkTicket.serialNumber.toString()
                    valueSerie = itl.checkTicket.netAmount.toString()
                }
                is BalotoViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    setupInformationError(itl.errorMessage.toString())
                    btnNext.visibility = View.GONE
                }
                is BalotoViewModel.ViewEvent.ShowMessage -> {
                    hideDialogProgress()
                    showOkAlertDialog("", itl.message){
                        if (itl.isSuccess){
                            finish()
                        }
                    }
                }
            }
        })

        viewModel.getParameters()
    }

    private fun setupInformationSuccess(valueTIcket: String) {
        layoutAwardValue.visibility = View.VISIBLE
        val value = formatValue(valueTIcket.toDouble())
        lblTagAward.text = R_string(R.string.text_award_value)
        lblAwardValue.text = getString(R.string.text_label_parameter_coin, value)
        lblAwardValue.visibility = View.VISIBLE
    }

    private fun setupInformationError(errorMessage: String) {
        layoutAwardValue.visibility = View.VISIBLE
        lblTagTicket.text = R_string(R.string.text_ticket_information)
        lblTagAward.text = errorMessage
        lblAwardValue.visibility = View.GONE
    }

    private fun initListenersViews() {
        btnNext.visibility = View.GONE
        btnBack.setOnClickListener { finish() }
        btnCheckTicket.setOnClickListener {
            DeviceUtil.hideKeyboard(txtNumberTicketOne, this@BalotoCheckTicketActivity)
            DeviceUtil.hideKeyboard(txtNumberTicketTwo, this@BalotoCheckTicketActivity)
            DeviceUtil.hideKeyboard(txtNumberTicketThree, this@BalotoCheckTicketActivity)
            if(isValidForm()) {
                checkNumberTicket()
            }
        }
        btnNext.setOnClickListener { showTicketDialog() }
        txtNumberTicketOne.addTextChangedListener(JumpToNextEditTextWatcher(4,txtNumberTicketTwo))
        txtNumberTicketTwo.addTextChangedListener(JumpToNextEditTextWatcher(9,txtNumberTicketThree))
    }

    private fun checkNumberTicket() {
        showDialogProgress(getString(R.string.message_dialog_request))
        val ticketSerialNumber = createTicketSerialNumber()
        isForeground = false
        viewModel.checkNumberTicket(ticketSerialNumber)
    }

    private fun isValidForm(): Boolean {
        hideLabelErrors()
        var isValid = true
        if(txtNumberTicketOne.text.isEmpty() &&
            txtNumberTicketTwo.text.isEmpty() &&
            txtNumberTicketThree.text.isEmpty()) {
            labelErrorNumberEmpty.visibility = View.VISIBLE
            isValid = false

            return isValid
        }

        labelErrorNumberEmpty.visibility = View.GONE

        if(txtNumberTicketOne.text.length < 4) {
            labelErrorNumberFormat.visibility = View.VISIBLE
            isValid = false
        }
        if(txtNumberTicketTwo.text.length < 9) {
            labelErrorNumberFormat.visibility = View.VISIBLE
            isValid = false
        }
        if(txtNumberTicketThree.text.length < 6) {
            labelErrorNumberFormat.visibility = View.VISIBLE
            isValid = false
        }
        return isValid
    }

    private fun hideLabelErrors() {
        labelErrorNumberEmpty.visibility = View.GONE
        labelErrorNumberFormat.visibility = View.GONE
    }

    private fun createTicketSerialNumber(): String {
        return "${txtNumberTicketOne.text}-${txtNumberTicketTwo.text}-${txtNumberTicketThree.text}"
    }

    private fun showTicketDialog(){
        PaymentConfirmationDialog().show(this, R.layout.dialog_pay_prize,
            R_string(R.string.title_ticket_pay_prize), null, { view ->
                view.labelNumberSerie.text = createTicketSerialNumber().replace("-"," ")
                view.labelValue.text = "$${formatValue(valueSerie.toDouble())}"
            },{
                showDialogProgress(getString(R.string.message_dialog_request))
                viewModel.checkTiket(createTicketSerialNumber(), true)
            })
    }
}
