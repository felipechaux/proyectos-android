package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseCheckResultLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResultsDTO
import co.com.pagatodo.core.data.model.LotteryResultModel
import kotlinx.android.synthetic.main.item_lottery_result.view.*

class LotteryResultAdapter(var data: List<ResultsDTO>): RecyclerView.Adapter<LotteryResultAdapter.LotteryResultViewHolder>() {

    lateinit var context: Context
    private var inflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotteryResultViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_lottery_result, parent, false)
        return LotteryResultViewHolder( view!!)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: LotteryResultViewHolder, position: Int) {

        val result = data[position]
        holder.number?.text = result.number
        holder.serie?.text = result.serie
    }

    inner class LotteryResultViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val serie: TextView? = view.itemLotteryResultSerie
        val number: TextView? = view.itemLotteryResultNumber

    }
}