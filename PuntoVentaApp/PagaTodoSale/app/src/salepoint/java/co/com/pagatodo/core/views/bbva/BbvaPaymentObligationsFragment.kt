package co.com.pagatodo.core.views.bbva


import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.salepoint.fragment_bbva_payment_obligations.*

class BbvaPaymentObligationsFragment : BaseFragment() {

    private lateinit var viewModel: BbvaViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bbva_payment_obligations, container, false)
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
        initListenersRWatcher()
    }

    private fun success(message: String) {
        hideDialogProgress()
        showOkAlertDialog("", message){
            onBackPressed(BbvaPaymentObligationsFragment())
        }
    }

    private fun loadErrors(errorMessage: String) {
        hideDialogProgress()
        showOkAlertDialog("", errorMessage)
    }

    private fun initListenersRWatcherconfirm() {

        var confirmDepositValue = true

        txtBbvaConfirmPaymentValue.addTextChangedListener(object : TextWatcher {

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

                    if (txtBbvaConfirmPaymentValue.text.toString().isNotEmpty()) {
                        labelErrorConfirmPaymentValue.visibility = View.GONE
                        try {
                            var originalString = txtBbvaConfirmPaymentValue.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()
                            txtBbvaConfirmPaymentValue.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue(value)
                                )
                            )
                            txtBbvaConfirmPaymentValue.setSelection(txtBbvaConfirmPaymentValue.text.length)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    } else {
                        labelErrorConfirmPaymentValue.visibility = View.VISIBLE
                    }

                    confirmDepositValue = true
                }


            }
        })

        txtBbvaReferenceNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {


                labelErrorReferenceNumber.visibility = View.GONE

            }
        })

    }

    private fun initListenersRWatcher (){

        var confirmDepositValue = true

        txtBbvaPaymentValue.addTextChangedListener(object : TextWatcher {

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

                    if (txtBbvaPaymentValue.text.toString().isNotEmpty()) {
                        labelErrorPaymentValue.visibility = View.GONE
                        try {
                            var originalString = txtBbvaPaymentValue.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value = originalString.toDouble()
                            txtBbvaPaymentValue.setText(
                                getString(
                                    R.string.text_label_parameter_coin,
                                    formatValue(value)
                                )
                            )
                            txtBbvaPaymentValue.setSelection(txtBbvaPaymentValue.text.length)

                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }
                    } else {
                        labelErrorPaymentValue.visibility = View.VISIBLE
                    }

                    confirmDepositValue = true
                }


            }
        })

        initListenersRWatcherconfirm()
    }

    private fun initListenersClicks() {

        btnPaymentBack.setOnClickListener { activity?.onBackPressed() }
        btnPay.setOnClickListener { confirmBillPayment() }

    }

    private fun confirmBillPayment() {

        if(validateForm()){

            val inflater = this.layoutInflater
            val mDialogView = inflater.inflate(R.layout.layout_bbva_confirm_payment_obligation, null)
            mDialogView.findViewById<TextView>(R.id.lblReferenceNumber).text = txtBbvaReferenceNumber.text
            mDialogView.findViewById<TextView>(R.id.lblTotalBillValue).text = txtBbvaConfirmPaymentValue.text
            val alertDialog = activity?.let {
                androidx.appcompat.app.AlertDialog.Builder(it).apply {
                    setTitle(RUtil.R_string(R.string.bbva_pay_obligation_modal_title))
                    setView(mDialogView)
                    setCancelable(false)
                    setNegativeButton(RUtil.R_string(R.string.text_btn_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton(RUtil.R_string(R.string.text_btn_pay)) { dialog, _ ->
                        dialog.dismiss()
                        dispatchPayObligations()
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

    private fun dispatchPayObligations() {

        val requestBbvaDTO = RequestBbvaDTO().apply {

            this.value = txtBbvaConfirmPaymentValue.text.toString().resetValueFormat()
            this.numReceipt = "000091"
            this.rrn = "000586"
            this.concept = "100123"
            this.descProduc = "PAGO OBLIGACION TARJETA BBVA"
            this.typeTransactionAdvisor = "I"

        }

        showDialogProgress(getString(R.string.text_load_data))
        viewModel.paymentObligations(requestBbvaDTO)



    }

    private fun validateForm(): Boolean {

        var isValid = true


        if (txtBbvaReferenceNumber.text.isEmpty()) {
            isValid = false
            labelErrorReferenceNumber.visibility = View.VISIBLE
        } else {
            labelErrorReferenceNumber.visibility = View.GONE
        }

        if (txtBbvaPaymentValue.text.isEmpty()) {
            isValid = false
            labelErrorPaymentValue.visibility = View.VISIBLE
        } else {
            labelErrorPaymentValue.visibility = View.GONE
        }

        if (txtBbvaConfirmPaymentValue.text.isEmpty()) {
            isValid = false
            labelErrorConfirmPaymentValue.visibility = View.VISIBLE
        } else {
            labelErrorConfirmPaymentValue.visibility = View.GONE
        }

        if(isValid){

            if (txtBbvaConfirmPaymentValue.text.toString() != txtBbvaPaymentValue.text.toString()) {
                isValid = false
                labelErrorConfirmPaymentValue.visibility = View.VISIBLE
            } else {
                labelErrorConfirmPaymentValue.visibility = View.GONE
            }


        }

        return isValid

    }

}
