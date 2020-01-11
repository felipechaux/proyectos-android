package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GiroCriteriaDTO
import co.com.pagatodo.core.data.dto.GiroMovementDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.formatValue
import kotlinx.android.synthetic.main.item_movement_summary_details.view.*


class GiroMovementAdapter(var data: List<GiroMovementDTO>, var isDefaultRow: Boolean = false) :
    RecyclerView.Adapter<GiroMovementAdapter.GiroMovementViewHolder>() {

    var index = -1

    lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiroMovementViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_movement_summary_details, parent, false)
        return GiroMovementViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: GiroMovementViewHolder, position: Int) {

        if (!isDefaultRow) {

            val movement = data[position]

            holder.itemView.lblReferencePin.text = movement.reference
            holder.itemView.lblConcept.text = movement.concept

            if (movement.concept == context.getString(R.string.giro_movement_type_concept_entry)) {

                holder.itemView.lblEntry.text =
                    context.getString(
                        R.string.text_label_parameter_coin,
                        formatValue(movement.value?.toDouble() ?: 0.0)
                    )
                holder.itemView.lblEgress.text = context.getString(R.string.text_label_parameter_coin, formatValue(0.0))
            } else {
                holder.itemView.lblEntry.text = context.getString(R.string.text_label_parameter_coin, formatValue(0.0))
                holder.itemView.lblEgress.text =
                    context.getString(
                        R.string.text_label_parameter_coin,
                        formatValue(movement.value?.toDouble() ?: 0.0)
                    )

            }

            holder.itemView.lblDate.text =
                if (movement.date != null && movement.date != "") formatDate(movement.date!!) else ""
            holder.itemView.lblHour.text = movement.hour
        }
    }

    fun formatDate(str: String): String {

        val day = str.substring(0, 2)
        val month = str.substring(2, 4)
        val year = str.substring(4, 8)
        return "${day}/${month}/${year}"
    }


    inner class GiroMovementViewHolder(view: View) : RecyclerView.ViewHolder(view)

}



