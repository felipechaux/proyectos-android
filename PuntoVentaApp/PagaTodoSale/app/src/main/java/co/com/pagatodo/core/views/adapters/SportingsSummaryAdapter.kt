package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SportingBetModel
import kotlinx.android.synthetic.main.item_league14_summary.view.*

class SportingsSummaryAdapter(var data: List<SportingBetModel>): RecyclerView.Adapter<SportingsSummaryAdapter.SportingsSummaryViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportingsSummaryViewHolder {
        context = parent.context
        return SportingsSummaryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_league14_summary, parent, false))
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: SportingsSummaryViewHolder, position: Int) {
        val item = data[position]

        if (position != 0){
            holder.itemView.containerTitleSummary.visibility = View.GONE
        }

        holder.itemView.titleNumberSummary.text = "${position + 1}"
        holder.itemView.titleLocalNameSummary.text = item.localName ?: ""
        holder.itemView.titleVisitantNameSummary.text = item.visitorName ?: ""

        if (item.isLocalResult ?: false){
            holder.itemView.titleWinSummary.text = "L"
        }else if (item.isVisitorResult ?: false){
            holder.itemView.titleWinSummary.text = "V"
        }else{
            holder.itemView.titleWinSummary.text = "E"
        }
    }

    inner class SportingsSummaryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}