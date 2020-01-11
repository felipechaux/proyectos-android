package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import co.com.pagatodo.core.exceptions.FailedTransactionException
import io.reactivex.Observable
import java.lang.Exception

@Entity
data class ProductEntityRoom(
    @PrimaryKey
    @ColumnInfo(name = "product_code") var productCode: String,
    @ColumnInfo(name = "product_name") var productName: String? = null,
    @Ignore var productParameters: List<ProductParameterEntityRoom>? = null
) {
    constructor(): this("", null, null)
}

@Entity(primaryKeys = ["id", "product_code"],
    foreignKeys = [ForeignKey(
        entity = ProductEntityRoom::class,
        parentColumns = ["product_code"],
        childColumns = ["product_code"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class ProductParameterEntityRoom(
    var id: String,
    @ColumnInfo(name = "product_code") var productCode: String,
    @ColumnInfo var key: String? = null,
    @ColumnInfo var value: String? = null
)

@Entity
class ProductEntityFullRoom {
    @Embedded
    var product: ProductEntityRoom? = null
    @Relation(parentColumn = "product_code", entityColumn = "product_code", entity = ProductParameterEntityRoom::class)
    var productParameters: List<ProductParameterEntityRoom>? = null
}

@Dao
abstract class ProductEntityRoomDAO: BaseDAO<ProductEntityRoom> {
    @Query("SELECT * FROM ProductEntityRoom")
    abstract fun getAll(): List<ProductEntityRoom>

    @Query("select * from ProductEntityRoom where product_code=:productCode")
    abstract fun getAllByCode(productCode: String): ProductEntityFullRoom

    @Query("SELECT * FROM ProductEntityRoom")
    abstract fun getAllAsync(): Observable<List<ProductEntityRoom>>

    @Query("select * from ProductEntityRoom where product_code = :productCode")
    abstract fun getProductByCode(productCode: String): ProductEntityRoom

    @Query("select * from ProductEntityRoom where product_code = :productCode")
    abstract fun getProductByCodeAsync(productCode: String): Observable<ProductEntityRoom>

    @Query("SELECT * FROM ProductEntityRoom")
    abstract fun getAllWithRelationship(): Observable<List<ProductEntityFullRoom>>

    @Query("SELECT * FROM ProductEntityRoom where product_code = :productCode")
    abstract fun getWithRelationship(productCode: String): Observable<ProductEntityFullRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<ProductEntityRoom>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAllProductParameterEntityRoom(items: List<ProductParameterEntityRoom>)

    @Query("delete from ProductEntityRoom")
    abstract fun deleteAll()

    @Query("select * from ProductEntityRoom p join ProductParameterEntityRoom pp on p.product_code=pp.product_code where p.product_code=:code")
    abstract fun getProductEntity(code: String): ProductEntityFullRoom?

    @Query("select * from ProductEntityFullRoom")

    @Transaction
    open fun saveProduct(items: List<ProductEntityRoom>) {
        try {
            insertAll(items)
            items.forEach {
                it.productParameters?.let { param ->
                    insertAllProductParameterEntityRoom(param)
                }
            }
        }
        catch (e: Exception) {
            throw FailedTransactionException("Failed Transaction: ${e.message} [${ProductEntityRoom::class.simpleName}]")
        }
    }

    open fun getProductInfoAsync(code: String): Observable<ProductEntityRoom> {
        val response = getProductEntity(code)
        val productEntity = response?.product
        productEntity?.productParameters = response?.productParameters

        productEntity?.let {
            return Observable.create {
                it.onNext(productEntity)
                it.onComplete()
            }
        } ?: run {
            return Observable.empty()
        }
    }

    open fun getProductInfo(code: String): ProductEntityRoom? {
        val response = getProductEntity(code)
        val productEntity = response?.product
        productEntity?.productParameters = response?.productParameters
        return productEntity
    }
}