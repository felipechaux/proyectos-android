package co.com.pagatodo.core.views.adapters

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.DisplayMetrics
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.util.DeviceUtil
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_pay_raffle.*
import kotlinx.android.synthetic.main.item_change_game.view.*
import kotlinx.android.synthetic.main.item_mega_goal_games.view.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class MegaGoalAdapter(var data: List<SportingBetModel>): RecyclerView.Adapter<MegaGoalAdapter.MegaGoalViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MegaGoalViewHolder {
        context = parent.context
        return MegaGoalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mega_goal_games, parent, false))
    }

    override fun getItemCount(): Int {
        return data.count()
    }

    override fun onBindViewHolder(holder: MegaGoalViewHolder, position: Int) {
        val item = data[position]
        holder.itemView.tvLocal.text = item.localName
        holder.itemView.tvVisitor.text = item.visitorName

        if (item.isLocalResult ?: false && item.isVisitorResult ?: false){
            holder.itemView.etLocalMarker.setText(item.localMarker.toString())
            holder.itemView.etVisitorMarker.setText(item.visitorMarker.toString())
            item.isLocalResult = true
            item.isVisitorResult = true
        }else{
            item.localMarker = 0
            item.visitorMarker = 0
            holder.itemView.etLocalMarker.setText("")
            holder.itemView.etVisitorMarker.setText("")
        }

        if (DeviceUtil.isSalePoint()){
            val columnNumber = 3
            val spaceToReduceForItem = 30
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            holder.itemView.getLayoutParams().width = width/columnNumber - spaceToReduceForItem
        }
    }

    inner class MegaGoalViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.btnPlusLocal.setOnClickListener {
                if (!isFull()){
                    if (adapterPosition >= 0 && (data[adapterPosition].localMarker ?: 0) < 9) {
                        data[adapterPosition].localMarker =  (data[adapterPosition].localMarker ?: 0) + 1
                        data[adapterPosition].isLocalResult = true
                        itemView.etLocalMarker.setText("${data[adapterPosition].localMarker}")
                    }
                }else{
                    if (adapterPosition >= 0
                        && data[adapterPosition].isLocalResult ?: false
                        && (data[adapterPosition].localMarker ?: 0) < 9){
                        data[adapterPosition].localMarker =  (data[adapterPosition].localMarker ?: 0) + 1
                        itemView.etLocalMarker.setText("${data[adapterPosition].localMarker}")
                    }
                }
            }

            itemView.btnRestLocal.setOnClickListener {
                if (adapterPosition >= 0 && (data[adapterPosition].localMarker ?: 0) > 0){
                    data[adapterPosition].localMarker = (data[adapterPosition].localMarker ?: 1) - 1
                    itemView.etLocalMarker.setText("${data[adapterPosition].localMarker}")
                }else if (adapterPosition >= 0){
                    itemView.etLocalMarker.setText("")
                    data[adapterPosition].isLocalResult = false
                }
            }

            itemView.btnPlusVisitor.setOnClickListener {
                if (!isFull()){
                    if (adapterPosition >= 0 && (data[adapterPosition].visitorMarker ?: 0) < 9) {
                        data[adapterPosition].visitorMarker =  (data[adapterPosition].visitorMarker ?: 0) + 1
                        data[adapterPosition].isVisitorResult = true
                        itemView.etVisitorMarker.setText("${data[adapterPosition].visitorMarker}")
                    }
                }else{
                    if (adapterPosition >= 0
                        && data[adapterPosition].isVisitorResult ?: false
                        && (data[adapterPosition].visitorMarker ?: 0) < 9){
                        data[adapterPosition].visitorMarker =  (data[adapterPosition].visitorMarker ?: 0) + 1
                        itemView.etVisitorMarker.setText("${data[adapterPosition].visitorMarker}")
                    }
                }
            }

            itemView.btnRestVisitor.setOnClickListener {
                if (adapterPosition >= 0 && (data[adapterPosition].visitorMarker ?: 0) > 0){
                    data[adapterPosition].visitorMarker = (data[adapterPosition].visitorMarker ?: 0) - 1
                    itemView.etVisitorMarker.setText("${data[adapterPosition].visitorMarker}")
                }else if (adapterPosition >= 0){
                    itemView.etVisitorMarker.setText("")
                    data[adapterPosition].isVisitorResult = false
                }
            }

            itemView.etLocalMarker.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP){
                    val availableItem = data[adapterPosition].isLocalResult ?: false
                    val digitedGoalNumber = itemView.etLocalMarker.text.toString()
                    if(!isFull()) {
                        if (!digitedGoalNumber.isEmpty()) {
                            data[adapterPosition].localMarker = itemView.etLocalMarker.text.toString().toInt()
                            data[adapterPosition].isLocalResult = true
                        } else {
                            data[adapterPosition].localMarker = 0
                            data[adapterPosition].isLocalResult = false
                        }
                        if (isFull()){
                            context.let { it1 -> DeviceUtil.hideKeyboard(itemView.etVisitorMarker, it1) }
                        }
                    }else if(availableItem){
                        if (!digitedGoalNumber.isEmpty()){
                            data[adapterPosition].localMarker = itemView.etLocalMarker.text.toString().toInt()
                            data[adapterPosition].isLocalResult = true
                        }else {
                            data[adapterPosition].localMarker = 0
                            data[adapterPosition].isLocalResult = false
                        }
                    }

                    return@setOnKeyListener true
                }
                false
            }

            itemView.etVisitorMarker.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP){
                    val availableItem = data[adapterPosition].isVisitorResult ?: false
                    val digitedGoalNumber = itemView.etVisitorMarker.text.toString()
                    if(!isFull()){
                        if (!digitedGoalNumber.isEmpty()){
                            data[adapterPosition].visitorMarker = itemView.etVisitorMarker.text.toString().toInt()
                            data[adapterPosition].isVisitorResult = true
                        } else {
                            data[adapterPosition].visitorMarker = 0
                            data[adapterPosition].isVisitorResult = false
                        }
                        if (isFull()){
                            context.let { it1 -> DeviceUtil.hideKeyboard(itemView.etVisitorMarker, it1) }
                        }
                    }else if(availableItem){
                        if (!digitedGoalNumber.isEmpty()){
                            data[adapterPosition].visitorMarker = digitedGoalNumber.toInt()
                            data[adapterPosition].isVisitorResult = true
                        }else {
                            data[adapterPosition].visitorMarker = 0
                            data[adapterPosition].isVisitorResult = false
                        }
                    }
                    return@setOnKeyListener true
                }
                false
            }


            itemView.etLocalMarker.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val digitedGoalNumber = itemView.etLocalMarker.text.toString()
                    if (digitedGoalNumber.isEmpty()){
                        data[adapterPosition].localMarker = 0
                        data[adapterPosition].isLocalResult = false
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            itemView.etVisitorMarker.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val digitedGoalNumber = itemView.etVisitorMarker.text.toString()
                    if (digitedGoalNumber.isEmpty()){
                        data[adapterPosition].visitorMarker = 0
                        data[adapterPosition].isVisitorResult = false
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        private fun isFull(): Boolean{
            var full = false
            val array = data.filter { it.isLocalResult ?: true && it.isVisitorResult ?: true }
            if(array.count() == 2){

                full=true
                try{
                    Handler().post({
                        notifyDataSetChanged()
                    })
                }catch(e: Exception){
                    Log.i("MEGAGOL", e.message)
                }
            }
            return full
        }
    }
}