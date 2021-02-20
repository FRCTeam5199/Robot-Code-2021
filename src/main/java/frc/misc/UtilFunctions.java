package frc.misc;

import frc.robot.RobotNumbers;

public class UtilFunctions {

    public static double getTargetVelocity(double FPS) {
        return convertDriveFPStoRPM(FPS) * RobotNumbers.DRIVEBASE_SENSOR_UNITS_PER_ROTATION / 600.0;
    }

    public static double convertDriveFPStoRPM(double FPS) {
        return (FPS / RobotNumbers.MAX_SPEED) * RobotNumbers.MAX_MOTOR_SPEED;
    }

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

    public static double mathematicalMod(double value, double modulo) {
        return (value - Math.floor(value / modulo) * modulo);
    }

    public static double wheelCircumference() {
        return RobotNumbers.WHEEL_DIAMETER * Math.PI;
    }
}