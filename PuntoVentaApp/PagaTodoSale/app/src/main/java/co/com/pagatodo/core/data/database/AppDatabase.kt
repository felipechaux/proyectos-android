package co.com.pagatodo.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.com.pagatodo.core.data.database.entities.*

@Database(entities = [
    SessionEntity::class,
    StubEntity::class,
    LotteryEntityRoom::class,
    PayMillionaireEntityRoom::class,
    ProductLotteriesEntityRoom::class,
    OperatorEntityRoom::class,
    PackageEntityRoom::class,
    ModalityEntityRoom::class,
    PromotionalEntityRoom::class,
    ModalitiesValuesEntityRoom::class,
    RaffleEntityRoom::class,
    KeyValueParameterEntityRoom::class,
    ProductEntityRoom::class,
    ProductParameterEntityRoom::class,
    PrinterConfigurationEntity::class,
    PrinterTypeEntity::class,
    MenuEntityRoom::class],
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDAO
    abstract fun stubDao(): StubEntityRoomDAO
    abstract fun lotteryDao(): LotteryEntityRoomDAO
    abstract fun payMillionaireDao(): PayMillionaireEntityRoomDAO
    abstract fun productLotteriesDao(): ProductLotteriesEntityRoomDAO
    abstract fun operatorDao(): OperatorEntityRoomDAO
    abstract fun packageDao(): PackageEntityRoomDAO
    abstract fun modalityDao(): ModalityEntityRoomDAO
    abstract fun promotionalDao(): PromotionalEntityRoomDAO
    abstract fun raffleDao(): RaffleEntityRoomDAO
    abstract fun keyValueDao(): KeyValueParameterEntityRoomDAO
    abstract fun productDao(): ProductEntityRoomDAO
    abstract fun menuDao(): MenuEntityRoomDAO
    abstract fun printerConfigurationDao(): EpsonPrinterConfigurationDAO
    abstract fun printerTypeDao(): PrinterTypeDAO
}