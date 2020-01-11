package co.com.pagatodo.core.views.superchance

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.request.RequestSuperChanceModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.SuperChanceAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.daylottery.DayLotteryComponentView
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import co.com.pagatodo.core.views.components.lottery.LotteryComponentView
import com.jaredrummler.materialspinner.MaterialSpinner
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_super_chance.*
import kotlinx.android.synthetic.main.activity_super_chance.btnClear
import kotlinx.android.synthetic.main.activity_super_chance.daysView
import kotlinx.android.synthetic.main.activity_super_chance.layoutRandom3
import kotlinx.android.synthetic.main.activity_super_chance.layoutRandom4
import kotlinx.android.synthetic.main.activity_super_chance.layoutRandom5
import kotlinx.android.synthetic.main.item_superchance_game.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_chance_summary.*
import java.util.*

class SuperChanceActivity : BaseActivity(),
    DayLotteryComponentView.OnDayLotteryListener,
    SuperChanceAdapter.OnListenerAdapter {

    private var data: String? = null
    private var maxLimit = 0
    private var minLimit = 0
    private var maxLotterys = 0
    private var maxRowsGames= 5
    private var raffleDate: Date? = null
    private var minimumTicketPrice = 0
    private var maximumTicketPrice = 0
    private var selectedLotteries = listOf<LotteryModel>()
    private val subscriptions = arrayListOf<Disposable>()
    private var superchanceGames = mutableListOf<SuperchanceModel>()
    private lateinit var superChanceViewModel: SuperChanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_chance)
        setupView()
        requestPrinterPermission()
    }

    override fun onResume() {
        super.onResume()
        initSubscription()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.forEach { it.dispose() }
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.superchance_title))
        daysView.listener = this
        updateDateInRaffleView(daysView.getSelectedDate())
        updateLotteryCount(0)
        setValuesInitInSummary()
        setupViewModel()
        initListenersViews()
    }

    override fun onSelectedDay(day: Int, dayName: String, date: Date) {
        resetDataWhenDayChanges()
        lotteryComponentView.applyFilterByDay(day)
        updateDateInRaffleView(date)
    }

    private fun setupViewModel() {
        superChanceViewModel = ViewModelProviders.of(this).get(SuperChanceViewModel::class.java)
        initSubscriptions()
        superChanceViewModel.loadProductParameters()
    }

    private fun updateDateInRaffleView(date: Date) {
        raffleDate = date
        tvRaffleDay.text = convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, date)
    }

    private fun resetDataWhenDayChanges() {
        selectedLotteries = listOf()
        updateLotteryCount(0)
    }

    private fun updateLotteryCount(count: Int) {
        tvLotteries.text = "$count"
    }

    private fun setValuesInitInSummary() {
        tvStub.text = getStub()
        tvIva.text = getString(R.string.text_iva_empty)
        tvTotal.text = getString(R.string.text_iva_empty)
        tvBet.text = getString(R.string.text_iva_empty)
    }

    override fun onTextChange(text: String) {
        var value = 0
        superchanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperchance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            val spinnerValue: MaterialSpinner = view.findViewById(R.id.spinnerValue)

            if(!editText.text.isEmpty()) {
                value += spinnerValue.text.toString().resetValueFormat().toInt()
            }
        }
        if(!selectedLotteries.isEmpty()) {
            value *= selectedLotteries.size
        }

        val totalIva = (value * CurrencyUtils.getIvaPercentage()).toInt()
        val totalToPay = value - totalIva

        tvTotal.text = getString(R.string.text_label_parameter_coin, formatValue("$value".toDouble()))
        tvIva.text = getString(R.string.text_label_parameter_coin, formatValue("$totalIva".toDouble()))
        tvBet.text = getString(R.string.text_label_parameter_coin, formatValue("$totalToPay".toDouble()))
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { getDataFromRecycler() }
        layoutRandom3.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            superChanceViewModel.loadRandomNumbers(1, 3)
        }
        layoutRandom4.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            superChanceViewModel.loadRandomNumbers(1, 4)
        }
        layoutRandom5.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            superChanceViewModel.loadRandomNumbers(5, 4)
        }

        btnClear.setOnClickListener {
            superchanceGames = mutableListOf()
            setupChanceGame()
        }

        btnClearSP.setOnClickListener {
            superchanceGames = mutableListOf()
            setupChanceGame()
        }

    }

    private fun initSubscriptions() {
        superChanceViewModel.singleLiveEvent.observe(this, Observer { itl ->
            when (itl) {
                is SuperChanceViewModel.ViewEvent.ResponseModalities -> {}
                is SuperChanceViewModel.ViewEvent.ResponseKeyValueParameters -> {
                    itl.parameters.forEach {
                        if(it.key.equals(getString(R.string.name_column_minimum_value))) {
                            minimumTicketPrice = it.value?.toInt() ?: 0
                        }
                        if(it.key.equals(getString(R.string.name_column_maximum_value))) {
                            maximumTicketPrice = it.value?.toInt() ?: 0
                        }
                        if(it.key.equals(getString(R.string.name_column_values))) {
                            data = it.value
                        }

                        if(it.key.equals(getString(R.string.key_max_size_bet))){
                            maxRowsGames = it.value!!.toInt()
                        }

                        if(it.key.equals(getString(R.string.name_column_maximum_lotterys))){
                            maxLotterys = it.value!!.toInt()
                            lotteryComponentView.setMaxNumberSelection(it.value!!.toInt())
                        }


                    }
                    setupChanceGame()
                }
                is SuperChanceViewModel.ViewEvent.ResponseRandomNumber -> {
                    hideDialogProgress()
                    setRandomNumbersInFields(itl.numbers)
                }
                is SuperChanceViewModel.ViewEvent.ResponseSuccess -> {
                    hideDialogProgress()
                    itl.successMessage?.let {
                        showOkAlertDialog("", it) { navigateToMenu(this) }
                    }
                }
                is SuperChanceViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    itl.errorMessage?.let {
                        showOkAlertDialog("", it)
                    }
                }
            }
        })
    }

    private fun setRandomNumbersInFields(numbers: List<String>) {
        if(numbers.isNotEmpty()) {
            if(numbers.size == 1) {
                superchanceGames.forEachIndexed { index, _ ->
                    val view: View = recyclerItemsSuperchance.getChildAt(index)
                    val editText: EditText = view.findViewById(R.id.txtNumber)
                    if(editText.hasFocus()) {
                        editText.text = SpannableStringBuilder(numbers[0])
                        return
                    }
                }
                val view: View = recyclerItemsSuperchance.getChildAt(0)
                view.txtNumber.text = SpannableStringBuilder(numbers[0])
            } else {
                setRandomNumbersInAllFields(numbers)
            }
        }
    }

    private fun setRandomNumbersInAllFields(numbers: List<String>) {
        superchanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperchance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            editText.setText(numbers[index])
        }
    }

    private fun setupChanceGame() {



        for (i in 1..maxRowsGames) {
            superchanceGames.add(SuperchanceModel())
        }

        recyclerItemsSuperchance.apply {
            layoutManager = LinearLayoutManager(this@SuperChanceActivity)
            setHasFixedSize(true)
        }
        setupAdapterForSuperchanceGames(superchanceGames, data)
    }

    private fun setupAdapterForSuperchanceGames(chanceGames: List<SuperchanceModel>, data: String?) {
        val values = data?.split(",")
        val valuesFormat = mutableListOf<String>()

        values?.mapTo(valuesFormat,{
            getString(R.string.text_label_parameter_coin, formatValue(it.toDouble()))
        })

        maxLimit = 4
        minLimit = 3
        val chanceAdapter = SuperChanceAdapter(chanceGames, valuesFormat, maxLimit, this)
        recyclerItemsSuperchance.adapter = chanceAdapter
    }

    private fun initSubscription() {
        subscriptions.add(
            lotteryComponentView.getObservableLotteryComponent().subscribe { response ->
                when(response.first) {
                    LotteryComponentView.LotteryAction.COMPONENT_IS_READY -> {
                        lotteryComponentView.selectCurrentDay()
                    }
                    LotteryComponentView.LotteryAction.CHECKBOX_ITEM_SELECTED -> {
                        response.second?.let {
                            selectedLotteries = it
                            updateLotteryCount(selectedLotteries.size)
                            updateTotalValue()
                        }
                    }
                }
            }
        )
    }

    private fun updateTotalValue() {
        var value = 0
        superchanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperchance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            val spinnerValue: MaterialSpinner = view.findViewById(R.id.spinnerValue)

            if(!editText.text.isEmpty()) {
                value += spinnerValue.text.toString().resetValueFormat().toInt()
            }
        }
        if(!selectedLotteries.isEmpty()) {
            value *= selectedLotteries.size
        }

        val totalIva = (value * CurrencyUtils.getIvaPercentage()).toInt()
        val totalToPay = value - totalIva

        tvTotal.text = getString(R.string.text_label_parameter_coin, formatValue("$value".toDouble()))
        tvIva.text = getString(R.string.text_label_parameter_coin, formatValue("$totalIva".toDouble()))
        tvBet.text = getString(R.string.text_label_parameter_coin, formatValue("$totalToPay".toDouble()))
    }

    private fun getDataFromRecycler() {
        if(validateInformationAndSeletedLotteries()) {
            val valuesList = getValueAndNumber()
            if(validateTotalValue(valuesList.second)) {
                processBet(valuesList.first, valuesList.second)
            }
        }
    }

    private fun getValueAndNumber(): Pair<List<String>, List<String>>{
        val valuesFields = arrayListOf<String>()
        val valuesSpinners = arrayListOf<String>()

        superchanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperchance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            val spinner: MaterialSpinner = view.findViewById(R.id.spinnerValue)

            if(!editText.text.isEmpty()) {
                valuesFields.add(editText.text.toString())
                valuesSpinners.add(spinner.text.toString().resetValueFormat())
            }
        }

        return Pair(valuesFields, valuesSpinners)
    }

    private fun hasValueInArray(item: ChanceModel, array: List<ChanceModel>): Boolean {
        var isExist = false
        array.forEach {
            if (item.number == it.number) {
                isExist = true
                return@forEach
            }
        }
        return isExist
    }

    private fun getNumbersList(): List<ChanceModel> {
        val models = arrayListOf<ChanceModel>()
        val data = (recyclerItemsSuperchance.adapter as SuperChanceAdapter).data
        data.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperchance.getChildAt(index)
            val etNumber = view.txtNumber
            if (etNumber.text.toString().isNotBlank()) {
                models.add(
                    ChanceModel().apply {
                        number = etNumber.text.toString()
                    }
                )
            }
        }
        return models
    }

    private fun processBet(valuesFields: List<String>, valuesSpinners: List<String>) {
        val superchanceList = arrayListOf<SuperchanceModel>()
        val totalValue = calculateTotalValueInSpinners(valuesSpinners)
        valuesFields.forEachIndexed { index, element ->
            val superchanceModel = SuperchanceModel().apply {
                number = element
                value = valuesSpinners[index]
            }
            superchanceList.add(superchanceModel)
        }
        showConfirmationDialog(superchanceList, totalValue)
    }

    private fun showConfirmationDialog(superchanceList: List<SuperchanceModel>, totalValue: Double) {

        val totalIva = (totalValue * CurrencyUtils.getIvaPercentage()).toInt()
        val totalToPay = totalValue - totalIva

        PaymentConfirmationDialog().show(this, R.layout.layout_payment_confirm_maxichance,
            R_string(R.string.text_title_superchance_dialog), null, { view ->
                view.findViewById<TextView>(R.id.labelDate).text = tvRaffleDay.text.toString()
                view.findViewById<TextView>(R.id.labelLotteries).text = tvLotteries.text.toString()
                view.findViewById<TextView>(R.id.labelLotteryNumbers).text = setupLotteryNumbersInTextview(superchanceList)
                view.findViewById<TextView>(R.id.labelStub).text = tvStub.text
                view.findViewById<TextView>(R.id.labelBet).text = "$${formatValue(totalToPay)}"
                view.findViewById<TextView>(R.id.labelIva).text = "$${formatValue(totalIva.toDouble())}"
                view.findViewById<TextView>(R.id.labelValue).text = "$${formatValue(totalValue)}"
            },{
                showDialogProgress(getString(R.string.message_dialog_request))
                val request = RequestSuperChanceModel().apply {
                    this.chances = superchanceList
                    this.lotteries = selectedLotteries
                    this.totalValue = totalValue
                    this.raffleDateModel = this@SuperChanceActivity.raffleDate
                    this.stubs = getStub()
                    this.transactionTime = DateUtil.getCurrentDateInUnix()
                    this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                    this.maxRows = maxRowsGames
                }
                isForeground = false
                superChanceViewModel.paySuperchance(request)
            })
    }

    private fun setupLotteryNumbersInTextview(superchanceList: List<SuperchanceModel>): String {
        var numbers = ""
        superchanceList.forEachIndexed { index, it ->
            numbers = if (index == 0){
                numbers + it.number
            }else{
                numbers +"-"+ it.number
            }
        }
        return numbers
    }

    private fun validateInformationAndSeletedLotteries(): Boolean {
        val numbersGames = mutableListOf<String>()
        superchanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperchance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            val spinner: MaterialSpinner = view.findViewById(R.id.spinnerValue)

            if(!editText.text.isEmpty()) {
                if(!validateLimit(editText.text.toString())) {
                    return false
                }
                numbersGames.add(editText.text.toString())
            }
        }
        if(numbersGames.isEmpty()) {
            showOkAlertDialog("", getString(R.string.message_error_chance_not_games))
            return false
        }
        if(selectedLotteries.size > maxLotterys && maxLotterys != 0) {
            showOkAlertDialog("", getString(R.string.message_error_super_chance_lotteries_selected, 4))
            return false
        }
        if(selectedLotteries.isEmpty()) {
            showOkAlertDialog("", getString(R.string.message_error_min_super_chance_lotteries_selected, 1))
            return false
        }
        return true
    }

    private fun validateLimit(valueInField: String): Boolean {
        if(valueInField.length < minLimit) {
            showOkAlertDialog("", getString(R.string.message_error_super_chance_limit_number, minLimit))
            return false
        }
        return true
    }

    private fun calculateTotalValueInSpinners(valuesSpinners: List<String>): Double {
        var value = 0.0
        valuesSpinners.forEach { element ->
            value += element.toDouble()
        }
        value *= selectedLotteries.size
        return value
    }

    private fun validateTotalValue(valuesSpinners: List<String>): Boolean {
        val value = calculateTotalValueInSpinners(valuesSpinners)
        if(value.toInt() < minimumTicketPrice) {
            showOkAlertDialog("", getString(R.string.message_error_super_chance_limit_value, minimumTicketPrice))
            return false
        }
        if(value.toInt() > maximumTicketPrice) {
            showOkAlertDialog("", getString(R.string.message_error_super_chance_limit_max_value, maximumTicketPrice))
            return false
        }
        return true
    }
}
