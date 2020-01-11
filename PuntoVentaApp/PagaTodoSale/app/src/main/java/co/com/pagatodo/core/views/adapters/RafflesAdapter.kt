package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.RaffleModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.item_raffles.view.*

class RafflesAdapter (var data: List<RaffleModel>): RecyclerView.Adapter<RafflesAdapter.RaffleViewHolder>(){

    interface OnActionListener {
        fun onClickMenu(currentRaffle: RaffleModel?)
    }

    lateinit var context: Context
    private var inflater: LayoutInflater? = null
    private var listener: OnActionListener? = null

    fun setListener(listener: OnActionListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaffleViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_raffles, parent, false)

        return RaffleViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: RaffleViewHolder, position: Int) {
        val raffle = data[position]

        val hourFormated = DateUtil.convertMilitaryHourToNormal(DateUtil.StringFormat.HHMMA,raffle.drawTime ?: "")
        val dateFormated = DateUtil.convertStringToDate(DateUtil.StringFormat.EEE_DD_MM_YY_SPLIT,raffle.dateDraw ?: "").toLowerCase().capitalize()

        holder.itemView.tv_raffle_name.text = raffle.raffleName
        holder.itemView.tv_raffle_id.text = raffle.raffleId.toString()
        holder.itemView.tv_raffle_lottery.text = raffle.lotteryName
        holder.itemView.tv_raffle_date.text = "${dateFormated} ${hourFormated}"
        holder.itemView.tv_raffle_price.text = "$ ${formatValue(raffle.price?.toDouble() ?: 0.0)}"

        holder.itemView.setOnClickListener {
            data.forEach{raffle ->
                raffle.isEnable = false
            }
            raffle.isEnable = true
            listener?.onClickMenu(raffle)
            notifyDataSetChanged()
        }

        if (raffle.isEnable){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGraySilver))
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
        }
    }

    inner class RaffleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}