package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.item_fraction.view.*
import kotlinx.android.synthetic.main.item_virtual_lottery.view.*
import kotlinx.android.synthetic.main.item_virtual_lottery.view.lblDate
import kotlinx.android.synthetic.main.item_virtual_lottery.view.lblValue

class FractionsListAdapter (var data: ResponseCheckNumberLotteryModel?, var model: VirtualLotteryModel) :  RecyclerView.Adapter<FractionsListAdapter.LotteryViewHolder>(){
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotteryViewHolder {
        context = parent.context
        return LotteryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fraction, parent, false))
    }

    override fun getItemCount(): Int {
        return data?.tickets!!.size
    }

    override fun onBindViewHolder(holder: LotteryViewHolder, position: Int) {
        val lottery = data?.tickets?.get(position)
        //val dateFormated = DateUtil.convertMilitaryHourToNormal(DateUtil.StringFormat.HHMMA,lottery. ?: "")

        holder.itemView.lblNumberBet.text = lottery!!.number
        //holder.itemView.lblSorteo.text = context.getString(R.string.text_label_parameter_coin, lottery.award?.let { formatValue(it) })
        holder.itemView.lblFractions.text = lottery.fraction
        //holder.itemView.lblDate.text = "${formatDate(lottery.date)} ${dateFormated}"
        holder.itemView.lblSerie.text = lottery.serie
    }


    private fun formatDate(dateString: String?): String {
        val newDate = dateString?.let { DateUtil.addBackslashToStringDate(it) }
        return newDate?.let { DateUtil.convertStringToDate(DateUtil.StringFormat.EEE_DD_MM_YY_SPLIT, it).toLowerCase().capitalize() }.toString()
    }

    inner class LotteryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}