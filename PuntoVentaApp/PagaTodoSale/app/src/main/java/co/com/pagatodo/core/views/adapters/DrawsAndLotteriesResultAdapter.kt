package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.LotteryResultModel
import kotlinx.android.synthetic.main.item_draw_lottery_result.view.*

class DrawsAndLotteriesResultAdapter(var data: List<LotteryResultModel>): RecyclerView.Adapter<DrawsAndLotteriesResultAdapter.DrawsAndLotteriesResultViewHolder>() {

    lateinit var context: Context
    private var inflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawsAndLotteriesResultViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_draw_lottery_result, parent, false)
        return DrawsAndLotteriesResultViewHolder( view!!)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: DrawsAndLotteriesResultViewHolder, position: Int) {
        val result = data[position]
        holder.serie?.text = result.serie
        holder.number?.text = result.number
        holder.lotteryName?.text = result.lotteryName?.toLowerCase()?.capitalize()
    }

    inner class DrawsAndLotteriesResultViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val serie: TextView? = view.tvSerie
        val number: TextView? = view.tvNumber
        val lotteryName: TextView? = view.tvLotteryName
    }
}