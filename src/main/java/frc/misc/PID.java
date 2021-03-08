package frc.misc;

/**
 * A wrapper to hold PID values. Use this in lieu of 4 different variables for cleanness, the {@link #toString() print}
 * method, the {@link #equals(Object) equals method} and general concoeness
 */
public class PID {
    /**
     * Do not instanitate a zeroed PID object. Instead use this static reference.
     */
    public static final PID EMPTY_PID = new PID(0, 0, 0, 0);
    public final double P, I, D, F;

    public PID(double p, double i, double d) {
        this(p, i, d, 0);
    }

    public PID(double p, double i, double d, double f) {
        P = p;
        I = i;
        D = d;
        F = f;
    }

    public double getP() {
        return P;
    }

    public double getI() {
        return I;
    }

    public double getD() {
        return D;
    }

    public double getF() {
        return F;
    }

    /**
     * Compares two PID objects and returns true if they contain identiacal values
     *
     * @param other a PID object to compare
     * @return true if the two pid objects have the same values
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof PID)
            return ((PID) other).P == P && ((PID) other).I == I && ((PID) other).D == D && ((PID) other).F == F;
        throw new IllegalArgumentException("We are better than this. Do not pass a bad object into and equals method");
    }

    /**
     * Formats the PID object for ez printing
     *
     * @return A tidy string representation of this object
     */
    @Override
    public String toString() {
        return "PIDF (P: " + P + ", I: " + I + ", D: " + D + ", F: " + F + ")";
    }
}