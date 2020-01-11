package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import kotlinx.android.synthetic.main.item_salepoint_menu.view.*


class SalePointMenuAdapter(var data: List<MainMenuModel>) : RecyclerView.Adapter<SalePointMenuAdapter.GiroMenuViewHolder>() {

    var index = -1

    interface OnActionListener {
        fun onClickMenu(type: MainMenuEnum?)
    }

    lateinit var context: Context
    private var listener: MenuAdapter.OnActionListener? = null

    fun setListener(listener: MenuAdapter.OnActionListener) {
        this.listener = listener
    }

    fun setSelectedItem(index:Int){

        this.index =index
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiroMenuViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_salepoint_menu, parent, false)
        return GiroMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: GiroMenuViewHolder, position: Int) {

        val menu = data[position]
        holder.itemView.tvMenu.text = menu.name
        holder.itemView.setOnClickListener {
            index = position
            notifyDataSetChanged()
            listener?.onClickMenu(menu.menuType)
        }

        if (!menu.isEnable) {

            holder.itemView.visibility = View.GONE
        }

        if (index == position) {

            holder.itemView.setBackgroundResource(R.color.colorPrimary)
            holder.itemView.tvMenu.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        } else {

            holder.itemView.setBackgroundResource(R.drawable.item_salepoint_menu_background)
            holder.itemView.tvMenu.setTextColor(ContextCompat.getColor(context, R.color.colorGrayLight))


        }


    }

    inner class GiroMenuViewHolder(view: View) : RecyclerView.ViewHolder(view)

}



