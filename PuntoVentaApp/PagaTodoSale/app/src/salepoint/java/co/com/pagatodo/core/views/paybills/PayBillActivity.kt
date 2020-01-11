package co.com.pagatodo.core.views.paybills

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.BillModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.adapters.PayBillsAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.salepoint.activity_pay_bills.*
import kotlinx.android.synthetic.salepoint.dialog_pay_bills.view.*
import kotlinx.android.synthetic.salepoint.item_pay_bills.*
import kotlinx.android.synthetic.salepoint.layout_buttons_back_next.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PayBillActivity : BaseActivity() {

    private lateinit var viewModel: PayBillViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_bills)
        setupView()
        setupViewModel()
        initListenerViews()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.pay_bills))
        payBillList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
        initTextWatcher()
    }

    private fun initTextWatcher() {

        txtNumberRequested.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                // Not implemented
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not implemented
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int
            ) {
                if (txtNumberRequested.text.toString().isEmpty())
                    labelErrorNumberRequested.visibility = View.VISIBLE
                else
                    labelErrorNumberRequested.visibility = View.GONE

            }
        })

        labelErrorNumberRequested.visibility = View.GONE

    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(PayBillViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when(it){
                is PayBillViewModel.ViewEvent.ResponseSuccess -> {
                    showOkAlertDialog("", it.successMessage ?: R_string(R.string.text_pay_bill_success)){
                        finish()
                    }
                }
                is PayBillViewModel.ViewEvent.ResponseError -> {
                    showOkAlertDialog("",it.errorMessage ?: "")
                }
            }
        })

        viewModel.agreementList.observe(this, Observer {
            hideDialogProgress()
            val agreementList = mutableListOf<String>()
            agreementList.add(R_string(R.string.message_error_select_agreement))
            it.forEach {
                agreementList.add(it.description?.nameService ?: "")
            }

            setAgreementListAdapter(agreementList)
            setupBillAdapter(BillModel())
        })

        viewModel.bill.observe(this, Observer {

            hideDialogProgress()

            if(it.serviceId != null){
                setupBillAdapter(it)
            }else{
                showOkAlertDialog("",getString(R.string.pay_bill_error_search))
            }

        })

        showDialogProgress(getString(R.string.text_load_data))
        viewModel.dispatchGetAgreements()
    }

    private fun initListenerViews() {
        btnSearch.setOnClickListener { getBill() }
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { payBill() }
    }

    private fun isValidForm(): Boolean {
        hideLabelErrors()
        var isValid = true

        if(spinner_agreements.selectedIndex == 0) {
            labelErrorAgreements.visibility = View.VISIBLE
            isValid = false
        }

        if(txtNumberRequested.text.isEmpty()) {
            labelErrorNumberRequested.visibility = View.VISIBLE
            isValid = false
        }
        else {
            labelErrorNumberRequested.visibility = View.GONE
        }

        if(viewModel.getBill() == null ){
            showOkAlertDialog("", "Error, consulte una factura")
            isValid = false
        }

        else{
            if(viewModel.getBill()?.billService != null && viewModel.getBill()!!.billService?.isPartialPayment!! && viewModel.getBill()!!.valueToPay!!.toString().toDouble() > viewModel!!.getBill()!!.billValue!!.toDouble()) {
                showOkAlertDialog("", "El valor a pagar debe ser menor al valor de la factura")
                isValid = false
            }
        }
        


        return isValid
    }

    private fun isValidSearch(): Boolean {
        hideLabelErrors()
        var isValid = true

        if(spinner_agreements.selectedIndex == 0) {
            labelErrorAgreements.visibility = View.VISIBLE
            isValid = false
        }

        if(txtNumberRequested.text.isEmpty()) {
            labelErrorNumberRequested.visibility = View.VISIBLE
            isValid = false
        }
        else {
            labelErrorNumberRequested.visibility = View.GONE
        }

        return isValid
    }

    private fun getBill(){
        DeviceUtil.hideKeyboard(txtNumberRequested, this)
        if (isValidSearch()){
            showDialogProgress(getString(R.string.text_load_data))
            val description = viewModel.getAgreementDescription(spinner_agreements.selectedIndex - 1)
            viewModel.dispatchGetBill(description?.idCompanyService ?: "", spinner_agreements.text.toString(),txtNumberRequested.text.toString())
        }
    }

    private fun payBill(){
        if(isValidForm())
            showConfirmationDialog()

    }

    private fun setAgreementListAdapter(items: List<String>){
        spinner_agreements.setItems(items)
        spinner_agreements.setOnItemSelectedListener{_,position,_,_ ->
            labelErrorAgreements.visibility = View.GONE
            if (position != 0) {
                val description = viewModel.getAgreementDescription(position-1)
                setupBillAdapter(BillModel())
                txtNumberRequested.setText("")
                lbDocument.text =  description?.startLabel
                initTextWatcher()
            }
        }
    }

    private fun setupBillAdapter(billModel: BillModel){
        val billList = mutableListOf<BillModel>()
        billList.add(billModel)
        val adapter = PayBillsAdapter(billList,viewModel)
        payBillList.adapter = adapter
    }

    private fun showConfirmationDialog(){
        val description = viewModel.getAgreementDescription(spinner_agreements.selectedIndex)

        val bill = viewModel.getBill()

        if (bill != null && bill.serviceId != "0" ){

            if(bill.valueToPay?.isEmpty()?:true || bill.valueToPay == "0"){
                showOkAlertDialog("", getString(R.string.pay_bills_value_pay_error))
                return
            }

            PaymentConfirmationDialog().show(this, R.layout.dialog_pay_bills,
                R_string(R.string.title_ticket_pay_bills), null, { view ->
                    view.textBilledService.text = spinner_agreements.text
                    view.textUserCode.text = txtNumberRequested.text
                    view.textUserName.text = bill.userName
                    view.textBillNumber.text = bill.flatIdBill
                    view.textBillDate.text = bill.expirationDate
                    view.textBillValue.text = "$${formatValue((bill.billValue ?: "").toDouble())}"
                    view.textTotalValue.text = "$${formatValue((bill.valueToPay ?: "").toDouble())}"
                },{
                    showDialogProgress(getString(R.string.message_dialog_request))

                    viewModel.dispatchPayBill(
                        description?.idCompanyService ?: "",
                        txtNumberRequested.text.toString(),
                        spinner_agreements.text.toString())
                })
        }else{

            showOkAlertDialog("", "Error, consulte una factura")

        }
    }

    private fun hideLabelErrors(){
        labelErrorAgreements.visibility = View.GONE
        labelErrorNumberRequested.visibility = View.GONE
    }


}