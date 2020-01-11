package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import io.reactivex.Observable

@Entity
class KeyValueParameterEntityRoom {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    @ColumnInfo var key: String? = null
    @ColumnInfo var value: String? = null
}

@Dao
abstract class KeyValueParameterEntityRoomDAO: BaseDAO<KeyValueParameterEntityRoom> {
    @Query("SELECT * FROM KeyValueParameterEntityRoom")
    abstract fun getAll(): Observable<List<KeyValueParameterEntityRoom>>

    @Query("select * from KeyValueParameterEntityRoom where `key` = :key")
    abstract fun getByKey(key: String): KeyValueParameterEntityRoom?

    @Insert
    abstract fun insertAll(items: List<KeyValueParameterEntityRoom>)

    @Query("delete from KeyValueParameterEntityRoom")
    abstract fun deleteAll()

    @Query("delete from KeyValueParameterEntityRoom where `key` = :key ")
    abstract fun deleteByKeyAll(key: String)
}