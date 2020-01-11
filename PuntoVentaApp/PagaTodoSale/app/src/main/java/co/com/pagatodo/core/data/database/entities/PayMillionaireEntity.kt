package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable

@Entity
data class PayMillionaireEntityRoom(
    @androidx.room.PrimaryKey val code: String,
    @ColumnInfo val productCode: String?,
    @ColumnInfo val name: String?,
    @ColumnInfo val value: String?,
    @ColumnInfo val accumulated: String?,
    @ColumnInfo(name = "number_of_digits") val numberOfDigits: String?
)

@Dao
abstract class PayMillionaireEntityRoomDAO: BaseDAO<PayMillionaireEntityRoom> {
    @Query("SELECT * FROM PayMillionaireEntityRoom")
    abstract fun getAll(): Observable<List<PayMillionaireEntityRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<PayMillionaireEntityRoom>)

    @Query("delete from PayMillionaireEntityRoom")
    abstract fun deleteAll()
}