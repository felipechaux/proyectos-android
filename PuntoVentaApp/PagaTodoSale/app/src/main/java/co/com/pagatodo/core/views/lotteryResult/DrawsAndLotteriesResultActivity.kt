package co.com.pagatodo.core.views.lotteryResult

import android.annotation.SuppressLint
import android.app.DatePickerDialog

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.DrawsAndLotteriesResultAdapter
import co.com.pagatodo.core.views.adapters.LotteryResultAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_draws_and_lotteries_result.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import java.util.*


class DrawsAndLotteriesResultActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var queryViewModel: QueryViewModel
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draws_and_lotteries_result)
        setupViewModel()
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.lottery_result_title))
        setupDialogPicker()
        setupLotterySpinner()
        setupInitViewListeners()
        rvResult.apply {
            layoutManager = LinearLayoutManager(this@DrawsAndLotteriesResultActivity)
        }
    }

    private fun setupDialogPicker() {
        tvSelectedDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, R.style.DateDialogTheme, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }
    }

    private fun setupLotterySpinner(){
        spinnerLotteries.setItems(queryViewModel.getAllLotteries())
    }

    @SuppressLint("CheckResult")
    fun setupInitViewListeners() {
        btnNext.setOnClickListener {

            if( validateForm()){

                val drawString = convertDateToStringFormat(DateUtil.StringFormat.DDMMYY, calendar.time)
                val lotteryCode = if(spinnerLotteries.text.toString() != R_string(R.string.placeholder_lottery_result_select_one))
                    spinnerLotteries.text.toString().substringBefore("-")
                else null
                showDialogProgress(getString(R.string.message_dialog_request))
                queryViewModel.dispatchQueryLotteryResult(drawString, lotteryCode)

            }


        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validateForm():Boolean{

        var isValid = true

        if(tvSelectedDate.text.toString().isEmpty()){
            labelErrorSelectedDate.visibility = View.VISIBLE
            isValid = false
        }else{
            labelErrorSelectedDate.visibility = View.GONE
        }

       return isValid

    }

    private fun setupViewModel() {

        queryViewModel = ViewModelProviders.of(this).get(QueryViewModel::class.java)

         queryViewModel.singleLiveEvent.observe(this, Observer { event ->
             when (event) {
                   is QueryViewModel.ViewEvent.DrawsLotteryResultQuery -> {
                        if (event.result.isEmpty()) {
                            showOkAlertDialog("", R_string(R.string.message_no_results_found))
                        }else{
                            var listReverse = event.result.asReversed();
                            rvResult.adapter = DrawsAndLotteriesResultAdapter(listReverse)
                            viewSeparator.visibility = View.VISIBLE
                            titleListLotterys.visibility = View.VISIBLE
                        }
                        progressDialog.dismiss()
                    }
                }
            })
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateLabel()
    }

    private fun updateDateLabel() {
        labelErrorSelectedDate.visibility = View.GONE
        tvSelectedDate.text = SpannableStringBuilder(
            convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendar.time)
        )
    }
}
