package co.com.pagatodo.core.views.colpensionesbeps

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.request.RequestColpensionesBepsMakeCollectionModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.salepoint.activity_colpensiones_beps.*
import kotlinx.android.synthetic.salepoint.dialog_pay_colpensiones_beps.view.*
import java.util.*

class ColpensionesBepsActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: ColpensionesBepsViewModel
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colpensiones_beps)
        setupView()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(getString(R.string.colpensiones_beps_title))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel(){
        viewModel = ViewModelProviders.of(this).get(ColpensionesBepsViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when(it){
                is ColpensionesBepsViewModel.ViewEvent.ResponseSuccess -> {
                    // not implemented
                }
                is ColpensionesBepsViewModel.ViewEvent.ResponseError -> {
                    showOkAlertDialog("", it.message ?: "")
                }
                is ColpensionesBepsViewModel.ViewEvent.LoadParameters -> loadParameter(
                    it.documents
                )
            }
        })
        viewModel.dispatchGetProductParameters()

    }

    private fun initListenersViews(){
        txtDocument.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorTextDocument.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // not implemented
            }
        })

        txtBirthdate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorBirthdate.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // not implemented
            }
        })

        txtBirthdate.setOnClickListener {
            showDateDialog()
        }

        imgCalendar.setOnClickListener {
            showDateDialog()
        }

        txtInputValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorTextInputValue.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // not implemented
            }
        })

        txtConfirmInputValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorConfirmInputValue.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // not implemented
            }
        })

        txtInputValue.addTextChangedListener(AddCurrencySymbolTextWatcher(txtInputValue,
            object : AddCurrencySymbolTextWatcherListener {
                override fun onTextChange(number: String) {
// not implemented
                }
            }))

        txtConfirmInputValue.addTextChangedListener(AddCurrencySymbolTextWatcher(txtConfirmInputValue,
            object : AddCurrencySymbolTextWatcherListener {
                override fun onTextChange(number: String) {
// not implemented
                }
            }))

        txtPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorPhoneNumber.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // not implemented
            }
        })

        spinnerDocumentType.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // not implemented
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorDocument.visibility=View.GONE
            }
        })

        viewModel.getParameter()

        containerClear.setOnClickListener{ clearFields() }
        btnNext.setOnClickListener { showConfirmationDialog() }
        btnBack.setOnClickListener { finish() }
    }

    private fun loadParameter(listDocuments: List<String>) {
        spinnerDocumentType.setItems(listDocuments)
        spinnerDocumentType.selectedIndex =
            listDocuments.indexOf(getString(R.string.DOCUMENT_TYPE_DEFAULT))
    }

    private fun showDateDialog(){
        val datePickerDialog = DatePickerDialog(this, R.style.DateDialogTheme, this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }

    private fun clearFields(){
        spinnerDocumentType.selectedIndex = 0
        txtDocument.setText("")
        txtBirthdate.setText("")
        txtInputValue.setText("")
        txtConfirmInputValue.setText("")
        txtPhoneNumber.setText("")
    }

    private fun showConfirmationDialog(){
        if (validateFields()){
            PaymentConfirmationDialog().show(this, R.layout.dialog_pay_colpensiones_beps,
                R_string(R.string.title_dialog_pay_title_colpensiones), null, { view ->
                    view.tvDocument.text = txtDocument.text.toString()
                    view.tvPhone.text = if(txtPhoneNumber.text.isNotEmpty()) txtPhoneNumber.text.toString() else "-"
                    view.tvValue.text = txtInputValue.text.toString()
                },{
                    showDialogProgress(getString(R.string.message_dialog_request))
                    viewModel.dispatchMakeCollection(createMakeCollectionRequest())
                })
        }
    }

    private fun createMakeCollectionRequest(): RequestColpensionesBepsMakeCollectionModel{
        val inputValue = txtInputValue.text.toString().replace("$","").replace(".","")
        val date = txtBirthdate.text.toString().split("/")

        return RequestColpensionesBepsMakeCollectionModel().apply {
            value = inputValue
            documentType = spinnerDocumentType.text.toString()
            documentNumber = txtDocument.text.toString()
            birthDay = date[0]
            birthMonth = date[1]
            phone = txtPhoneNumber.text.toString()
        }
    }

    private fun validateFields(): Boolean{

        var isSuccess = true


        if (txtDocument.text.isEmpty()){
            labelErrorTextDocument.visibility = View.VISIBLE
            isSuccess = false
        }
        if (txtBirthdate.text.isEmpty()){
            labelErrorBirthdate.visibility = View.VISIBLE
            isSuccess = false
        }
        if (txtInputValue.text.isEmpty()){
            labelErrorTextInputValue.visibility = View.VISIBLE
            isSuccess = false
        }
        if (txtConfirmInputValue.text.isEmpty()){
            labelErrorConfirmInputValue.visibility = View.VISIBLE
            isSuccess = false
        }
        if (!txtConfirmInputValue.text.toString().equals(txtInputValue.text.toString())){
            labelErrorConfirmInputValue.visibility = View.VISIBLE
            isSuccess = false
        }
        if (txtPhoneNumber.text.isNotEmpty()){

            if(!txtPhoneNumber.text.startsWith(R_string(R.string.text_number_three)) || txtPhoneNumber.text.length < 10 ) {
                labelErrorPhoneNumber.visibility = View.VISIBLE
                isSuccess = false
            }
        }else{
            labelErrorPhoneNumber.visibility = View.VISIBLE
            isSuccess = false
        }

        if(isSuccess && txtConfirmInputValue.text.toString().resetValueFormat().toInt() < viewModel.minValue){

            val msm = "El valor mínimo de aporte es $${formatValue(viewModel.minValue.toDouble())}"
            showOkAlertDialog("",msm)
            isSuccess = false
        }

        return isSuccess
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        txtBirthdate.text = SpannableStringBuilder(
            convertDateToStringFormat(DateUtil.StringFormat.DDMM_SPLIT_BACKSLASH, calendar.time)
        )
    }

}
