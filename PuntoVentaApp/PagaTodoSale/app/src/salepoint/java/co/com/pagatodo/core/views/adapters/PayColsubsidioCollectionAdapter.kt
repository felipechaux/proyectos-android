package co.com.pagatodo.core.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.BillModel
import co.com.pagatodo.core.data.model.ColsubsidioObligationModel
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.colsubsidio.ColsubsidioCollectionViewModel
import co.com.pagatodo.core.views.paybills.PayBillViewModel
import kotlinx.android.synthetic.salepoint.item_pay_colsubsidio_collection.view.*

class PayColsubsidioCollectionAdapter(var data: List<ColsubsidioObligationModel>, var viewModel: ColsubsidioCollectionViewModel): RecyclerView.Adapter<PayColsubsidioCollectionAdapter.PayColsubsidioCollectionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayColsubsidioCollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pay_colsubsidio_collection, parent, false)
        return PayColsubsidioCollectionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PayColsubsidioCollectionViewHolder, position: Int) {
        var item = data[position]

       //holder.itemView.txtColsubsidioRecaudoSelect
        holder.itemView.txtColsubsidioRecaudoReference.text=if(item.reference.isNullOrEmpty())"-" else item.reference
        holder.itemView.txtColsubsidioRecaudoObligation.text=if(item.obligationType.isNullOrEmpty())"-" else item.obligationType
        holder.itemView.txtColsubsidioRecaudoDescription.text=if(item.description.isNullOrEmpty())"-" else item.description
        holder.itemView.txtColsubsidioRecaudoExpirateDate.text=if(item.expirateDate.isNullOrEmpty())"-" else item.expirateDate
        holder.itemView.txtColsubsidioRecaudoStatus.text=if(item.status.isNullOrEmpty())"-" else item.status
        holder.itemView.txtColsubsidioRecaudoFeeValue.text=if(item.feeValue.isNullOrEmpty())"-" else "$${formatValue(item.feeValue.toString().toDouble())}"

/*
        holder.itemView.btDelete.setOnClickListener {
            item = BillModel()
            data = listOf(item)
            notifyDataSetChanged()
            viewModel.deleteBill()

        }*/

    }

    inner class PayColsubsidioCollectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}