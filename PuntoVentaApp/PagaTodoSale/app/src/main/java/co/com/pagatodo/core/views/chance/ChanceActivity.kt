package co.com.pagatodo.core.views.chance

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ProductModel
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.request.RequestChanceModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.ChanceAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.base.saveLastTransaction
import co.com.pagatodo.core.views.components.daylottery.DayLotteryComponentView
import co.com.pagatodo.core.views.components.dialogs.chancepayment.ChancePaymentDialogFragment
import co.com.pagatodo.core.views.components.lottery.LotteryComponentView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_chance.*
import kotlinx.android.synthetic.main.item_change_game.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_chance_summary.*
import kotlinx.android.synthetic.main.layout_change_games.*
import kotlinx.android.synthetic.main.layout_random_three_four_chance.*
import java.util.*


/**
* Esta clase es la actividad principal del producto chance, en esta clase encontraremos los métodos necesarios
* para comunicarse con la interfaz del usuario y el viewmodel, esta clase hereda de la clase BaseActivity
*
*/
class ChanceActivity : BaseActivity(),
    DayLotteryComponentView.OnDayLotteryListener,
    ChanceAdapter.OnChanceGamesListener,
    ChancePaymentDialogFragment.OnChancePaymentDialogListener {

    //Viewmodel de la respectiva clase
    private lateinit var chanceViewModel: ChanceViewModel
    //Lista de subscripciones, este array es usado para obtener las respuestas de lotteryComponent u otros componentes
    //en caso de ser necesario
    private var subscriptions = arrayListOf<Disposable>()
    //Listado de las loterias que han sido seleccionadas
    private var selectedLotteries = listOf<LotteryModel>()
    //Información del producto, como cantidad maxima de apuestas
    private var productModel: ProductModel? = null
    //Actual fila seleccionada donde se digitan las apuestas
    private var chanceRowSelected: Int = 0
    //Dialogo usado para confirmar la apuestas y digitar el valor sugerido
    private var confirmDialog: ChancePaymentDialogFragment? = null
    //Validación en caso de repetir la apuesta
    private var isRepeatBet = false
    //validacion siwtch al deshabilitar checks
    private var isUnCheckChild:Boolean=true
    //validacion en caso de no encontrar loterias / no pertimitir activar siwtch
    private var resultLotteries:Boolean=true

    /**
     * Método llamado al momento de crear la actividad, necesario para inicializar los elementos de la interfaz,
     * el viewmodel, y para cargar la información del producto.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chance)

        setupViewModel()
        setupView()
        requestPrinterPermission()
        chanceViewModel.fetchChanceProduct()


    }

    /**
     * Método llamado al momento en que se ingresa a la actividad.
     * También es llamado cuando la aplicación vuelve del background.
     */
    override fun onResume() {
        super.onResume()
        initSubscription()
    }

    /**
     * Método llamado al momento de abandonar la actividad.
     */
    override fun onPause() {
        super.onPause()
        subscriptions.forEach { it.dispose() }
    }

    /**
     * Método usado para cargar los datos del producto en la interfaz
     */
    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.chance_title))
        daysView.listener = this
        //Se le asigna el valor a isRepeatBet que se carga desde los extras
        isRepeatBet = (intent.getBooleanExtra(R_string(R.string.shared_key_repeat_bet),false) && CURRENT_CHANCE_BET != null)

        updateStubInSummaryView()

        btnBack.setOnClickListener { onBackPressed() }
        btnNext.setOnClickListener { showConfirmationDialog() }

        //Actualizar las apuestas del día actual en la interfaz
        updateDateInRaffleView(daysView.getSelectedDate())
        updateLotteryCount(0)
        setupChanceGame()

        layoutRandom3.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            chanceViewModel.dispatchGenerateRandom(1, 3) }
        layoutRandom4.setOnClickListener {
            showDialogProgress(R_string(R.string.message_dialog_generate_random))
            chanceViewModel.dispatchGenerateRandom(1, 4) }
        layoutRepeat.setOnClickListener { validateRepeatChance() }

        initChangeListenerSwitchLotteries()

        btnClearSP.setOnClickListener {
            setupChanceGame()
        }

        btnClear.setOnClickListener {
            setupChanceGame()
        }


    }

    private fun  initChangeListenerSwitchLotteries(){
        switchLotteries.setOnCheckedChangeListener { buttonView, isChecked ->

            if (!resultLotteries) {
                switchLotteries.isChecked=false
            }else{
                if (isUnCheckChild) {
                    updateLotteryCount(lotteryComponent.checkAllLoterias(isChecked, maxLotteries()))
                }
                if (!isChecked) {
                    updateLotteryCount(0)
                    switchLotteries.isChecked = false
                }
            }

        }

    }

    /**
     * Método utilizado para obtener el maximo de loterias
     */
    private fun maxLotteries():Int{
        val item = productModel?.parameters?.filter { it.key == R_string(R.string.sp_max_amount_lotteries_param_service) }
        val maxLotteries = item?.first()?.value ?: "-1"
        return maxLotteries.toInt()
    }

    private fun validateRepeatChance(){
        val chanceValidation = ChanceValidations(
            selectedLotteries,
            getValidGamesOfChance(),
            productModel,
            ChanceValidations.getAllValidationType()
        )
        if (chanceValidation.isValidFieldsChance()){
            showAlertDialog(R_string(R.string.title_dialog_repeat_bet), R_string(R.string.message_dialog_repeat_bet), {
                showDialogProgress("mensaje")
                setupValuesToRepeatBet()
            }, {})
        }else{
            showDialogProgress("mensaje")
            setupValuesToRepeatBet()
        }
    }

    /**
     * Método utilizado en caso de que se vaya a repetir la apuesta
     */
    private fun setupValuesToRepeatBet(){
        //Cargar la ultima apuesta realizada en chance
        val currentBet = CURRENT_CHANCE_BET
        if (currentBet != null){
            //Cargar el día de la ultima apuesta realizada y asignarlo al componente daysView para verse reflejado en la interfaz
            val day = currentBet?.lotteries?.first()?.lotteryDay
            if (!day.equals(R_string(R.string.chance_weekly_day)) && !day.equals(R_string(R.string.chance_weekly_day_without_zero)))
                daysView.selectCustomDayAction(daysView.getDayFromNumber(day?.toInt() ?: 0))

            //Cargar las loterias seleccionadas en la ultima apuesta
            selectedLotteries = currentBet?.lotteries ?: arrayListOf()
            //Cargar la lista de las apuestas realizadas
            val chanceGames = mutableListOf<ChanceModel>()
            currentBet?.chances?.forEach {
                chanceGames.add(ChanceModel().apply {
                    number = it.number
                    direct = it.direct
                    combined = it.combined
                    paw = it.paw
                    nail = it.nail
                })
            }
            //Completar la lista con elementos vacíos

            val  rows =  productModel?.parameters?.filter { it.key == R_string(R.string.key_max_size_bet) }?.last()?.value?.toInt()?:5

            for (i in 1..(rows-chanceGames.count())) {
                chanceGames.add(ChanceModel())
            }
            //Actualizar la interfaz con los datos de la última apuesta

            updateAdapterForChanceGamesRepeatBet(chanceGames)
            lotteryComponent.setSelections(selectedLotteries)
            tvLotteries.text = selectedLotteries.size.toString()

            if(selectedLotteries.count()==lotteryComponent.adapter.data.size || selectedLotteries.count()==maxLotteries() ){
                switchLotteries.setOnCheckedChangeListener(null)
                switchLotteries.isChecked = true
                initChangeListenerSwitchLotteries()
            }

            hideDialogProgress()

        }else{
            hideDialogProgress()
            showOkAlertDialog(R_string(R.string.title_message_not_repeat_bet), R_string(R.string.message_not_repeat_bet))
        }
    }

    /**
     * Método utilizado para actualizar la colilla en la interfaz
     */
    fun updateStubInSummaryView() {
        tvStub.text = getStub()
    }

    /**
     * Método utlizado para inicializar el viewmodel, el cual se encarga de realizar los llamados al interactor
     * También es usado para el manejo de la información del producto
     */
    private fun setupViewModel() {
        chanceViewModel = ViewModelProviders.of( this).get(ChanceViewModel::class.java)
        //Se crea un observador para el momento de generar un número aleatorio
        chanceViewModel.randomNumberLiveData.observe(this, Observer<List<String>> { numbers ->
            if (numbers.isNotEmpty()) {
                val chanceAdapter = (rvChanceGames.adapter as ChanceAdapter)
                chanceAdapter.data[chanceRowSelected].number = "${numbers[0]}"
                val view: View = rvChanceGames.getChildAt(chanceRowSelected)
                hideDialogProgress()
                view.etNumber.text = SpannableStringBuilder("${numbers[0]}")
            }
        })

        //Se crea un observable para mostrar las respuestas de los servicios en la interfaz de usuario
        chanceViewModel.singleLiveEvent.observe(this, Observer {
            progressDialog.dismiss()
            confirmDialog?.dismiss()
            when(it) {
                //Respuesta cuando el pago es exitoso
                is ChanceViewModel.ViewEvent.PaySuccess -> {
                    updateStubInSummaryView()
                    hideDialogProgress()
                    showOkAlertDialog("", it.successMessage) {
                        navigateToMenu(this)
                    }
                }
                //Respuesta cuando ocurrió un error en el pago
                is ChanceViewModel.ViewEvent.PayError -> {
                    hideDialogProgress()
                    showOkAlertDialog("", it.errorMessage)
                }
                //Cargar la información del producto
                is ChanceViewModel.ViewEvent.ChanceProductInfo -> {
                    productModel = it.productModel
                    updateViewAfterLoadDataFromViewModel()
                    setupChanceGame()
                }
            }
        })
    }
    /**
     * Método utilizado para actualizar la vista despues de cargar la información del producto en el viewmodel
     */
    private fun updateViewAfterLoadDataFromViewModel() {
        val item = productModel?.parameters?.filter { it.key == R_string(R.string.sp_max_amount_lotteries_param_service) }
        val maxLotteries = item?.first()?.value ?: "-1"
        //Ajustar la cantidad maxima de loterias a seleccionar
        lotteryComponent.setMaxNumberSelection(maxLotteries.toInt())

    }

    /**
     * Iniciar la subscripción a los eventos del componenente de las loterías
     */
    private fun initSubscription() {

        subscriptions.add(
            lotteryComponent.getObservableLotteryComponent().subscribe { response ->
                when(response.first) {
                    //Evento cuando el componente se inicializa
                    LotteryComponentView.LotteryAction.COMPONENT_IS_READY -> {
                        lotteryComponent.selectCurrentDay()
                        updateViewAfterLoadDataFromViewModel()
                    }

                    //Evento cuando se selecciona una lotería
                    LotteryComponentView.LotteryAction.CHECKBOX_ITEM_SELECTED -> {
                        response.second?.let {
                            selectedLotteries = it
                            updateLotteryCount(selectedLotteries.count())

                            // todos los check en on, 1 hijo o mas off -> switch off
                            if(selectedLotteries.count()< lotteryComponent.adapter.data.size ){
                                isUnCheckChild=false
                                switchLotteries.isChecked=false

                            }
                            else if(selectedLotteries.count()==lotteryComponent.adapter.data.size || selectedLotteries.count()==maxLotteries() ){
                                switchLotteries.isChecked=true

                            }
                             isUnCheckChild=true

                            calculateValuesOfSummary()

                        }


                    }

                }
            }
        )

    }

    override fun onBackPressed() {
        Toast.makeText(this,"GONE",Toast.LENGTH_LONG).show()
        lotteryComponent.visibility = View.GONE
        this.finishAndRemoveTask()
    }


    /**
     * Actualizar en la interfaz la cantidad de loterías seleccionadas
     */
    private fun updateLotteryCount(count: Int) {
        tvLotteries.text = "$count"
    }

    /**
     * Actualizar el adaptador de las apuestas
     */
    private fun updateAdapterForChanceGames(chanceGames: List<ChanceModel>) {
        val chanceAdapter = ChanceAdapter(chanceGames).apply {
            setOnChanceGameListener(this@ChanceActivity)
        }
        rvChanceGames.adapter = chanceAdapter
    }

    /**
     * Actualizar el adaptador de las apuestas
     */
    private fun updateAdapterForChanceGamesRepeatBet(chanceGames: List<ChanceModel>) {

        rvChanceGames.adapter
        val chanceAdapter = ChanceAdapter(chanceGames,true).apply {
            setOnChanceGameListener(this@ChanceActivity)
        }
        rvChanceGames.adapter = chanceAdapter
    }

    /**
     * Asignar los juegos a la grilla
     */
    private fun setupChanceGame() {


        val  rows =  productModel?.parameters?.filter { it.key == R_string(R.string.key_max_size_bet) }?.last()?.value?.toInt()?:5

        val chanceGames = mutableListOf<ChanceModel>()
        for (i in 1..rows) {
            chanceGames.add(ChanceModel())
        }
        rvChanceGames.apply {
            layoutManager = LinearLayoutManager(this@ChanceActivity)
            setHasFixedSize(true)
        }
        updateAdapterForChanceGames(chanceGames)
    }

    /**
     * Calcular el valor de las apuestas
     */
    private fun calculateValuesOfSummary() {
        var countSelectedLotteries = selectedLotteries.count()
        countSelectedLotteries = if(countSelectedLotteries == 0) 1 else countSelectedLotteries
        val valueWithoutIva = getTotalBetWithoutIva() * countSelectedLotteries
        val iva = (valueWithoutIva * CurrencyUtils.getIvaPercentage()).toInt()
        tvBet.text = getString(R.string.text_label_parameter_coin, formatValue("$valueWithoutIva".toDouble()))
        tvIva.text = getString(R.string.text_label_parameter_coin, formatValue("$iva".toDouble()))
        tvTotal.text = getString(R.string.text_label_parameter_coin, formatValue("${(valueWithoutIva + iva)}".toDouble()))
    }

    /**
     * Evento al momento de cambiar el día
     */
    override fun onSelectedDay(day: Int, dayName: String, date: Date) {
        //reset off switch
        switchLotteries.isChecked=false
        resetDataWhenDayChanges()
        lotteryComponent.applyFilterByDay(day)
        updateDateInRaffleView(date)

        //validacion para no poder habilitar switch si no hay loterias
        resultLotteries=lotteryComponent.adapter.data.isNotEmpty()


        if (!isRepeatBet){
            calculateValuesOfSummary()
        }
    }

    /**
     * Actualizar la fecha seleccionada en la interfaz
     */
    private fun updateDateInRaffleView(date: Date) {
        tvRaffleDay.text = "${DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, date)}"
    }

    /**
     * Evento llamado al momento de cambiar los valores en la grilla,
     * este evento calcula a la vez los valores de la apuesta
     */
    override fun onTextChanged(adapterPosition: Int, fieldType: ChanceAdapter.ChanceGameType, text: String) {
        calculateValuesOfSummary()
    }

    /**
     * Evento al momento de cambiar de fila
     */
    override fun onSelectedRow(row: Int) {
        chanceRowSelected = if(row >= 0) row else 0
    }

    /**
     * Evento llamado al momento de realizar apuesta
     */
    override fun onPayChance(value: Int, suggestedValue: Int) {
        hideKeyboard()
        //Validar las loterias seleccionadas
        validateLotterySelectionAndContinue {
            val chanceGames = getValidGamesOfChance()
            val raffleDate = tvRaffleDay.text.toString()
            showDialogProgress(R_string(R.string.message_dialog_request))
            val request = RequestChanceModel().apply {
                this.chances = chanceGames
                this.lotteries = selectedLotteries
                this.valueWithoutIva = value.toDouble()
                this.suggestedValue = suggestedValue.toDouble()
                this.raffleDate = raffleDate
                this.stubs = getStub()
                this.transactionTime = DateUtil.getCurrentDateInUnix()
                this.transactionTime = DateUtil.getCurrentDateInUnix()
                this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                this.maxRows = productModel?.parameters?.filter { it.key == R_string(R.string.key_max_size_bet) }?.last()?.value?.toInt()?:5
            }
            saveLastTransaction(request, ProductName.CHANCE.rawValue)
            isForeground = false
            chanceViewModel.dispatchPayChance(request)
        }
    }

    /**
     * Obtener los juegos validos
     */
    private fun getValidGamesOfChance(): List<ChanceModel> {
        return (rvChanceGames.adapter as ChanceAdapter).data.filter { it.number?.isNotEmpty() ?: false }
    }

    /**
     * Método utilizado para validas las loterias seleccionadas
     */
    private fun validateLotterySelectionAndContinue(closure: () -> Unit?) {
        if(selectedLotteries.count() > 0) {
            closure()
        }
        else {
            Log.d( R_string(R.string.title_message_error_lottery_selected), R_string(R.string.message_error_lottery_selected))
        }
    }

    /**
     * Método usado para mostrar el modal de confirmación de pago
     * tambien es donde se puede realizar el ajuste al valor que se va apostar
     * el valor minimo apostar es el indicado en el modal de pago
     */
    private fun showConfirmationDialog() {
        hideKeyboard()
        //Crear la varible encargada de realizar las validaciones del las loterias, los numeros, y los valores
            val chanceValidation = ChanceValidations(
                selectedLotteries,
                getValidGamesOfChance(),
                productModel,
                ChanceValidations.getAllValidationType()
            )
            chanceValidation.isValidChance {
            if (!it.first) {
                var title=""
                var message=it.second
                //se delimita por - para obtener titulo y mensaje
                val splitMessage=it.second.split("-")
                //validacion si el mensaje esta delimitado por -
                if(splitMessage.size>1){
                       title=splitMessage[0]
                       message=splitMessage[1]
                }
                showOkAlertDialog(title, message)
            }else {
                var countSelectedLotteries = selectedLotteries.count()
                countSelectedLotteries = if(countSelectedLotteries == 0) 1 else countSelectedLotteries
                val valueWithoutIva = getTotalBetWithoutIva() * countSelectedLotteries

                val numbers = StringBuilder()
                getValidChances().forEach {
                    numbers.append("${it.number}-")
                }
                val responseNumbers = numbers.dropLast(1)

                val minValueM = productModel?.parameters?.filter { it.key == R_string(R.string.key_min_value_ticket)}?.last()?.value?:0

                val bundle = Bundle().apply {
                    putString(R_string(R.string.bundle_value_raffle_date), tvRaffleDay.text.toString())
                    putString(R_string(R.string.bundle_value_lotteries_number), tvLotteries.text.toString())
                    putString(R_string(R.string.bundle_value_numbers), responseNumbers.toString())
                    putString(R_string(R.string.bundle_value_stubs), getStub())
                    putInt(R_string(R.string.bundle_value_without_Iva), valueWithoutIva)
                    putString(R_string(R.string.bundle_value_min_amount), minValueM.toString())
                }
                confirmDialog = ChancePaymentDialogFragment.newInstance(bundle)
                confirmDialog?.setOnChancePaymentDialogListener(this)
                confirmDialog?.show(supportFragmentManager, ChancePaymentDialogFragment::class.java.simpleName)
            }
        }
    }

    /**
     * Obtener el listado de los numeros de chance válidos
     */
    private fun getValidChances(): List<ChanceModel> {
        val chances = (rvChanceGames.adapter as ChanceAdapter).data
        val arrayNumbers = arrayListOf<ChanceModel>()
        chances.forEachIndexed { index, item ->
            val view = rvChanceGames.getChildAt(index)
            arrayNumbers.add(
                ChanceModel().apply {
                    number = view.etNumber.text.toString().removeLeadingZero()
                    direct = view.etDirect.text.toString().removeLeadingZero()
                    combined = view.etCombined.text.toString().removeLeadingZero()
                    paw = view.etPaw.text.toString().removeLeadingZero()
                    nail = view.etNail.text.toString().removeLeadingZero()
                }
            )
        }
        return arrayNumbers.filter { it.number?.isNotBlank() == true }
    }

    /**
     * Obtener el valor total de la apuesta sin iva
     */
    private fun getTotalBetWithoutIva(): Int {
        var numResponse = 0
        val data = getValidChances()
        data.forEach {
            val direct = getIntOrNull(it.direct)
            val combined = getIntOrNull(it.combined)
            val paw = getIntOrNull(it.paw)
            val nail = getIntOrNull(it.nail)
            numResponse += direct + combined + paw + nail
        }
        return numResponse
    }

    /**
     * Actualizar los datos de los chances al momento de cambiar la fecha
     */
    private fun resetDataWhenDayChanges() {
        selectedLotteries = listOf()
        updateLotteryCount(0)
    }
}
