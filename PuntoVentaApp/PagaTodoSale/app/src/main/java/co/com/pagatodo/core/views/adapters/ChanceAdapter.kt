package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.util.AddCurrencySymbolTextWatcher
import co.com.pagatodo.core.util.AddCurrencySymbolTextWatcherListener
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_change_game.view.*
import java.util.concurrent.TimeUnit

class ChanceAdapter(var data: List<ChanceModel>, var isRepeatGame: Boolean = false) :
    RecyclerView.Adapter<ChanceAdapter.ChanceViewHolder>() {

    enum class ChanceGameType {
        number, direct, combined, paw, nail
    }

    interface OnChanceGamesListener {
        fun onTextChanged(adapterPosition: Int, fieldType: ChanceGameType, text: String)
        fun onSelectedRow(row: Int)
    }

    private val timeEventTextChange: Long = 0
    lateinit var context: Context
    private var inflater: LayoutInflater? = null
    private var subscriptions = arrayListOf<Disposable>()

    private var listener: OnChanceGamesListener? = null

    fun setOnChanceGameListener(listener: OnChanceGamesListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChanceViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_change_game, parent, false)
        return ChanceViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: ChanceViewHolder, position: Int) {
        val chance = data[position]

        holder.itemView.etNail.text = SpannableStringBuilder(chance.nail ?: "")
        holder.itemView.etPaw.text = SpannableStringBuilder(chance.paw ?: "")
        holder.itemView.etDirect.text = SpannableStringBuilder(chance.direct ?: "")
        holder.itemView.etCombined.text = SpannableStringBuilder(chance.combined ?: "")
        holder.itemView.etNumber.text = SpannableStringBuilder(chance.number ?: "")

        if(isRepeatGame){
            holder.enableCellValidator(holder.itemView.etNumber.text.toString().length)
        }

        holder.initViewR()


    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subscriptions.forEach {
            it.dispose()
        }
    }

    inner class ChanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun initViewR() {

            subscriptions.add(
                RxTextView.textChanges(itemView.etNumber)
                    .debounce(timeEventTextChange, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (adapterPosition >= 0) {
                            data[adapterPosition].number = it.toString()
                            enableCellValidator(it.toString().length)
                            listener?.onTextChanged(
                                adapterPosition,
                                ChanceGameType.number,
                                it.toString()
                            )
                        }
                    }
            )

            itemView.btnDelete.setOnClickListener { clearRow() }

            itemView.etNumber.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    listener?.onSelectedRow(adapterPosition)
                }
            }

            itemView.etDirect.addTextChangedListener(
                AddCurrencySymbolTextWatcher(itemView.etDirect,
                    object : AddCurrencySymbolTextWatcherListener {
                        override fun onTextChange(number: String) {
                            data[adapterPosition].direct = number.toDouble().toString()
                        }
                    })
            )

            itemView.etCombined.addTextChangedListener(
                AddCurrencySymbolTextWatcher(itemView.etCombined,
                    object : AddCurrencySymbolTextWatcherListener {
                        override fun onTextChange(number: String) {
                            data[adapterPosition].combined = number.toDouble().toString()
                        }
                    })
            )

            itemView.etPaw.addTextChangedListener(
                AddCurrencySymbolTextWatcher(itemView.etPaw,
                    object : AddCurrencySymbolTextWatcherListener {
                        override fun onTextChange(number: String) {
                            data[adapterPosition].paw = number.toDouble().toString()
                        }
                    })
            )

            itemView.etNail.addTextChangedListener(
                AddCurrencySymbolTextWatcher(itemView.etNail,
                    object : AddCurrencySymbolTextWatcherListener {
                        override fun onTextChange(number: String) {
                            data[adapterPosition].nail = number.toDouble().toString()
                        }
                    })
            )


            subscriptions.add(
                RxTextView.textChanges(itemView.etDirect)
                    .debounce(timeEventTextChange, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (adapterPosition >= 0) {
                            data[adapterPosition].direct = it.toString()
                            enablePawAndNailFields()
                            listener?.onTextChanged(
                                adapterPosition,
                                ChanceGameType.direct, it.toString()
                            )
                        }
                    }
            )

            subscriptions.add(
                RxTextView.textChanges(itemView.etCombined)
                    .debounce(timeEventTextChange, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (adapterPosition >= 0) {
                            data[adapterPosition].combined = it.toString()
                            listener?.onTextChanged(
                                adapterPosition,
                                ChanceGameType.combined, it.toString()
                            )
                        }
                    }
            )

            subscriptions.add(
                RxTextView.textChanges(itemView.etPaw)
                    .debounce(timeEventTextChange, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (adapterPosition >= 0) {
                            data[adapterPosition].paw = it.toString()
                            listener?.onTextChanged(
                                adapterPosition,
                                ChanceGameType.paw, it.toString()
                            )
                        }
                    }
            )

            subscriptions.add(
                RxTextView.textChanges(itemView.etNail)
                    .debounce(timeEventTextChange, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (adapterPosition >= 0) {
                            data[adapterPosition].nail = it.toString()
                            listener?.onTextChanged(
                                adapterPosition,
                                ChanceGameType.nail, it.toString()
                            )
                        }
                    }
            )


        }

        private fun enablePawAndNailFields(enableValue: Boolean) {
            itemView.etPaw.isEnabled = enableValue
            itemView.etNail.isEnabled = enableValue
            if (!enableValue) {
                itemView.etPaw.text = SpannableStringBuilder("")
                itemView.etNail.text = SpannableStringBuilder("")
            }
        }

        private fun enablePawAndNailFields() {

            if (itemView.etNumber.text.toString().length == 3) {

                if (itemView.etDirect.text.toString().isNotEmpty()) {

                    itemView.etPaw.isEnabled = true
                    itemView.etNail.isEnabled = true
                    itemView.etPaw.setBackgroundResource(R.drawable.edittext_with_rounded_border)
                    itemView.etNail.setBackgroundResource(R.drawable.edittext_with_rounded_border)
                } else {
                    itemView.etPaw.isEnabled = false
                    itemView.etNail.isEnabled = false
                    itemView.etPaw.setBackgroundResource(R.drawable.edittext_with_rounded_border_disable)
                    itemView.etNail.setBackgroundResource(R.drawable.edittext_with_rounded_border_disable)
                    itemView.etPaw.setText("")
                    itemView.etNail.setText("")
                }

            }


        }

        fun enableCellValidator(size: Int) {

            itemView.etPaw.isEnabled = false
            itemView.etNail.isEnabled = false
            itemView.etCombined.isEnabled = false
            itemView.etDirect.isEnabled = false

            itemView.etPaw.setBackgroundResource(R.drawable.edittext_with_rounded_border_disable)
            itemView.etNail.setBackgroundResource(R.drawable.edittext_with_rounded_border_disable)
            itemView.etCombined.setBackgroundResource(R.drawable.edittext_with_rounded_border_disable)
            itemView.etDirect.setBackgroundResource(R.drawable.edittext_with_rounded_border_disable)


            if (size == 3 || size == 4) {

                itemView.etCombined.isEnabled = true
                itemView.etDirect.isEnabled = true
                itemView.etCombined.setBackgroundResource(R.drawable.edittext_with_rounded_border)
                itemView.etDirect.setBackgroundResource(R.drawable.edittext_with_rounded_border)

                if(isRepeatGame){
                    enablePawAndNailFields()
                }else{
                    itemView.etPaw.setText("")
                    itemView.etNail.setText("")
                }



            } else if (size == 2) {

                itemView.etPaw.isEnabled = true
                itemView.etNail.isEnabled = true
                itemView.etPaw.setBackgroundResource(R.drawable.edittext_with_rounded_border)
                itemView.etNail.setBackgroundResource(R.drawable.edittext_with_rounded_border)

                itemView.etCombined.setText("")
                itemView.etDirect.setText("")


            } else if (size == 1) {

                itemView.etNail.isEnabled = true
                itemView.etNail.setBackgroundResource(R.drawable.edittext_with_rounded_border)

                itemView.etPaw.setText("")
                itemView.etCombined.setText("")
                itemView.etDirect.setText("")

            } else if (size == 0) {

                itemView.etPaw.setText("")
                itemView.etDirect.setText("")
                itemView.etCombined.setText("")
                itemView.etNail.setText("")

            }


            enablePawAndNailFields()
        }

        private fun clearRow() {
            itemView.etNumber.text = SpannableStringBuilder("")
            itemView.etDirect.text = SpannableStringBuilder("")
            itemView.etCombined.text = SpannableStringBuilder("")
            itemView.etPaw.text = SpannableStringBuilder("")
            itemView.etNail.text = SpannableStringBuilder("")
        }
    }
}