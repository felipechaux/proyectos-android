package co.com.pagatodo.core.views.adapters

import android.view.View
import android.view.ViewGroup
import co.com.pagatodo.core.R
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_baloto.view.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.data.model.BalotoResultsModel
import kotlinx.android.synthetic.main.item_title.view.*

class BalotoResultAdapter(
    var title: String,
    var data: List<BalotoResultsModel>
): RecyclerView.Adapter<BalotoResultAdapter.BalotoViewHolder>() {

    lateinit var context: Context

    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when(data[position].typeItem) {
            0 -> TYPE_TITLE
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalotoViewHolder {
        context = parent.context
        return when (viewType) {
            TYPE_TITLE -> BalotoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_title, parent, false))
            else -> BalotoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_baloto, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BalotoViewHolder, position: Int) {
        val baloto = data[position]
        if(baloto.typeItem == 1) {
            holder.itemView.lblName.text = R_string(R.string.text_title_baloto_dialog_draw)
            holder.itemView.lblNameRevenge.text = R_string(R.string.text_title_baloto_revenge_dialog_draw)
            holder.itemView.lblNumber.text = setFormatToNumbers(baloto.balotoNumber)
            holder.itemView.lblNumberRevenge.text = setFormatToNumbers(baloto.revengeNumber)
            holder.itemView.lblDate.text = setFormatToDate(baloto.date)
            holder.itemView.lblDateRevenge.text = setFormatToDate(baloto.date)
            holder.itemView.lblDraw.text = " ${baloto.draw}"
            holder.itemView.lblDrawRevenge.text = " ${baloto.draw}"
        } else {
            holder.itemView.lblTitle.text = title
        }
    }

    private fun setFormatToNumbers(numbers: String?): String {
        val fourWhiteSpaces = "    "
        val twoWhiteSpaces = "  "
        return numbers.toString().trim()
            .replace(fourWhiteSpaces, R_string(R.string.text_label_sb))
            .replace(twoWhiteSpaces, R_string(R.string.text_dash))
    }

    private fun setFormatToDate(date: String?): String {
        return " ${date.toString().toLowerCase().capitalize()}"
    }

    inner class BalotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}