package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable

//@Entity(primaryKeys = ["product_code", "lottery_code"])
@Entity(primaryKeys = ["product_code", "lottery_code"],
    foreignKeys = [ForeignKey(
        entity = ProductEntityRoom::class,
        parentColumns = ["product_code"],
        childColumns = ["product_code"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class ProductLotteriesEntityRoom(
    @ColumnInfo(name = "product_code") val productCode: String,
    @ColumnInfo(name = "lottery_code") val lotteryCode: String,
    @ColumnInfo(name = "full_name") val fullName: String? = null,
    @ColumnInfo(name = "short_name") val shortName: String? = null,
    @ColumnInfo(name = "date") val date: String? = null,
    @ColumnInfo(name = "hour") val hour: String? = null,
    @ColumnInfo(name = "fractions") val fractions: String? = null,
    @ColumnInfo(name = "fraction_value") val fractionValue: String? = null,
    @ColumnInfo(name = "draw") val draw: String? = null,
    @ColumnInfo(name = "award") val award: String? = null
)

@Dao
abstract class ProductLotteriesEntityRoomDAO: BaseDAO<ProductLotteriesEntityRoom> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(users: List<ProductLotteriesEntityRoom>)

    @Query("SELECT * FROM ProductLotteriesEntityRoom")
    abstract fun getAll(): List<ProductLotteriesEntityRoom>

    @Query("select * from ProductLotteriesEntityRoom where product_code = :productCode")
    abstract fun getAllByProductCode(productCode: String): List<ProductLotteriesEntityRoom>

    @Query("SELECT * FROM ProductLotteriesEntityRoom")
    abstract fun getAllAsync(): Observable<List<ProductLotteriesEntityRoom>>

    @Query("delete from ProductLotteriesEntityRoom")
    abstract fun deleteAll()
}