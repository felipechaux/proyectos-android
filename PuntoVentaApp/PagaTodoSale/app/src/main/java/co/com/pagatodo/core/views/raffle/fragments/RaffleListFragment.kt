package co.com.pagatodo.core.views.raffle.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.RaffleModel
import co.com.pagatodo.core.views.adapters.RafflesAdapter
import kotlinx.android.synthetic.main.fragment_raffle_list.*

class RaffleListFragment : Fragment(), RafflesAdapter.OnActionListener {

    interface OnFragmentInteractionListener {
        fun getListRaffle(): List<RaffleModel>?
        fun selectRaffle(raffleModel: RaffleModel)
        fun hideNextButton()
    }

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_raffle_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    fun setOnFragmentListener(listener: OnFragmentInteractionListener) {
        this.listener = listener
    }

    fun setupView(){
        listener?.hideNextButton()
        rvRaffleList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
        }

        updateRaffleList(listener?.getListRaffle() ?: arrayListOf())
    }

    fun updateRaffleList(data: List<RaffleModel>){
        val adapter = RafflesAdapter(data)
        adapter.setListener(this)
        rvRaffleList.adapter = adapter
    }

    override fun onClickMenu(currentRaffle: RaffleModel?) {
        listener?.selectRaffle(currentRaffle ?: RaffleModel())
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
