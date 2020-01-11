package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable

@Entity(primaryKeys = ["code", "product_code"],
    foreignKeys = [ForeignKey(
        entity = ProductEntityRoom::class,
        parentColumns = ["product_code"],
        childColumns = ["product_code"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class ModalityEntityRoom(
    @ColumnInfo val code: String,
    @ColumnInfo(name = "product_code") val productCode: String,
    @ColumnInfo val description: String? = null
)

@Dao
abstract class ModalityEntityRoomDAO: BaseDAO<ModalityEntityRoom> {
    @Query("SELECT * FROM ModalityEntityRoom")
    abstract fun getAllAsync(): Observable<List<ModalityEntityRoom>>

    @Query("SELECT * FROM ModalityEntityRoom")
    abstract fun getAll(): Observable<List<ModalityEntityRoom>>

    @Query("select * from ModalityEntityRoom where product_code = :productCode")
    abstract fun getAllByProductCode(productCode: String): List<ModalityEntityRoom>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<ModalityEntityRoom>)

    @Query("delete from ModalityEntityRoom")
    abstract fun deleteAll()
}