package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable

@Entity
data class LotteryEntityRoom(
    @androidx.room.PrimaryKey val code: String,
    @ColumnInfo val name: String? = null,
    @ColumnInfo(name = "full_name") val fullName: String? = null,
    @ColumnInfo val date: String? = null,
    @ColumnInfo val hour: String? = null,
    @ColumnInfo(name = "lottery_day") val lotteryDay: String? = null,
    @ColumnInfo(name = "number_figures") val numberFigures: Int,
    @ColumnInfo(name = "serie_figure") val serieFigure: Int
)

@Dao
abstract class LotteryEntityRoomDAO: BaseDAO<LotteryEntityRoom> {
    @Query("SELECT * FROM LotteryEntityRoom")
    abstract fun getAll(): List<LotteryEntityRoom>

    @Query("select * from LotteryEntityRoom where code in (:ids)")
    abstract fun getAllByIds(ids: ArrayList<String>): List<LotteryEntityRoom>

    @Insert
    abstract fun insertAll(items: List<LotteryEntityRoom>)

    @Query("delete from LotteryEntityRoom")
    abstract fun deleteAll()
}