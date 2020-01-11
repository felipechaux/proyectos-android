package co.com.pagatodo.core.util

import android.util.Log
import java.lang.Exception
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    enum class DaysOfWeek(val rawValue: String) {
        MONDAY("Monday"),
        TUESDAY("Tuesday"),
        WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"),
        FRIDAY("Friday"),
        SATURDAY("Saturday"),
        SUNDAY("Sunday")
    }

    enum class StringFormat(val rawValue: String) {
        DDDYYYY("DDDyyyy"),
        DDMMYY("ddMMyyyy"),
        DDMM_SPLIT_BACKSLASH("dd/MM"),
        DDMMYY_SPLIT_BACKSLASH("dd/MM/yyyy"),
        YYYYMMDD_SPLIT_DASH("yyyy-MM-dd"),
        DDMMYYYY_SPLIT_DASH("dd-MM-yyyy"),
        YYYYHOUR_SPLIT_WHITE_SPACE("yyyy  HH:mm:ss"),
        DDMMYYHOUR_SPLIT_WHITE_SPACE("ddMMyyyy  HH:mm:ss"),
        DDMMYYHOURM_SPLIT_WHITE_SPACE("ddMMyyyy  HH:mm:ss.SS"),
        HHMMSS("HH:mm:ss"),
        HHMMA("hh:mm a"),
        DMMMYY("dMMMyy"),
        DMMMYYHOUR_SPLIT_WHITE_SPACE("MMM d, yyyy  HH:mm:ss"),
        E_DD_MM_YY("E dd MMM yy"),
        EEE_DD_MM_YY("EEE dd MMM yy"),
        EEE_DD_MM_YYYY("EEEE dd 'DE' MMM 'DE' yyyy"),
        MMM_DD_YY("MMM dd, yyyy"),
        EEE_DD_MM_YY_FULL("EEEE dd MMMM yyyy"),
        EEE_DD_MM_YY_SPLIT("E dd/MM/yy"),
        DDMMYY_HHMMA_SPLIT_BACKSLASH("dd/MM/yyyy hh:mm a"),
        DDMMYY_HHMMA_SPLIT_DASH("dd-MM-yyyy hh:mm a"),
        DDMMYY_HHMMSS_SPLIT_DASH("dd-MM-yyyy HH:mm:ss"),
        EEE_DD_MMM_YY_HHMMSS_SPLIT_WHITE_SPACE("EEE dd MMM yy HH:mm:ss")
    }

    companion object {
        fun getCurrentDate() = Calendar.getInstance().time

        fun convertMillisecondsToDate(milliseconds: Long, stringFormat: String): String {
            val simpleDateFormat = SimpleDateFormat(stringFormat, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliseconds
            return simpleDateFormat.format(calendar.time)
        }

        fun getCurrentDateDDMMYYYY(): String {
            val simpleDateFormat = SimpleDateFormat(StringFormat.DDMMYY_SPLIT_BACKSLASH.rawValue, Locale.getDefault())
            return simpleDateFormat.format(getCurrentDate())
        }

        fun getNextSevenDays(): List<Date> {
            val calendar = Calendar.getInstance()
            val nextSevenDays = arrayListOf<Date>()
            nextSevenDays.add(calendar.time)
            for (i in 1..6) {
                calendar.add(Calendar.DATE, 1)
                nextSevenDays.add(calendar.time)
            }
            return nextSevenDays
        }

        fun addBackslashToStringDate(stringDate: String): String {
            var newStringDate = ""
            if(stringDate.length == 8) {
                val stringBuilder = StringBuilder(stringDate)
                stringBuilder.insert(2, "/")
                stringBuilder.insert(5, "/")
                newStringDate = stringBuilder.toString()
            }
            return newStringDate
        }

        fun convertDateToStringFormat(stringFormat: String, date: Date): String {
            val simpleDateFormat = SimpleDateFormat(stringFormat, Locale.getDefault())
            return simpleDateFormat.format(date)
        }

        fun convertDateToStringFormat(format: StringFormat, date: Date): String {
            val simpleDateFormat = SimpleDateFormat(format.rawValue, Locale.getDefault())
            return simpleDateFormat.format(date)
        }

        fun convertStringToDate(format: StringFormat, stringDate: String): String {
            val sourceFormat = SimpleDateFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH.rawValue, Locale.getDefault())
            val dateClose = sourceFormat.parse(stringDate)
            return convertDateToStringFormat(format, dateClose).toUpperCase()
        }

        fun convertStringToDateFormat(format: StringFormat, stringDate: String):Date {
            val sourceFormat = SimpleDateFormat(format.rawValue, Locale.getDefault())
            val dateClose = sourceFormat.parse(stringDate)
            return  dateClose
        }

        fun convertMilitaryHourToNormal(format: StringFormat, stringHour: String): String {
            var dateToConvert = stringHour.trim()
            if (stringHour.split(":").count() == 2){
                dateToConvert = dateToConvert + ":00"
            }
            val sourceFormat = SimpleDateFormat(DateUtil.StringFormat.HHMMSS.rawValue, Locale.getDefault())
            val dateClose = sourceFormat.parse(dateToConvert)
            return convertDateToStringFormat(format, dateClose).toLowerCase()
        }

        fun getCurrentDateInUnix(): Long {
            return System.currentTimeMillis() / 1000L
        }

        fun getCurrentAbrNameOfMonth(): String {
            //return getSpanishNameOfMonth(Calendar.getInstance().get(Calendar.MONTH))
            val numberMonth = SimpleDateFormat("MM").format(Calendar.getInstance().time).toInt()
            return getSpanishNameOfMonth(numberMonth)
        }

        fun getCurrentAbrNameOfMonth(date:Date): String {
            //return getSpanishNameOfMonth(Calendar.getInstance().get(Calendar.MONTH))
            val numberMonth = SimpleDateFormat("MM").format(date).toInt()
            return getSpanishNameOfMonth(numberMonth)
        }

        fun getCurrentDay(): Int{
            return Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        }

        fun getCurrentDay(date: Date): Int{

            return Calendar.getInstance().apply { time = date }.get(Calendar.DAY_OF_MONTH)
        }

        fun getSpanishNameOfMonth(month: Int): String {
            return when(month) {
                1 -> "ENE"
                2 -> "FEB"
                3 -> "MAR"
                4 -> "ABR"
                5 -> "MAY"
                6 -> "JUN"
                7 -> "JUL"
                8 -> "AGO"
                9 -> "SEP"
                10 -> "OCT"
                11 -> "NOV"
                else -> {
                    "DIC"
                }
            }
        }

        fun firstHourIsLessThanSecondHour(firstHour: String, secondHour: String): Boolean {

            val firstSplit = firstHour.split(":")
            val secondSplit = secondHour.split(":")

            val cal1 = Calendar.getInstance()
            cal1.set(Calendar.HOUR_OF_DAY, firstSplit[0].toInt())
            cal1.set(Calendar.MINUTE, firstSplit[1].toInt())
            cal1.set(Calendar.SECOND, firstSplit[2].toInt())

            val cal2 = Calendar.getInstance()
            cal2.set(Calendar.HOUR_OF_DAY, secondSplit[0].toInt())
            cal2.set(Calendar.MINUTE, secondSplit[1].toInt())
            cal2.set(Calendar.SECOND, secondSplit[2].toInt())
            return cal1.before(cal2)
        }
    }
}