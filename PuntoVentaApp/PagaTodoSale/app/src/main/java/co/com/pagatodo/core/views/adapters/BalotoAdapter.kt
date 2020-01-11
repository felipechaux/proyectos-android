package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.BalotoBoardModel
import co.com.pagatodo.core.util.InputFilterMinMax
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import kotlinx.android.synthetic.main.item_board_baloto.view.*

class BalotoAdapter (var data: MutableList<BalotoBoardModel>): RecyclerView.Adapter<BalotoAdapter.BalotoViewHolder>()  {

    interface OnActionListener {
        fun onDeleteBoard(index: Int?)
        fun onSetAutomaticBoard(index: Int?, isAutomatic:Boolean)
    }

    lateinit var context: Context
    private var inflater: LayoutInflater? = null
    private var listener: OnActionListener? = null

    fun setListener(listener: OnActionListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalotoViewHolder {
        context = parent.context
        inflater = LayoutInflater.from(context)
        val view = inflater?.inflate(R.layout.item_board_baloto, parent, false)

        return BalotoViewHolder(view ?: View(context))
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: BalotoViewHolder, position: Int) {

        val balotoBoardModel = data[position]
        val numbers = data[position].selections ?: arrayOf()

        holder.itemView.txtItemPanelTitleBaloto.text = "${R_string(R.string.baloto_text_panel)} ${position + 1}"

        if (numbers.isNotEmpty()){
            holder.itemView.etPanelBalota1.setText(numbers[0])
            holder.itemView.etPanelBalota2.setText(numbers[1])
            holder.itemView.etPanelBalota3.setText(numbers[2])
            holder.itemView.etPanelBalota4.setText(numbers[3])
            holder.itemView.etPanelBalota5.setText(numbers[4])
            holder.itemView.etPanelBalota6.setText(numbers[5])
        }else{
            setCharacterInEdit(holder.itemView, "",true,0)
        }

        holder.itemView.cbAutomaticBaloto.isChecked = data[position].quickpick ?: false
        holder.itemView.cbRematchBaloto.isChecked = data[position].addonPlayed ?: false


        formatPanelBalota(holder.itemView.etPanelBalota1,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
        formatPanelBalota(holder.itemView.etPanelBalota2,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
        formatPanelBalota(holder.itemView.etPanelBalota3,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
        formatPanelBalota(holder.itemView.etPanelBalota4,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
        formatPanelBalota(holder.itemView.etPanelBalota5,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)

        formatPanelBalota(holder.itemView.etPanelBalota6,balotoBoardModel.secondarySelectionsLowNumber?:1,balotoBoardModel.secondarySelectionsHighNumber?:16)

    }

    private fun formatPanelBalota(edt:EditText,min:Int,max:Int){
        edt.filters = arrayOf<InputFilter>(  InputFilterMinMax(min,max))
    }

    private fun setCharacterInEdit(itemView: View, character: String, isEnabled: Boolean = true,position: Int){

        if(!isEnabled) {
            disableFilter(itemView.etPanelBalota1)
            disableFilter(itemView.etPanelBalota2)
            disableFilter(itemView.etPanelBalota3)
            disableFilter(itemView.etPanelBalota4)
            disableFilter(itemView.etPanelBalota5)
            disableFilter(itemView.etPanelBalota6)
        }else{
            val balotoBoardModel = data[position]
            formatPanelBalota(itemView.etPanelBalota1,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
            formatPanelBalota(itemView.etPanelBalota2,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
            formatPanelBalota(itemView.etPanelBalota3,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
            formatPanelBalota(itemView.etPanelBalota4,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)
            formatPanelBalota(itemView.etPanelBalota5,balotoBoardModel.primarySelectionsLowNumber?:1,balotoBoardModel.primarySelectionsHighNumber?:43)

            formatPanelBalota(itemView.etPanelBalota6,balotoBoardModel.secondarySelectionsLowNumber?:1,balotoBoardModel.secondarySelectionsHighNumber?:16)

        }

        itemView.etPanelBalota1.setText(character)
        itemView.etPanelBalota1.isEnabled = isEnabled

        itemView.etPanelBalota2.setText(character)
        itemView.etPanelBalota2.isEnabled = isEnabled

        itemView.etPanelBalota3.setText(character)
        itemView.etPanelBalota3.isEnabled = isEnabled

        itemView.etPanelBalota4.setText(character)
        itemView.etPanelBalota4.isEnabled = isEnabled

        itemView.etPanelBalota5.setText(character)
        itemView.etPanelBalota5.isEnabled = isEnabled

        itemView.etPanelBalota6.setText(character)
        itemView.etPanelBalota6.isEnabled = isEnabled
    }

    private fun disableFilter(edt:EditText){
        edt.filters = arrayOf<InputFilter>(  )
    }



    private fun nextNumberField(count:Int, et:EditText){
        if (count >= 2){
            et.requestFocus()
        }
    }

    inner class BalotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.cbAutomaticBaloto.setOnCheckedChangeListener { _, _ ->
                listener?.onSetAutomaticBoard(adapterPosition, itemView.cbAutomaticBaloto.isChecked)
                if (itemView.cbAutomaticBaloto.isChecked){
                    setCharacterInEdit(itemView, "*",false,adapterPosition)
                }else{
                    setCharacterInEdit(itemView, "",true,adapterPosition)
                    itemView.etPanelBalota1.requestFocus()
                }
            }

            itemView.cbRematchBaloto.setOnCheckedChangeListener { buttonView, isChecked ->
                data[adapterPosition].addonPlayed = itemView.cbRematchBaloto.isChecked
            }

            itemView.btDeletePanel.setOnClickListener {
                if (itemCount > 1) {
                    listener?.onDeleteBoard(adapterPosition)
                }else{
                    itemView.cbAutomaticBaloto.isChecked = false
                    itemView.cbRematchBaloto.isChecked = false
                    itemView.etPanelBalota1.setText("")
                    itemView.etPanelBalota2.setText("")
                    itemView.etPanelBalota3.setText("")
                    itemView.etPanelBalota4.setText("")
                    itemView.etPanelBalota5.setText("")
                    itemView.etPanelBalota6.setText("")
                }
            }

            addTextWatchers()

        }

        private fun addTextWatchers() {
            itemView.etPanelBalota1.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    val text = itemView.etPanelBalota1.text.toString()
                    data[adapterPosition].selections?.set(0, text)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                    nextNumberField(number?.length ?: 0, itemView.etPanelBalota2)
                }
            })

            itemView.etPanelBalota2.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    val text = itemView.etPanelBalota2.text.toString()
                    data[adapterPosition].selections?.set(1, text)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                    nextNumberField(number?.length ?: 0, itemView.etPanelBalota3)
                }
            })

            itemView.etPanelBalota3.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    val text = itemView.etPanelBalota3.text.toString()
                    data[adapterPosition].selections?.set(2, text)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                    nextNumberField(number?.length ?: 0, itemView.etPanelBalota4)
                }
            })

            itemView.etPanelBalota4.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    val text = itemView.etPanelBalota4.text.toString()
                    data[adapterPosition].selections?.set(3, text)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                    nextNumberField(number?.length ?: 0, itemView.etPanelBalota5)
                }
            })

            itemView.etPanelBalota5.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    val text = itemView.etPanelBalota5.text.toString()
                    data[adapterPosition].selections?.set(4, text)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                    nextNumberField(number?.length ?: 0, itemView.etPanelBalota6)
                }
            })

            itemView.etPanelBalota6.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    val text = itemView.etPanelBalota6.text.toString()
                    data[adapterPosition].selections?.set(5, text)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}