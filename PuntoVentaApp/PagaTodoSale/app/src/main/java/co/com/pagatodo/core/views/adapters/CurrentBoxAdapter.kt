package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductBoxModel
import kotlinx.android.synthetic.main.item_currentbox.view.*

class CurrentBoxAdapter(var data: List<ProductBoxModel>): RecyclerView.Adapter<CurrentBoxAdapter.CurrentBoxViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentBoxViewHolder {
        context = parent.context
        return CurrentBoxViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currentbox, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CurrentBoxViewHolder, position: Int) {
        holder.itemView.lblName.text = context.getString(R.string.text_label_parameter_two_points, data[position].name.toString().toLowerCase().capitalize())
        holder.itemView.lblValue.text = data[position].saleValue.toString()
    }

    inner class CurrentBoxViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}