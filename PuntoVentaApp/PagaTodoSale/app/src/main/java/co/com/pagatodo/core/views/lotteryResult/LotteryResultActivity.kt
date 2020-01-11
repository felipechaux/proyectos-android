package co.com.pagatodo.core.views.lotteryResult

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseCheckResultLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResultsDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.adapters.LotteryResultAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_lottery_result.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import java.util.*

class LotteryResultActivity : BaseActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var queryViewModel: QueryViewModel
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottery_result)
        setupViewModel()
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.lottery_result_title_secos))
        setupDialogPicker()
        setupLotterySpinner()
        setupInitViewListeners()

        spinnerLotteries.setOnItemSelectedListener { view, position, id, item ->
            labelErrorSelectedLottery.visibility = View.GONE
        }

        rvResult.apply {
            layoutManager = LinearLayoutManager(this@LotteryResultActivity)
        }
    }

    private fun setupDialogPicker() {
        tvSelectedDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, R.style.DateDialogTheme, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
            labelErrorSelectedDate.visibility = View.GONE
        }
    }

    private fun setupLotterySpinner() {
        spinnerLotteries.setItems(queryViewModel.getAllLotteries())
    }

    @SuppressLint("CheckResult")
    fun setupInitViewListeners() {
        btnNext.setOnClickListener {

            if (validateFrom()) {
                val drawString =
                    convertDateToStringFormat(DateUtil.StringFormat.DDMMYY, calendar.time)
                val lotteryCode =
                    if (spinnerLotteries.text.toString() != R_string(R.string.placeholder_lottery_result_select_one))
                        spinnerLotteries.text.toString().substringBefore("-")
                    else null
                showDialogProgress(getString(R.string.message_dialog_request))
                queryViewModel.dispatchQueryLotteryResult(drawString, lotteryCode?:"")
            }

        }
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewModel() {
        queryViewModel = ViewModelProviders.of(this).get(QueryViewModel::class.java)
        queryViewModel.singleLiveEvent.observe(this, Observer {
            when (it) {
                is QueryViewModel.ViewEvent.LotteryResultQuery -> setupResultLottery(it.result)
                is QueryViewModel.ViewEvent.ResponseError -> errors(it.errorMessage?:"")
            }
        })
    }

    private fun setupResultLottery(result: ResponseCheckResultLotteryDTO) {

        val namePrize = result.results?.filter { it.namePrize?.toUpperCase(Locale.getDefault()) == getString(R.string.lottery_result_filter)}

        if(namePrize?.count() !=0){

            itemLotteryNumber.setText(namePrize?.last()?.number)

            val value = "$${formatValue(namePrize?.last()?.value?.toDouble()?:0.0)}"
            itemLotteryJackpot.setText(value)
            itemLotterySerie.setText(namePrize?.last()?.serie)
        }


        val listSecos = result.results?.filter { it.namePrize?.toUpperCase(Locale.getDefault()) != getString(R.string.lottery_result_filter)}

        val listSecosStr = listSecos?.map { it.namePrize }?.groupBy{it}?.map { it.key!! }?: arrayListOf<String>()

        val items = mutableListOf<String>()

        items.add(getString(R.string.text_lottery_result_seco_select).replace(":",""))
        items.addAll(listSecosStr)

        spinnerSecos.setItems(items)

        spinnerSecos.setOnItemSelectedListener { view, position, id, item ->

            if(position >0){
                val subSecos = listSecos?.filter { it.namePrize == item }
                itemLotteryQuantity.setText(subSecos?.first()?.quantity)
                rvResult.adapter = LotteryResultAdapter(subSecos?: arrayListOf())
                calculateHeightRc(subSecos?: arrayListOf())

            }else{
                itemLotteryQuantity.setText("")
                rvResult.adapter = LotteryResultAdapter(arrayListOf())
                calculateHeightRc(arrayListOf())
            }

        }

        hideDialogProgress()
    }


    private fun calculateHeightRc(result:List<ResultsDTO>) {
        val params = rvResult.getLayoutParams()

        if (result.size > 3) {

            if (!DeviceUtil.isSalePoint())
                params.height = 60 * result.size

        }else{
            if (!DeviceUtil.isSalePoint())
                params.height = 150
        }

    }

    private fun errors(errorMessage: String) {

        hideDialogProgress()
        showOkAlertDialog("", errorMessage)

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateLabel()
    }

    private fun updateDateLabel() {
        tvSelectedDate.text = SpannableStringBuilder(
            convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendar.time)
        )
    }

    private fun validateFrom(): Boolean {

        var isValid = true

        if (tvSelectedDate.text.toString().isEmpty()) {

            isValid = false
            labelErrorSelectedDate.visibility = View.VISIBLE
        } else {
            labelErrorSelectedDate.visibility = View.GONE
        }

        if (spinnerLotteries.selectedIndex == 0){

            isValid = false
            labelErrorSelectedLottery.visibility = View.VISIBLE
        } else {
            labelErrorSelectedLottery.visibility = View.GONE
        }



        return isValid

    }
}
