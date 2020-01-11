package co.com.pagatodo.core.views.virtualwallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.request.RequestActivatePinModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.activity_virtual_wallet.*
import kotlinx.android.synthetic.main.dialog_pay_virtual_wallet.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class VirtualWalletActivity : BaseActivity() {

    private lateinit var viewModel: VirtualWalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_wallet)

        setupView()
    }

    private fun setupView() {
        setupBaseView()

        if (DeviceUtil.isSalePoint())
            updateTitle(R_string(R.string.title_virtual_wallet))
        else
            updateTitle(R_string(R.string.text_recharge_virtual_wallet))

        setupViewModel()
        initListeners()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(VirtualWalletViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when (it) {
                is VirtualWalletViewModel.ViewEvent.ResponseError -> {
                    showOkAlertDialog("", it.errorMessage ?: "")
                }
                is VirtualWalletViewModel.ViewEvent.ResponseSuccess -> {
                    showOkAlertDialog(
                        getString(R.string.message_success_recharge_virtual_wallet),
                        getString(
                            R.string.text_label_success_recharge_virtual_wallet,
                            viewModel.currentPin.value?.pin?.pin
                        )
                    ) {
                        finish()
                    }
                }
            }
        })

        viewModel.currentPin.observe(this, Observer {
            hideDialogProgress()
            showPayDialog()
        })
        
        viewModel.dispatchGetProductParameters()
    }

    private fun initListeners() {

        btnNext.setOnClickListener { queryNumberPin() }
        btnBack.setOnClickListener { finish() }

        txtPinNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorEnterPin.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // sin usar
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // sin usar
            }
        })
    }

    private fun queryNumberPin() {
        validateForm {
            showDialogProgress(R_string(R.string.message_dialog_load_data))
            viewModel.dispatchQueryPin(txtPinNumber.text.toString())
        }
    }

    private fun showPayDialog() {
        PaymentConfirmationDialog().show(this, R.layout.dialog_pay_virtual_wallet,
            R_string(R.string.title_dialog_pay_virtual_wallet), null, { view ->
                val value = viewModel.currentPin.value?.pin?.value ?: "0"
                view.tvPinNumber.text = viewModel.currentPin.value?.pin?.pin ?: "-"
                view.tvPinValue.text = "$${formatValue(value.toDouble())}"
            }, {
                showDialogProgress(getString(R.string.message_dialog_request))
                isForeground = false
                viewModel.dispatchActivatePin(createActivatePinRequest())
            })
    }

    private fun createActivatePinRequest(): RequestActivatePinModel {
        return RequestActivatePinModel().apply {
            pin = viewModel.currentPin.value?.pin?.pin
            value = viewModel.currentPin.value?.pin?.value
            transactionTime = DateUtil.getCurrentDateInUnix()
            sequenceTransaction = StorageUtil.getSequenceTransaction()
        }
    }

    private fun validateForm(success: () -> Unit?) {
        if (txtPinNumber.text.isNullOrEmpty()) {
            labelErrorEnterPin.visibility = View.VISIBLE
        } else {
            success()
        }
    }

}
