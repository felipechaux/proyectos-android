package co.com.pagatodo.core.data.print.chance

import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil


class ChanceProductsPrint : IChanceProductsPrint {


    override fun printSuperChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> ChancePruductsPrintQ2().printSuperChance(printModel, games, isRetry, callback)
            DatafonoType.NEW9220.type -> ChanceProductsPrintNEW9220().printSuperChance(printModel, games, isRetry, callback)
        }

    }


    override fun printMaxiChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> ChancePruductsPrintQ2().printMaxiChance(printModel,games,isRetry,callback)
            DatafonoType.NEW9220.type -> ChanceProductsPrintNEW9220().printMaxiChance(printModel,games,isRetry,callback)
        }

    }



    override fun printPaymillionaire(
        totalValueBetting: Double,
        totalValuePaid: Double,
        totalValueIva: Double,
        raffleDate: String,
        lotteries: List<LotteryModel>,
        chances: List<ChanceModel>,
        iva: Int,
        sellerName: String,
        uniqueSerial: String,
        digitChecking: String,
        prizePlan: List<String>,
        selectedModeValue: ModeValueModel?,
        stubs: String,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> ChancePruductsPrintQ2().printPaymillionaire(totalValueBetting ,totalValuePaid ,totalValueIva ,raffleDate ,lotteries ,chances,iva,sellerName ,uniqueSerial ,digitChecking ,prizePlan,selectedModeValue,stubs ,isRetry ,callback)
            DatafonoType.NEW9220.type -> ChanceProductsPrintNEW9220().printPaymillionaire(totalValueBetting ,totalValuePaid ,totalValueIva ,raffleDate ,lotteries ,chances,iva,sellerName ,uniqueSerial ,digitChecking ,prizePlan,selectedModeValue,stubs ,isRetry ,callback)
        }

    }



    override fun printChance(
        totalValueBetting: Double,
        totalValuePaid: Double,
        totalValueOverloaded: Double,
        totalValueIva: Double,
        raffleDate: String,
        raffleHour: String,
        lotteries: List<LotteryModel>,
        chances: List<ChanceModel>,
        iva: Int,
        sellerName: String,
        uniqueSerial: String,
        digitChecking: String,
        stubs: String,
        maxRows: Int,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> ChancePruductsPrintQ2().printChance(totalValueBetting,totalValuePaid,totalValueOverloaded,totalValueIva,raffleDate,raffleHour,lotteries ,chances ,iva,sellerName ,uniqueSerial ,digitChecking ,stubs,maxRows ,isRetry ,callback)
            DatafonoType.NEW9220.type -> ChanceProductsPrintNEW9220().printChance(totalValueBetting,totalValuePaid,totalValueOverloaded,totalValueIva,raffleDate,raffleHour,lotteries ,chances ,iva,sellerName ,uniqueSerial ,digitChecking ,stubs,maxRows ,isRetry ,callback)
        }
    }


}