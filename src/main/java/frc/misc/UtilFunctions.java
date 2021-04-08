package frc.misc;

public class UtilFunctions {
    /**
     * Used in  to get the weighted average between two points
     *
     * @param voltage the voltage to be in between
     * @param uppers  the entry from the voltage table for the higher voltage {voltage, value}
     * @param lowers  the entry from the voltage table for the lower voltage {voltage, value}
     * @return the weighted average between the upper and lower voltage with respect to the battery voltage
     * @author jojo2357
     */
    public static double weightedAverage(double voltage, double[] uppers, double[] lowers) {
        return lowers[1] + (uppers[1] - lowers[1]) * (voltage - lowers[0]) * (uppers[0] - lowers[0]);
    }

    /**
     * So when you do {@code -3 % 2} in java, you get -1. However this isnt true. In java, since division truncates,
     * {@code -3 / 2 == -1} which means that in order to be consistent, {@code -1 * 2 - (-3) == 1}. Hence, {@code -3 % 2
     * == -1}. If we did this with normal math, {@code -3 / 2 == -2 remainder 1}. This is because division in the
     * positive numbers will give you an underestimate (ie {@code 3 / 2 == 1 r 1}) so continuing the pattern it becomes
     * apparent that java does it wrong.
     * <p>
     * Why use this method? Well if you want to easily limit the bounds of a function in a clean way ({@link
     * frc.ballstuff.shooting.Turret Turret}) then this is the method for you! For the low, low price of 5.99 plus
     * shipping and handling it could be yours!
     *
     * @param value  the dividend
     * @param modulo the divisor
     * @return the true modulus of the given value and divisor pair
     */
    public static double mathematicalMod(double value, double modulo) {
        return (value - Math.floor(value / modulo) * modulo);
    }
}