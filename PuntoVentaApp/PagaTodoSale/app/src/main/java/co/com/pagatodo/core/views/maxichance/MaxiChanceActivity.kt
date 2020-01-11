package co.com.pagatodo.core.views.maxichance

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.data.model.*
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
import kotlinx.android.synthetic.main.activity_maxi_chance.*
import kotlinx.android.synthetic.main.item_superchance_game.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_chance_summary.*
import kotlinx.android.synthetic.main.layout_payment_confirm_maxichance.view.*
import java.util.*

class MaxiChanceActivity : BaseActivity(),
    DayLotteryComponentView.OnDayLotteryListener,
    SuperChanceAdapter.OnListenerAdapter {

    private lateinit var maxiChanceViewModel: MaxiChanceViewModel
    private lateinit var promotionModel: PromotionModel

    private var minimumTicketPrice = 0
    private var maximumTicketPrice = 0

    private var selectedLotteries = listOf<LotteryModel>()
    private val subscriptions = arrayListOf<Disposable>()
    private var maxichanceGames = mutableListOf<SuperchanceModel>()
    private var raffleDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maxi_chance)
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
        updateTitle(R_string(R.string.maxichance_title))
        daysView.listener = this
        updateDateInRaffleView(daysView.getSelectedDate())
        updateLotteryCount(0)
        setInitValuesInSummary()
        setupViewModel()
        initViewListeners()
    }

    override fun onSelectedDay(day: Int, dayName: String, date: Date) {
        resetDataWhenDayChanges()
        lotteryComponent.applyFilterByDay(day)
        updateDateInRaffleView(date)
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

    private fun updateTotalValue() {
        tvTotal.setText(getString(R.string.giro_payment_defauld_amount))
        tvIva.setText(getString(R.string.giro_payment_defauld_amount))
        tvBet.setText(getString(R.string.giro_payment_defauld_amount))
        var value = 0
        maxichanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsMaxichance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            val spinnerValue: MaterialSpinner = view.findViewById(R.id.spinnerValue)

            if(!editText.text.isEmpty()) {
                value += spinnerValue.text.toString().resetValueFormat().toInt()
            }
        }
        if(!selectedLotteries.isEmpty()) {
            value *= selectedLotteries.size
        }

        val totalToPay= (value / CurrencyUtils.getIvaPercentageSporting()).toInt()
        val totalIva  = value - totalToPay

        tvTotal.text = getString(R.string.text_label_parameter_coin, formatValue("$value".toDouble()))
        tvIva.text = getString(R.string.text_label_parameter_coin, formatValue("$totalIva".toDouble()))
        tvBet.text = getString(R.string.text_label_parameter_coin, formatValue("$totalToPay".toDouble()))
    }

    private fun setInitValuesInSummary() {
        tvStub.text = getStub()
        tvIva.text = getString(R.string.text_iva_empty)
        tvTotal.text = getString(R.string.text_iva_empty)
        tvBet.text = getString(R.string.text_iva_empty)
    }

    override fun onTextChange(text: String) {
        var value = 0
        maxichanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsMaxichance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            val spinnerValue: MaterialSpinner = view.findViewById(R.id.spinnerValue)

            if(!editText.text.isEmpty()) {
                value += spinnerValue.text.toString().resetValueFormat().toInt()
            }
        }
        if(!selectedLotteries.isEmpty()) {
            value *= selectedLotteries.size
        }

        val totalToPay= (value / CurrencyUtils.getIvaPercentageSporting()).toInt()
        val totalIva  = value - totalToPay

        tvTotal.text = getString(R.string.text_label_parameter_coin, formatValue("$value".toDouble()))
        tvIva.text = getString(R.string.text_label_parameter_coin, formatValue("$totalIva".toDouble()))
        tvBet.text = getString(R.string.text_label_parameter_coin, formatValue("$totalToPay".toDouble()))
    }

    private fun setupViewModel() {
        maxiChanceViewModel = ViewModelProviders.of(this).get(MaxiChanceViewModel::class.java)
        initSubscriptions()
        maxiChanceViewModel.loadPromotional()
    }

    private fun initSubscriptions() {
        maxiChanceViewModel.singleLiveEvent.observe(this, Observer { itl ->
            when (itl) {
                is MaxiChanceViewModel.ViewEvent.ResponseSuccess -> {
                    hideDialogProgress()
                    itl.successMessage?.let {
                        progressDialog.dismiss()
                        showOkAlertDialog("", it) { navigateToMenu(this) }
                    }
                }
                is MaxiChanceViewModel.ViewEvent.ResponseModalities -> {
                    promotionModel = itl.promotional
                    lotteryComponent.setMaxNumberSelection(promotionModel.lotteriesQuantity?:4)
                    maxiChanceViewModel.fetchChanceProduct()
                    setupValuesOfModalities()
                }
                is MaxiChanceViewModel.ViewEvent.ResponseRandomNumber -> {
                    hideDialogProgress()
                    setRandomNumbersInFields(itl.numbers)
                }
                is MaxiChanceViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    itl.errorMessage?.let { showOkAlertDialog("", it) }
                }
                is MaxiChanceViewModel.ViewEvent.ResponseProductInfo -> {
                    setValuesForProductModel(itl.productModel)
                }
            }
        })
    }

    private fun setValuesForProductModel(productModel: ProductModel) {
        productModel.parameters?.forEach {
            if(it.key.equals(getString(R.string.name_column_minimum_value))) {
                minimumTicketPrice = it.value?.toInt() ?: 0
            }
            if(it.key.equals(getString(R.string.name_column_maximum_value))) {
                maximumTicketPrice = it.value?.toInt() ?: 0
            }
        }
    }

    private fun setRandomNumbersInFields(numbers: List<String>) {
        if(numbers.isNotEmpty()) {
            if(numbers.size == 1) {
                maxichanceGames.forEachIndexed { index, _ ->
                    val view: View = recyclerItemsMaxichance.getChildAt(index)
                    val editText: EditText = view.findViewById(R.id.txtNumber)
                    if(editText.hasFocus()) {
                        editText.text = SpannableStringBuilder(numbers[0])
                        return
                    }
                }
                val view: View = recyclerItemsMaxichance.getChildAt(0)
                view.txtNumber.text = SpannableStringBuilder(numbers[0])
            } else {
                setRandomNumbersInAllFields(numbers)
            }
        }
    }

    private fun setRandomNumbersInAllFields(numbers: List<String>) {
        maxichanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsMaxichance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)
            editText.setText(numbers[index])
        }
    }

    private fun initViewListeners() {
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { convertRecyclerDataToLists() }
        layoutRandom3.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            maxiChanceViewModel.loadRandomNumbers(1, 3)
        }
        layoutRandom4.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            maxiChanceViewModel.loadRandomNumbers(1, 4)
        }
        layoutRandom5.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            maxiChanceViewModel.loadRandomNumbers(5, 4)
        }

        btnClear.setOnClickListener {
            maxichanceGames = mutableListOf()
            setupValuesOfModalities()
        }

        btnClearSP.setOnClickListener {
            maxichanceGames = mutableListOf()
            setupValuesOfModalities()
        }

    }

    private fun setupValuesOfModalities() {
        val modalitiesValues: List<String>? = promotionModel.modalitiesValues
        val limit = promotionModel.bettingAmount ?: 0
        for (i in 1..limit) {
            maxichanceGames.add(SuperchanceModel())
        }
        recyclerItemsMaxichance.apply {
            layoutManager = LinearLayoutManager(this@MaxiChanceActivity)
            setHasFixedSize(true)
        }
        setupAdapterForSuperchanceGames(maxichanceGames, modalitiesValues)
    }

    private fun setupAdapterForSuperchanceGames(chanceGames: List<SuperchanceModel>, modalitiesValues: List<String>?) {

        val valuesFormat = mutableListOf<String>()

        modalitiesValues?.mapTo(valuesFormat,{
            getString(R.string.text_label_parameter_coin, formatValue(it.toDouble()))
        })

        val chanceAdapter = SuperChanceAdapter(chanceGames, valuesFormat, 4, this)
        recyclerItemsMaxichance.adapter = chanceAdapter
    }

    private fun initSubscription() {
        subscriptions.add(
            lotteryComponent.getObservableLotteryComponent().subscribe { response ->
                when(response.first) {
                    LotteryComponentView.LotteryAction.COMPONENT_IS_READY -> {
                        lotteryComponent.selectCurrentDay()
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

    private fun convertRecyclerDataToLists() {
        if(validateInformationAndSeletedLotteries()) {
            val valuesFields = arrayListOf<String>()
            val valuesSpinners = arrayListOf<String>()

            maxichanceGames.forEachIndexed { index, _ ->
                val view: View = recyclerItemsMaxichance.getChildAt(index)
                val editText: EditText = view.findViewById(R.id.txtNumber)
                val spinner: MaterialSpinner = view.findViewById(R.id.spinnerValue)

                if(!editText.text.isEmpty()) {
                    valuesFields.add(editText.text.toString())
                    valuesSpinners.add(spinner.text.toString().resetValueFormat())
                }
            }
            if(validateTotalValue(valuesSpinners)) {
                processBet(valuesFields , valuesSpinners)
            }
        }
    }

    private fun validateTotalValue(valuesSpinners: List<String>): Boolean {
        val value = calculateTotalValueInSpinners(valuesSpinners)
        if(value.toInt() < minimumTicketPrice) {
            showOkAlertDialog("", getString(R.string.message_error_super_chance_limit_value, minimumTicketPrice))
            return false
        }
        return true
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
        val data = (recyclerItemsMaxichance.adapter as SuperChanceAdapter).data
        data.forEachIndexed { index, _ ->
            val view: View = recyclerItemsMaxichance.getChildAt(index)
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

    private fun validateInformationAndSeletedLotteries(): Boolean {
        val numberLotteries = promotionModel.lotteriesQuantity ?: 4
        val numbersGames = mutableListOf<String>()
        maxichanceGames.forEachIndexed { index, _ ->
            val view: View = recyclerItemsMaxichance.getChildAt(index)
            val editText: EditText = view.findViewById(R.id.txtNumber)

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
        if(selectedLotteries.size > numberLotteries) {
            showOkAlertDialog("", getString(R.string.message_error_chance_max_lotteries, numberLotteries.toString()))
            return false
        }
        if(selectedLotteries.isEmpty()) {
            showOkAlertDialog("", getString(R.string.message_error_min_super_chance_lotteries_selected, 1))
            return false
        }
        return true
    }

    private fun validateLimit(valueInField: String): Boolean {
        val values = promotionModel.quantityFigures?.split(";")
        if(values != null) {
            if(values.isNotEmpty()) {
                val valueMin = values[0]
                val valueMax = values[1]
                if(valueInField.length < valueMin.toInt()) {
                    showOkAlertDialog("", getString(R.string.message_error_chance_min_characters_field, valueMin))
                    return false
                }
                if(valueInField.length > valueMax.toInt()) {
                    showOkAlertDialog("", getString(R.string.message_error_chance_max_characters_field, valueMax))
                    return false
                }
            }
        }
        return true
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
        createConfirmationDialog(superchanceList, totalValue)
    }

    private fun createConfirmationDialog(superchanceList: List<SuperchanceModel>, totalValue: Double) {

        val totalToPay  = (totalValue / CurrencyUtils.getIvaPercentageSporting()).toInt()
        val totalIva = totalValue - totalToPay

        PaymentConfirmationDialog().show(this, R.layout.layout_payment_confirm_maxichance,
            RUtil.R_string(R.string.text_title_maxichance_dialog), null, { view ->
                //view.labelLotteryNumbersTitle.visibility = View.GONE
                //view.labelLotteryNumbers.visibility = View.GONE
                var games=""
                maxichanceGames.forEachIndexed { index, _ ->
                    val view: View = recyclerItemsMaxichance.getChildAt(index)
                    val editText: EditText = view.findViewById(R.id.txtNumber)
                    if(!editText.text.isEmpty()) {
                        games+=editText.text.toString()+"-"
                    }
                }
                view.labelLotteryNumbers.text=games.substring(0,games.length-1)
                view.labelDate.text = tvRaffleDay.text.toString()
                view.labelLotteries.text = tvLotteries.text.toString()
                view.labelStub.text = tvStub.text
                view.labelBet.text = "$${formatValue(totalToPay.toDouble())}"
                view.labelIva.text = "$${formatValue(totalIva.toDouble())}"
                view.labelValue.text = "$${formatValue(totalValue)}"
            },{
                showDialogProgress(getString(R.string.message_dialog_request))
                val request = RequestSuperChanceModel().apply {
                    this.chances = superchanceList
                    this.lotteries = selectedLotteries
                    this.totalValue = totalValue
                    this.raffleDateModel = raffleDate
                    this.stubs = getStub()
                    this.transactionTime = DateUtil.getCurrentDateInUnix()
                    this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                }
                isForeground = false
                maxiChanceViewModel.payMaxichance(request)
            })
    }

    private fun calculateTotalValueInSpinners(valuesSpinners: List<String>): Double {
        var value = 0.0
        valuesSpinners.forEach { element ->
            value += element.toDouble()
        }
        value *= selectedLotteries.size
        return value
    }
}