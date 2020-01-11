package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SuperAstroModel
import co.com.pagatodo.core.util.AddCurrencySymbolTextWatcher
import co.com.pagatodo.core.util.AddCurrencySymbolTextWatcherListener
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.resetValueFormat
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jaredrummler.materialspinner.MaterialSpinner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_superastro_game.view.*
import java.util.concurrent.TimeUnit

class SuperAstroAdapter(
    var data: MutableList<SuperAstroModel>,
    var optionValidateFields: Int,
    var listener: OnListenerAdapter? = null): RecyclerView.Adapter<SuperAstroAdapter.SuperAstroViewHolder>() {
    private var maxValueGame = 0

    interface OnListenerAdapter {
        fun onGenerateRamdon(position: Int)
        fun onDeleteGameItem(position: Int)
    }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperAstroViewHolder {
        context = parent.context
        return SuperAstroViewHolder(LayoutInflater.from(context).inflate(R.layout.item_superastro_game, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SuperAstroViewHolder, position: Int) {
        val item = data[position]
        loadZodiacListInSpinner(holder.itemView.spinnerZodiacSigns, position)
        clickGenerateRandomNumber(holder.itemView.imgRandomNumber,holder.itemView.labelErrorZodiac ,position)
        if (item.number?.isNotEmpty() == true){
            holder.itemView.txtNumber.setText(item.number)
            val tmpValue = item.value?.toString()
            holder.itemView.txtValueGame.setText(tmpValue)
            item.zodiacs?.forEachIndexed { index, it ->
                if (it == item.zodiacSelected){
                    holder.itemView.spinnerZodiacSigns.selectedIndex = index
                }
            }
        }
    }

    inner class SuperAstroViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
         init {
             itemView.imgClear.setOnClickListener {
                 itemView.txtNumber.setText("")
                 itemView.txtValueGame.setText("")
                 itemView.spinnerZodiacSigns.selectedIndex = 0

                 itemView.labelErrorNumber.visibility = View.GONE
                 itemView.labelErrorNumberFormatInvalid.visibility = View.GONE
                 itemView.labelErrorValue.visibility = View.GONE
                 itemView.labelErrorValueInvalid.visibility = View.GONE
                 itemView.labelErrorZodiac.visibility = View.GONE

                 if (adapterPosition > 0){
                     listener?.onDeleteGameItem(adapterPosition)
                     notifyItemRemoved(adapterPosition)
                 }
             }

            RxTextView.textChanges(itemView.txtNumber)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (adapterPosition >= 0){
                        data[adapterPosition].number = it.toString()
                        itemView.labelErrorNumber.visibility = View.GONE
                        itemView.labelErrorNumberFormatInvalid.visibility = View.GONE
                    }
                }

            var isEditable = true
            itemView.txtValueGame.addTextChangedListener(AddCurrencySymbolTextWatcher(itemView.txtValueGame,
                object : AddCurrencySymbolTextWatcherListener {
                    override fun onTextChange(number: String) {
                        if(isEditable){
                            isEditable = false
                            data[adapterPosition].value = number.toDouble()
                            itemView.labelErrorValue.visibility = View.GONE
                            itemView.labelErrorValueInvalid.visibility = View.GONE
                            if(data[adapterPosition].value!! > maxValueGame){
                                val newString = "$${formatValue(number.substring(0, number.length-1).toDouble())}"
                                itemView.txtValueGame.setText(newString)
                                itemView.txtValueGame.setSelection(newString.length)
                                data[adapterPosition].value = newString.resetValueFormat().toDouble()
                            }
                            isEditable = true
                        }
                    }
                }))

            itemView.spinnerZodiacSigns.setOnItemSelectedListener { view, position, id, item ->
                if (adapterPosition >= 0) {
                    data[adapterPosition].zodiacSelected = item.toString()
                    itemView.labelErrorZodiac.visibility = View.GONE
                }
            }

            itemView.spinnerZodiacSigns.setOnTouchListener { _, _ ->
                 val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                 imm.hideSoftInputFromWindow(itemView.txtNumber.windowToken, 0)
            }
        }
    }

    private fun clickGenerateRandomNumber(itemView: View,itemErrorView: View,position: Int) {
        itemView.imgRandomNumber.setOnClickListener {
            itemErrorView.labelErrorZodiac.visibility =  View.GONE
            listener?.onGenerateRamdon(position)
        }
    }

    private fun loadZodiacListInSpinner(spinnerZodiacs: MaterialSpinner, position: Int) {
        val astroModel = data[position]
        val zodiacs: MutableList<String>? = astroModel.zodiacs
        zodiacs?.add(0, context.getString(R.string.placeholder_zodiac))
        zodiacs?.let {
            spinnerZodiacs.setItems(it)
        }
    }

    public fun setMaxValueGame(maxValueGame: Int){
        this.maxValueGame = maxValueGame
        Log.i("Valos maximo:", this.maxValueGame.toString())
    }
}