package co.com.pagatodo.core.views.paymillionaire

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.util.DateUtil.Companion.convertDateToStringFormat
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.OnlyNumberAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.lottery.LotteryComponentView
import io.reactivex.disposables.Disposable
import java.util.*
import android.text.SpannableStringBuilder
import android.widget.LinearLayout
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductModel
import co.com.pagatodo.core.data.model.request.RequestPayMillonaireModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.adapters.PayMillionaireModalityAdapter
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.activity_pay_millonaire.*
import kotlinx.android.synthetic.main.activity_pay_millonaire.btnClear
import kotlinx.android.synthetic.main.activity_pay_millonaire.layoutRandom5
import kotlinx.android.synthetic.main.activity_pay_millonaire.lotteryComponent
import kotlinx.android.synthetic.main.item_only_number.view.*
import kotlinx.android.synthetic.main.item_pm_modalities_layout.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_chance_summary.*
import kotlinx.android.synthetic.main.layout_payment_confirm_paymillionaire.view.*

class PayMillionaireActivity: BaseActivity(),
    OnlyNumberAdapter.OnAdapterListener,
    PayMillionaireModalityAdapter.OnAdapterListener {

    private var subscriptions = arrayListOf<Disposable>()
    private var selectedLotteries = mutableListOf<LotteryModel>()
    private var selectedValueMode = 0.0
    private var selectedModeValue: ModeValueModel? = null
    private var productModel: ProductModel? = null
    private lateinit var payMillonaireViewModel: PayMillionaireViewModel
    private var maxGames = 5
    private var chanceRowSelected: Int = 0
    private var maxLotteries = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_millonaire)
        setupViewModel()
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

    private fun initSubscription() {
        subscriptions.add(
            lotteryComponent.getObservableLotteryComponent().subscribe { response ->
                when(response.first) {
                    LotteryComponentView.LotteryAction.COMPONENT_IS_READY -> {
                        lotteryComponent.selectCurrentDay()
                        updateViewAfterLoadDataFromViewModel()
                    }
                    LotteryComponentView.LotteryAction.CHECKBOX_ITEM_SELECTED -> {
                        response.second?.let {
                            selectedLotteries = it.toMutableList()
                            updateLotteryCount(selectedLotteries.count())
                            calculateValuesOfSummary()
                        }
                    }
                }
            }
        )
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.pay_millionaire_title))
        tvStub.text = getStub()
        tvRaffleDay.text = convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, Calendar.getInstance().time)

        setupGame()

        btnNext.setOnClickListener { dispatchPayPayMillionaire() }
        btnBack.setOnClickListener { onBackPressed() }
        layoutRandom5.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            selectedModeValue?.let {
                if (it.numberOfDigits?.toInt() == 4) {
                    payMillonaireViewModel.dispatchGenerateRandom(5, 4,
                        PayMillionaireViewModel.ViewEvent.RandomNumberType.Five
                    )
                }else{
                    payMillonaireViewModel.dispatchGenerateRandom(5, 3,
                        PayMillionaireViewModel.ViewEvent.RandomNumberType.Five
                    )
                }
            } ?: run {
                payMillonaireViewModel.dispatchGenerateRandom(5, 3,
                    PayMillionaireViewModel.ViewEvent.RandomNumberType.Five
                )
            }
        }

        btnClear.setOnClickListener {
            setupGame()
        }

        btnClearSP.setOnClickListener {
            setupGame()
        }

    }


    private fun setupGame() {
        val numbers = arrayListOf<String>()
        for (i in 1..maxGames) {
            numbers.add("")
        }

        rvBet.apply {
            layoutManager = LinearLayoutManager(this@PayMillionaireActivity)
            adapter = OnlyNumberAdapter(numbers, this@PayMillionaireActivity)
        }
    }

    private fun setupViewModel() {
        payMillonaireViewModel = ViewModelProviders.of(this).get(PayMillionaireViewModel::class.java)

        payMillonaireViewModel.productModelLiveData.observe(this, Observer {
            productModel = it
            updateViewAfterLoadDataFromViewModel()
        })

        payMillonaireViewModel.singleLiveEvent.observe(this, Observer {
            progressDialog.dismiss()
            when(it) {
                is PayMillionaireViewModel.ViewEvent.PaySuccess -> {
                    hideDialogProgress()
                    showOkAlertDialog("", it.successMessage) {
                        navigateToMenu(this)
                    }
                }
                is PayMillionaireViewModel.ViewEvent.PayError -> {
                    hideDialogProgress()
                    showOkAlertDialog("", it.errorMessage)
                }
                is PayMillionaireViewModel.ViewEvent.RandomNumber -> {
                    hideDialogProgress()
                    when(it.randomType) {
                        PayMillionaireViewModel.ViewEvent.RandomNumberType.Three,
                        PayMillionaireViewModel.ViewEvent.RandomNumberType.Four -> {
                            if (it.numbers.count() > 0) {
                                fillFocusedFieldWithRandomNumber(it.numbers[0])
                            }
                        }
                        PayMillionaireViewModel.ViewEvent.RandomNumberType.Five -> {
                            fillFormWithRandomNumbers(it.numbers)
                        }
                    }
                }
                is PayMillionaireViewModel.ViewEvent.ShowMode -> {
                    val adapter = PayMillionaireModalityAdapter(it.modes, this@PayMillionaireActivity)
                    adapter.mSelectedItem = 0
                    rvModalities?.apply {
                        layoutManager = LinearLayoutManager(this@PayMillionaireActivity, LinearLayout.HORIZONTAL, false)
                        this.adapter = adapter
                    }
                    selectedModeValue = it.modes.first()
                    selectedValueMode = it.modes.first().value?.toDouble() ?: 0.0
                }
            }
        })
        payMillonaireViewModel.dispatchMode()
        payMillonaireViewModel.fetchProduct()
    }

    private fun updateViewAfterLoadDataFromViewModel() {
        val item = productModel?.parameters?.filter { it.key == R_string(R.string.sp_max_amount_lotteries_param_service) }
        if (!item.isNullOrEmpty()) {
            maxLotteries = item.first().value?.toInt() ?: R_string(R.string.max_lotteries_paymillionaire).toInt()
            if(maxLotteries != 0)
                lotteryComponent.setMaxNumberSelection(maxLotteries)
        }
    }

    private fun fillFormWithRandomNumbers(numbers: List<String>) {
        numbers.forEachIndexed { index, item ->
            val view: View = rvBet.getChildAt(index)
            view.etNumber.text = SpannableStringBuilder("$item")
        }
    }

    private fun fillFocusedFieldWithRandomNumber(number: String) {
        val adapter = (rvBet.adapter as OnlyNumberAdapter)
        adapter.data[chanceRowSelected] = "$number"
        val view: View = rvBet.getChildAt(chanceRowSelected)
        view.etNumber.text = SpannableStringBuilder("$number")
    }

    private fun cleanNumberInRow(index: Int) {
        val view: View = rvBet.getChildAt(index)
        view.etNumber.setText("")
    }

    private fun updateLotteryCount(count: Int) {
        tvLotteries.text = "$count"
    }

    private fun getNumbersList(): List<ChanceModel> {
        val models = arrayListOf<ChanceModel>()
        val data = (rvBet.adapter as OnlyNumberAdapter).data
        data.forEachIndexed { index, _ ->
            val view: View = rvBet.getChildAt(index)
            val etNumber = view.etNumber
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

    private fun calculateValuesOfSummary() {
        val totalToPay  = Math.round(selectedValueMode / CurrencyUtils.getIvaPercentageSporting())
        val totalIva = selectedValueMode - totalToPay

        tvIva.text = getString(R.string.text_label_parameter_coin,
            formatValue("$totalIva".toDouble())
        )
        tvTotal.text = getString(R.string.text_label_parameter_coin,
            formatValue("$selectedValueMode".toDouble())
        )
        tvBet.text = getString(R.string.text_label_parameter_coin,
            formatValue("$totalToPay".toDouble())
        )
    }

    private fun dispatchPayPayMillionaire() {
        validateSelectedModality {
            if(selectedLotteries.count() > 0) {
                validateEqualityOfDigits {
                    validateRepeatedNumbers {
                        validateChanceNumbers {
                            if (maxLotteries != 0 && selectedLotteries.count() != maxLotteries){
                                showOkAlertDialog("", getString(R.string.message_error_number_loteries, maxLotteries))
                            }else{
                                PaymentConfirmationDialog().show(this, R.layout.layout_payment_confirm_paymillionaire,
                                    R_string(R.string.title_confirm_paymillionaire), null,
                                    { view ->
                                        view.tvRaffleDate.text = tvRaffleDay.text
                                        view.tvLotteriesCount.text = tvLotteries.text
                                        view.tvStub.text = tvStub.text
                                        view.tvBet.text = tvBet.text.toString()
                                        view.tvIva.text = tvIva.text.toString()
                                        view.tvTotalPaid.text = tvTotal.text.toString()
                                    },{
                                        val raffleDate = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, Calendar.getInstance().time)
                                        val request = RequestPayMillonaireModel().apply {
                                            this.chances = getNumbersList()
                                            this.lotteries = selectedLotteries
                                            this.valueWithoutIva = selectedValueMode
                                            this.product = productModel
                                            this.selectedValue = selectedModeValue
                                            this.raffleDate = raffleDate
                                            this.stubs = getStub()
                                            this.transactionTime = DateUtil.getCurrentDateInUnix()
                                            this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                                        }
                                        isForeground = false
                                        payMillonaireViewModel.dispatchPayMillionaire(request)
                                        showDialogProgress(R_string(R.string.message_dialog_request))
                                    })
                            }
                        }
                    }
                }
            }else {
                showOkAlertDialog("", R_string(R.string.message_error_lottery_selected))
            }
        }
    }

    private fun hasValueInArray(item: ChanceModel, array: List<ChanceModel>): Boolean {
        var exist = false
        array.forEach {
            if (item.number == it.number) {
                exist = true
                return@forEach
            }
        }
        return exist
    }

    private fun validateSelectedModality(closure: () -> Unit) {
        var isSelected = false
        val data = (rvModalities.adapter as PayMillionaireModalityAdapter).data
        data.forEachIndexed { index, _ ->
            val view: View = rvModalities.getChildAt(index)
            if (view.radio.isChecked) {
                isSelected = true
                return@forEachIndexed
            }
        }

        if (isSelected) {
            closure()
        }else{
            showOkAlertDialog("", R_string(R.string.message_error_selected_modality))
        }
    }

    private fun validateEqualityOfDigits(closure: () -> Unit){
        var isThreeNumber = false
        var isFourNumber = false
        var isMinorThree = false

        getNumbersList().forEach {
            val count = it.number?.trim()?.length
            if (count == 3) {
                isThreeNumber = true
            }else if (count == 4) {
                isFourNumber = true
            }else {
                isMinorThree = true
            }
        }

        if (isMinorThree) {
            showOkAlertDialog("", R_string(R.string.message_error_only_three_and_four_digits))
        }else if (isThreeNumber && isFourNumber) {
            showOkAlertDialog("", R_string(R.string.message_error_numbers_must_have_same_number_digits))
        }else {
            closure()
        }
    }

    private fun validateRepeatedNumbers(closure: () -> Unit) {
        var isValid = true
        val numbers = getNumbersList()
        numbers.forEachIndexed { index,  item ->
            val filteredValues = numbers.filterIndexed { internalIndex, _ -> internalIndex != index }
            val isExist = hasValueInArray(item, filteredValues)
            if (isExist) {
                isValid = false
                return@forEachIndexed
            }
        }

        if (isValid) {
            closure()
        }else {
            showOkAlertDialog("", R_string(R.string.message_error_duplicate_number))
        }
    }

    private fun validateChanceNumbers(closure: () -> Unit) {
        var isValidNotEmpty = true
        var isValidDigits = true
        var isValidDigitsForMode = true
        val numbers = (rvBet.adapter as OnlyNumberAdapter).data
        numbers.forEachIndexed { index, _ ->
            val view = rvBet.getChildAt(index)
            if (view.etNumber.text.isNullOrBlank()) {
                isValidNotEmpty = false
                return@forEachIndexed
            }else {
                if (view.etNumber.text.count() < 3 ){
                    isValidDigits = false
                    return@forEachIndexed
                }
                if (view.etNumber.text.count() != selectedModeValue?.numberOfDigits?.toInt()){
                    isValidDigitsForMode = false
                    return@forEachIndexed
                }
            }
        }

        if (isValidNotEmpty && isValidDigits && isValidDigitsForMode) {
            closure()
        }else if (!isValidNotEmpty) {
            showOkAlertDialog("", R_string(R.string.message_all_input_required))
        }else if (!isValidDigits) {
            showOkAlertDialog("", R_string(R.string.message_error_min_digits_paymillionaire))
        }else if (!isValidDigitsForMode){
            showOkAlertDialog("", R_string(R.string.message_error_numbers_must_have_same_number_digits))
        }
    }

    override fun onTextChanged(adapterPosition: Int, text: String) {
        calculateValuesOfSummary()
    }

    override fun onSelectedRow(row: Int) {
        chanceRowSelected = if(row >= 0) row else 0
    }

    override fun onclickRandom3(index: Int) {
        chanceRowSelected = index
        showDialogProgress(R_string(R.string.message_dialog_generate_random))
        payMillonaireViewModel.dispatchGenerateRandom(1, 3,
            PayMillionaireViewModel.ViewEvent.RandomNumberType.Three
        )
    }

    override fun onClickRandom4(index: Int) {
        chanceRowSelected = index
        showDialogProgress(R_string(R.string.message_dialog_generate_random))
        payMillonaireViewModel.dispatchGenerateRandom(1, 4,
            PayMillionaireViewModel.ViewEvent.RandomNumberType.Four
        )
    }

    override fun onClickDelete(index: Int) {
        chanceRowSelected = index
        cleanNumberInRow(index)
    }

    override fun onModalitySelected(item: ModeValueModel) {
        val isNotFirstModalitySelection = selectedModeValue != null
        val isDifferentModality = selectedModeValue != item
        if(isNotFirstModalitySelection && isDifferentModality && hasLotteriesOrNumbers()){
            showAlertDialog("", R_string(R.string.text_warning_change_paymillionaire_modalitiy), {
                runAfterAcceptModalityConfirmation(item)
            }, {
                rollbackSelectedModality()
            })
        } else if (isDifferentModality){
            runAfterAcceptModalityConfirmation(item)
        }
    }

    private fun hasLotteriesOrNumbers(): Boolean {
        val numberListNotEmpty = getNumbersList().isNotEmpty()
        val selectedLotteriesNotEmpty = selectedLotteries.isNotEmpty()
        return numberListNotEmpty || selectedLotteriesNotEmpty
    }

    private fun runAfterAcceptModalityConfirmation(item: ModeValueModel) {
        val number = item.numberOfDigits?.toInt() ?: 3
        executeEventDependingOnTheModality(number)
        selectedLotteries.clear()
        selectedModeValue = item
        selectedValueMode = item.value?.toDouble() ?: 0.0
        calculateValuesOfSummary()
    }

    private fun rollbackSelectedModality() {
        selectedModeValue?.let { modality ->
            val indexRadio = if (modality.numberOfDigits?.toInt() == 3) 0 else 1
            (rvModalities.adapter as PayMillionaireModalityAdapter).data.forEachIndexed { index, _ ->
                val view = rvModalities.getChildAt(index)
                view.radio.isChecked = index == indexRadio
            }
        } ?: run {
            (rvModalities.adapter as PayMillionaireModalityAdapter).data.forEachIndexed { index, _ ->
                val view = rvModalities.getChildAt(index)
                view.radio.isChecked = false
            }
        }
    }

    private fun executeEventDependingOnTheModality(number: Int) {
        clearAll()
        val data = (rvBet.adapter as OnlyNumberAdapter).data
        when(number) {
            3 -> {
                data.forEachIndexed { index, _ ->
                    val view: View = rvBet.getChildAt(index)
                    view.btnRandom3.visibility = View.VISIBLE
                    view.btnRandom4.visibility = View.GONE
                }
            }
            else -> {
                data.forEachIndexed { index, _ ->
                    val view: View = rvBet.getChildAt(index)
                    view.btnRandom3.visibility = View.GONE
                    view.btnRandom4.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun clearAll() {
        lotteryComponent.clearSelection()
        updateLotteryCount(0)
        (rvBet.adapter as OnlyNumberAdapter).data.forEachIndexed { index, _ ->
            val view = rvBet.getChildAt(index)
            if(view != null && view.etNumber != null){
                view.etNumber.setText("")
            }

        }
    }
}