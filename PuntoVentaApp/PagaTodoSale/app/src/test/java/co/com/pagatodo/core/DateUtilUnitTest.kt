package co.com.pagatodo.core

import co.com.pagatodo.core.util.DateUtil
import org.junit.Test
import java.util.*

class DateUtilUnitTest {

    @Test
    fun currentDateDDMMYYYYSuccess() {
        val currentDate = DateUtil.getCurrentDateDDMMYYYY()
        assert(currentDate.split("/").count() == 3)
    }

    @Test
    fun nextSevenDaysSuccess() {
        assert(DateUtil.getNextSevenDays().count() == 7)
    }

    @Test
    fun convertDateToStringFormatSuccess() {
        val convertedDateString = DateUtil.convertDateToStringFormat("dd/mm/YYYY", Calendar.getInstance().time)
        assert(convertedDateString.split("/").count() == 3)
    }
}