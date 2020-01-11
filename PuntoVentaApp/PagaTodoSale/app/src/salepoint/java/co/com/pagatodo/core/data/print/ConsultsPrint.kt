package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.print.POSPrinter
import co.com.pagatodo.core.util.resetValueFormat
import java.util.*

class ConsultsPrint : BasePrint(), IConsultsPrint {

    override fun printConsult(
        products: List<ProductBoxModel>,
        saleTotal: String,
        stubs: String,
        sellerName: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val stringBuilder = StringBuilder()
        var posPrinter = POSPrinter()
        var numberOfSaleItems = 0

        when (PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {

                val sellerCode = getPreference<String>(R_string(R.string.shared_key_seller_code))
                val calendar = Calendar.getInstance()
                val dateFull = DateUtil.convertDateToStringFormat(
                    DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
                    calendar.time
                )
                val timeFull =
                    DateUtil.convertDateToStringFormat(DateUtil.StringFormat.HHMMSS, calendar.time)

                stringBuilder.append("FECHA: $dateFull")
                stringBuilder.append("\nHORA: $timeFull")
                stringBuilder.append("\nCOD. VENDEDOR: $sellerCode")
                stringBuilder.append("\nVENDEDOR: ${getPreference<String>(R_string(R.string.shared_key_seller_name))}")
                stringBuilder.append("\nVOLUMEN: ${getDeviceId()}\n")

                val valuesList = createDataList(products)
                var total = 0



                valuesList.first.forEachIndexed { index, item ->
                    when (item){
                        "COLILLAS IMPRESAS", "COLILLAS ANULADAS" -> {
                            stringBuilder.append("\n${valuesList.first.get(index)} ${valuesList.second[index]}")
                            numberOfSaleItems++
                        }
                        else ->{
                            stringBuilder.append("\n${valuesList.first.get(index)} ${valuesList.second[index]}")
                            total += valuesList.second[index]?.replace("$","")?.replace(".","")?.toInt()
                            numberOfSaleItems++
                        }
                    }
                }
                stringBuilder.append(
                    "\n\n${R_string(R.string.print_saletotal_label_pdv)} $${formatValue(
                        total.toDouble()
                    )}"
                )
            }
        }

        val linePositionsToCenter = arrayOf(0, 1, 2, 3, 4, 7 + numberOfSaleItems)
        posPrinter.printWithAlignment(stringBuilder.toString(), linePositionsToCenter, callback)
    }

    private fun createDataList(products: List<ProductBoxModel>): Pair<List<String>, List<String>> {
        val valuesName = arrayListOf<String>()
        val valuesData = arrayListOf<String>()
        products.filter { it.saleValue?.resetValueFormat() != "0" }.forEach { item ->
            item.name?.let { valuesName.add(it) }
            item.saleValue?.let { valuesData.add(it) }
        }
        return Pair(valuesName, valuesData)
    }
}