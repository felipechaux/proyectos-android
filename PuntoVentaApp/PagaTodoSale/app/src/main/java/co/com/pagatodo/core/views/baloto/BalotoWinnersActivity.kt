package co.com.pagatodo.core.views.baloto

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.BalotoResultsModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.BalotoWinnerAdapter
import co.com.pagatodo.core.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_baloto_winners.*
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class BalotoWinnersActivity : BaseActivity() {

    lateinit var viewModel: BalotoViewModel
    private var isBaloto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baloto_winners)
        isBaloto = intent.extras?.get(R_string(R.string.bundle_is_baloto)) as Boolean
        setupView()
    }

    private fun setupView() {
        setupBaseView()
        updateTitle(R_string(R.string.baloto_winners_menu_title))
        if(isBaloto) {
            lblTitle.text = R_string(R.string.text_title_baloto)
        } else {
            lblTitle.text = R_string(R.string.baloto_text_rematch)
        }
        setupViewModel()
        initListenersViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BalotoViewModel::class.java)
        initSubscriptions()
        showDialogProgress(getString(R.string.message_dialog_request))
        isForeground = false
        if(isBaloto) {
            viewModel.getResults(R_string(R.string.report_information_baloto))
        } else {
            viewModel.getResults(R_string(R.string.report_information_revenge))
        }
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener { finish() }
        btnNext.visibility = View.INVISIBLE
    }

    private fun initSubscriptions() {
        viewModel.singleLiveEvent.observe(this, Observer { itl ->
            when(itl) {
                is BalotoViewModel.ViewEvent.GetBalotoResults -> {
                    hideDialogProgress()
                    if(itl.balotoResults.isNotEmpty()) {
                        setInformationInView(itl.balotoResults.first())
                    } else {
                        showOkAlertDialog("", R_string(R.string.message_error_results_empty)) { finish() }
                    }
                }
                is BalotoViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    showOkAlertDialog("", itl.errorMessage.toString()) { finish() }
                }
            }
        })
    }

    private fun setInformationInView(model: BalotoResultsModel) {
        tvTagDrawNumber.text = model.draw
        tvTagDrawDate.text = model.date.toString().toLowerCase().capitalize()
        val number: String = if(isBaloto) {
            model.balotoNumber.toString().trim()
        } else {
            model.revengeNumber.toString().trim()
        }
        tvTagGameResult.text = formatNumberWinner(number)
        model.awards?.toMutableList()?.let { setupRecyclerResults(it) }
    }

    private fun formatNumberWinner(number: String): String {
        val oneWhiteSpaces = " "
        val twoWhiteSpaces = "   "
        return number.replace(twoWhiteSpaces, oneWhiteSpaces)
    }

    private fun setupRecyclerResults(results: MutableList<Triple<String, String, String>>) {
        recyclerItemsWinners.apply {
            layoutManager = LinearLayoutManager(this@BalotoWinnersActivity)
            setHasFixedSize(true)
        }
        val balotoAdapter = BalotoWinnerAdapter(results)
        recyclerItemsWinners.adapter = balotoAdapter
    }
}
