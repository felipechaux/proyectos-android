package co.com.pagatodo.core.views.sporting.megagoals

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.data.model.request.RequestSportingModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.adapters.MegaGoalAdapter
import co.com.pagatodo.core.views.adapters.MegaGoalSummaryAdapter
import co.com.pagatodo.core.views.base.SportingBaseActivity
import co.com.pagatodo.core.views.components.dialogs.PaymentConfirmationDialog
import co.com.pagatodo.core.views.sporting.SportingViewModel
import kotlinx.android.synthetic.main.activity_mega_goals.*
import kotlinx.android.synthetic.main.dialog_pay_sportings.view.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*
import kotlinx.android.synthetic.main.layout_header_sportings.*
import kotlinx.android.synthetic.main.layout_subheader_megagoals.*

class MegaGoalsActivity : SportingBaseActivity() {

    private lateinit var viewModel: SportingViewModel

    private var teamAdapter: MegaGoalAdapter? = null
    private var summaryAdapter: MegaGoalSummaryAdapter? = null
    private var numberOfRows = if (DeviceUtil.isSalePoint()) 2 else 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mega_goals)
        setupView()
        setupViewModel()
    }

    private fun setupView(){
        setupBaseView()
        updateTitle(R_string(R.string.megagol_title))
        btnDelete.visibility = View.VISIBLE

        rvMegagoalGames.apply {
            layoutManager = GridLayoutManager(context, numberOfRows, GridLayoutManager.HORIZONTAL, false)
        }

        if(DeviceUtil.isSalePoint())
            paddingForSportingItems(0,0,0,0, rvMegagoalGames)

        tvTagRandom.setOnClickListener { randomMarkers() }
        btnRandom.setOnClickListener { randomMarkers() }
        btnDelete.setOnClickListener { clearMarkers() }
        btnBack.setOnClickListener { backAction() }
        btnNext.setOnClickListener { setupSummaryAdapter() }
    }

    private fun setupViewModel(){
        viewModel = ViewModelProviders.of(this).get(SportingViewModel::class.java)
        viewModel.singleLiveEvent.observe(this, Observer {
            hideDialogProgress()
            when(it){
                is SportingViewModel.ViewEvent.ResponseMessage -> {
                    showOkAlertDialog("",it.message ?: R_string(R.string.message_success_bet)) {
                        if (it.backToMenu == true){
                            navigateToMenu(this)
                        }
                    }
                }
            }
        })
        viewModel.sportingEventModel.observe(this, Observer {
            setupTeamAdapter(it)
        })
        showDialogProgress(R_string(R.string.text_load_data))
        viewModel.getParameters(R_string(R.string.code_sporting_mega_goals_product).toInt())
    }

    private fun setupTeamAdapter(data: List<SportingBetModel>){
        tvGrid.text = viewModel.codeGrid
        tvAccumulated.text = "$ ${formatValue(viewModel.accumulated.toDouble())}"
        tvClosingSales.text = viewModel.closeDate
        teamAdapter = MegaGoalAdapter(data)
        rvMegagoalGames.adapter = teamAdapter
        hideDialogProgress()
    }

    private fun setupSummaryAdapter(){
        if (containerSummaryMegaGoal.visibility == View.VISIBLE){
            showPayModal()
        }else if (isValidMarkers()){
            containerGamesMegagoal.visibility = View.GONE
            containerSummaryMegaGoal.visibility = View.VISIBLE
            rvMegagolSummary.apply {
                layoutManager = LinearLayoutManager(this@MegaGoalsActivity)
            }
            summaryAdapter =
                MegaGoalSummaryAdapter(viewModel.getTeamsForMegaGoalSummary())
            rvMegagolSummary.adapter = summaryAdapter
        }
    }

    private fun backAction(){
        if (containerSummaryMegaGoal.visibility == View.VISIBLE){
            containerGamesMegagoal.visibility = View.VISIBLE
            containerSummaryMegaGoal.visibility = View.GONE
        }else{
            finish()
        }
    }

    private fun showPayModal(){

        val bet = Math.floor(viewModel.getIvaBet())
        val ivaBet = Math.floor(viewModel.getBetValue() - bet)
        val totalValue = viewModel.getBetValue()

        PaymentConfirmationDialog().show(this, R.layout.dialog_pay_sportings,
            R_string(R.string.title_confirm_recharge_megagoal), null, { view ->
                view.textDrawDate.text = viewModel.closeDate.split(" ")[0]
                view.textGameDate.text = R_string(R.string.megagoal_title)
                view.textGrid.text = viewModel.codeGrid
                view.textBet.text = "$${formatValue(bet)}"
                view.textIva.text = "$${formatValue(ivaBet)}"
                view.textTotalValue.text = "$${formatValue(totalValue.toDouble())}"
            },{
                val request = RequestSportingModel().apply {
                    this.productCode = R_string(R.string.code_sporting_mega_goals_product).toInt()
                    this.image = BitmapFactory.decodeResource(resources, R.drawable.ic_sportings_footer)
                    this.productStatusCode = ProductName.MEGAGOAL.rawValue
                    this.transactionTime = DateUtil.getCurrentDateInUnix()
                    this.sequenceTransaction = StorageUtil.getSequenceTransaction()
                }
                viewModel.sellMegaGoaldBet(request)
                showDialogProgress(R_string(R.string.message_dialog_request))
            })
    }

    private fun randomMarkers(){
        viewModel.generateRandomGameMegaGoal()
        teamAdapter?.notifyDataSetChanged()
    }

    private fun clearMarkers(){
        viewModel.clearMarkers()
        teamAdapter?.notifyDataSetChanged()
    }

    private fun isValidMarkers(): Boolean {
        val arrayTeams = viewModel.sportingEventModel.value?.filter { it.isLocalResult ?: false && it.isVisitorResult ?: false }
        if ((arrayTeams?.count() ?: 0) < 2){
            showOkAlertDialog(getString(R.string.title_incomplete_game_dialog),getString(R.string.text_title_incomplete_game_dialog))
        }
        return (arrayTeams?.count() ?: 0) >= 2
    }
}
