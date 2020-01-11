package co.com.pagatodo.core.views.superastro

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModalityModel
import co.com.pagatodo.core.data.model.SuperAstroModel
import co.com.pagatodo.core.data.model.request.RequestSuperAstroModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.views.adapters.SuperAstroAdapter
import co.com.pagatodo.core.views.adapters.SuperAstroConfirmAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import co.com.pagatodo.core.views.components.lottery.LotteryComponentView
import com.jaredrummler.materialspinner.MaterialSpinner
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_super_astro.*
import kotlinx.android.synthetic.main.item_superastro_game.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_payment_confirm_superastro.view.*
import kotlinx.android.synthetic.main.layout_random_three_four.*

class SuperAstroActivity : BaseActivity(), SuperAstroAdapter.OnListenerAdapter {

    private var minimumTicketPrice = 0
    private var maximumTicketPrice = 0
    private var maximumAmountBets = 0
    private var minimumAmountValueBets = 0
    private var maxValueGame = 0
    private var zodiacSings: String? = null
    private var positionForRandomNumber = 0

    private lateinit var superAstroAdapter: SuperAstroAdapter
    private lateinit var superAstroViewModel: SuperAstroViewModel
    private lateinit var modalityMode: ModalityModel

    private var superAstroModelList = mutableListOf<SuperAstroModel>()
    private var selectedLotteries = listOf<LotteryModel>()
    private val subscriptions = arrayListOf<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_astro)
        setupView()
    }


    override fun onBackPressed() {
        Toast.makeText(this,"GONE",Toast.LENGTH_LONG).show()
        this.finish()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(RUtil.R_string(R.string.superastro_title))
        setupViewModel()
        initListenersViews()

        dividerVertical.visibility = View.INVISIBLE
        layoutRandom3.visibility = View.INVISIBLE
        layoutRandom4.visibility = View.INVISIBLE
        layoutRepeat.setOnClickListener {
            validateRepeatBet()
        }

    }

    private fun setupViewModel() {
        superAstroViewModel = ViewModelProviders.of(this).get(SuperAstroViewModel::class.java)
        initSubscriptions()
        initSubscriptionsForLotteries()
        superAstroViewModel.loadModalities()
    }

    @SuppressLint("CheckResult")
    private fun initSubscriptions() {
        superAstroViewModel.singleLiveEvent.observe(this, Observer { itl ->
            when (itl) {
                is SuperAstroViewModel.ViewEvent.ResponseKeyValueParameters -> {
                    setupValueParametersForProduct(itl.parameters)
                    setupRecyclerViewItemsSuperAstro()
                }
                is SuperAstroViewModel.ViewEvent.ResponseRandomNumber -> {
                    if (!itl.numbers.isEmpty()) {
                        val view: View = recyclerItemsSuperAstro.getChildAt(positionForRandomNumber)
                        val txtNumber: EditText = view.findViewById(R.id.txtNumber)
                        txtNumber.setText(itl.numbers[0])
                        val spinnerZodiacs: MaterialSpinner = view.findViewById(R.id.spinnerZodiacSigns)
                        val randomZodiacs = (1..spinnerZodiacs.getItems<String>().size - 1).random()
                        spinnerZodiacs.selectedIndex = randomZodiacs

                        superAstroModelList.get(positionForRandomNumber).zodiacSelected = spinnerZodiacs.text.toString()
                        superAstroModelList.get(positionForRandomNumber).zodiacs =  formatZodiacList()
                        hideDialogProgress()

                    }
                }
                is SuperAstroViewModel.ViewEvent.ResponseModalities -> {
                    if (!itl.modalities.isEmpty()) {
                        modalityMode = itl.modalities[0]
                        superAstroViewModel.loadProductParameters()
                    }
                }
                is SuperAstroViewModel.ViewEvent.ResponseSuccess -> {
                    hideDialogProgress()
                    itl.successMessage?.let { showOkAlertDialog("", it) { navigateToMenu(this) } }
                }
                is SuperAstroViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    itl.errorMessage?.let { showOkAlertDialog("", it) }
                }
            }
        })
    }

    private fun validateRepeatBet() {
        if (getNumbersList().isEmpty() && selectedLotteries.isEmpty()) {
            setupRepeatBet()
        } else {
            showAlertDialog(
                RUtil.R_string(R.string.title_dialog_repeat_bet),
                RUtil.R_string(R.string.message_dialog_repeat_bet), {
                    setupRepeatBet()
                }, {})
        }
    }

    private fun setupRepeatBet() {
        val currentBet = CURRENT_SUPERASTRO_BET
        if (currentBet != null) {
            superAstroModelList.clear()
            currentBet.superastroList?.forEach {
                superAstroModelList.add(SuperAstroModel().apply {
                    number = it.number
                    value = it.value
                    zodiacs = it.zodiacs
                    zodiacSelected = it.zodiacSelected
                })
            }

            selectedLotteries = currentBet.lotteries ?: mutableListOf()
            lotteryComponentView.setSelections(selectedLotteries)

            superAstroAdapter = SuperAstroAdapter(superAstroModelList, 4, this)
            recyclerItemsSuperAstro.adapter = superAstroAdapter
            recyclerItemsSuperAstro.isNestedScrollingEnabled = false

            ViewCompat.setNestedScrollingEnabled(recyclerItemsSuperAstro, false)

        } else {
            showOkAlertDialog("", RUtil.R_string(R.string.message_not_repeat_bet))
        }
    }

    private fun initSubscriptionsForLotteries() {
        subscriptions.add(
            lotteryComponentView.getObservableLotteryComponent().subscribe { response ->
                when (response.first) {
                    LotteryComponentView.LotteryAction.COMPONENT_IS_READY -> {
                        lotteryComponentView.selectCurrentDay()
                    }
                    LotteryComponentView.LotteryAction.CHECKBOX_ITEM_SELECTED -> {
                        response.second?.let {
                            selectedLotteries = it
                        }
                    }
                }
            }
        )
    }

    private fun setupValueParametersForProduct(parameters: List<KeyValueModel>) {
        parameters.forEach {
            if (it.key.equals(getString(R.string.name_column_minimum_value))) {
                minimumTicketPrice = it.value?.toInt() ?: 0
            }
            if (it.key.equals(getString(R.string.name_column_maximum_value))) {
                maximumTicketPrice = it.value?.toInt() ?: 0
            }
            if (it.key.equals(getString(R.string.name_column_maximum_bets))) {
                maximumAmountBets = it.value?.toInt() ?: 0
            }
            if (it.key.equals(getString(R.string.name_column_minimum_value_bets))) {
                minimumAmountValueBets = it.value?.toInt() ?: 0
            }
            if (it.key.equals(getString(R.string.name_column_zodiacs_signs))) {
                zodiacSings = it.value
            }
            if (it.key.equals(getString(R.string.name_column_maximun_value_bets))) {
                maxValueGame = it.value?.toInt() ?: 0
            }
        }
    }

    private fun setupRecyclerViewItemsSuperAstro() {

        val model = SuperAstroModel()
        model.zodiacs = formatZodiacList()
        superAstroModelList.add(model)
        recyclerItemsSuperAstro.apply {
            layoutManager = LinearLayoutManager(context)
        }
        superAstroAdapter = SuperAstroAdapter(superAstroModelList, 4, this)
        recyclerItemsSuperAstro.adapter = superAstroAdapter
        superAstroAdapter.setMaxValueGame(maxValueGame)
        recyclerItemsSuperAstro.isNestedScrollingEnabled = false
        ViewCompat.setNestedScrollingEnabled(recyclerItemsSuperAstro, false)
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { getDataFromRecycler() }
        btnAddGame.setOnClickListener {
            if (isValidForm()) {
                addGameItemToRecycler()
            }
        }
    }

    private fun formatZodiacList():MutableList<String>{
        val zodiacList = zodiacSings?.split(",") as MutableList<String>
        for (listZodiac in zodiacList) {
            if (listZodiac == getString(R.string.list_signos_todos)) {
                zodiacList.removeAt(12)
                zodiacList.add(0, getString(R.string.list_signos_todos))
            }
        }

        return zodiacList
    }

    private fun addGameItemToRecycler() {

        if (superAstroModelList.size < maximumAmountBets) {

            if (superAstroModelList.sumBy { it.value!!.toInt() } <= maximumTicketPrice) {

                val model = SuperAstroModel()
                model.zodiacs = formatZodiacList()
                superAstroModelList.add(model)
                superAstroAdapter.notifyItemInserted(superAstroModelList.size)
                if (superAstroModelList.size == maximumAmountBets) {
                    enableButton(false)
                }

            } else {
                showOkAlertDialog(
                    "",
                    getString(R.string.message_error_value_maximum_bet, maximumTicketPrice)
                )
            }


        }
    }

    private fun enableButton(enable: Boolean) {
        if (enable) {
            btnAddGame.isEnabled = true
            btnAddGame.setBackgroundResource(R.drawable.rounded_button_white)
            btnAddGame.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        } else {
            btnAddGame.isEnabled = false
            btnAddGame.setBackgroundResource(R.drawable.rounded_button_grey)
            btnAddGame.setTextColor(ContextCompat.getColor(this, R.color.colorGrayLight3))
        }
    }

    private fun getDataFromRecycler() {
        if (isValidForm(true)) {
            if (validateSeletedLotteries()) {
                validateRepeatedNumbers {
                    processBet()
                }
            }
        }
    }

    private fun processBet() {
        val totalValue = calculateTotalValueOfGames()
        if (validateBetValues(totalValue)) {
            showConfirmationDialog(totalValue)
        }
    }

    private fun validateBetValues(totalValue: Double): Boolean {
        return if (totalValue >= minimumTicketPrice) {
            if (totalValue <= maximumTicketPrice) {
                true
            } else {
                showOkAlertDialog(
                    "",
                    getString(R.string.message_error_value_maximum_bet, maximumTicketPrice)
                )
                false
            }
        } else {
            showOkAlertDialog(
                "",
                getString(R.string.message_error_value_minimum_bet, minimumTicketPrice)
            )
            false
        }
    }

    private fun showConfirmationDialog(totalValue: Double) {

        val totalToPay = Math.ceil( totalValue / ( CurrencyUtils.getIvaPercentage() + 1.0 ))
        val totalIva = totalValue - totalToPay

        PaymentConfirmationDialog().show(this, R.layout.layout_payment_confirm_superastro,
            RUtil.R_string(R.string.text_title_superastro_dialog), null, { view ->
                view.lblLotteries.text = getSelectedLotteryNames()
                view.lblStuf.text = getStub()
                view.lblBet.text =
                    getString(R.string.text_label_parameter_coin, formatValue(totalToPay))
                view.lblIva.text = getString(
                    R.string.text_label_parameter_coin,
                    formatValue("$totalIva".toDouble())
                )
                view.lblTotalValue.text =
                    getString(R.string.text_label_parameter_coin, formatValue(totalValue))

                view.recyclerItemsSuperAstroConfirm.apply {
                    layoutManager = LinearLayoutManager(this@SuperAstroActivity)
                }
                val superAstroConfirmAdapter = SuperAstroConfirmAdapter(superAstroModelList)
                view.recyclerItemsSuperAstroConfirm.adapter = superAstroConfirmAdapter
            }, {
                showDialogProgress(getString(R.string.message_dialog_request))
                val request = RequestSuperAstroModel().apply {
                    this.superastroList = superAstroModelList
                    this.lotteries = selectedLotteries
                    this.value = totalValue
                    this.stubs = getStub()
                    this.transactionTime = DateUtil.getCurrentDateInUnix()
                    this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                }
                isForeground = false
                superAstroViewModel.paySuperAstro(request)
            })
    }

    private fun getSelectedLotteryNames(): String {
        var names = ""
        selectedLotteries.forEachIndexed { index, item ->
            names = if (index == 0) {
                item.name ?: ""
            } else {
                names + " - " + item.name
            }
        }
        return names
    }

    private fun calculateTotalValueOfGames(): Double {
        var totalValue = 0.0
        superAstroModelList.forEach {
            totalValue += it.value ?: 0.0
        }
        totalValue *= selectedLotteries.size
        return totalValue
    }

    //TODO: Refactorizar este método
    private fun isValidForm(isNext:Boolean=false): Boolean {
        superAstroModelList.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperAstro.getChildAt(index)
            val txtNumber: EditText = view.findViewById(R.id.txtNumber)
            val txtValue: EditText = view.findViewById(R.id.txtValueGame)
            val lblErrorNumber: TextView = view.findViewById(R.id.labelErrorNumber)
            val lblErrorNumberFormat: TextView =
                view.findViewById(R.id.labelErrorNumberFormatInvalid)
            val lblErrorValue: TextView = view.findViewById(R.id.labelErrorValue)
            val lblErrorValueInvalid: TextView = view.findViewById(R.id.labelErrorValueInvalid)
            val spinnerZodiacs: MaterialSpinner = view.findViewById(R.id.spinnerZodiacSigns)
            val lblErrorZodiac: TextView = view.findViewById(R.id.labelErrorZodiac)
            if (isEmptyField(txtNumber, lblErrorNumber) && isEmptyField(
                    txtValue,
                    lblErrorValue
                ) && hasSpinnerSelectedItem(spinnerZodiacs, lblErrorZodiac)
            ) return false
            if (isEmptyField(txtNumber, lblErrorNumber) && isEmptyField(
                    txtValue,
                    lblErrorValue
                )
            ) return false
            if (isEmptyField(txtNumber, lblErrorNumber)) return false
            if (isEmptyField(txtValue, lblErrorValue)) return false
            var valueText = txtValue.text.toString()
            valueText = valueText.replace("$", "").replace(".", "")
            if (isValidAmountBet(valueText.toDouble())) return false
            if (!validateLimitDigits(txtNumber, lblErrorNumberFormat)) return false
            if (isOneHundredMultiple(txtValue, lblErrorValueInvalid)) return false
            if (validateMinimumAmountBet(txtValue)) return false
            if(isNext)
                if (hasSpinnerSelectedItem(spinnerZodiacs, lblErrorZodiac)) return false
        }
        return true
    }

    private fun validateSeletedLotteries(): Boolean {
        if (selectedLotteries.isEmpty()) {
            showOkAlertDialog(
                getString(R.string.title_dialog_select_bet),
                getString(R.string.message_dialog_select_bet)
            )
            return false
        }
        return true
    }

    private fun isValidAmountBet(value: Double): Boolean {
        if (value < minimumAmountValueBets) {
            showOkAlertDialog(
                "",
                getString(R.string.message_error_value_minimum_bet, minimumAmountValueBets)
            )
            return true
        }
        return false
    }

    private fun isEmptyField(editText: EditText, textError: TextView): Boolean {
        return if (editText.text.isEmpty()) {
            textError.visibility = View.VISIBLE
            true
        } else {
            textError.visibility = View.GONE
            false
        }
    }

    private fun isOneHundredMultiple(txtValue: EditText, lblErrorValueInvalid: TextView): Boolean {
        var originalString = txtValue.text.toString()
        originalString = originalString.replace("$", "").replace(".", "")
        val valueOperation = originalString.toDouble()
        val valueValidate = valueOperation.toInt() % 50
        return if (valueValidate != 0) {
            lblErrorValueInvalid.visibility = View.VISIBLE
            lblErrorValueInvalid.text = getString(R.string.message_error_value_no_validate)
            true
        } else {
            lblErrorValueInvalid.visibility = View.GONE
            false
        }
    }

    private fun validateLimitDigits(txtNumber: EditText, lblErrorNumberFormat: TextView): Boolean {
        return if (txtNumber.text.length < 4) {
            lblErrorNumberFormat.visibility = View.VISIBLE
            lblErrorNumberFormat.text = getString(R.string.message_error_number_no_valid)
            false
        } else {
            lblErrorNumberFormat.visibility = View.GONE
            true
        }
    }

    private fun validateMinimumAmountBet(txtValue: EditText): Boolean {
        var originalString = txtValue.text.toString()
        originalString = originalString.replace("$", "").replace(".", "")

        return if (originalString.toInt() < minimumAmountValueBets) {
            showOkAlertDialog(
                "",
                getString(R.string.message_error_value_minimum_bet, minimumAmountValueBets)
            )
            false
        } else {
            false
        }
    }

    private fun hasSpinnerSelectedItem(
        spinnerZodiacs: MaterialSpinner,
        lblErrorZodiac: TextView
    ): Boolean {
        return if (spinnerZodiacs.selectedIndex == 0) {
            lblErrorZodiac.visibility = View.VISIBLE
            true
        } else {
            lblErrorZodiac.visibility = View.GONE
            false
        }
    }

    private fun validateRepeatedNumbers(closure: () -> Unit) {
        var isValid = true
        val numbers = getNumbersList()
        numbers.forEachIndexed { index, item ->
            val filteredValues =
                numbers.filterIndexed { internalIndex, _ -> internalIndex != index }
            val isExist = isExistValueInArray(item, filteredValues)
            if (isExist) {
                isValid = false
                return@forEachIndexed
            }
        }

        if (isValid) {
            closure()
        } else {
            showOkAlertDialog("", getString(R.string.message_error_duplicate_number))
        }
    }

    private fun getNumbersList(): List<SuperAstroModel> {
        val models = arrayListOf<SuperAstroModel>()
        val data = (recyclerItemsSuperAstro.adapter as SuperAstroAdapter).data
        data.forEachIndexed { index, _ ->
            val view: View = recyclerItemsSuperAstro.getChildAt(index)
            val etNumber = view.txtNumber
            val spinner = view.spinnerZodiacSigns
            if (etNumber.text.toString().isNotBlank()) {
                models.add(
                    SuperAstroModel().apply {
                        number = etNumber.text.toString()
                        zodiacSelected = spinner.text.toString()
                    }
                )
            }
        }
        return models
    }

    private fun isExistValueInArray(item: SuperAstroModel, array: List<SuperAstroModel>): Boolean {
        var isExist = false
        array.forEach {
            if (item.number == it.number && item.zodiacSelected.equals(it.zodiacSelected)) {
                isExist = true
                return@forEach
            }
        }
        return isExist
    }

    override fun onGenerateRamdon(position: Int) {
        if (!superAstroModelList.isEmpty()) {
            showDialogProgress(RUtil.R_string(R.string.message_dialog_generate_random))
            positionForRandomNumber = position
            superAstroViewModel.loadRandomNumbers(1, 4)
        }
    }

    override fun onDeleteGameItem(position: Int) {
        if (position != 0) {
            superAstroModelList.removeAt(position)
            if (superAstroModelList.size == maximumAmountBets) {
                enableButton(false)
            } else {
                enableButton(true)
            }
        }
    }
}
