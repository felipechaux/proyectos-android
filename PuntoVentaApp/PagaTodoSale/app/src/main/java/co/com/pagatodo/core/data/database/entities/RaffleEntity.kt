package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable

@Entity
data class RaffleEntityRoom(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "raffle_id") val raffleId: Int,
    @ColumnInfo(name = "raffle_name") val raffleName: String? = null,
    @ColumnInfo(name = "description_raffle") val descriptionRaffle: String? = null,
    @ColumnInfo(name = "lotteryName") val lotteryName: String? = null,
    @ColumnInfo(name = "date_draw") val dateDraw: String? = null,
    @ColumnInfo val price: Int? = null,
    @ColumnInfo(name = "count_number_figures") val countNumberFigures: Int? = null,
    @ColumnInfo(name = "price_prize") val pricePrize: String? = null,
    @ColumnInfo(name = "draw_time") val drawTime: String? = null,
    @ColumnInfo val logo: Int? = null,
    @ColumnInfo(name = "message_raffle") val messageRaffle: String? = null
)

@Dao
abstract class RaffleEntityRoomDAO: BaseDAO<RaffleEntityRoom> {
    @Query("SELECT * FROM RaffleEntityRoom")
    abstract fun getAll(): List<RaffleEntityRoom>

    @Query("SELECT * FROM RaffleEntityRoom")
    abstract fun getAllAsync(): Observable<List<RaffleEntityRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<RaffleEntityRoom>)

    @Query("delete from RaffleEntityRoom")
    abstract fun deleteAll()
}