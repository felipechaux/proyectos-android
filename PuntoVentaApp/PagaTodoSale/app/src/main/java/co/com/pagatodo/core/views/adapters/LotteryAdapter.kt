package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.item_virtual_lottery.view.*

class LotteryAdapter(
    var data: List<VirtualLotteryModel>,
    var listener: OnListenerAdapter? = null
): RecyclerView.Adapter<LotteryAdapter.LotteryViewHolder>() {

    interface OnListenerAdapter {
        fun onClickListener(model: VirtualLotteryModel)
        fun onShowProgressDialog(message: String)
        fun onCloseProgressDialog()
        fun onShowOkAlertDialog(title: String, message: String, closure: () -> Unit?)
        fun onShowOkAlertDialog(title: String, message: String)
        fun onBackFragment()
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotteryViewHolder {
        context = parent.context
        return LotteryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_virtual_lottery, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LotteryViewHolder, position: Int) {
        val lottery = data[position]
        val dateFormated = DateUtil.convertMilitaryHourToNormal(DateUtil.StringFormat.HHMMA,lottery.hour ?: "")

        holder.itemView.lblTitle.text = context.getString(R.string.text_label_lottery, lottery.fullName?.toLowerCase()?.capitalize())
        holder.itemView.lblDraw.text = lottery.draw
        holder.itemView.lblFraction.text = lottery.fractions
        holder.itemView.lblAward.text = context.getString(R.string.text_label_parameter_coin, lottery.award?.let { formatValue(it) })
        holder.itemView.lblDate.text = "${formatDate(lottery.date)} ${dateFormated}"
        holder.itemView.lblValue.text = context.getString(R.string.text_label_parameter_coin_with_space, lottery.fractionValue?.let { formatValue(it) })

        holder.itemView.setOnClickListener {
            data.forEach{
                it.isSelected = false
            }
            lottery.isSelected = true
            listener?.onClickListener(lottery)
            notifyDataSetChanged()
        }

        if (lottery.isSelected){
            holder.itemView.layoutItemLottery.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGraySilver))
        }else{
            holder.itemView.layoutItemLottery.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
        }
    }

    private fun formatDate(dateString: String?): String {
        val newDate = dateString?.let { DateUtil.addBackslashToStringDate(it) }
        return newDate?.let { DateUtil.convertStringToDate(DateUtil.StringFormat.EEE_DD_MM_YY_SPLIT, it).toLowerCase().capitalize() }.toString()
    }

    inner class LotteryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}