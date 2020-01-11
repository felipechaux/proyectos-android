package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.util.RUtil
import kotlinx.android.synthetic.main.item_giro_check_request.view.*


class GiroCheckRequestAdapter(var data: List<GirorCheckRequestsDTO>) :
    RecyclerView.Adapter<GiroCheckRequestAdapter.GiroConsultViewHolder>() {

    var index = -1

    interface OnActionListener {
        fun onClickItemPrint(girorCheckRequestsDTO:GirorCheckRequestsDTO )
        fun onClickItemDetail(girorCheckRequestsDTO:GirorCheckRequestsDTO )
    }

    lateinit var context: Context
    private var listener: OnActionListener? = null

    fun setListener(listener: OnActionListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiroConsultViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_giro_check_request, parent, false)
        return GiroConsultViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: GiroConsultViewHolder, position: Int) {

        val checkRequest = data[position]

        holder.itemView.lblReference.setText(checkRequest.reference)
        holder.itemView.lblTypeRequest.setText(checkRequest.requestName)
        holder.itemView.lblStatus.setText(checkRequest.status)

        if(checkRequest.isPrinter?:true && checkRequest.status == RUtil.R_string(R.string.GIRO_CHECKREQUEST_PRINT)){
            holder.itemView.btnPrint.setImageResource(R.drawable.ic_print)
            holder.itemView.btnPrint.visibility = View.VISIBLE

        } else {
            holder.itemView.btnPrint.setImageResource(R.drawable.ic_print_off)
            holder.itemView.btnPrint.isEnabled = false
        }

        holder.itemView.btnView.setOnClickListener {

            listener?.onClickItemDetail(checkRequest)

        }

        holder.itemView.btnPrint.setOnClickListener {

            listener?.onClickItemPrint(checkRequest)

        }

    }



    inner class GiroConsultViewHolder(view: View) : RecyclerView.ViewHolder(view)

}



