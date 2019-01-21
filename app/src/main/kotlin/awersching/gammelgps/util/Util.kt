package awersching.gammelgps.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object Util {

    private val round = DecimalFormat("#.##")

    init {
        val symbols = DecimalFormatSymbols.getInstance()
        symbols.decimalSeparator = '.'
        round.decimalFormatSymbols = symbols
    }

    fun round(number: Double): String {
        return round.format(number)
    }
}
