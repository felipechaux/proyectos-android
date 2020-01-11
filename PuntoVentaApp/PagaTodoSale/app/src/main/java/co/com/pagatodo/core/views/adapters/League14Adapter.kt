package co.com.pagatodo.core.views.adapters

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.BuildConfig
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import kotlinx.android.synthetic.main.item_league_14.view.*

class League14Adapter(var data: List<SportingBetModel>): RecyclerView.Adapter<League14Adapter.League14ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): League14ViewHolder {
        context = parent.context
        return League14ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_league_14, parent, false))
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: League14ViewHolder, position: Int) {
        val item = data[position]

        holder.itemView.txtL14Equip1.text = item.localName
        holder.itemView.txtL14Equip2.text = item.visitorName

        if (item.isLocalResult ?: false){
            holder.itemView.btL14Local.callOnClick()
        }
        if (item.isTieResult ?: false){
            holder.itemView.btL14Tie.callOnClick()
        }
        if (item.isVisitorResult ?: false){
            holder.itemView.btL14Visitant.callOnClick()
        }

        if (DeviceUtil.isSalePoint()){
            val columnNumber = 3
            val spaceToReduceForItem = 30
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            holder.itemView.getLayoutParams().width = width / columnNumber - spaceToReduceForItem
        }
    }

    inner class League14ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.btL14Local.setOnClickListener{
                changeItemStatus(it)
            }
            itemView.btL14Tie.setOnClickListener{
                changeItemStatus(it)
            }
            itemView.btL14Visitant.setOnClickListener{
                changeItemStatus(it)
            }
        }

        private fun changeItemStatus(view: View){
            data[adapterPosition].isLocalResult = false
            data[adapterPosition].isTieResult = false
            data[adapterPosition].isVisitorResult = false

            itemView.containerL14Local.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrayLight3))
            itemView.containerL14Tie.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrayLight3))
            itemView.containerL14Visitant.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrayLight3))

            itemView.btL14Local.setBackgroundResource(R.drawable.rounded_button_white_center_transparent)
            itemView.btL14Tie.setBackgroundResource(R.drawable.rounded_button_white_center_transparent)
            itemView.btL14Visitant.setBackgroundResource(R.drawable.rounded_button_white_center_transparent)

            when (view.id){
                R.id.btL14Local -> {
                    data[adapterPosition].isLocalResult = true
                    itemView.containerL14Local.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRedButton))
                }
                R.id.btL14Tie -> {
                    data[adapterPosition].isTieResult = true
                    itemView.containerL14Tie.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRedButton))
                }
                R.id.btL14Visitant -> {
                    data[adapterPosition].isVisitorResult = true
                    itemView.containerL14Visitant.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRedButton))
                }
            }
            view.background = context.getDrawable(R.drawable.rounded_button_white_center_white)
        }

    }
}