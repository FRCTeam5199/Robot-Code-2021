package frc.misc;

import frc.robot.RobotNumbers;

public class UtilFunctions {
    
    public static double convertDriveFPStoRPM(double FPS) {
        return FPS * (RobotNumbers.MAX_MOTOR_SPEED / RobotNumbers.MAX_SPEED);
    }

    public static double getTargetVelocity(double FPS) {
        return convertDriveFPStoRPM(FPS) * RobotNumbers.DRIVEBASE_SENSOR_UNITS_PER_ROTATION / 600.0;
    }
}