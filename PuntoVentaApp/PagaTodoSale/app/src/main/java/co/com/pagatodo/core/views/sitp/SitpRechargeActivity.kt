package co.com.pagatodo.core.views.sitp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.salepoint.activity_sitp_recharge.*
import java.util.concurrent.TimeUnit

class SitpRechargeActivity : BaseActivity() {

    private var rechargeValue = ""
    private var rechargeValueConfirm = ""
    private var cardBalance: Int = 0
    private var cardBalanceOk = false
    private var samBalance: Int = 0
    private var samBalanceOk = false
    var balanceValue = ""

    private lateinit var rechargeSitpViewModel: RechargeSitpViewModel
    private var alertDialogo: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitp_recharge)
        setupView()
        setupClientSitp()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(getString(R.string.sitp_recharge_title))
        txtRechargeConfirmValue.isEnabled = false
        initListenersViews()
        setupViewModel()
    }

    private fun setupViewModel() {
        rechargeSitpViewModel = ViewModelProviders.of(this).get(RechargeSitpViewModel::class.java)

        rechargeSitpViewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is RechargeSitpViewModel.ViewEvent.Errors -> errorLoadParameters(it.errorMessage)
                is RechargeSitpViewModel.ViewEvent.GetRechargeSitpSuccess -> rechargeSitpSuccess(
                    getString(R.string.recharge_sitp_transaction)
                )
            }
        })

    }

    private fun errorLoadParameters(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)

    }

    private fun initListenersViews() {
        initListenersOnClick()
        initListenersEditText(txtRechargeValue)
        initListenersEditText(txtRechargeConfirmValue)

    }

    private fun initListenersOnClick() {
        btnNext.setOnClickListener { sendRecharge() }
        btnBack.setOnClickListener { finish() }
        btnCardBalance.setOnClickListener {
            getCardBalance()
            rechargeSitpSuccess(getString(R.string.balance_sitp_transaction))
        }
        btnDeviceBalance.setOnClickListener {
            getSAMBalance()
            rechargeSitpSuccess(getString(R.string.sam_balance_sitp_transaction))
        }

        txtRechargeValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    txtRechargeConfirmValue.isEnabled = true
                    labelErrorRechargeValue.visibility = View.GONE
                } else {
                    labelErrorRechargeValue.visibility = View.VISIBLE
                }

            }
        })

        txtRechargeConfirmValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // not implemented
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                // not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (count > 0) {
                    labelErrorRechargeConfirmValue.visibility = View.GONE
                } else {
                    if (labelErrorRechargeCoincideValue.visibility == View.VISIBLE) {
                        labelErrorRechargeCoincideValue.visibility = View.GONE
                    }
                    labelErrorRechargeConfirmValue.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun getSAMBalance() {
        samBalanceOk = false
        if (client?.getSAMBalance()!! > 0) {
            samBalanceOk = true
            samBalance = client!!.getValue()
            balanceValue = formatValue(samBalance.toDouble())
        } else {
            showError()
        }
    }

    private fun getCardBalance() {
        cardBalanceOk = false
        if (client?.getCardBalance()!! > 0) {
            cardBalanceOk = true
            cardBalance = client!!.getValue()
            balanceValue = formatValue(cardBalance.toDouble())

        } else {
            showError()
        }
    }

    private fun sendRecharge() {
        if (validateFields()) {
            showModal()
        }
    }

    private fun showModal() {
        var cardId = ""
        var cardIdNumber = ""

        if (client!!.getCardId() > 0) {
            cardId = client!!.getLastCardID()
            cardIdNumber = cardId.substring(0, 16)
        } else {
            showError()
        }
        getCardBalance()
        if (cardIdNumber.isNotEmpty() && cardBalanceOk) {
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_sitp_recharge_reload, null)

            dialogView.findViewById<TextView>(R.id.labelNumberCard)
                .setText(cardIdNumber)
            dialogView.findViewById<TextView>(R.id.labelValueBalance)
                .setText(balanceValue)
            dialogView.findViewById<TextView>(R.id.labelValueReload)
                .setText(txtRechargeValue.text.toString())

            alertDialogo = AlertDialog.Builder(this).apply {
                setTitle(RUtil.R_string(R.string.sitp_recharge_title))
                setView(dialogView)
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setPositiveButton(getString(R.string.text_btn_pay)) { dialog, which ->
                    dialog.dismiss()
                    if (rechargeSitp()) {
                        showDialogProgress(getString(R.string.message_dialog_load_data))
                        rechargeSitpViewModel.getRechargeSitp(
                            cardIdNumber,
                            resetValueFormat(rechargeValue).toInt()
                        )
                    }
                }
            }.show()
            alertDialogo?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(this, R.color.secondaryText)
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun initListenersEditText(txtValue: EditText) {
        RxTextView.textChanges(txtValue)
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (txtValue.text.toString().isNotEmpty()) {
                    try {
                        var originalString = txtValue.text.toString()
                        originalString = originalString
                            .replace(".", "")
                            .replace("$", "")
                        val value = originalString.toDouble()
                        getOriginalValues()
                        txtValue.setText(
                            getString(
                                R.string.text_label_parameter_coin,
                                formatValue(value)
                            )
                        )
                        txtValue.setSelection(txtValue.text.length)
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }
                }
            }
    }

    private fun getOriginalValues() {
        var originalValue = txtRechargeValue.text.toString()
        originalValue = originalValue
            .replace(".", "")
            .replace("$", "")
        rechargeValue = originalValue

        var originalValueConfirm = txtRechargeConfirmValue.text.toString()
        originalValueConfirm = originalValueConfirm
            .replace(".", "")
            .replace("$", "")
        rechargeValueConfirm = originalValueConfirm
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (resetValueFormat(rechargeValue).isEmpty()) {
            isValid = false
            labelErrorRechargeValue.visibility = View.VISIBLE
        } else if (resetValueFormat(rechargeValueConfirm).isEmpty()) {
            isValid = false
            labelErrorRechargeConfirmValue.visibility = View.VISIBLE
        } else if (resetValueFormat(rechargeValueConfirm) != resetValueFormat(rechargeValue)) {
            isValid = false
            if (labelErrorRechargeConfirmValue.visibility == View.VISIBLE) {
                labelErrorRechargeConfirmValue.visibility = View.GONE
            }
            labelErrorRechargeCoincideValue.visibility = View.VISIBLE
        } else {
            if (labelErrorRechargeCoincideValue.visibility == View.VISIBLE) {
                labelErrorRechargeCoincideValue.visibility = View.GONE
            }
        }

        return isValid

    }

    private fun resetValueFormat(amount: String) = amount.replace("$", "").replace(".", "")

    private fun rechargeSitp(): Boolean {
        var result = false
        rechargeValue = txtRechargeValue.text.toString()
        if (client?.getCardRecharge(resetValueFormat(rechargeValue).toInt())!! > 0) {
            cardBalance = client!!.getValue()
            balanceValue = formatValue(cardBalance.toDouble())
            result = true
        } else {
            showError()
        }
        return result
    }

    private fun rechargeSitpSuccess(transaction: String) {

        runOnUiThread {
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_sitp_recharge_result, null)
            when (transaction) {
                getString(R.string.balance_sitp_transaction) -> {
                    dialogView.findViewById<TextView>(R.id.labelBalanceTitle)
                        .setText(getString(R.string.text_label_balance_card))
                }
                getString(R.string.sam_balance_sitp_transaction) -> {
                    dialogView.findViewById<TextView>(R.id.labelBalanceTitle)
                        .setText(getString(R.string.text_label_sam_balance))
                }

            }
            dialogView.findViewById<TextView>(R.id.labelBalance)
                .setText(getString(R.string.text_label_parameter_coin, balanceValue))

            alertDialogo = AlertDialog.Builder(this).apply {
                setTitle(RUtil.R_string(R.string.sitp_recharge_info_balance))
                setView(dialogView)
                setCancelable(false)

                setPositiveButton(getString(R.string.text_btn_accept)) { dialog, which ->
                    if (transaction.equals(getString(R.string.recharge_sitp_transaction))) {
                        finish()
                    }
                }
            }.show()
        }
    }

}
