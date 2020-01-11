package co.com.pagatodo.core.views.betplay

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.request.RequestBetplayModel
import co.com.pagatodo.core.data.print.PrintManager
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import com.jakewharton.rxbinding2.widget.RxTextView
import com.pos.device.printer.Printer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_betplay.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_payment_confirm_recharges.view.*
import java.util.concurrent.TimeUnit

class BetplayActivity : BaseActivity() {

    private lateinit var betplayViewModel: BetplayViewModel
    private var minimumTicketPrice = 0
    private var maximumTicketPrice = 0
    private var rechargeValue = ""
    private var rechargeValueConfirm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_betplay)
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.betplay_title))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel() {
        betplayViewModel = ViewModelProviders.of(this).get(BetplayViewModel::class.java)
        initSubscriptions()
        betplayViewModel.loadProductParameters()
    }

    @SuppressLint("CheckResult")
    private fun initSubscriptions() {
        betplayViewModel.singleLiveEvent.observe(this, Observer { itl ->
            when(itl) {
                is BetplayViewModel.ViewEvent.ResponseKeyValueParameters -> {
                    itl.parameters.forEach {
                        if(it.key.equals(getString(R.string.name_column_minimum_value))) {
                            minimumTicketPrice = it.value?.toInt() ?: 0
                        }
                        if(it.key.equals(getString(R.string.name_column_maximum_value))) {
                            maximumTicketPrice = it.value?.toInt() ?: 0
                        }
                    }
                }
                is BetplayViewModel.ViewEvent.ResponseSuccess -> {
                    hideDialogProgress()
                    itl.successMessage?.let { it1 -> showOkAlertDialog("", it1) { navigateToMenu(this) } }
                }
                is BetplayViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    if(itl.errorMessage?.take(3).equals(R_string(R.string.code_error_betplay_document_not_found), ignoreCase = true)) {
                        itl.errorMessage?.let { it1 -> showOkAlertDialog("", it1) { } }
                    }else if(itl.errorMessage?.take(3).equals(R_string(R.string.code_error_betplay_provider_not_response), ignoreCase = true)) {
                        itl.errorMessage?.let { it1 -> showOkAlertDialog("", it1) { } }
                    }else{
                        showOkAlertDialog("", R_string(R.string.message_error_transaction))
                    }
                }
            }
        })

        txtNumberId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorNumberId.visibility = View.GONE
            }
        })

        txtNumberIdConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorNumberIdConfirm.visibility = View.GONE
                labelErrorNumberIdNotEqual.visibility = View.GONE
            }
        })

        txtValueRecharge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorValueRecharge.visibility = View.GONE
            }
        })

        txtValueRechargeConfirm.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                labelErrorValueRechargeConfirm.visibility = View.GONE
                labelErrorValueRechargeNotEqual.visibility = View.GONE
            }
        }
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { dispatchRecharge() }
        initListenersEditText(txtValueRecharge)
        initListenersEditText(txtValueRechargeConfirm)
    }

    @SuppressLint("CheckResult")
    private fun initListenersEditText(txtValue: EditText) {
        RxTextView.textChanges(txtValue)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io( ))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (txtValue.text.toString().isNotEmpty()){
                    try {
                        var originalString = txtValue.text.toString()
                        originalString = originalString
                            .replace(".", "")
                            .replace("$", "")
                        val value = originalString.toDouble()
                        getOriginalValues()
                        txtValue.setText(getString(R.string.text_label_parameter_coin, formatValue(value)))
                        txtValue.setSelection(txtValue.text.length)
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }
                }
            }
    }

    private fun getOriginalValues(){
        var originalValue = txtValueRecharge.text.toString()
        originalValue = originalValue
            .replace(".", "")
            .replace("$", "")
        rechargeValue = originalValue

        var originalValueConfirm = txtValueRechargeConfirm.text.toString()
        originalValueConfirm = originalValueConfirm
            .replace(".", "")
            .replace("$", "")
        rechargeValueConfirm = originalValueConfirm
    }

    private fun dispatchRecharge() {
        cleaAllFocus()
        getOriginalValues()
        if(isValidFields()) {
            if(validateRechargeValue()) {
                showConfirmationDialog()
            }
        }
    }

    private fun cleaAllFocus(){
        txtNumberId.clearFocus()
        txtNumberIdConfirm.clearFocus()
        txtValueRecharge.clearFocus()
        txtValueRechargeConfirm.clearFocus()
    }

    //TODO Refactor
    private fun isValidFields(): Boolean {
        var isValid = true

        if(txtNumberId.text.isEmpty()) {
            labelErrorNumberId.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorNumberId.visibility = View.GONE
        }

        labelErrorNumberIdNotEqual.visibility = View.GONE
        labelErrorNumberIdConfirm.visibility = View.GONE

        if(txtNumberIdConfirm.text.isEmpty()) {
            labelErrorNumberIdConfirm.visibility = View.VISIBLE
            isValid = false
        }else if(!(txtNumberId.text.isEmpty() && txtNumberIdConfirm.text.isEmpty())) {
            if(txtNumberId.text.toString() != txtNumberIdConfirm.text.toString()) {
                labelErrorNumberIdNotEqual.visibility = View.VISIBLE
                isValid = false
            } else {
                labelErrorNumberIdNotEqual.visibility = View.GONE
            }
        }else {
            labelErrorNumberIdNotEqual.visibility = View.GONE
            labelErrorNumberIdConfirm.visibility = View.GONE
        }

        if(txtValueRecharge.text.isEmpty()) {
            labelErrorValueRecharge.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorValueRecharge.visibility = View.GONE
        }

        labelErrorValueRechargeConfirm.visibility = View.GONE
        labelErrorValueRechargeNotEqual.visibility = View.GONE

        if(txtValueRechargeConfirm.text.isEmpty()) {
            labelErrorValueRechargeConfirm.visibility = View.VISIBLE
            isValid = false
        }else if(!(txtValueRecharge.text.isEmpty() && txtValueRechargeConfirm.text.isEmpty())) {
            if(!rechargeValue.equals(rechargeValueConfirm)) {
                labelErrorValueRechargeNotEqual.visibility = View.VISIBLE
                isValid = false
            }
        }else {
            labelErrorValueRechargeConfirm.visibility = View.GONE
            labelErrorValueRechargeNotEqual.visibility = View.GONE
        }
        return isValid
    }

    private fun validateRechargeValue(): Boolean {
        var isValid = true
        if(rechargeValue.toInt() < minimumTicketPrice) {
            showOkAlertDialog("", getString(R.string.text_label_parameter_min_value,
                formatValue(minimumTicketPrice.toDouble())
            ))
            isValid = false
        }
        if(rechargeValue.toInt() > maximumTicketPrice){
            showOkAlertDialog("", getString(R.string.text_label_parameter_max_value,
                formatValue(maximumTicketPrice.toDouble())
            ))
            isValid = false
        }
        return isValid
    }

    private fun showConfirmationDialog() {
        PaymentConfirmationDialog().show(this, R.layout.layout_payment_confirm_recharges,
            RUtil.R_string(R.string.title_confirm_recharge_betplay), null, { view ->
                view.labelOperator.text = R_string(R.string.betplay_title)
                view.labelPackageTitle.visibility = View.GONE
                view.labelPackage.visibility = View.GONE
                view.labelNumber.text = txtNumberId.text
                view.labelValue.text = "$ ${formatValue(rechargeValue.toDouble())}"
            },{
                showDialogProgress(getString(R.string.message_dialog_request))
                val request = RequestBetplayModel().apply {
                    this.numberDoc = txtNumberId.text.toString()
                    this.value = rechargeValue.toDouble()
                    this.stubs = getStub()
                    this.transactionTime = DateUtil.getCurrentDateInUnix()
                    this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                }
                betplayViewModel.isValidDocument(request)
            })
    }
}
