package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.util.formatValue
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.item_pm_modalities_layout.view.*

class PayMillionaireModalityAdapter(var data: List<ModeValueModel>, var listener: OnAdapterListener? = null): RecyclerView.Adapter<PayMillionaireModalityAdapter.PayMillionaireModalityViewHolder>() {

    interface OnAdapterListener {
        fun onModalitySelected(item: ModeValueModel)
    }

    lateinit var context: Context
    private var inflater: LayoutInflater? = null
    private var subscriptions = arrayListOf<Disposable>()
    var mSelectedItem = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayMillionaireModalityViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_pm_modalities_layout, parent, false)
        return PayMillionaireModalityViewHolder( view!!)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: PayMillionaireModalityViewHolder, position: Int) {
        val item = data[position]
        holder.itemView.tvName.text = item.name
        holder.itemView.tvValue.text = "$ ${formatValue((item.value ?: 0).toDouble())}"
        holder.itemView.radio.isChecked = position == mSelectedItem
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subscriptions.forEach {
            it.dispose()
        }
    }

    inner class PayMillionaireModalityViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            val clickListener = View.OnClickListener {
                mSelectedItem = adapterPosition
                notifyDataSetChanged()

                val index = if(layoutPosition >= 0) layoutPosition else 0
                listener?.onModalitySelected(data[index])
            }
            itemView.setOnClickListener(clickListener)
            itemView.radio.setOnClickListener(clickListener)
        }
    }
}