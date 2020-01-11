package co.com.pagatodo.core.views.currentbox

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.data.model.request.RequestUtilModel
import co.com.pagatodo.core.data.model.response.ResponseCurrentBoxModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.PrinterStatusInfo
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.CurrentBoxAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_current_box.*
import okhttp3.internal.Util
import java.util.*

class CurrentBoxActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var currentBoxViewModel: CurrentBoxViewModel
    private lateinit var responseModel: ResponseCurrentBoxModel

    private val calendar = Calendar.getInstance()
    private var isCurrentBox = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_box)
        isCurrentBox = intent.extras?.get(R_string(R.string.bundle_is_currentbox)) as Boolean
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        if(isCurrentBox) {
            updateTitle(R_string(R.string.currentbox_title))
        }else{
            btnPrint.visibility = View.GONE
            updateTitle(R_string(R.string.reportsales_title))
        }
        setListenersClick()
        setupViewModel()
        initSubscriptions()
    }

    private fun setListenersClick() {
        btnPrint.setOnClickListener {

            showDialogProgress(getString(R.string.text_load_data))
            isForeground = false

            if(!DeviceUtil.isSalePoint())
                currentBoxViewModel.launchUpdateStub()
            else
                currentBoxViewModel.printCurrentBox(currentBoxViewModel.rechargeLiveDataCurrentBoxSuccess.value?: ResponseCurrentBoxModel(),"" )

        }

        val datePickerDialog = DatePickerDialog(this, R.style.DateDialogTheme, this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val aDayInMillis = (1000*60*60*24)
        val yesterday = System.currentTimeMillis() - aDayInMillis
        datePickerDialog.datePicker.maxDate = yesterday
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        txtDate.setOnClickListener {
            datePickerDialog.show()
        }
    }

    private fun setupViewModel() {
        currentBoxViewModel = ViewModelProviders.of(this).get(CurrentBoxViewModel::class.java)
        if(isCurrentBox) {
            showDialogProgress(getString(R.string.message_dialog_request))
            currentBoxViewModel.getCurrentBox()
        }
        else {
            layoutSearchView.visibility = View.VISIBLE
        }
    }

    private fun initSubscriptions() {
        currentBoxViewModel.rechargeLiveDataCurrentBoxSuccess.observe(this, Observer {
            hideDialogProgress()
            if(it.productsBox.isNullOrEmpty() || it.productsBox?.count()==0) {
                lblTitle.visibility = View.VISIBLE
                btnPrint.visibility = View.GONE

                showOkAlertDialog("",getString(R.string.current_box_not_sale)){
                    isForeground = false
                    finish()
                }

            } else {
                responseModel = it
                showInformation(responseModel.productsBox)
            }
        })
        currentBoxViewModel.rechargeLiveDataErrorResponse.observe(this, Observer {
            hideDialogProgress()
            lblTitle.text = it.errorMessage
            showOkAlertDialog("",it.errorMessage ?: ""){
                finish()
            }
        })
    }

    private fun showInformation(productsBox: List<ProductBoxModel>?) {
        lblTitle.text = getString(R.string.text_label_parameter_total_value, responseModel.totalSale)
        lblTitle.visibility = View.VISIBLE
        setupRecycler(productsBox)
        if(isCurrentBox) {
            btnPrint.visibility = View.VISIBLE
        }
    }

    private fun setupRecycler(listItems: List<ProductBoxModel>?) {
        recyclerItemsCurrentBox.apply {
            layoutManager = LinearLayoutManager(this@CurrentBoxActivity)
            setHasFixedSize(true)
        }
        val chanceAdapter = listItems?.let { CurrentBoxAdapter(it) }
        recyclerItemsCurrentBox.adapter = chanceAdapter
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateLabel()
    }

    private fun updateDateLabel() {
        txtDate.text = SpannableStringBuilder(DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendar.time))
        showDialogProgress(getString(R.string.message_dialog_request))
        currentBoxViewModel.getCurrentBox(txtDate.text.toString().replace("/", ""))
    }

}