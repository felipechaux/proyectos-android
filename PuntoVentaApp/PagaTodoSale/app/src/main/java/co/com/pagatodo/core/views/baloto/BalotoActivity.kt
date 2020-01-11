package co.com.pagatodo.core.views.baloto

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.BalotoBoardModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.adapters.BalotoAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import kotlinx.android.synthetic.main.activity_baloto.*
import kotlinx.android.synthetic.main.dialog_baloto_pay.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class BalotoActivity : BaseActivity(), BalotoAdapter.OnActionListener {

    lateinit var viewModel: BalotoViewModel
    private var numberDraw = 1
    private var data = arrayListOf<BalotoBoardModel>()
    private var adapter: BalotoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baloto)

        setupView()
        setupModel()
        setupReciclerView()
    }

    private fun setupModel(){
        viewModel = ViewModelProviders.of(this).get(BalotoViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            when(it){
                is BalotoViewModel.ViewEvent.ShowMessage -> {
                    hideDialogProgress()
                    showOkAlertDialog("",it.message){
                        navigateToMenu(this)
                    }
                }
                is BalotoViewModel.ViewEvent.ValidationReturn -> {
                    if (it.response.first){
                        showDialogToPay()
                    }else if (it.response.second.isNotEmpty()){
                        showOkAlertDialog("",it.response.second)
                    }
                }
            }
        })

        viewModel.boardListLiveData.observe(this, Observer {
            updateBoardLists()
        })

        showDialogProgress(R_string(R.string.message_dialog_load_data))
        viewModel.getParameters()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(R_string(R.string.title_view_baloto))

        btnAddBalotoBoard.setOnClickListener { addPanel() }
        btNumberIncrementDraw.setOnClickListener { incrementOrDecrementNumberDraw(true) }
        btNumberDecrementDraw.setOnClickListener { incrementOrDecrementNumberDraw(false) }
        btnNext.setOnClickListener { tapButtonNext() }
        btnBack.setOnClickListener { finish() }
    }

    private fun setupReciclerView(){
        rvBalotoBoards.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        adapter = BalotoAdapter(viewModel.boardListLiveData.value ?: mutableListOf())
        adapter?.setListener(this)
        rvBalotoBoards.adapter = adapter
    }

    private fun addItemToRecyclerView(item: BalotoBoardModel) {
        adapter?.data?.add(item)
        adapter?.notifyItemInserted(data.count() - 1)
    }

    private fun deleteItemFromRecyclerView(index: Int) {
        adapter?.data?.removeAt(index)
        adapter?.notifyItemRemoved(index)
    }

    private fun tapButtonNext(){
        viewModel.validateNumbersFromBoards()
    }

    private fun showDialogToPay(){
        PaymentConfirmationDialog().show(this, R.layout.dialog_baloto_pay,
            R_string(R.string.title_ticket_baloto), null, { view ->
                val numbers = viewModel.getNumbersFromList()
                view.dialogPayBalotoTitleNumber.text = numbers.first
                view.dialogPayBalotoNumber.text = numbers.second
                view.dialogPayBalotoTotalValue.text = "$ ${formatValue(viewModel.getTotalBet().toDouble())}"
            },{
                showDialogProgress(getString(R.string.message_dialog_request))
                viewModel.dispatchPayBalotoBet(viewModel.getRequestBalotoModel())
            })
    }

    private fun addPanel(){
        val item = BalotoBoardModel().apply {
            selections = arrayOf("","","","","","")
            quickpick = false
            addonPlayed = false
            stake = viewModel.parametersModel.basePrice
            primarySelectionsLowNumber = viewModel.parametersModel.primarySelectionsLowNumber
            primarySelectionsHighNumber = viewModel.parametersModel.primarySelectionsHighNumber
            secondarySelectionsLowNumber = viewModel.parametersModel.secondarySelectionsLowNumber
            secondarySelectionsHighNumber = viewModel.parametersModel.secondarySelectionsHighNumber
        }
        addItemToRecyclerView(item)
        updateBoardLists()

        rvBalotoBoards.scrollToPosition(adapter?.data?.count() ?: 0)
        switchStatusAddBoardButton()
    }

    private fun incrementOrDecrementNumberDraw(increment: Boolean){
        val shouldIncrement = viewModel.isInitializedParameters() && increment && numberDraw < viewModel.maxDuration
        if (shouldIncrement){
            numberDraw += 1
        }else if (!increment && numberDraw > 1){
            numberDraw -= 1
        }
        viewModel.betDuration = numberDraw
        etNumberDraw.setText(numberDraw.toString())
    }

    private fun switchStatusAddBoardButton(){
        if (!adapter?.data.isNullOrEmpty()
            && adapter?.data?.count()!! < viewModel.maxBoards){
            btnAddBalotoBoard.isEnabled = true
            btnAddBalotoBoard.background = getDrawable(R.drawable.rounded_button_white)
            btnAddBalotoBoard.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        }else{
            btnAddBalotoBoard.isEnabled = false
            btnAddBalotoBoard.background = getDrawable(R.drawable.but_background_gray)
            btnAddBalotoBoard.setTextColor(ContextCompat.getColor(this, R.color.colorGrayLight3))
        }
    }

    private fun updateBoardLists(){
        hideDialogProgress()
        if (adapter != null){
            setupReciclerView()
        }
        val header = getString(R.string.baloto_text_info,viewModel.primarySelectionsLowNumber,
            viewModel.primarySelectionsHighNumber,
            viewModel.secondarySelectionsLowNumber,
            viewModel.secondarySelectionsHighNumber,
            formatValue(viewModel.basePrice.toDouble()),
            formatValue(viewModel.getBalotoRematchSum().toDouble()))

        textBalotoInfo.text = header.replace("/","|")
    }

    override fun onDeleteBoard(index: Int?) {
        if (index != null) {
            deleteItemFromRecyclerView(index)
            switchStatusAddBoardButton()
            rvBalotoBoards.scrollToPosition(index)
        }
    }

    override fun onSetAutomaticBoard(index: Int?, isAutomatic: Boolean) {
        viewModel.setAutomaticBoard(index ?: 0, isAutomatic)
    }

}


