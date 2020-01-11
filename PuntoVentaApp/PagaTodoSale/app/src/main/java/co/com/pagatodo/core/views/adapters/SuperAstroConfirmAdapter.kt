package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SuperAstroModel
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.item_superastro_confirm_payment.view.*

class SuperAstroConfirmAdapter( var data: MutableList<SuperAstroModel> ) : RecyclerView.Adapter<SuperAstroConfirmAdapter.SuperAstroConfirmViewHolder>()  {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperAstroConfirmViewHolder {
        context = parent.context
        return SuperAstroConfirmViewHolder(LayoutInflater.from(context).inflate(R.layout.item_superastro_confirm_payment, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SuperAstroConfirmViewHolder, position: Int) {
        val value = formatValue(data[position].value.toString().toDouble())

        val zodiac = data[position].zodiacSelected?.split("-") ?: arrayListOf()

        holder.itemView.lblIndexGame.text = context.getString(R.string.text_label_parameter_games, (position + 1))
        holder.itemView.lblNumberAstro.text = data[position].number.toString()
        if (zodiac.count() >= 2){
            holder.itemView.lblSignZodiac.text = zodiac[1].toLowerCase().capitalize()
        }else{
            holder.itemView.lblSignZodiac.text = zodiac[0].toLowerCase().capitalize()
        }
        holder.itemView.lblValue.text = context.getString(R.string.text_label_parameter_coin, value)
    }

    inner class SuperAstroConfirmViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {}
    }
}