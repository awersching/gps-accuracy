package awersching.gammelgps.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Util {

    private static final DecimalFormat round = new DecimalFormat("#.##");

    static {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        round.setDecimalFormatSymbols(symbols);
    }

    public static String round(double number) {
        return round.format(number);
    }
}
