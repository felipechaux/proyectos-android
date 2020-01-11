package co.com.pagatodo.core.views.components.daylottery

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.DateUtil
import kotlinx.android.synthetic.main.day_lottery_component_view.view.*
import java.text.DateFormatSymbols
import java.util.*

class DayLotteryComponentView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    interface OnDayLotteryListener {
        fun onSelectedDay(day: Int, dayName: String, date: Date)
    }

    private var dayViews = mutableListOf<View>()
    private var isSelectedDay: Boolean = false
    private var selectedDate = Date()
    var listener: OnDayLotteryListener? = null


    init {

        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.day_lottery_component_view, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DayLotteryComponentView,
            0, 0).apply {

            try {
                isSelectedDay = getBoolean(R.styleable.DayLotteryComponentView_isSelectCurrentDay, false)
            } finally {
                recycle()
            }
        }

        setup()
    }

    private fun setup() {

        dayViews.add(dayMonday)
        dayViews.add(dayTuesday)
        dayViews.add(dayWednesday)
        dayViews.add(dayThursday)
        dayViews.add(dayFriday)
        dayViews.add(daySaturday)
        dayViews.add(daySunday)

        dayViews.forEach {
            it.setOnClickListener { v -> onSelectDay(v) }
        }

        if (isSelectedDay) {
            val calendar = Calendar.getInstance()
            val currentDayInt = calendar.get(Calendar.DAY_OF_WEEK)
            val currentDay = DateFormatSymbols(Locale.ENGLISH).weekdays[currentDayInt]
            setSelectDay(currentDay)
            selectedDate = calendar.time
        }
    }


    private fun mapLotteryDayIntToDayInt(dayInt: Int): Int {
        var calendarDay = 1
        when(dayInt) {
            1 -> calendarDay = Calendar.SUNDAY
            2 -> calendarDay = Calendar.MONDAY
            3 -> calendarDay = Calendar.TUESDAY
            4 -> calendarDay = Calendar.WEDNESDAY
            5 -> calendarDay = Calendar.THURSDAY
            6 -> calendarDay = Calendar.FRIDAY
            7 -> calendarDay = Calendar.SATURDAY
        }
        return calendarDay
    }

    private fun getDateOfSelectedDay(dayInt: Int): Date {
        val nextSevenDays = DateUtil.getNextSevenDays()
        val calendar = Calendar.getInstance()
        var selectedDate = nextSevenDays.first()
        for (item in nextSevenDays) {
            calendar.time = item
            if (dayInt == calendar.get(Calendar.DAY_OF_WEEK)) {
                selectedDate = item
                break
            }
        }
        return selectedDate
    }

    private fun onSelectDay(view: View) {

        val dayString = ( view.tag as String).toUpperCase()
        var dayInt = 0

        when (dayString) {
            DateUtil.DaysOfWeek.MONDAY.rawValue.toUpperCase() -> dayInt = Calendar.MONDAY
            DateUtil.DaysOfWeek.TUESDAY.rawValue.toUpperCase() -> dayInt = Calendar.TUESDAY
            DateUtil.DaysOfWeek.WEDNESDAY.rawValue.toUpperCase() -> dayInt = Calendar.WEDNESDAY
            DateUtil.DaysOfWeek.THURSDAY.rawValue.toUpperCase() -> dayInt = Calendar.THURSDAY
            DateUtil.DaysOfWeek.FRIDAY.rawValue.toUpperCase() -> dayInt = Calendar.FRIDAY
            DateUtil.DaysOfWeek.SATURDAY.rawValue.toUpperCase() -> dayInt = Calendar.SATURDAY
            DateUtil.DaysOfWeek.SUNDAY.rawValue.toUpperCase() -> dayInt = Calendar.SUNDAY
        }

        val dayName = view.tag as String
        setSelectDay(dayName)
        selectedDate = getDateOfSelectedDay(dayInt)
        listener?.onSelectedDay(dayInt, dayName, selectedDate)
    }

    fun setSelectDay(day: String) {
        setBackgroundColorSelected(day)
    }

    fun getSelectedDate(): Date{
        return selectedDate
    }

    fun getDayFromNumber(day: Int): String {
        var stringDay = ""
        when (day) {
            Calendar.MONDAY -> stringDay = DateUtil.DaysOfWeek.MONDAY.rawValue
            Calendar.TUESDAY -> stringDay = DateUtil.DaysOfWeek.TUESDAY.rawValue
            Calendar.WEDNESDAY -> stringDay = DateUtil.DaysOfWeek.WEDNESDAY.rawValue
            Calendar.THURSDAY -> stringDay = DateUtil.DaysOfWeek.THURSDAY.rawValue
            Calendar.FRIDAY -> stringDay = DateUtil.DaysOfWeek.FRIDAY.rawValue
            Calendar.SATURDAY -> stringDay = DateUtil.DaysOfWeek.SATURDAY.rawValue
            Calendar.SUNDAY -> stringDay = DateUtil.DaysOfWeek.SUNDAY.rawValue
        }
        return stringDay
    }

    fun selectCustomDayAction(dayString: String) {

        var dayInt = 0

        when (dayString.toUpperCase()) {
            DateUtil.DaysOfWeek.MONDAY.rawValue.toUpperCase() -> dayInt = Calendar.MONDAY
            DateUtil.DaysOfWeek.TUESDAY.rawValue.toUpperCase() -> dayInt = Calendar.TUESDAY
            DateUtil.DaysOfWeek.WEDNESDAY.rawValue.toUpperCase() -> dayInt = Calendar.WEDNESDAY
            DateUtil.DaysOfWeek.THURSDAY.rawValue.toUpperCase() -> dayInt = Calendar.THURSDAY
            DateUtil.DaysOfWeek.FRIDAY.rawValue.toUpperCase() -> dayInt = Calendar.FRIDAY
            DateUtil.DaysOfWeek.SATURDAY.rawValue.toUpperCase() -> dayInt = Calendar.SATURDAY
            DateUtil.DaysOfWeek.SUNDAY.rawValue.toUpperCase() -> dayInt = Calendar.SUNDAY
        }

        setSelectDay(dayString)
        selectedDate = getDateOfSelectedDay(dayInt)
        listener?.onSelectedDay(dayInt, dayString, selectedDate)
    }

    private fun setBackgroundColorSelected(day: String) {

        var textColorInt: Int
        var backGroundColor: Int

        dayViews.map {

            if (it.tag == day) {
                textColorInt = R.color.colorWhite
                backGroundColor = R.color.colorRedApp
            }
            else {
                textColorInt = R.color.colorGrayDark1
                backGroundColor = R.color.colorGraySilver
            }

            (it as TextView).setTextColor(ContextCompat.getColor(context, textColorInt))
            DrawableCompat.setTint(it.background, ContextCompat.getColor(context, backGroundColor))
        }
    }


    fun shouldSelectCurrectDay(shouldSelectDay: Boolean) {
        isSelectedDay = shouldSelectDay
    }

    fun isSelectedDay() = isSelectedDay
}