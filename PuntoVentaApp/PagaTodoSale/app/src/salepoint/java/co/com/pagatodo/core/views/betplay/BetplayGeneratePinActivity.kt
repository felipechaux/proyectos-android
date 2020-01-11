package co.com.pagatodo.core.views.betplay

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.AddCurrencySymbolTextWatcher
import co.com.pagatodo.core.util.AddCurrencySymbolTextWatcherListener
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_betplay_generate_pin.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class BetplayGeneratePinActivity : BaseActivity() {

    private lateinit var viewModel: BetplayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_betplay_generate_pin)
        setupViewModel()
        setupView()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BetplayViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer{ itl ->
            hideDialogProgress()
            when(itl) {
                is BetplayViewModel.ViewEvent.ResponseSuccess -> {
                    viewDisable.visibility = View.GONE
                    txtCollectValue.isEnabled = true
                    txtCollectValueConfirm.isEnabled = true
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

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.betplay_pin_title))
        initListenersViews()
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { collectBetplay() }
        btnQueryId.setOnClickListener { queryNumberID() }

        txtNumberId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorNumberId.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        txtCollectValue.addTextChangedListener(AddCurrencySymbolTextWatcher(txtCollectValue,
            object : AddCurrencySymbolTextWatcherListener {
                override fun onTextChange(number: String) {
                    labelErrorCollectValue.visibility = View.GONE
                }
            }))

        txtCollectValueConfirm.addTextChangedListener(AddCurrencySymbolTextWatcher(txtCollectValueConfirm,
            object : AddCurrencySymbolTextWatcherListener {
                override fun onTextChange(number: String) {
                    labelErrorCollectConfirmValue.visibility = View.GONE
                    labelErrorCollectNotEquals.visibility = View.GONE
                }
            }))
    }

    private fun queryNumberID(){
        if (isValidFieldDocument()){
            showDialogProgress(R_string(R.string.message_dialog_request))
            viewModel.validateDocument(txtNumberId.text.toString())
        }
    }

    private fun collectBetplay(){
        if (isValidFields()){
            showDialogProgress(R_string(R.string.message_dialog_request))
            viewModel.dispatchCollectBetplay(txtNumberId.text.toString(), txtCollectValue.text.toString())
        }
    }

    private fun isValidFieldDocument(): Boolean {
        var isValid = true

        if(txtNumberId.text.isEmpty()) {
            labelErrorNumberId.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorNumberId.visibility = View.GONE
        }
        return isValid
    }

    private fun isValidFields(): Boolean {
        var isValid = true

        if(txtNumberId.text.isEmpty()) {
            labelErrorNumberId.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorNumberId.visibility = View.GONE
        }

        if (viewDisable.visibility == View.GONE) {
            if(txtCollectValue.text.isEmpty()) {
                labelErrorCollectValue.visibility = View.VISIBLE
                isValid = false
            } else {
                labelErrorCollectValue.visibility = View.GONE
            }

            if(txtCollectValueConfirm.text.isEmpty()) {
                labelErrorCollectConfirmValue.visibility = View.VISIBLE
                isValid = false
            } else {
                labelErrorCollectConfirmValue.visibility = View.GONE
            }

            if(txtCollectValueConfirm.text.isNotEmpty() && txtCollectValue.text.isNotEmpty()) {
                if (txtCollectValueConfirm.text.toString() != txtCollectValue.text.toString()){
                    labelErrorCollectNotEquals.visibility = View.VISIBLE
                    isValid = false
                }
            } else {
                labelErrorCollectNotEquals.visibility = View.GONE
            }
        }

        return isValid
    }
}
