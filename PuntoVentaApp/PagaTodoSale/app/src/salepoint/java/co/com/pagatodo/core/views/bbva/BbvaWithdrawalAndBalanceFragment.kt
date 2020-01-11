package co.com.pagatodo.core.views.bbva

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestBbvaDTO
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import co.com.pagatodo.core.views.base.BaseFragment
import kotlinx.android.synthetic.salepoint.fragment_bbva_withdrawal_and_balance.*

class BbvaWithdrawalAndBalanceFragment : BaseFragment() {

    private lateinit var viewModel: BbvaViewModel
    var isDebitCard: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bbva_withdrawal_and_balance, container, false)
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

    private fun success(message: String) {

        showOkAlertDialog("", message){
            hideDialogProgress()
            onBackPressed(BbvaWithdrawalAndBalanceFragment())
        }
    }

    private fun loadErrors(errorMessage: String) {

        showOkAlertDialog("", errorMessage){
            hideDialogProgress()
            baseActivity().hideDialogProgress()
        }
    }

    private fun setupFragmentView() {
        initListenersViews()
    }

    private fun initListenersViews() {
        initListenersClicks()
        listCategoryType()
        listWithdrawalType()
        listAccountType() // No debit card.
    }

    private fun initListenersClicks() {


        spinnerWithdrawalType.setOnItemSelectedListener { view, position, id, item ->

            if (spinnerWithdrawalType.text.toString() == getString(R.string.bbva_withdrawal_type_placeholder)) {
                withDebitCard.visibility = View.GONE
                noDebitCard.visibility = View.GONE
                divider.visibility = View.GONE
                isDebitCard = null

            } else if (spinnerWithdrawalType.text.toString() == "Sin tarjeta de débito") {
                noDebitCard.visibility = View.VISIBLE
                withDebitCard.visibility = View.GONE
                divider.visibility = View.VISIBLE
                isDebitCard = false
            } else {
                withDebitCard.visibility = View.VISIBLE
                noDebitCard.visibility = View.GONE
                divider.visibility = View.VISIBLE
                isDebitCard = true

            }

        }

        btnWithdrawalBack.setOnClickListener { activity?.onBackPressed() }
        btnWithdrawalPay.setOnClickListener { confirmWithdrawal() }

        initListenersRWatcherValue(
            txtBbvaWithDebitWithdrawalValue,
            labelErrorWithDebitWithdrawalValue
        )
        initListenersRWatcherValue(
            txtBbvaWithDebitWithdrawalValueConfirm,
            labelErrorWithDebitWithdrawalValueConfirm
        )

        initListenersRWatcherValue(txtBbvaNoDebitWithdrawalValue, labelErrorNoDebitWithdrawalValue)
        initListenersRWatcherValue(
            txtBbvaNoDebitWithdrawalValueConfirm,
            labelErrorNoDebitWithdrawalValueConfirm
        )

        clearWatcher(txtBbvaNoDebitDocumentNumber,labelErrorNoDebitDocumentNumber)


    }

    private fun initListenersRWatcherValue(edt: EditText, label: TextView) {

        var isTwValueConfirm = true

        edt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (isTwValueConfirm) {

                    isTwValueConfirm = false

                    if (edt.text.toString().isNotEmpty()) {
                        label.visibility = View.GONE
                        try {
                            var originalString = edt.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()
                            edt.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue(value)
                                )
                            )
                            edt.setSelection(edt.text.length)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    } else {
                        label.visibility = View.VISIBLE
                    }

                    isTwValueConfirm = true

                }

            }
        })

    }


    private fun clearWatcher(edt: EditText, label: TextView) {


        edt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                label.visibility = View.GONE


            }
        })

    }


    private fun confirmWithdrawal() {

        if (validateFormGeneric()) {

            val inflater = this.layoutInflater
            val mDialogView = inflater.inflate(R.layout.layout_bbva_confirm_withdrawal, null)
            mDialogView.findViewById<TextView>(R.id.lblWithdrawalType).text =
                spinnerWithdrawalType.text.toString()
            if (this.isDebitCard == false) {
                mDialogView.findViewById<TextView>(R.id.lblWithdrawalAccountNumber).text =
                    "No definido en el mockup"
                mDialogView.findViewById<TextView>(R.id.lblWithdrawalValue).text =
                    txtBbvaNoDebitWithdrawalValue.text.toString()
            } else {
                mDialogView.findViewById<TextView>(R.id.lblWithdrawalAccountNumber).text =
                    "No definido en el mockup"
                mDialogView.findViewById<TextView>(R.id.lblWithdrawalValue).text =
                    txtBbvaWithDebitWithdrawalValue.text.toString()
            }
            val alertDialog = activity?.let {
                androidx.appcompat.app.AlertDialog.Builder(it).apply {
                    setTitle(RUtil.R_string(R.string.bbva_withdrawal_modal_title))
                    setView(mDialogView)
                    setCancelable(false)
                    setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton(RUtil.R_string(R.string.text_btn_make_withdrawal)) { dialog, _ ->
                        makeWithdrawal()
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

    private fun makeWithdrawal() {

        val requestBbvaDTO = RequestBbvaDTO().apply {

            this.value = if(isDebitCard ?:false) txtBbvaWithDebitWithdrawalValueConfirm.text.toString().resetValueFormat() else txtBbvaNoDebitWithdrawalValueConfirm.text.toString().resetValueFormat()
            this.numReceipt = "000091"
            this.rrn = "000586"
            this.concept = "100123"
            this.typeTransactionAdvisor = "I"
            this.descProduc = "DEPOSITOS TARJETA BBVA"

        }

        showDialogProgress(getString(R.string.text_load_data))

        viewModel.accountDeposit(requestBbvaDTO)

    }

    private fun withdrawalSuccess() {
        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_withdrawal_modal_success_title))
                setMessage(RUtil.R_string(R.string.bbva_withdrawal_modal_success_message))
                setCancelable(false)
                setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    private fun withdrawalDeclined() {
        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_withdrawal_modal_declined_title))
                setMessage(RUtil.R_string(R.string.bbva_withdrawal_modal_declined_message))
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
                setTitle(RUtil.R_string(R.string.bbva_withdrawal_modal_reprint_title))
                setMessage(RUtil.R_string(R.string.bbva_withdrawal_modal_reprint_message))
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

    private fun withdrawalStatus() {
        activity?.let {
            androidx.appcompat.app.AlertDialog.Builder(it).apply {
                setTitle(RUtil.R_string(R.string.bbva_withdrawal_modal_status_title))
                setMessage(RUtil.R_string(R.string.bbva_withdrawal_modal_status_message))
                setCancelable(false)
                setPositiveButton(RUtil.R_string(R.string.text_btn_accept)) { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }
    }

    private fun listCategoryType() {
        val list = ArrayList<String>()
        list.add("Categoría 1")
        list.add("Categoría 2")
        list.add("Categoría 3")
        list.add("Categoría 4")

        spinnerWithdrawalType.setItems(list)
    }

    private fun listWithdrawalType() {
        val list = ArrayList<String>()

        list.add(getString(R.string.bbva_withdrawal_type_placeholder))
        list.add("Sin tarjeta de débito")
        list.add("Con tarjeta de débito")

        spinnerWithdrawalType.setItems(list)
    }

    private fun listAccountType() {
        val list = ArrayList<String>()
        list.add("Corriente")
        list.add("Ahorro")

        spinnerNoDebitAccountType.setItems(list)
    }

    private fun validateFormGeneric(): Boolean {

        when (isDebitCard) {
            null -> {
                showErros(getString(R.string.bbva_withdrawal_type_label_error))
                return false
            }
            false -> return validateFormIsNotDebitCard()
            true -> return validateFormIsDebitCard()

        }
    }

    private fun showErros(msm: String) {
        hideDialogProgress()
        hideKeyboard()

        showOkAlertDialog("", msm)

    }

    private fun validateFormIsDebitCard(): Boolean {
        var isValid = true

        if (txtBbvaWithDebitWithdrawalValue.text.toString().isEmpty()) {
            labelErrorWithDebitWithdrawalValue.visibility = View.VISIBLE
            isValid = false
        } else
            labelErrorWithDebitWithdrawalValue.visibility = View.GONE


        if (txtBbvaWithDebitWithdrawalValueConfirm.text.toString().isEmpty()) {
            labelErrorWithDebitWithdrawalValueConfirm.visibility = View.VISIBLE
            isValid = false
        } else
            labelErrorWithDebitWithdrawalValueConfirm.visibility = View.GONE


        if (isValid) {

            if (txtBbvaWithDebitWithdrawalValue.text.toString() != txtBbvaWithDebitWithdrawalValueConfirm.text.toString()) {
                labelErrorWithDebitWithdrawalValueConfirmTwo.visibility = View.VISIBLE
                isValid = false
            } else
                labelErrorWithDebitWithdrawalValueConfirmTwo.visibility = View.GONE

        }

        return isValid
    }

    private fun validateFormIsNotDebitCard(): Boolean {
        var isValid = true


        if (txtBbvaNoDebitDocumentNumber.text.toString().isEmpty()) {
            labelErrorNoDebitDocumentNumber.visibility = View.VISIBLE
            isValid = false
        } else
            labelErrorNoDebitDocumentNumber.visibility = View.GONE


        if (spinnerNoDebitAccountType.text.toString().isEmpty()) {
            labelErrorNoDebitAccountType.visibility = View.VISIBLE
            isValid = false
        } else
            labelErrorNoDebitAccountType.visibility = View.GONE


        if (txtBbvaNoDebitWithdrawalValue.text.toString().isEmpty()) {
            labelErrorNoDebitWithdrawalValue.visibility = View.VISIBLE
            isValid = false
        } else
            labelErrorNoDebitWithdrawalValue.visibility = View.GONE


        if (txtBbvaNoDebitWithdrawalValueConfirm.text.toString().isEmpty()) {
            labelErrorNoDebitWithdrawalValueConfirm.visibility = View.VISIBLE
            isValid = false
        } else
            labelErrorNoDebitWithdrawalValueConfirm.visibility = View.GONE


        if (isValid) {

            if (txtBbvaNoDebitWithdrawalValue.text.toString() != txtBbvaNoDebitWithdrawalValueConfirm.text.toString()) {
                labelErrorNoDebitWithdrawalValueConfirmTwo.visibility = View.VISIBLE
                isValid = false
            } else
                labelErrorNoDebitWithdrawalValueConfirmTwo.visibility = View.GONE
        }

        return isValid
    }

}
