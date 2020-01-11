package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import kotlinx.android.synthetic.main.item_range_raffle.view.*

class RaffleRangeAdapter (var data: List<String>): RecyclerView.Adapter<RaffleRangeAdapter.RaffleRangeViewHolder>()  {

    interface OnActionListener {
        fun onClickMenu(currentRaffle: String?)
    }

    lateinit var context: Context
    private var inflater: LayoutInflater? = null
    private var listener: OnActionListener? = null
    private var index = -1

    fun setListener(listener: OnActionListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaffleRangeViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_range_raffle, parent, false)

        return RaffleRangeViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: RaffleRangeViewHolder, position: Int) {
        val raffle = data[position]

        holder.itemView.txtNumberAvailable.text = raffle

        holder.itemView.setOnClickListener {
            index = position
            listener?.onClickMenu(raffle)
            notifyDataSetChanged()
        }

        if (index == position){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGraySilver))
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
        }
    }

    inner class RaffleRangeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}