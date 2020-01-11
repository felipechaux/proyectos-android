package co.com.pagatodo.core.views.recharge

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.request.RequestRechargeHistoryModel
import co.com.pagatodo.core.data.model.response.ResponseRechargeHistoryModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.views.adapters.RechargeAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_recharge_history.*
import java.text.SimpleDateFormat
import java.util.*

class RechargeHistoryActivity : BaseActivity() {

    private lateinit var rechargeViewModel: RechargeViewModel
    private lateinit var rechargeHistory: ResponseRechargeHistoryModel

    private var showItems: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge_history)
        setupView()
    }

    private fun setupView() {
        title = if(!DeviceUtil.isSalePoint())
            getString(R.string.recharge_history_title)
        else
            getString(R.string.recharge_history_title_pdv)
        setupBaseView()
        updateTitle(title as String)
        setupViewModel()
        initListenersViews()
        txtDateRecharge.setOnClickListener {
            showDialogDatePicker()
        }
        recyclerItems.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupViewModel() {
        rechargeViewModel = ViewModelProviders.of(this).get(RechargeViewModel::class.java)
        initSubscriptions()
    }

    private fun initListenersViews() {
        txtNumberRecharge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorNumber.visibility = View.GONE
            }
        })

        txtDateRecharge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                labelErrorDate.visibility = View.GONE
            }
        })

        btnConsult.setOnClickListener { checkRecharge() }
    }

    private fun initSubscriptions() {
        rechargeViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is RechargeViewModel.ViewEvent.ResponseRechargeHistory -> {
                    rechargeHistory = it.rechargeHistoryModel
                    hideDialogProgress()
                    if(rechargeHistory.success) {
                        if (rechargeHistory.recharges?.isNotEmpty() ?: false){
                            showRechargeResults(rechargeHistory)
                        }else{
                            showOkAlertDialog("",R_string(R.string.text_label_empty_recharge))
                        }
                    } else {
                        showConfirmAlertDialog(rechargeHistory.message)
                    }
                }
                is RechargeViewModel.ViewEvent.ResponseError -> {
                    if (it.errorMessage != null) {
                        showConfirmAlertDialog(it.errorMessage)
                    }
                    progressDialog.dismiss()
                }
            }
        })
    }

    private fun showRechargeResults(recharge:ResponseRechargeHistoryModel){
        lineDivider.visibility = View.VISIBLE
        recyclerItems.visibility = View.VISIBLE
        val adapter = RechargeAdapter(recharge.recharges ?: arrayListOf(), this)
        recyclerItems.adapter = adapter
        recyclerItems.addItemDecoration(SimpleDividerItemDecoration(this))
    }

    private fun checkRecharge() {
        if(validateFields()) {
            showDialogProgress(getString(R.string.message_dialog_request))
            rechargeViewModel.checkRecharge(createRequestRechargeHistoryModel())
        }
    }

    private fun createRequestRechargeHistoryModel(): RequestRechargeHistoryModel {
        return RequestRechargeHistoryModel().apply {
            number = txtNumberRecharge.text.toString()
            date = txtDateRecharge.text.toString().replace("/","")
            userType = getPreference(R_string(R.string.shared_key_user_type))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = getPreference(R_string(R.string.code_product_recharge))
        }
    }

    private fun validateFields(): Boolean {
        var isValidFields = true
        hideErrorMessages()
        if(txtNumberRecharge.text.isEmpty()) {
            labelErrorNumber.visibility = View.VISIBLE
            isValidFields = false
        }
        if(txtDateRecharge.text.isEmpty()) {
            labelErrorDate.visibility = View.VISIBLE
            isValidFields = false
        }
        return isValidFields
    }

    private fun hideErrorMessages(){
        labelErrorNumber.visibility = View.GONE
        labelErrorDate.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogDatePicker() {
        val calendar = Calendar.getInstance()
        val formate = SimpleDateFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH.rawValue, Locale.getDefault())
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val datePicker = DatePickerDialog(this, R.style.DateDialogTheme, DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val date = formate.format(calendar.time)
            txtDateRecharge.text = date
        }, currentYear, currentMonth, currentDay)
        datePicker.datePicker.maxDate = calendar.timeInMillis
        datePicker .show()
    }

    private fun showConfirmAlertDialog(message: String?) {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setMessage(message)
                setCancelable(false)
                setPositiveButton(getString(R.string.text_btn_accept)){ _, _ ->
                    finish()
                }
            }.show()
        }
    }
}