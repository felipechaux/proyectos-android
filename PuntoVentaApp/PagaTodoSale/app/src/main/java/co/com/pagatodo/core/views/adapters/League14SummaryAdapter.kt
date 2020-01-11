package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.views.base.SportingBaseActivity
import kotlinx.android.synthetic.main.item_league14_summary.view.*

class League14SummaryAdapter(var data: List<SportingBetModel>): RecyclerView.Adapter<League14SummaryAdapter.SportingsSummaryViewHolder>() {

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

        var local = item.localName ?: ""
        var visitor = item.visitorName ?: ""

        if(!DeviceUtil.isSalePoint()){
            if(local.contains(" ")){
                local = local.replaceFirst(" ","\n")
                visitor = visitor.plus("\n")
            } else if(visitor.contains(" ")){
                visitor = visitor.replaceFirst(" ","\n")
                local = local.plus("\n")
            }
        }

        holder.itemView.titleNumberSummary.text = "${position + 1}"
        holder.itemView.titleLocalNameSummary.text = local
        holder.itemView.titleVisitantNameSummary.text = visitor

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