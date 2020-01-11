package co.com.pagatodo.core.util.print

import android.graphics.Bitmap

import java.io.ByteArrayOutputStream

//Paquete de instrucciones comunes
object ESCUtil {

    val ESC: Byte = 0x1B// Escape
    val FS: Byte = 0x1C// Text delimiter
    val GS: Byte = 0x1D// Group separator
    val DLE: Byte = 0x10// data link escape
    val EOT: Byte = 0x04// End of transmission
    val ENQ: Byte = 0x05// Enquiry character
    val SP: Byte = 0x20// Spaces
    val HT: Byte = 0x09// Horizontal list
    val LF: Byte = 0x0A//Print and wrap (horizontal orientation)
    val CR: Byte = 0x0D// Home key
    val FF: Byte = 0x0C// Carriage control (print and return to the standard mode (in page mode))
    val CAN: Byte = 0x18// Canceled (cancel print data in page mode)

    //Inicializar la impresora
    fun init_printer(): ByteArray {
        val result = ByteArray(2)
        result[0] = ESC
        result[1] = 0x40
        return result
    }

    //Comando de densidad de impresión
    fun setPrinterDarkness(value: Int): ByteArray {
        val result = ByteArray(9)
        result[0] = GS
        result[1] = 40
        result[2] = 69
        result[3] = 4
        result[4] = 0
        result[5] = 5
        result[6] = 5
        result[7] = (value shr 8).toByte()
        result[8] = value.toByte()
        return result
    }

    /**
     * Imprimir un solo código QR comando personalizado sunmi
     * * @param code: datos del código QR
     * * @param modulesize: tamaño de bloque de código QR (unidad: punto, valor 1 a 16)
     * * @param errorlevel: nivel de corrección de errores de código QR (0 a 3)
     * * 0 - Nivel de corrección de errores L (7%)
     * * 1 - Nivel de corrección de errores M (15%)
     * * 2 - Nivel de corrección de errores Q (25%)
     * * 3 - Nivel de corrección de errores H (30%)
     */
    fun getPrintQRCode(code: String, modulesize: Int, errorlevel: Int): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code))
            buffer.write(getBytesForPrintQRCode(true))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return buffer.toByteArray()
    }

    /**
     * Código bidimensional horizontal de instrucciones personalizadas de sunmi.
     * * @param code1: datos del código QR
     * * @param code2: datos del código QR
     * * @param modulesize: tamaño de bloque de código QR (unidad: punto, valor 1 a 16)
     * * @param errorlevel: nivel de corrección de errores de código QR (0 a 3)
     * * 0 - Nivel de corrección de errores L (7%)
     * * 1 - Nivel de corrección de errores M (15%)
     * * 2 - Nivel de corrección de errores Q (25%)
     * * 3 - Nivel de corrección de errores H (30%)
     */
    fun getPrintDoubleQRCode(code1: String, code2: String, modulesize: Int, errorlevel: Int): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code1))
            buffer.write(getBytesForPrintQRCode(false))
            buffer.write(getQCodeBytes(code2))

            //Añadir espacio lateral
            buffer.write(byteArrayOf(0x1B, 0x5C, 0x18, 0x00))

            buffer.write(getBytesForPrintQRCode(true))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return buffer.toByteArray()
    }

    /**
     * Raster imprimir código QR
     */
    fun getPrintQRCode2(data: String, size: Int): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00

        val bytes2 = BytesUtil.getZXingQRCode(data, size)
        return BytesUtil.byteMerger(bytes1, bytes2!!)
    }

    /**
     * Imprimir código de barras unidimensional
     */
    fun getPrintBarCode(data: String, symbology: Int, height: Int, width: Int, textposition: Int): ByteArray {
        var height = height
        var width = width
        var textposition = textposition
        if (symbology < 0 || symbology > 10) {
            return byteArrayOf(LF)
        }

        if (width < 2 || width > 6) {
            width = 2
        }

        if (textposition < 0 || textposition > 3) {
            textposition = 0
        }

        if (height < 1 || height > 255) {
            height = 162
        }

        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(
                byteArrayOf(
                    0x1D,
                    0x66,
                    0x01,
                    0x1D,
                    0x48,
                    textposition.toByte(),
                    0x1D,
                    0x77,
                    width.toByte(),
                    0x1D,
                    0x68,
                    height.toByte(),
                    0x0A
                )
            )

            val barcode: ByteArray?
            if (symbology == 10) {
                barcode = BytesUtil.getBytesFromDecString(data)
            } else {
                barcode = data.toByteArray(charset("GB18030"))
            }

            if (symbology > 7) {
                buffer.write(
                    byteArrayOf(
                        0x1D,
                        0x6B,
                        0x49,
                        (barcode!!.size + 2).toByte(),
                        0x7B,
                        (0x41 + symbology - 8).toByte()
                    )
                )
            } else {
                buffer.write(byteArrayOf(0x1D, 0x6B, (symbology + 0x41).toByte(), barcode!!.size.toByte()))
            }
            buffer.write(barcode)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return buffer.toByteArray()
    }


    //Impresión de mapa de bits raster
    fun printBitmap(bitmap: Bitmap): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00

        val bytes2 = BytesUtil.getBytesFromBitMap(bitmap)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    //Modo de conjunto de impresión de mapa de bits ráster
    fun printBitmap(bitmap: Bitmap, mode: Int): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = mode.toByte()

        val bytes2 = BytesUtil.getBytesFromBitMap(bitmap)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    //Impresión de mapa de bits raster
    fun printBitmap(bytes: ByteArray): ByteArray {
        val bytes1 = ByteArray(4)
        bytes1[0] = GS
        bytes1[1] = 0x76
        bytes1[2] = 0x30
        bytes1[3] = 0x00

        return BytesUtil.byteMerger(bytes1, bytes)
    }


    /*
	*	Seleccione el modo de conjunto de instrucciones de mapa de bits
	 * Es necesario configurar 1B 33 00 para establecer el interlineado en 0
	 */
    fun selectBitmap(bitmap: Bitmap, mode: Int): ByteArray {
        return BytesUtil.byteMerger(
            BytesUtil.byteMerger(
                byteArrayOf(0x1B, 0x33, 0x00),
                BytesUtil.getBytesFromBitMap(bitmap, mode)
            ), byteArrayOf(0x0A, 0x1B, 0x32)
        )
    }

    /**
     * Salta el número especificado de líneas.
     */
    fun nextLine(lineNum: Int): ByteArray {
        val result = ByteArray(lineNum)
        for (i in 0 until lineNum) {
            result[i] = LF
        }

        return result
    }

    // ------------------------underline-----------------------------
    //Establecer subrayado 1 punto
    fun underlineWithOneDotWidthOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 1
        return result
    }

    //Establecer subrayado 2 puntos
    fun underlineWithTwoDotWidthOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 2
        return result
    }

    //Cancelar subrayado
    fun underlineOff(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 45
        result[2] = 0
        return result
    }

    // ------------------------bold-----------------------------
    /**
     * Fuente en negrita
     */
    fun boldOn(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 69
        result[2] = 0xF
        return result
    }

    /**
     * Cancelar fuente negrita
     */
    fun boldOff(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 69
        result[2] = 0
        return result
    }

    // ------------------------character-----------------------------
    /*
	*Modo de un solo byte activado
	 */
    fun singleByte(): ByteArray {
        val result = ByteArray(2)
        result[0] = FS
        result[1] = 0x2E
        return result
    }

    /*
	*Modo byte único desactivado
 	*/
    fun singleByteOff(): ByteArray {
        val result = ByteArray(2)
        result[0] = FS
        result[1] = 0x26
        return result
    }

    /**
     * Establecer un conjunto de caracteres de un solo byte
     */
    fun setCodeSystemSingle(charset: Byte): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 0x74
        result[2] = charset
        return result
    }

    /**
     * Establecer el conjunto de caracteres multibyte
     */
    fun setCodeSystem(charset: Byte): ByteArray {
        val result = ByteArray(3)
        result[0] = FS
        result[1] = 0x43
        result[2] = charset
        return result
    }

    // ------------------------Align-----------------------------

    /**
     * Izquierda
     */
    fun alignLeft(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 0
        return result
    }

    /**
     * Alineación central
     */
    fun alignCenter(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 1
        return result
    }

    /**
     * A la derecha
     */
    fun alignRight(): ByteArray {
        val result = ByteArray(3)
        result[0] = ESC
        result[1] = 97
        result[2] = 2
        return result
    }

    //Cortador
    fun cutter(): ByteArray {
        return byteArrayOf(0x1d, 0x56, 0x01)
    }

    //Papel a marca negra
    fun gogogo(): ByteArray {
        return byteArrayOf(0x1C, 0x28, 0x4C, 0x02, 0x00, 0x42, 0x31)
    }


    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////          private                /////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////
    private fun setQRCodeSize(modulesize: Int): ByteArray {
        //Código bidimensional de instrucciones de configuración de tamaño de bloque
        val dtmp = ByteArray(8)
        dtmp[0] = GS
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x43
        dtmp[7] = modulesize.toByte()
        return dtmp
    }

    private fun setQRCodeErrorLevel(errorlevel: Int): ByteArray {
        //Código QR corrección de errores de ajuste de nivel de instrucción
        val dtmp = ByteArray(8)
        dtmp[0] = GS
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x45
        dtmp[7] = (48 + errorlevel).toByte()
        return dtmp
    }


    private fun getBytesForPrintQRCode(single: Boolean): ByteArray {
        //Imprime el código QR de los datos guardados
        val dtmp: ByteArray
        if (single) {        //Solo se imprime un código QR en la misma línea, seguido de una nueva línea
            dtmp = ByteArray(9)
            dtmp[8] = 0x0A
        } else {
            dtmp = ByteArray(8)
        }
        dtmp[0] = 0x1D
        dtmp[1] = 0x28
        dtmp[2] = 0x6B
        dtmp[3] = 0x03
        dtmp[4] = 0x00
        dtmp[5] = 0x31
        dtmp[6] = 0x51
        dtmp[7] = 0x30
        return dtmp
    }

    private fun getQCodeBytes(code: String): ByteArray {
        //Instrucciones de almacenamiento de código QR
        val buffer = ByteArrayOutputStream()
        try {
            val d = code.toByteArray(charset("GB18030"))
            var len = d.size + 3
            if (len > 7092) len = 7092
            buffer.write(0x1D.toByte().toInt())
            buffer.write(0x28.toByte().toInt())
            buffer.write(0x6B.toByte().toInt())
            buffer.write(len.toByte().toInt())
            buffer.write((len shr 8).toByte().toInt())
            buffer.write(0x31.toByte().toInt())
            buffer.write(0x50.toByte().toInt())
            buffer.write(0x30.toByte().toInt())
            var i = 0
            while (i < d.size && i < len) {
                buffer.write(d[i].toInt())
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return buffer.toByteArray()
    }
}