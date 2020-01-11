package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import io.reactivex.Observable

@Entity
data class StubEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "serie1") val serie1: String? = null,
    @ColumnInfo(name = "serie2") val serie2: String? = null,
    @ColumnInfo(name = "unique_serie") val uniqueSerie: String? = null,
    @ColumnInfo(name = "in_use") val inUse: Boolean = false,
    @ColumnInfo(name = "entity_code") val entityCode: String? = null,
    @ColumnInfo(name = "final_rank") val finalRank: String? = null,
    @ColumnInfo(name = "chance_type") val chanceType: String? = null
)

@Dao
interface StubEntityRoomDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg user: StubEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<StubEntity>)

    @Query("SELECT * FROM StubEntity")
    fun getAllAsync(): Observable<List<StubEntity>>

    @Query("SELECT * FROM StubEntity")
    fun getAll(): List<StubEntity>

    @Query("delete from StubEntity")
    fun deleteAll()
}
