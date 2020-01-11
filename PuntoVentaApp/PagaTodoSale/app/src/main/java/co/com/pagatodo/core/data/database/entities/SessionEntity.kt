package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import io.reactivex.Observable

@Entity
data class SessionEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "seller_code") val sellerCode: String? = null,
    @ColumnInfo(name = "seller_name") val sellerName: String? = null,
    @ColumnInfo(name = "token") val token: String? = null,
    @ColumnInfo(name = "session_id") val sessionId: String? = null,
    @ColumnInfo(name = "code_point_sale") val codePointSale: String? = null,
    @ColumnInfo(name = "municipality_code") val municipalityCode: String? = null,
    @ColumnInfo(name = "office_code") val officeCode: String? = null
)

@Dao
interface SessionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: SessionEntity)

    @Query("SELECT * FROM SessionEntity")
    fun getAll(): List<SessionEntity>

    @Query("SELECT * FROM SessionEntity")
    fun getAllAsync(): Observable<List<SessionEntity>>

    @Query("select * from SessionEntity where id=:id")
    fun getById(id: Int): SessionEntity

    @Query("delete from SessionEntity")
    fun deleteAll()
}