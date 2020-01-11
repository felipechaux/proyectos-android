package co.com.pagatodo.core.views.payu

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.PayuModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.salepoint.activity_payu_collecting.*
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.*
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.btnDelete
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.btnQuery
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.labelErrorRefNumber
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.txtRefNumber
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.txtReference
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.txtUserDoc
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.txtUserName
import kotlinx.android.synthetic.salepoint.activity_payu_reprint.txtValueToPay
import kotlinx.android.synthetic.salepoint.dialog_pay_u_collecting.view.*

class PayuReprintActivity : BaseActivity() {

    private lateinit var viewModel: PayuViewModel

    private var mPayuModel: PayuModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payu_reprint)
        setupView()
        setupViewModel()
        initListeners()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.menu_pay_u_reprint))
        txtReference.text = getString(R.string.text_dash)
        txtUserName.text = getString(R.string.text_dash)
        txtUserDoc.text = getString(R.string.text_dash)
        txtValueToPay.text = getString(R.string.text_dash)
    }

    private fun setupViewModel(){
        viewModel = ViewModelProviders.of(this).get(PayuViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when(it){
                is PayuViewModel.ViewEvent.ResponseError -> {
                    showOkAlertDialog("", it.errorMessage ?: "")
                }
                is PayuViewModel.ViewEvent.ResponseSuccess -> {
                    showOkAlertDialog(getString(R.string.title_success_pay_u_collecting),getString(R.string.message_dialog_success_pay_u_collecting, "Un valor de prueba")){
                        finish()
                    }
                }
                is PayuViewModel.ViewEvent.QuerySuccess -> loadQuery (it.payuModel)

            }
        })

        viewModel.payu.observe(this, Observer {
            hideDialogProgress()

            txtReference.text = it.referenceNumber
            txtUserName.text = it.userName
            txtUserDoc.text = it.userDocument
            txtValueToPay.text = it.valueToPay

        })
    }

    private fun loadQuery(payuModel: PayuModel?) {
        val valor = payuModel?.valueToPay
        hideDialogProgress()
        txtReference.text = payuModel?.referenceNumber
        txtUserName.text = payuModel?.userName
        txtUserDoc.text = payuModel?.userDocument
        txtValueToPay.text = "$ ${valor?.toDouble()?.let { formatValue(it) }}"

        mPayuModel = payuModel

    }

    private fun initListeners(){

        btnQuery.setOnClickListener { getPayUCollecting() }
        btnNext.setOnClickListener {
            rePrintCollectPayu()
        }
        btnBack.setOnClickListener { finish() }
        btnDelete.setOnClickListener{ clearForm() }

        txtRefNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorRefNumber.visibility = View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun clearForm() {
        setupView()
    }

    private fun rePrintPayPayu(){
        if (isValidForm()){
            showDialogProgress(getString(R.string.text_load_data))
            viewModel.reprintPayu(mPayuModel!!)
        }
    }

    private fun rePrintCollectPayu(){
        if(isValidForm()) {
            if (txtUserName.text.toString() != getString(R.string.text_dash)
                && txtReference.text.toString() != getString(R.string.text_dash)
                && txtUserDoc.text.toString() != getString(R.string.text_dash)
                && txtValueToPay.text.toString() != getString(R.string.text_dash)) {
                showRePrintDialog()
            } else {
                showOkAlertDialog("","error")
            }
        }
    }

    private fun getPayUCollecting(){
        if (isValidForm()){
            showDialogProgress(getString(R.string.text_load_data))
            viewModel.dispatchQueryReprintPayu(txtRefNumber.text.toString())

            hideKeyboard()
        }
    }

    private fun hideLabelErrors(){
        labelErrorRefNumber.visibility = View.GONE
    }

    private fun isValidForm(): Boolean {
        hideLabelErrors()
        var isValid = true

        if(txtRefNumber.text.isNullOrEmpty()){
            labelErrorRefNumber.visibility = View.VISIBLE
            isValid = false
        }

        return isValid
    }

    private fun showRePrintDialog(){

        showOkAlertDialog(getString(R.string.pay_u_title_confirm_reprint),getString(R.string.pay_u_confirm_reprint)){
            rePrintPayPayu()
        }

    }
}
