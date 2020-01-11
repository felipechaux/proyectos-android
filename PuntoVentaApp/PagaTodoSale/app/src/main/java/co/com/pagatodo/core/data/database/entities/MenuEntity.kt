package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable
import java.util.*

@Entity
data class MenuEntityRoom (
    @androidx.room.PrimaryKey val code: String,
    @ColumnInfo val name: String? = null,
    @ColumnInfo val label: String? = null,
    @ColumnInfo val description: String? = null,
    @ColumnInfo val status: String? = null,
    @ColumnInfo(name = "product_code") val productCode: String? = null
)

@Dao
abstract class MenuEntityRoomDAO: BaseDAO<MenuEntityRoom> {
    @Query("SELECT * FROM MenuEntityRoom")
    abstract fun getAll(): Observable<List<MenuEntityRoom>>

    @Query("select * from MenuEntityRoom where product_code like (:productCode)")
    abstract fun getAllByProductCode(productCode: String): List<MenuEntityRoom>

    @Insert
    abstract fun insertAll(items: List<MenuEntityRoom>)

    @Query("delete from MenuEntityRoom")
    abstract fun deleteAll()
}