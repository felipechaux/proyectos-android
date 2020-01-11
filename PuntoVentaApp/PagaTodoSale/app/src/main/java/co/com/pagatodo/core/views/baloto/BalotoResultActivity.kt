package co.com.pagatodo.core.views.baloto

import android.os.Bundle
import android.view.View
import co.com.pagatodo.core.R
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.com.pagatodo.core.views.base.BaseActivity
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.data.model.BalotoResultsModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import kotlinx.android.synthetic.main.activity_baloto_result.*
import co.com.pagatodo.core.views.adapters.BalotoResultAdapter
import kotlinx.android.synthetic.main.layout_buttons_back_next.*

class BalotoResultActivity : BaseActivity() {

    lateinit var viewModel: BalotoViewModel

    private lateinit var balotos: List<BalotoResultsModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baloto_result)
        setupView()
    }

    private fun setupView() {
        setupBaseView(searchBar)
        updateTitle(R_string(R.string.baloto_result_title))
        setupViewModel()
        setupSearchBar()
        initListenersViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(BalotoViewModel::class.java)
        initSubscriptions()
        showDialogProgress(getString(R.string.message_dialog_request))
        isForeground = false
        viewModel.getResults(R_string(R.string.report_winning_baloto))
    }

    private fun initSubscriptions() {
        viewModel.singleLiveEvent.observe(this, Observer { itl ->
            when(itl) {
                is BalotoViewModel.ViewEvent.GetBalotoResults -> {
                    hideDialogProgress()
                    balotos = itl.balotoResults
                    if(itl.balotoResults.isNotEmpty()) {
                        val title = R_string(R.string.title_view_baloto_history)
                        setupRecyclerResults(itl.balotoResults.toMutableList(), title)
                    } else {
                        showOkAlertDialog("", R_string(R.string.message_error_results_empty)) { hideKeyboardAndFinish() }
                    }
                }
                is BalotoViewModel.ViewEvent.ResponseError -> {
                    hideDialogProgress()
                    showOkAlertDialog("", itl.errorMessage.toString()) { hideKeyboardAndFinish() }
                }
            }
        })
    }

    private fun hideKeyboardAndFinish() {
        searchBar.clearFocus()
        finish()
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener {
            hideKeyboardAndFinish()
        }
        btnNext.visibility = View.INVISIBLE
    }

    private fun setupRecyclerResults(results: MutableList<BalotoResultsModel>, title: String) {
        results.add(0, BalotoResultsModel().apply {
            this.typeItem = 0
        })
        recyclerItemsBaloto.apply {
            layoutManager = LinearLayoutManager(this@BalotoResultActivity)
            setHasFixedSize(true)
        }
        val balotoAdapter = BalotoResultAdapter(title, results)
        recyclerItemsBaloto.adapter = balotoAdapter
    }

    private fun setupSearchBar() {
        searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterQuery(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterQuery(newText)
                return false
            }
        })
    }

    private fun filterQuery(newString: String?) {
        val filteredList = mutableListOf<BalotoResultsModel>()
        balotos.forEach {
            if(it.draw.toString().contains(newString.toString())) {
                filteredList.add(it)
            }
        }
        val title = R_string(R.string.title_view_baloto_result_draw)
        setupRecyclerResults(filteredList, title)
    }
}
