package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO
import co.com.pagatodo.core.exceptions.FailedTransactionException
import io.reactivex.Observable
import java.lang.Exception
import androidx.room.Dao

@Entity
class OperatorEntityRoom {
    @PrimaryKey var id: Long = 0
    @ColumnInfo var name: String? = null
    @ColumnInfo var operator: String? = null
    @ColumnInfo(name = "operator_name") var operatorName: String? = null
    @ColumnInfo(name = "operator_code") var operatorCode: String? = null
    @ColumnInfo(name = "operator_code_package") var operatorCodePackage: String? = null
    @Ignore var packages: List<PackageEntityRoom>? = null
    @ColumnInfo(name = "min_value") var minValue: Int? = null
    @ColumnInfo(name = "max_value") var maxValue: Int? = null
    @ColumnInfo(name = "min_digits") var minDigits: Int? = null
    @ColumnInfo(name = "max_digits") var maxDigits: Int? = null
    @ColumnInfo(name = "multiple_digits") var multipleDigits: Int? = null
}

@Entity
class OperatorsWithPackages {
    @Embedded
    var operator: OperatorEntityRoom? = null
    @Relation(parentColumn = "id", entityColumn = "operator_id", entity = PackageEntityRoom::class)
    var packages: List<PackageEntityRoom>? = null
}

@Dao
abstract class OperatorEntityRoomDAO: BaseDAO<OperatorEntityRoom> {
    @Query("select * from OperatorEntityRoom")
    abstract fun getAll(): Observable<List<OperatorEntityRoom>>

    @Query("select * from OperatorEntityRoom")
    abstract fun getAllWithPackages(): List<OperatorsWithPackages>?

    @Query("select * from OperatorEntityRoom")
    abstract fun getAllWithPackagesAsync(): Observable<List<OperatorsWithPackages>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<OperatorEntityRoom>)

    @Insert
    abstract fun insertAllPackages(items: List<PackageEntityRoom>)

    @Query("delete from OperatorEntityRoom")
    abstract fun deleteAll()

    @Query("select * from PackageEntityRoom")
    abstract fun getPackages(): List<PackageEntityRoom>

    @Transaction
    open fun insertWithPackages(items: List<OperatorEntityRoom>) {

        try {
            insertAll(items)
            items.forEach {
                val _package = it.packages
                if(_package?.isNotEmpty() == true) {
                    insertAllPackages(_package)
                }
            }
        }
        catch (e: Exception) {
            throw FailedTransactionException("Failed Transaction in ${OperatorEntityRoom::class.simpleName}")
        }
    }

    open fun getOperators(): List<OperatorEntityRoom> {
        var operatorsEntities = arrayListOf<OperatorEntityRoom>()
        val response = getAllWithPackages()
        response?.let { res ->
            res.forEach {
                val packages = it.packages
                it.operator?.let {
                    var operator = it
                    operator?.packages = packages
                    operatorsEntities.add(operator)
                }
            }
        }
        return operatorsEntities
    }
}