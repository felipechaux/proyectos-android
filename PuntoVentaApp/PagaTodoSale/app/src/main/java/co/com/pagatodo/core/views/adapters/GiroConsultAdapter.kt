package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GiroCriteriaDTO
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.item_giro_consult.view.*


class GiroConsultAdapter(var data: List<GiroCriteriaDTO>) :
    RecyclerView.Adapter<GiroConsultAdapter.GiroConsultViewHolder>() {

    var index = -1

    interface OnActionListener {
        fun onClickItem(giroCriteriaDTO:GiroCriteriaDTO )
    }

    lateinit var context: Context
    private var listener: GiroConsultAdapter.OnActionListener? = null

    fun setListener(listener: GiroConsultAdapter.OnActionListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiroConsultViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_giro_consult, parent, false)
        return GiroConsultViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: GiroConsultViewHolder, position: Int) {

        val giro = data[position]

        val nameOrigin = "${giro.clientOrigin?.name} ${giro.clientOrigin?.lastName}"
        val nameDestination = "${giro.clientDestination?.name} ${giro.clientDestination?.lastName}"

        holder.itemView.txtReference.text = giro.reference

        holder.itemView.txtDocumentNumberOrigin.text = giro.clientOrigin?.id
        holder.itemView.txtDocumentNumberDestination.text = giro.clientDestination?.id
        holder.itemView.txtValue.text = context.getString(R.string.text_label_parameter_coin,formatValue(giro.giroValue?.toDouble()?:0.0))
        holder.itemView.txtNombreOrigin.text =  nameOrigin
        holder.itemView.txtNombreDestination.text = nameDestination
        holder.itemView.txtAgency.text = giro.agencyOrigen?.name

        holder.itemView.setOnClickListener {
            index = position
            notifyDataSetChanged()
            listener?.onClickItem(giro)
        }

        if (index == position) {
            changeColorItems(holder, R.color.colorWhite, R.color.colorGrayDarkDeep)

            if (!DeviceUtil.isSalePoint())
                holder.itemView.setBackgroundResource(R.color.colorGrayLight)

        } else {
            changeColorItems(holder, R.color.colorGrayDarkDeep, R.color.colorGraySilver)

            if (!DeviceUtil.isSalePoint())
                holder.itemView.setBackgroundResource(R.color.colorGraySilver)
        }


    }

    private fun changeColorItems(holder: GiroConsultViewHolder, color: Int, colorBackground: Int) {

        holder.itemView.txtReference.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtDocumentNumberOrigin.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtDocumentNumberDestination.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtValue.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtNombreOrigin.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtNombreDestination.setTextColor(ContextCompat.getColor(context, color))


        holder.itemView.txtTitleReference.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtTitleDocumentNumberOrigin.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtTitleDocumentNumberDestination.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtTitleValue.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtTitleNombreOrigin.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtTitleNombreDestination.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.txtAgency.setTextColor(ContextCompat.getColor(context, color))

        holder.itemView.lyReference.setBackgroundResource(colorBackground)
        holder.itemView.lyDocumentNumberOrigin.setBackgroundResource(colorBackground)
        holder.itemView.lyDocumentNumberDestination.setBackgroundResource(colorBackground)
        holder.itemView.lyValue.setBackgroundResource(colorBackground)
        holder.itemView.lyNombreOrigin.setBackgroundResource(colorBackground)
        holder.itemView.lyNombreDestination.setBackgroundResource(colorBackground)
        holder.itemView.lyAgencia.setBackgroundResource(colorBackground)

    }

    inner class GiroConsultViewHolder(view: View) : RecyclerView.ViewHolder(view)

}



