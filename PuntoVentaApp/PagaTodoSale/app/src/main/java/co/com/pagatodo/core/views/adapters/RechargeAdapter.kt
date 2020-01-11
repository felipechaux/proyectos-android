package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.RechargeHistoryModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.item_recharge_history.view.*

class RechargeAdapter(var data: List<RechargeHistoryModel>, val context: Context): RecyclerView.Adapter<RechargeAdapter.RechargeHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RechargeHistoryViewHolder {
        return RechargeHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recharge_history, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RechargeHistoryViewHolder, position: Int) {
        val recharge = data.get(position)
        val hour = DateUtil.convertMilitaryHourToNormal(DateUtil.StringFormat.HHMMA,recharge.hourRecharge ?: "")

        holder.itemView.lblDate.text = recharge.dateRecharge.let { DateUtil.addBackslashToStringDate(it ?: "") }
        holder.itemView.lblHour.text = hour
        holder.itemView.lblValue.text = "$"+ formatValue(recharge.rechargeAmount?.toDouble() ?: 0.0)
        holder.itemView.lblStatus.text = recharge.state?.toLowerCase()?.capitalize()
        holder.itemView.lblNumberReference.text = recharge.transactionId.toString()
    }

    inner class RechargeHistoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}