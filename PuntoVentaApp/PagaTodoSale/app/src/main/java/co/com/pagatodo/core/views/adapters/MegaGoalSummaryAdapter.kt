package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SportingBetModel
import kotlinx.android.synthetic.main.item_megagoal_summary.view.*

class MegaGoalSummaryAdapter(var data: List<SportingBetModel>): RecyclerView.Adapter<MegaGoalSummaryAdapter.MegaGoalSummaryViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MegaGoalSummaryViewHolder {
        context = parent.context
        return MegaGoalSummaryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_megagoal_summary, parent, false))
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: MegaGoalSummaryViewHolder, position: Int) {
        val item = data[position]

        if (position != 0){
            holder.itemView.containerTitleSummaryMegaGoal.visibility = View.GONE
        }

        holder.itemView.titleNumberSummaryMegaGoal.text = item.code!!.toInt().toString()
        holder.itemView.titleLocalNameSummaryMegaGoal.text = item.localName ?: ""
        holder.itemView.titleVisitantNameSummaryMegaGoal.text = item.visitorName ?: ""

        holder.itemView.txtSummaryMarkerLocal.text = item.localMarker.toString()
        holder.itemView.txtSummaryMarkerVisitor.text = item.visitorMarker.toString()
    }

    inner class MegaGoalSummaryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}