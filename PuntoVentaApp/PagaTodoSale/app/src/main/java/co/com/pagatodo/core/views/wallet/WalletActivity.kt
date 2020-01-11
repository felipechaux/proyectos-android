package co.com.pagatodo.core.views.wallet

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.response.ResponseWalletModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DateUtil.Companion.addBackslashToStringDate
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.OkInformationConfirmDialog
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_info_confirm_wallet.view.*
import java.util.*

class WalletActivity : BaseActivity() {

    private lateinit var viewModel: WalletViewModel
    private val calendar = Calendar.getInstance()
    private var isSelectedDate:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setupView()
        setupModel()
    }

    private fun setupView(){
        title = getString(R.string.wallet_title)
        setupBaseView()
        updateTitle(title as String)
        btnNext.setText("> ${R_string(R.string.text_btn_consult)}")
        tvWalletNumberSeller.setText(getPreference<String>(R_string(R.string.shared_key_seller_code)))
        setupDatePickerDialog()
        setupClickListenersView()
    }

    private fun setupClickListenersView() {
        btnNext.setOnClickListener { consultWallet() }
        btnBack.setOnClickListener { finish() }
    }

    private fun setupModel(){
        viewModel = ViewModelProviders.of(this).get(WalletViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            when(it){
                is WalletViewModel.ViewEvent.ResponseError -> {
                    showOkAlertDialog("", it.errorMessage ?: R_string(R.string.message_error_transaction))
                }
            }
        })
        viewModel.walletLiveData.observe(this, Observer<ResponseWalletModel>{
            hideDialogProgress()
            setupInfoInDialog(it)
        })
    }

    private fun setupDatePickerDialog() {
        tvWalletDate.setOnClickListener {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog =  DatePickerDialog(this, R.style.DateDialogTheme, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                labelErrorDateFilter.visibility = View.GONE
                tvWalletDate.text = convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendar.time)
                isSelectedDate = true
            }, year, month, day)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
    }

    private fun consultWallet() {
        labelErrorDateFilter.visibility = View.GONE
        if (!tvWalletNumberSeller.text.isNullOrEmpty() && isSelectedDate) {
            val sellerCode = tvWalletNumberSeller.text.toString()
            val dateQuery = convertDateToStringFormat(DateUtil.StringFormat.DDMMYY, calendar.time)
            showDialogProgress(getString(R.string.message_dialog_request))
            viewModel.fetchWallet(dateQuery, sellerCode)
        } else {
            labelErrorDateFilter.visibility = View.VISIBLE
        }
    }

    private fun setupInfoInDialog(response: ResponseWalletModel) {
        val title = getString(R.string.dialog_wallet_title)
        OkInformationConfirmDialog().show(this, R.layout.layout_info_confirm_wallet, title, null, { view ->
                view.lblTransactionDate.text = response.transactionDate?.let { addBackslashToStringDate(it) }
                view.lblTransactionHour.text = DateUtil.convertMilitaryHourToNormal(DateUtil.StringFormat.HHMMA,response.transactionHour ?: "")
                view.lblArea.text = response.area

                view.lblSellerSale.text = getString(
                    R.string.text_label_parameter_coin,
                    formatValue(response.sellerSale.toDouble())
                )
            view.lblSellerCollection.text = getString(
                R.string.text_label_parameter_coin,
                formatValue(response.sellerCollection.toDouble())
            )


            },{
                finish()
            })
    }
}
