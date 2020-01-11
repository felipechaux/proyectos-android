package co.com.pagatodo.core.views.lottery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.adapters.LotteryAdapter
import co.com.pagatodo.core.views.lottery.LotterySaleViewModel
import kotlinx.android.synthetic.main.fragment_lottery_list.*

class LotteryListFragment: Fragment(){

    interface OnFragmentInteractionListener {
        fun showMessageDialog(title: String, message: String, backMenu: Boolean)
    }

    private var listener: LotteryListFragment.OnFragmentInteractionListener? = null

    private lateinit var virtualViewModel: LotterySaleViewModel

    private lateinit var lotteries: List<VirtualLotteryModel>
    private lateinit var activityFragment: LotteryAdapter.OnListenerAdapter
    private var isVirtualFragment = false

    companion object {
        fun newInstance(isVirtual: Boolean, activity: LotteryAdapter.OnListenerAdapter, fragmentListener: OnFragmentInteractionListener) =
            LotteryListFragment().apply {
                isVirtualFragment = isVirtual
                activityFragment = activity
                listener = fragmentListener
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lottery_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        setupViewModel()
        setupSearchBar()
        initListenersViews()
    }

    private fun setupViewModel() {
        virtualViewModel = ViewModelProviders.of(this).get(LotterySaleViewModel::class.java)
        if(isVirtualFragment)
            virtualViewModel.loadLotteries(R_string(R.string.name_code_product_virtual_lottery))
        else
            virtualViewModel.loadLotteries(R_string(R.string.name_code_product_physical_lottery))
        initSubscriptions()
    }

    private fun initSubscriptions() {
        virtualViewModel.singleLiveEvent.observe(this, Observer {
            when(it) {
                is LotterySaleViewModel.ViewEvent.ResponseLotteries -> {
                    if(it.virtuaLotteries.isEmpty()){
                        listener?.showMessageDialog(R_string(R.string.title_dialog_empty_lotteries), R_string(R.string.message_dialog_empty_lotteries), true)
                    }else{
                        lotteries = it.virtuaLotteries
                        setupRecyclerViewLotteries(lotteries.toMutableList())
                    }
                }
            }
        })
    }

    private fun initListenersViews() {
        btnBack.setOnClickListener {
            activity?.finish()
        }
    }

    private fun setupRecyclerViewLotteries(lotteries: MutableList<VirtualLotteryModel>) {
        recyclerItems.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
        val lotteryAdapter = LotteryAdapter(lotteries, activityFragment)
        recyclerItems.adapter = lotteryAdapter
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
        val filteredList = mutableListOf<VirtualLotteryModel>()
        lotteries.forEach {
            if(it.fullName.toString().toLowerCase().contains(newString.toString().toLowerCase())
                || it.shortName.toString().toLowerCase().contains(newString.toString().toLowerCase())) {
                filteredList.add(it)
            }
        }
        setupRecyclerViewLotteries(filteredList)
    }
}
