package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SuperchanceModel
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jaredrummler.materialspinner.MaterialSpinner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_superchance_game.view.*
import java.util.concurrent.TimeUnit

class SuperChanceAdapter(
    var data: List<SuperchanceModel>,
    var values: List<String>?,
    var optionValidateFields: Int,
    var listener: OnListenerAdapter? = null): RecyclerView.Adapter<SuperChanceAdapter.SuperchanceViewHolder>() {

    interface OnListenerAdapter {
        fun onTextChange(text: String)
    }

    lateinit var context: Context
    private var subscriptions = arrayListOf<Disposable>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperchanceViewHolder {
        context = parent.context
        return SuperchanceViewHolder(LayoutInflater.from(context).inflate(R.layout.item_superchance_game, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SuperchanceViewHolder, position: Int) {
        values?.let { holder.itemView.spinnerValue.setItems(it) }
        validateField(optionValidateFields, holder.itemView.txtNumber)
        clearFieldText(holder.itemView.imgClear, holder.itemView.txtNumber, holder.itemView.spinnerValue)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subscriptions.forEach {
            it.dispose()
        }
    }

    private fun validateField(limit: Int, fieldValidate: EditText) {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(limit)
        fieldValidate.filters = filterArray
    }

    private fun clearFieldText(imgClear: ImageView, fieldText: EditText, spinner: MaterialSpinner) {
        imgClear.setOnClickListener {
            fieldText.text.clear()
            spinner.selectedIndex = 0
        }
    }

    inner class SuperchanceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            RxTextView.textChanges(itemView.txtNumber)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (adapterPosition >= 0) {
                        data[adapterPosition].number = it.toString()
                        listener?.onTextChange(it.toString())
                    }
                }, {
                    Log.d("Error",it.message)
                })

            itemView.spinnerValue.setOnItemSelectedListener { _, _, _, item ->
                listener?.onTextChange(item.toString())
            }

            itemView.spinnerValue.setOnTouchListener { _, _ ->
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(itemView.txtNumber.windowToken, 0)
            }
        }
    }
}