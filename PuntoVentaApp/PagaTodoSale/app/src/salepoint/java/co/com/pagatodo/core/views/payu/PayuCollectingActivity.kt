package co.com.pagatodo.core.views.payu

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.PayuModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.fragment_giro_payment.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.btnBack
import kotlinx.android.synthetic.main.layout_buttons_back_next.btnNext
import kotlinx.android.synthetic.salepoint.activity_payu_collecting.*
import kotlinx.android.synthetic.salepoint.dialog_pay_u_collecting.view.*

class PayuCollectingActivity : BaseActivity() {

    private lateinit var viewModel: PayuViewModel
    private  var mPayuModel: PayuModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payu_collecting)
        setupView()
        setupViewModel()
        initListeners()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.menu_pay_u_collecting))
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
                    //TODO: Replace "Test value" for real value of collect next line
                    showOkAlertDialog(getString(R.string.title_success_pay_u_collecting),getString(R.string.message_dialog_success_pay_u_collecting, "Test value")){
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

        btnQuery.setOnClickListener { getPayu() }
        btnNext.setOnClickListener { collectPayu() }
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

    private fun getPayu(){
        if (isValidForm()){
            showDialogProgress(getString(R.string.text_load_data))
            viewModel.dispatchGetPayu(txtRefNumber.text.toString())
            val inputManager: InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }

    private fun payPayu(){
        if (isValidForm()){
            showDialogProgress(getString(R.string.text_load_data))
            viewModel.dispatchGetPayu(mPayuModel!!)
        }
    }

    private fun collectPayu(){
        if(isValidForm()) {
            if (txtUserName.text.toString() != getString(R.string.text_dash)
                && txtReference.text.toString() != getString(R.string.text_dash)
                && txtUserDoc.text.toString() != getString(R.string.text_dash)
                && txtValueToPay.text.toString() != getString(R.string.text_dash)) {

                showPayDialog()

            } else {
               showOkAlertDialog("","error")

            }
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

    private fun showPayDialog(){
        PaymentConfirmationDialog().show(this, R.layout.dialog_pay_u_collecting,
            R_string(R.string.title_dialog_pay_u_collecting), null, { view ->

                view.tvRefNumber.text = mPayuModel?.referenceNumber
                view.tvUserName.text = mPayuModel?.userName
                view.tvUserDoc.text = mPayuModel?.userDocument
                view.tvValue.text = mPayuModel?.valueToPay
            },{
                showDialogProgress(getString(R.string.message_dialog_request))
                payPayu()
            })
    }


}
