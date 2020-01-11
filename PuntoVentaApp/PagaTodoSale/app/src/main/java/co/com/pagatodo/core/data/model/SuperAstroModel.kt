package co.com.pagatodo.core.data.model

class SuperAstroModel {
    var number: String? = null
    var value: Double? = null
    var zodiacs: MutableList<String>? = null
    var zodiacSelected: String? = null

    override fun toString(): String {
        return "SuperAstroModel(number=$number, value=$value, zodiacs=$zodiacs, zodiacSelected=$zodiacSelected)"
    }
}