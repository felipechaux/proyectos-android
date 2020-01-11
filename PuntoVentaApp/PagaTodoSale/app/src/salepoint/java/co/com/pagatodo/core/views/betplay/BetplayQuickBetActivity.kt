package co.com.pagatodo.core.views.betplay

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.activity_betplay_quick_bet.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.salepoint.layout_payment_betplay_quick_bet.view.*

class BetplayQuickBetActivity : BaseActivity() {
    private lateinit var viewModel: BetplayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_betplay_quick_bet)
        setupViewModel()
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.betplay_quick_bet_title))
        initListenersViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BetplayViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer{ itl ->
            hideDialogProgress()
            when(itl) {
                is BetplayViewModel.ViewEvent.ResponseSuccess -> {
                    itl.successMessage?.let { it1 -> showOkAlertDialog("", it1) { navigateToMenu(this) } }
                }
                is BetplayViewModel.ViewEvent.ResponseError -> {
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

        viewModel.loadProductParameters()
    }

    private fun initListenersViews() {

        txtNumberId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorNumberId.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        txtNumberIdConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorNumberIdConfirm.visibility = View.GONE
                labelErrorNumberIdNotEqual.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { showPayDialog() }
    }

    private fun showPayDialog(){
        if (isValidFields()){
            PaymentConfirmationDialog().show(this, R.layout.layout_payment_betplay_quick_bet,
                R_string(R.string.title_confirm_quickbet_betplay), null, { view ->
                    view.labelOperator.text = R_string(R.string.betplay_title)
                    view.labelNumber.text = txtNumberId.text
                },{
                    showDialogProgress(getString(R.string.message_dialog_request))
                    viewModel.dispatchPinQuickBet(txtNumberId.text.toString())
                },{}, R_string(R.string.text_btn_generate))
        }
    }

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

        return isValid
    }
}
