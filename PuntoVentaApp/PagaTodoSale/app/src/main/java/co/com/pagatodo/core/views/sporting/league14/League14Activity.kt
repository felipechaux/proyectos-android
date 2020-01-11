package co.com.pagatodo.core.views.sporting.league14

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.data.model.request.RequestSportingModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.adapters.League14Adapter
import co.com.pagatodo.core.views.adapters.League14SummaryAdapter
import co.com.pagatodo.core.views.base.SportingBaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import co.com.pagatodo.core.views.sporting.SportingViewModel
import co.com.pagatodo.core.views.sporting.sellSportingBets
import kotlinx.android.synthetic.main.activity_league14.*
import kotlinx.android.synthetic.main.dialog_pay_sportings.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_header_sportings.*
import kotlinx.android.synthetic.main.layout_subheader_megagoals.*
import kotlin.concurrent.thread

class League14Activity : SportingBaseActivity() {

    private lateinit var viewModel: SportingViewModel
    private var indexScroll: Int = 0
    private var numberOfRows = if (DeviceUtil.isSalePoint()) 5 else 7
    var teamAdapter: League14Adapter? = null
    var summaryAdapter: League14SummaryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league14)
        setupView()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(SportingViewModel::class.java)

        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when (it) {
                is SportingViewModel.ViewEvent.ResponseMessage -> {
                    showOkAlertDialog("", it.message ?: R_string(R.string.message_success_bet)) {
                        if (it.backToMenu == true) {
                            navigateToMenu(this)
                        }
                    }
                }
            }
        })

        viewModel.sportingEventModel.observe(this, Observer {
            setupHeader()
            setupRecyclerViews(it)
            hideDialogProgress()
        })

        showDialogProgress(R_string(R.string.text_load_data))
        viewModel.getParameters(R_string(R.string.code_sporting_league_product).toInt())
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.league_14_title))
        btnDelete.visibility = View.VISIBLE

        btnRandom.setOnClickListener {
            generateRandomGame()
        }

        btnBack.setOnClickListener {
            tapBack()
        }

        btnNext.setOnClickListener {
            tapNext()
        }

        btnDelete.setOnClickListener {
            clearResults()
        }
    }

    private fun clearResults() {
        viewModel.clearResults()
        viewModel.sportingEventModel.observe(this, Observer {
            updateAdapter(it)
        })

    }

    private fun setupHeader() {
        tvGrid.text = viewModel.codeGrid
        tvAccumulated.text = "$ ${formatValue(viewModel.accumulated.toDouble())}"
        tvClosingSales.text = viewModel.closeDate
    }

    private fun updateAdapter(listBets: List<SportingBetModel>){
        rvLeague14.apply {
            layoutManager =
                GridLayoutManager(context, numberOfRows, GridLayoutManager.HORIZONTAL, false)
            canScrollHorizontally(GridLayoutManager.HORIZONTAL)
        }
        teamAdapter = League14Adapter(listBets)
        rvLeague14.adapter = teamAdapter
        rvLeague14.isNestedScrollingEnabled = false
    }

    private fun setupRecyclerViews(listBets: List<SportingBetModel>) {
        rvLeague14.apply {
            layoutManager =
                GridLayoutManager(context, numberOfRows, GridLayoutManager.HORIZONTAL, false)
            canScrollHorizontally(GridLayoutManager.HORIZONTAL)
        }

        if (DeviceUtil.isSalePoint())
            paddingForSportingItems(0, 0, 0, 0, rvLeague14)

        teamAdapter = League14Adapter(listBets)
        rvLeague14.adapter = teamAdapter
        rvLeague14.isNestedScrollingEnabled = false

        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(rvLeague14)

        rvLeague14.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }
        })

        rvLeague14Summary.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun generateRandomGame() {
        viewModel.generateRandomGameLeague14()
        teamAdapter?.notifyDataSetChanged()
    }

    private fun tapNext() {
        when (indexScroll) {
            0 -> {
                if (DeviceUtil.isSalePoint()) {
                    if (viewModel.validateAllGamesIsChecked()) {
                        showSummaryAdapter()
                    } else {
                        showOkAlertDialog(
                            R_string(R.string.title_incomplete_game_dialog),
                            R_string(R.string.text_title_incomplete_game_dialog)
                        )
                    }
                } else {

                    val isValid = viewModel.validatePositionGamesIsChecked(6)

                    if(isValid){

                        rvLeague14.smoothScrollToPosition(
                            (viewModel.sportingEventModel.value?.count() ?: 14) - 1
                        )
                        indexScroll += 1

                    }else{
                        showOkAlertDialog(
                            R_string(R.string.title_incomplete_game_dialog),
                            R_string(R.string.text_title_incomplete_game_dialog)
                        )
                    }



                }
            }
            1 -> {
                if (viewModel.validateAllGamesIsChecked()) {
                    showSummaryAdapter()
                } else {
                    showOkAlertDialog(
                        R_string(R.string.title_incomplete_game_dialog),
                        R_string(R.string.text_title_incomplete_game_dialog)
                    )
                }
            }
            2 -> {
                showPayModal()
            }
        }
    }

    private fun tapBack() {

        when (indexScroll) {
            0 -> {
                onBackPressed()
            }
            1 -> {
                rvLeague14.smoothScrollToPosition(0)
                indexScroll -= 1
            }
            2 -> {
                containerL14Games.visibility = View.VISIBLE
                containerL14Summary.visibility = View.GONE
                rvLeague14.smoothScrollToPosition(
                    (viewModel.sportingEventModel.value?.count() ?: 14) - 1
                )
                indexScroll -= 1
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showPayModal() {
        val bet = Math.floor(viewModel.getIvaBet())
        val ivaBet = Math.floor(viewModel.getBetValue() - bet)
        val totalValue = viewModel.getBetValue()

        PaymentConfirmationDialog().show(this, R.layout.dialog_pay_sportings,
            R_string(R.string.title_confirm_recharge_league14), null, { view ->
                view.textDrawDate.text = viewModel.closeDate.split(" ")[0]
                view.textGameDate.text = R_string(R.string.league_14_title)
                view.textGrid.text = viewModel.codeGrid
                view.textBet.text = "$${formatValue(bet)}"
                view.textIva.text = "$${formatValue(ivaBet)}"
                view.textTotalValue.text = "$${formatValue(totalValue.toDouble())}"
            }, {
                showDialogProgress(R_string(R.string.message_dialog_request))
                val request = RequestSportingModel().apply {
                    this.productCode = R_string(R.string.code_sporting_league_product).toInt()
                    this.image =
                        BitmapFactory.decodeResource(resources, R.drawable.ic_sportings_footer)
                    this.productStatusCode = ProductName.LEAGUE14.rawValue
                    this.transactionTime = DateUtil.getCurrentDateInUnix()
                    this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                }
                viewModel.sellSportingBets(request)
            })
    }

    private fun showSummaryAdapter() {
        if (DeviceUtil.isSalePoint()) {
            indexScroll = 2
        } else {
            indexScroll += 1
        }

        containerL14Games.visibility = View.GONE
        containerL14Summary.visibility = View.VISIBLE

        summaryAdapter = League14SummaryAdapter(viewModel.sportingEventModel.value ?: arrayListOf())
        rvLeague14Summary.adapter = summaryAdapter
    }
}