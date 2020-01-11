package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable

@Entity(primaryKeys = ["id", "operator_id"],
    foreignKeys = [ForeignKey(
        entity = OperatorEntityRoom::class,
        parentColumns = ["id"],
        childColumns = ["operator_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class PackageEntityRoom(
    @ColumnInfo val id: Long,
    @ColumnInfo(name = "operator_id") val operatorId: Long,
    @ColumnInfo(name = "package_code") val packageCode: String? = null,
    @ColumnInfo(name = "package_name") val packageName: String? = null,
    @ColumnInfo(name = "package_value") val packageValue: Float? = null
)

@Dao
abstract class PackageEntityRoomDAO: BaseDAO<PackageEntityRoom> {
    @Query("SELECT * FROM PackageEntityRoom")
    abstract fun getAll(): Observable<List<PackageEntityRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<PackageEntityRoom>)

    @Query("delete from PackageEntityRoom")
    abstract fun deleteAll()
}