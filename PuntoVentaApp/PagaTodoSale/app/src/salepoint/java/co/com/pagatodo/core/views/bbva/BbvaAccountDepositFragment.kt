package co.com.pagatodo.core.views.bbva

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestBbvaDTO
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import co.com.pagatodo.core.views.base.BaseFragment
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.salepoint.fragment_bbva_account_deposit.*
import java.util.concurrent.TimeUnit


class BbvaAccountDepositFragment : BaseFragment() {

    private lateinit var viewModel: BbvaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bbva_account_deposit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupFragmentView()

    }

    private fun setupViewModel() {

        viewModel = ViewModelProviders.of(this).get(BbvaViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is BbvaViewModel.ViewEvent.Errors -> loadErrors(it.errorMessage)
                is BbvaViewModel.ViewEvent.Success -> success(it.message)
            }})

    }

    private fun setupFragmentView() {
        initListenersViews()
    }

    private fun initListenersViews() {
        initListenersClicks()
    }

    private fun success(message: String) {
        hideDialogProgress()
        showOkAlertDialog("", message){
            onBackPressed(BbvaAccountDepositFragment())
        }
    }

    private fun loadErrors(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun initListenersClicks() {

        btnDepositsBack.setOnClickListener { activity?.onBackPressed() }
        btnDepositsPay.setOnClickListener { confirmDeposit() }
        val radioGroup = activity!!.findViewById<RadioGroup>(R.id.bbvaRadioPaymentType)
        radioGroup?.setOnCheckedChangeListener { _, checkedId ->
            val bbvaDebit = activity!!.findViewById<RadioButton>(R.id.radioBbvaDebit)
            val bbvaCash = activity!!.findViewById<RadioButton>(R.id.radioBbvaCash)
            if (R.id.radioBbvaDebit == checkedId) {
                bbvaCash.isChecked = false
            } else {
                bbvaDebit.isChecked = false
            }
        }

        initListenersRWatcher()

    }

    @SuppressLint("CheckResult")
    private fun initListenersRWatcher() {

        txtBbvaDestinyAccountNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {

                labelErrorDestinyAccountNumber.visibility = View.GONE
            }
        })

        var depositValue = true
        txtBbvaDepositValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (depositValue) {
                    depositValue = false
                    if (txtBbvaDepositValue.text.toString().isNotEmpty()) {
                        labelErrorDepositValue.visibility = View.GONE
                        try {
                            var originalString = txtBbvaDepositValue.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()
                            txtBbvaDepositValue.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue(value)
                                )
                            )
                            txtBbvaDepositValue.setSelection(txtBbvaDepositValue.text.length)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    } else {
                        labelErrorDepositValue.visibility = View.VISIBLE
                    }

                    depositValue = true
                }
            }
        })

        initListenersRWatcherConfirm()

    }

    private fun initListenersRWatcherConfirm(){

        var confirmDepositValue = true

        txtBbvaConfirmDepositValue.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {

                if (confirmDepositValue) {
                    confirmDepositValue = false

                    if (txtBbvaConfirmDepositValue.text.toString().isNotEmpty()) {
                        labelErrorConfirmDepositValue.visibility = View.GONE
                        try {
                            var originalString = txtBbvaConfirmDepositValue.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()
                            txtBbvaConfirmDepositValue.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue(value)
                                )
                            )
                            txtBbvaConfirmDepositValue.setSelection(txtBbvaConfirmDepositValue.text.length)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    } else {
                        labelErrorConfirmDepositValue.visibility = View.VISIBLE
                    }

                    confirmDepositValue = true
                }


            }
        })
    }


    private fun confirmDeposit() {

        if (validateForm()) {

            val inflater = this.layoutInflater
            val mDialogView = inflater.inflate(R.layout.layout_bbva_confirm_deposit, null)
            mDialogView.findViewById<TextView>(R.id.lblTransferType).text =
                if (activity!!.findViewById<RadioButton>(R.id.radioBbvaDebit).isChecked) "Tarjeta débito" else "Efectivo"
            mDialogView.findViewById<TextView>(R.id.lblDestinyAccountNumber).text =
                txtBbvaDestinyAccountNumber.text
            mDialogView.findViewById<TextView>(R.id.lblDepositValue).text = txtBbvaDepositValue.text
            val alertDialog = activity?.let {
                androidx.appcompat.app.AlertDialog.Builder(it).apply {
                    setTitle(RUtil.R_string(R.string.bbva_pay_modal_title))
                    setView(mDialogView)
                    setCancelable(false)
                    setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton(RUtil.R_string(R.string.text_btn_pay)) { dialog, _ ->
                        dialog.dismiss()
                        dispatchAccountDeposit()
                    }
                }.show()
            }

            alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.secondaryText
                )
            )

        }


    }

    private fun dispatchAccountDeposit() {

        val requestBbvaDTO = RequestBbvaDTO().apply {

            this.value = txtBbvaConfirmDepositValue.text.toString().resetValueFormat()
            this.numReceipt = "000091"
            this.rrn = "000586"
            this.concept = "100123"
            this.typeTransactionAdvisor = "I"
            this.descProduc = "DEPOSITOS TARJETA BBVA"

        }

        showDialogProgress(getString(R.string.text_load_data))
        viewModel.accountDeposit(requestBbvaDTO)


    }

    private fun transactionSuccess() {

        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_deposits_transaction_success_title))
                setMessage(R.string.bbva_deposits_transaction_success_message)
                setCancelable(false)
                setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }

    }

    private fun transactionDeclined() {

        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_deposits_transaction_declined_title))
                setMessage(R.string.bbva_deposits_transaction_declined_message)
                setCancelable(false)
                setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }

    }

    private fun transactionStatus() {

        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_deposits_transaction_status_title))
                setMessage(R.string.bbva_deposits_transaction_status_message)
                setCancelable(false)
                setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }

    }

    private fun reprintTicket() {

        val alertDialog = activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_deposits_reprint_ticket_title))
                setMessage(R.string.bbva_deposits_reprint_ticket_message)
                setCancelable(false)
                setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(RUtil.R_string(R.string.text_btn_reprint)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }

        alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.secondaryText
            )
        )

    }

    private fun validateForm(): Boolean {

        var isValid = true


        if (spinnerDestinyAccountType.text == getString(R.string.bbva_deposits_placeholder_account_type)) {
            labelErrorDestinyAccountType.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorDestinyAccountType.visibility = View.GONE
        }

        if (txtBbvaDestinyAccountNumber.text.isEmpty()) {
            labelErrorDestinyAccountNumber.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorDestinyAccountNumber.visibility = View.GONE
        }


        if (txtBbvaDepositValue.text.isEmpty()) {
            labelErrorDepositValue.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorDepositValue.visibility = View.GONE
        }


        if (txtBbvaConfirmDepositValue.text.isEmpty()) {
            labelErrorConfirmDepositValue.visibility = View.VISIBLE
            isValid = false
        } else {
            labelErrorConfirmDepositValue.visibility = View.GONE
        }

        if (isValid) {

            if (txtBbvaDepositValue.text.toString() != txtBbvaConfirmDepositValue.text.toString()) {
                labelErrorConfirmDepositValue.visibility = View.VISIBLE
                isValid = false
            } else {
                labelErrorConfirmDepositValue.visibility = View.GONE
            }
        }

        return isValid

    }


}