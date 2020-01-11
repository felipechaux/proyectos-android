package co.com.pagatodo.core.views.components.lottery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.LotteryModel
import kotlinx.android.synthetic.main.item_lottery_component.view.*

class LotteryComponentAdapter(var data: List<LotteryModel>): RecyclerView.Adapter<LotteryComponentAdapter.LotteryComponentViewHolder>() {

    interface OnCheckboxListener {
        fun onCheckedChanged(lotteryItem: LotteryModel, isChecked: Boolean)
    }

    private var context: Context? = null
    private var inflater: LayoutInflater? = null
    private var checkboxListener: OnCheckboxListener? = null

    private var isValidateMaxNumberSelection = false
    private var maxNumberSelection = 0

    fun setOnCheckboxListener(listener: OnCheckboxListener) {
        checkboxListener = listener
    }

    fun setValidationCheckSelection(maxNumberSelection: Int, isValidateMaxNumberSelection: Boolean = false) {
        this.isValidateMaxNumberSelection = isValidateMaxNumberSelection
        this.maxNumberSelection = maxNumberSelection
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LotteryComponentViewHolder {
        context = parent.context
        return LotteryComponentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lottery_component, parent, false))
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: LotteryComponentViewHolder, position: Int) {
        val lottery = data[position]
        holder.lotteryName.text = lottery.name
        holder.chkLotterySelect.isChecked = lottery.isSelected
    }


    inner class LotteryComponentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal var lotteryName = itemView.tvLotteryName
        internal var chkLotterySelect = itemView.chkLotterySelect

        init {
            chkLotterySelect.setOnClickListener {
                val check = (it as AppCompatCheckBox)
                val item = data[adapterPosition]
                var isChecked = true
                if (maxNumberSelection > 0) {
                    if (data.filter { it.isSelected }.count() >= maxNumberSelection) {
                        chkLotterySelect.isChecked = false
                        item.isSelected = false
                        isChecked = false
                    }
                    else {
                        item.isSelected = check.isChecked
                    }
                }
                else {
                    item.isSelected = check.isChecked
                }
                checkboxListener?.onCheckedChanged(item, isChecked)
            }
        }
    }
}