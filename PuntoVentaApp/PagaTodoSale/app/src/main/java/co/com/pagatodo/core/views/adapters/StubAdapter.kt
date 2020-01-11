package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.StubModel
import kotlinx.android.synthetic.main.item_stub.view.*

class StubAdapter(var data: List<StubModel>): RecyclerView.Adapter<StubAdapter.StubViewHolder>() {

    private var context: Context? = null
    private var inflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StubViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_stub, parent, false)
        return StubViewHolder( view!! )
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: StubViewHolder, position: Int) {
        var stub = data[position]
        holder.itemView.tvSerie1.text = stub.serie1
        holder.itemView.tvSerie2.text = stub.serie2
    }

    inner class StubViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }
}