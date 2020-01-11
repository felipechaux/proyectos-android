package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_only_number.view.*
import java.util.concurrent.TimeUnit

class OnlyNumberAdapter(var data: MutableList<String>, var listener: OnAdapterListener? = null): RecyclerView.Adapter<OnlyNumberAdapter.OnlyNumberViewHolder>() {

    interface OnAdapterListener {
        fun onTextChanged(adapterPosition: Int, text: String)
        fun onSelectedRow(row: Int)
        fun onclickRandom3(index: Int)
        fun onClickRandom4(index: Int)
        fun onClickDelete(index: Int)
    }

    lateinit var context: Context
    private var subscriptions = arrayListOf<Disposable>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlyNumberViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_only_number, parent, false)
        return OnlyNumberViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: OnlyNumberViewHolder, position: Int) {
        val number = data[position]
        holder.itemView.etNumber.text = SpannableStringBuilder(number)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subscriptions.forEach {
            it.dispose()
        }
    }

    inner class OnlyNumberViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {

            itemView.etNumber.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    listener?.onSelectedRow(adapterPosition)
                }
            }

            subscriptions.add(
                RxTextView.textChanges(itemView.etNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .debounce(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (adapterPosition >= 0) {
                            val value = it.toString()
                            data[adapterPosition] = value
                            listener?.onTextChanged(adapterPosition, value)
                        }
                    }
            )

            itemView.btnRandom3.setOnClickListener { listener?.onclickRandom3(adapterPosition) }
            itemView.btnRandom4.setOnClickListener { listener?.onClickRandom4(adapterPosition) }
            itemView.btnDelete.setOnClickListener { listener?.onClickDelete(adapterPosition) }
        }
    }
}