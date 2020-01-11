package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import co.com.pagatodo.core.exceptions.FailedTransactionException
import io.reactivex.Observable
import java.lang.Exception

@Entity
class PromotionalEntityRoom {
    @PrimaryKey var id: Int = 0
    @ColumnInfo(name = "betting_amount") var bettingAmount: Int? = null
    @ColumnInfo(name = "quantity_figures") var quantityFigures: String? = null
    @ColumnInfo(name = "lotteries_quantity") var lotteriesQuantity: Int? = null
    @ColumnInfo(name = "is_generate_random") var isGenerateRandom: Boolean = false
    @ColumnInfo var name: String? = null
    @ColumnInfo(name = "open_value") var openValue: Boolean = false
    @Ignore var modalitiesValues: List<ModalitiesValuesEntityRoom>? = null
}

//@Entity(primaryKeys = ["id", "promotional_id"])
@Entity(primaryKeys = ["id", "promotional_id"],
    foreignKeys = [ForeignKey(
        entity = PromotionalEntityRoom::class,
        parentColumns = ["id"],
        childColumns = ["promotional_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class ModalitiesValuesEntityRoom(
    @ColumnInfo val id: Long,
    @ColumnInfo(name = "promotional_id") val promotionalId: Long,
    @ColumnInfo(name = "value") val values: String
)

@Entity
class PromotionalWithModalitiesValues {
    @Embedded
    var promotional: PromotionalEntityRoom? = null
    @Relation(parentColumn = "id", entityColumn = "promotional_id", entity = ModalitiesValuesEntityRoom::class)
    var modalitiesValues: List<ModalitiesValuesEntityRoom>? = null
}

@Dao
abstract class PromotionalEntityRoomDAO: BaseDAO<PromotionalEntityRoom> {
    @Query("SELECT * FROM PromotionalEntityRoom")
    abstract fun getAll(): Observable<List<PromotionalWithModalitiesValues>>

    @Query("SELECT * FROM PromotionalEntityRoom where name=:name")
    abstract fun getByName(name: String): Observable<PromotionalWithModalitiesValues>

    @Query("select * from PromotionalEntityRoom p join ModalitiesValuesEntityRoom m on p.id = m.promotional_id where p.name = :name")
    abstract fun getPromotionalByName(name: String): Observable<PromotionalWithModalitiesValues>

    @Query("SELECT * FROM PromotionalEntityRoom p join ModalitiesValuesEntityRoom m on p.id = m.promotional_id")
    abstract fun getAllWithModalitiesValues(): Observable<List<PromotionalWithModalitiesValues>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<PromotionalEntityRoom>)

    @Insert
    abstract fun insertAllModalities(items: List<ModalitiesValuesEntityRoom>)

    @Query("delete from PromotionalEntityRoom")
    abstract fun deleteAll()

    @Transaction
    open fun insertPromotionals(items: List<PromotionalEntityRoom>) {
        try{
            if (items.isNotEmpty()) {
                insertAll(items)
                items?.let {promotionals ->
                    promotionals?.forEach {
                        it?.modalitiesValues?.let { modalitiesValues ->
                            insertAllModalities(modalitiesValues)
                        }
                    }
                }
            }
        }
        catch (e: Exception) {
            throw FailedTransactionException("Failed Transaction in ${PromotionalEntityRoom::class.simpleName}")
        }
    }
}