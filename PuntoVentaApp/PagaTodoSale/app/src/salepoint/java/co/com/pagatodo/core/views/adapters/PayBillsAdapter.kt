package co.com.pagatodo.core.views.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.BillModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import co.com.pagatodo.core.views.paybills.PayBillViewModel
import kotlinx.android.synthetic.salepoint.item_pay_bills.view.*

class PayBillsAdapter(var data: List<BillModel>,var viewModel:PayBillViewModel): RecyclerView.Adapter<PayBillsAdapter.PayBillsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayBillsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pay_bills, parent, false)
        return PayBillsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PayBillsViewHolder, position: Int) {
        var item = data[position]

        initItems(item,holder)

        if(item.billService != null && item.billService?.isPartialPayment!!){
            holder.itemView.txtParcialValue.visibility = View.VISIBLE
            holder.itemView.txtValueToPay.visibility = View.GONE
            holder.itemView.txtParcialValue.setText(if (item.valueToPay.isNullOrEmpty()) "-" else "$${formatValue(item.valueToPay.toString().toDouble())}")
        }else{
            holder.itemView.txtValueToPay.visibility = View.VISIBLE
            holder.itemView.txtParcialValue.visibility = View.GONE
            holder.itemView.txtValueToPay.setText(if (item.valueToPay.isNullOrEmpty()) "-" else "$${formatValue(item.valueToPay.toString().toDouble())}")
        }


        holder.itemView.btDelete.setOnClickListener {
            item = BillModel()
            data = listOf(item)
            notifyDataSetChanged()
            viewModel.deleteBill()

        }

        initTextChangedListener(holder.itemView.txtParcialValue,item)
    }

    private fun initItems(item: BillModel, holder: PayBillsAdapter.PayBillsViewHolder) {

        holder.itemView.txtBilledService.text = if(item.billService?.serviceName.isNullOrEmpty()) "-" else item.billService?.serviceName
        holder.itemView.txtUserCode.text = if (item.accountNumber.isNullOrEmpty()) "-" else item.accountNumber
        holder.itemView.txtUserName.text = if (item.userName.isNullOrEmpty()) "-" else item.userName
        holder.itemView.txtBillNumber.text = if (item.flatIdBill.isNullOrEmpty()) "-" else item.flatIdBill
        holder.itemView.txtBillDate.text = if (item.expirationDate.isNullOrEmpty()) "-" else item.expirationDate
        holder.itemView.txtBillDescription.text = if (item.description.isNullOrEmpty()) "-" else item.description
        holder.itemView.txtBillValue.text = if (item.billValue.isNullOrEmpty()) "-" else "$${formatValue(item.billValue.toString().toDouble())}"
        holder.itemView.txtValueToPay.text = if (item.valueToPay.isNullOrEmpty()) "-" else "$${formatValue(item.valueToPay.toString().toDouble())}"
        holder.itemView.txtBillSate.text = if (item.billStatus.isNullOrEmpty()) "-" else item.billStatus

    }

    private fun initTextChangedListener(edt:EditText,item:BillModel){

        var isTextWatcher = true
        edt.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                // not implemented
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not implemented
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(isTextWatcher){

                    isTextWatcher = false


                    if (edt.text.toString().isNotEmpty()) {
                        try {
                            var originalString = edt.text.toString()
                            originalString = originalString.replace(".", "").replace("$", "")
                            val value =
                                if (originalString.isEmpty()) 0.0 else originalString.toDouble()
                            val textFormat = "$${formatValue(value)}"
                            edt.setText(textFormat)
                            edt.setSelection(edt.text.length)


                        } catch (nfe: NumberFormatException) {
                            nfe.printStackTrace()
                        }

                    }

                    item.valueToPay = edt.text.toString().resetValueFormat()

                    isTextWatcher = true


                }

            }

        })

    }

    inner class PayBillsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}