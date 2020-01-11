package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import kotlinx.android.synthetic.main.item_baloto_winner.view.*

class BalotoWinnerAdapter(
    var data: List<Triple<String, String, String>>
): RecyclerView.Adapter<BalotoWinnerAdapter.BalotoViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalotoViewHolder {
        context = parent.context
        return BalotoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_baloto_winner, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BalotoViewHolder, position: Int) {
        val triple = data[position]
        if(triple.first == R_string(R.string.text_label_tot)) {
            holder.itemView.tvTagDiv.text = R_string(R.string.text_label_total_value)
        } else {
            holder.itemView.tvTagDiv.text = triple.first
        }
        holder.itemView.tvTagValue.text = triple.second
        holder.itemView.tvTagWinners.text = triple.third
    }

    inner class BalotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}