package co.com.pagatodo.core.views.sitp.ClienteRecaudoSitp.constants

enum class ECodigosError private constructor(codigo: Int, descripcion: String) {
    /**ERRORES DE INICIALIZACION**/
    ERR_3(3, "Perdida de conexion al FCS"),
    ERR_8(8, "Error descargando archivos pendientes del FCS"),
    ERR_10(10, "Error al activar la comunicación con la SAM Tullave"),
    ERR_11(11, "Error al activar la comunicación con la SAM Angelcom"),
    ERR_18(18, "Problema aplicando parámetros de SAM"),
    ERR_20(20, "Error de lectura de llaves en SAM Angelcom"),
    ERR_21(21, "Problema obteniendo ADF de SAM"),
    ERR_31(31, "Problema descomprimiendo archivo por corrupción de este"),
    ERR_32(32, "Problema descomprimiendo archivo de extensión .gz"),
    ERR_33(33, "Problema descomprimiendo archivo de extensión .tar"),
    ERR_34(34, "Problema descomprimiendo firmware"),
    ERR_36(36, "Problema descomprimiendo Listas Negras Fijas"),
    ERR_37(37, "Problema descomprimiendo Listas Negras Variables"),
    ERR_41(41, "Problema creando directorios de archivo"),
    ERR_42(42, "Problema cargando archivo"),
    ERR_43(43, "Problema guardando archivo"),
    ERR_44(44, "Problema encontrando archivo en el directorio"),
    ERR_45(45, "Error al obtener la versión de los archivos del archivo VERSIONS"),
    ERR_46(46, "Error leyendo el archivo de configuración config.dat"),
    ERR_47(47, "Error cargando políticas tarifarias"),
    ERR_48(48, "Error cargando versiones del dispositivo"),
    ERR_49(49, "Error cargando parámetros operativos"),
    ERR_71(71, "Error, archivo de transacciones corrupto"),
    ERR_72(72, "Error, archivo de transacciones pendientes corrupto"),
    ERR_73(73, "Error, archivo de ventas corrupto"),
    ERR_74(74, "Error, archivo de ventas pendientes corrupto"),
    ERR_81(81, "Error cargando display de dongle"),

    /**ERRORES DE OPERACION**/
    ERR_7(7, "Error cargando subsidio por confirmación del FCS"),
    ERR_12(12, "Problema inicializando la SAM para carga de saldo"),
    ERR_13(13, "Problema cargando el saldo de la SAM"),
    ERR_14(14, "Problema Inicializando SAM para cancelación de recargas"),
    ERR_15(15, "Problema confirmando el crédito de SAM para cancelación de recargas"),
    ERR_16(16, "Problema realizando el débito de SAM para recargas"),
    ERR_17(17, "Problema confirmando recarga de SAM"),
    ERR_26(26, "Problema en la confirmación de recarga de tarjeta a la SAM"),
    ERR_27(27, "Problema inicializando cancelación de recarga en tarjeta"),
    ERR_28(28, "Problema cancelando recarga en tarjeta"),
    ERR_29(29, "Problema por pérdida de conexión a la tarjeta"),
    ERR_52(52, "Error en Transacción por dispositivo no inicializado"),
    ERR_75(75, "Error en lectura de balance de tarjeta"),
    ERR_76(76, "Error recargando subsidio en tarjeta"),
    /**ERRORES DE INFORMACION AL USUARIO**/
    ERR_19(19, "Recargue su dispositivo para seguir realizando transacciones"),
    ERR_22(22, "Tarjeta No válida para transacciones"),
    ERR_23(23, "Tarjeta No válida para transacción"),
    ERR_24(24, "Tarjeta No Vendida"),
    ERR_25(25, "Valor ingresado no válido"),
    ERR_30(30, "Tarjeta ya vendida. Ingrese una tarjeta nueva"),
    ERR_53(53, "Verifique que el password y el operador ingresados sean correctos"),
    ERR_61(
        61,
        "Problema Activando Tarjeta. Utilice una tarjeta diferente o Comuníquese con su operador"
    ),
    ERR_62(
        62,
        "Problema Activando Tarjeta. Utilice una tarjeta diferente o Comuníquese con su operador"
    ),
    ERR_63(
        63,
        "Problema Activando Tarjeta. Utilice una tarjeta diferente o Comuníquese con su operador"
    ),
    ERR_64(
        64,
        "Problema Activando Tarjeta. Utilice una tarjeta diferente o Comuníquese con su operador"
    ),
    ERR_65(65, "Función no válida para tarjetas Angelcom"),
    ERR_66(66, "Última transacción de tarjeta fallida. Por favor utilice una tarjeta diferente"),
    ERR_77(77, "Función no disponible para éste dispositivo"),
    ERR_78(78, "Función no válida para tarjetas Angelcom"),
    ERR_89(89, "Saldo de la Tarjeta es diferente a 0"),
    ERR_99(99, "Equipo fuera del horario de operación"),
    ERR_00(0, "Error desconocido");

    var codigo: Int = 0
    var descripcion: String

    init {
        this.codigo = codigo
        this.descripcion = descripcion
    }

    companion object {
        fun valueOf(codigo: Int): ECodigosError {
            for (error in values()) {
                if (error.codigo == codigo) {
                    return error
                }
            }
            values()[values().size-1].codigo = codigo
            return values()[values().size-1]
            throw RuntimeException(
                ("Error en el CodigoError, el identificador " + codigo + " no es valido.")
            )
        }
    }
}