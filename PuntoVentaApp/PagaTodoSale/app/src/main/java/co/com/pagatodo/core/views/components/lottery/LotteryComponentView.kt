package co.com.pagatodo.core.views.components.lottery

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.DatabaseViewModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.sporting.SportingSpacingItemDecoration
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.lottery_component_view.view.*
import java.util.*

class LotteryComponentView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs),
    LotteryComponentAdapter.OnCheckboxListener {

    enum class LotteryAction {
        COMPONENT_IS_READY,
        CHECKBOX_ITEM_SELECTED
    }

    private var observableLotteryComponentView = BehaviorSubject.create<Pair<LotteryAction, List<LotteryModel>?>>()
    private lateinit var lotteryModel: LotteryViewModel
    private var parentView: FragmentActivity
    private lateinit var dataLotteries: List<LotteryModel>
    lateinit var adapter: LotteryComponentAdapter

    /*Attrs*/
    private var lotteryLoadType = 0
    private var isCheckSelection = false
    private var maxNumberSelection = -1
    private var columnNumber = 1

    fun getObservableLotteryComponent(): Observable<Pair<LotteryAction, List<LotteryModel>?>> {
        return observableLotteryComponentView
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.lottery_component_view, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LotteryComponentView,
            0, 0).apply {

            try {
                lotteryLoadType = getInt(R.styleable.LotteryComponentView_lotteryLoadType, 0)
                isCheckSelection = getBoolean(R.styleable.LotteryComponentView_isCheckSelection, true)
                maxNumberSelection = getInt(R.styleable.LotteryComponentView_maxNumberSelection, -1)
                columnNumber = getInt(R.styleable.LotteryComponentView_columnNumber, 1)
            } finally { recycle() }
        }

        parentView = (context as FragmentActivity)
        setupView()
        setupModel()
    }

    fun setMaxNumberSelection(number: Int, isValidateMaxNumberSelection: Boolean = false) {
        maxNumberSelection = number
        val adapter = (rvLotteries.adapter as LotteryComponentAdapter)
        adapter.data.forEach {
            it.isSelected = false
        }
        adapter.setValidationCheckSelection(number, isValidateMaxNumberSelection)
        adapter.notifyDataSetChanged()
    }

    private fun setupView() {
        rvLotteries.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(parentView, columnNumber)
        }
    }

    private fun setupModel() {
        lotteryModel = ViewModelProviders.of( parentView ).get(LotteryViewModel::class.java)
        lotteryModel.lotteries.observe(parentView, Observer<List<LotteryModel>> {
            dataLotteries = it
            loadLotteries(it)
            observableLotteryComponentView.onNext(Pair(LotteryAction.COMPONENT_IS_READY, null))
        })

        loadLotteriesByType()
        configDatabaseEvents()


    }

    @SuppressLint("CheckResult")
    private fun configDatabaseEvents() {
        DatabaseViewModel.database.subscribe {
            if (it == DatabaseViewModel.DatabaseEvents.CHANCE_LOTTERIES_ADDED) {
                parentView.runOnUiThread {
                    loadLotteriesByType()
                }
            }
        }
    }

    private fun loadLotteriesByType() {
        when (lotteryLoadType) {
            0 -> lotteryModel.loadLotteriesForChance()
            1 -> lotteryModel.loadLotteriesForSuperChance()
            2 -> lotteryModel.loadLotteriesForPayMillionaire()
            3 -> lotteryModel.loadLotteriesForSuperAstro()
        }
    }

    fun selectCurrentDay() {
        val calendarDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        var lotteryDay = 1

        when(calendarDay) {
            Calendar.SUNDAY -> lotteryDay = 1
            Calendar.MONDAY -> lotteryDay = 2
            Calendar.TUESDAY -> lotteryDay = 3
            Calendar.WEDNESDAY -> lotteryDay = 4
            Calendar.THURSDAY -> lotteryDay = 5
            Calendar.FRIDAY -> lotteryDay = 6
            Calendar.SATURDAY -> lotteryDay = 7
        }
        applyFilterByDay(lotteryDay)
    }

    /**
     * Método utilizado para habilitar o inhabilitar los checks por medio del switch
     */
    fun checkAllLoterias(switch:Boolean,maxLotteries:Int):Int{
        var cont=0
        adapter.data.forEachIndexed{index, lotteryModel ->
            lotteryModel.isSelected = false
            if (switch) {
                if (index <= maxLotteries - 1 || maxLotteries == 0 || maxLotteries == null) {
                    lotteryModel.isSelected = switch
                    cont++
                }
            } else {
                lotteryModel.isSelected = false

            }
        }
        adapter.notifyDataSetChanged()
        val filteredList = adapter.data.filter { it.isSelected }
        observableLotteryComponentView.onNext(Pair(LotteryAction.CHECKBOX_ITEM_SELECTED, filteredList))
        return cont
    }

    fun getCurrentDay(): Int {
        val calendarDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        var lotteryDay = 1

        when(calendarDay) {
            Calendar.SUNDAY -> lotteryDay = 1
            Calendar.MONDAY -> lotteryDay = 2
            Calendar.TUESDAY -> lotteryDay = 3
            Calendar.WEDNESDAY -> lotteryDay = 4
            Calendar.THURSDAY -> lotteryDay = 5
            Calendar.FRIDAY -> lotteryDay = 6
            Calendar.SATURDAY -> lotteryDay = 7
        }
        return lotteryDay
    }

    fun loadAllLotteries() {
        loadLotteries(dataLotteries)
    }

    fun clearSelection() {
        adapter.data.forEach {
            it.isSelected = false
        }
        adapter.notifyDataSetChanged()
    }

    fun setSelections(selectedLotteries: List<LotteryModel>) {

        adapter.data.forEach { item ->
            val lottery = selectedLotteries.filter { it.code.equals(item.code) }
            if (lottery.isNotEmpty()){
                item.isSelected = lottery.first().isSelected
            }else{
                item.isSelected = false
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun loadLotteries(data: List<LotteryModel>) {
        adapter = LotteryComponentAdapter(data)
        adapter.setValidationCheckSelection(maxNumberSelection)
        adapter.setOnCheckboxListener(this)
        rvLotteries.adapter = this@LotteryComponentView.adapter
        labelErrorLotteris.visibility = View.GONE

        if(data.size==0) {
            labelErrorLotteris.visibility = View.VISIBLE
        }
    }

    fun applyFilterByDay(dayInt: Int) {
        dataLotteries.forEach { it.isSelected = false }
        val newData:List<LotteryModel>
        if (dayInt == getCurrentDay()){
            newData = dataLotteries.filter { it.lotteryDay == "$dayInt" || it.lotteryDay.equals(R_string(R.string.code_weekly_lotteries)) }
        }else{
            newData = dataLotteries.filter { it.lotteryDay == "$dayInt" }
        }
        loadLotteries(newData)


    }

    override fun onCheckedChanged(lotteryItem: LotteryModel, isChecked: Boolean) {
        val filteredList = adapter.data.filter { it.isSelected }
        observableLotteryComponentView.onNext(Pair(LotteryAction.CHECKBOX_ITEM_SELECTED, filteredList))


    }



}