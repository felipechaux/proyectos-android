package co.com.pagatodo.core.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.MainMenuModel
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.views.homemenu.MainMenuEnum
import kotlinx.android.synthetic.main.item_main_menu.view.*

class MenuAdapter(
    var data: List<MainMenuModel>
): RecyclerView.Adapter<MenuAdapter.MainMenuViewHolder>() {

    interface OnActionListener {
        fun onClickMenu(type: MainMenuEnum?)
    }

    lateinit var context: Context
    private var listener: OnActionListener? = null

    fun setListener(listener: OnActionListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_main_menu, parent, false)
        return MainMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: MainMenuViewHolder, position: Int) {
        val menu = data[position]
        holder.itemView.tvName.text = menu.name
        holder.itemView.setOnClickListener { listener?.onClickMenu(menu.menuType) }

        if((!DeviceUtil.isSalePoint() && position<=1) || (DeviceUtil.isSalePoint() && position <= 4)) {
            holder.itemView.spaceView.visibility = View.VISIBLE
        }

        if (!menu.isEnable){
            holder.itemView.visibility = View.GONE
        }
    }

    inner class MainMenuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}