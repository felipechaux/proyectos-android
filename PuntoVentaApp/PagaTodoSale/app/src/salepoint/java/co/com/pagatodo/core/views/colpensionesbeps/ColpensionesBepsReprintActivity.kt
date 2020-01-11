package co.com.pagatodo.core.views.colpensionesbeps

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.request.RequestColpensionesBepsQueryCollectionModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.salepoint.activity_colpensiones_beps_reprint.*

class ColpensionesBepsReprintActivity : BaseActivity() {

    private lateinit var viewModel: ColpensionesBepsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colpensiones_beps_reprint)
        setupView()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(getString(R.string.reprit_colpensiones_beps_title))
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel(){
        viewModel = ViewModelProviders.of(this).get(ColpensionesBepsViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when(it){
                is ColpensionesBepsViewModel.ViewEvent.ResponseSuccess -> {
                    showOkAlertDialog("",it.message ?: ""){
                        finish()
                    }
                }
                is ColpensionesBepsViewModel.ViewEvent.ResponseError -> {
                    showOkAlertDialog("", it.message ?: "")
                }
            }
        })
        viewModel.dispatchGetProductParameters()
    }

    private fun initListenersViews(){
        spinnerTransactionType.setItems(viewModel.arrayTypeTransaction)
        spinnerTransactionType.setOnItemSelectedListener { _, _, _, _ ->
            labelErrorSelectTransactionType.visibility = View.GONE
        }
        txtTransactionNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                labelErrorTransactionNumber.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        btnReprint.setOnClickListener { showConfirmationDialog() }
    }

    private fun validateFields(): Boolean {
        var isSuccess = true

        if (spinnerTransactionType.text.isEmpty() ){
            labelErrorSelectTransactionType.visibility = View.VISIBLE
            isSuccess = false
        }

        if (txtTransactionNumber.text.isEmpty()){
            labelErrorTransactionNumber.visibility = View.VISIBLE
            isSuccess = false
        }

        return isSuccess
    }

    private fun showConfirmationDialog(){
        hideKeyboard()
        if (validateFields()){
            showAlertDialog(R_string(R.string.title_confirm_reprint), R_string(R.string.text_confirm_reprint),{
                showDialogProgress(R_string(R.string.message_dialog_request))
                viewModel.dispatchQueryCollection(createQueryRequest())
            },{})
        }
    }

    private fun createQueryRequest(): RequestColpensionesBepsQueryCollectionModel {
        return RequestColpensionesBepsQueryCollectionModel().apply {
            transactionNumber = txtTransactionNumber.text.toString()
            queryType = spinnerTransactionType.text.toString()
        }
    }
}
