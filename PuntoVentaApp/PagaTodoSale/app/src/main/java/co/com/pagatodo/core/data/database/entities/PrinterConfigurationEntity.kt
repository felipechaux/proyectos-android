package co.com.pagatodo.core.data.database.entities

import androidx.room.*
import co.com.pagatodo.core.data.database.dao.BaseDAO

@Entity
class PrinterConfigurationEntity {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "printer_code") var printerCode: String? = null
    @ColumnInfo(name = "printer_type") var printerType: String? = null
    @ColumnInfo(name = "path_connection") var pathConnection: String? = null
}

@Entity
data class PrinterTypeEntity(
    @PrimaryKey var id: String = "01",
    @ColumnInfo var name: String = ""
)

@Dao
abstract class PrinterTypeDAO: BaseDAO<PrinterTypeEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrReplace(items: List<PrinterTypeEntity>)

    @Query("select * from PrinterTypeEntity")
    abstract fun getAll(): List<PrinterTypeEntity>
}

@Dao
abstract class EpsonPrinterConfigurationDAO: BaseDAO<PrinterConfigurationEntity> {
    @Query("select * from PrinterConfigurationEntity where printer_type=:printerType")
    abstract fun getPrinterConfiguration(printerType: String): PrinterConfigurationEntity
}