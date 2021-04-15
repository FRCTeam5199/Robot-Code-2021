package frc.motors;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import org.jetbrains.annotations.Nullable;

/**
 * A nice wrapper to hold a {@link #driver driving motor} and a {@link #steering steering motor}. Main util method is
 * {@link #getState()} which is used in {@link frc.telemetry.RobotTelemetrySwivel telem}
 */
public class SwerveMotorController {
    public AbstractMotorController driver, steering;

    /**
     * Creates a new swerve drive module with any motors in {@link SupportedMotors#values()}
     *
     * @param driverID          the id of the driver motor to instantiate
     * @param driverMotorType   the type of motor to create. If null, then no driving will be made
     * @param steeringID        the id of the steering motor to instantiate
     * @param steeringMotorType the type of motor to create. If null, then no steering will be made
     */
    public SwerveMotorController(int driverID, @Nullable SupportedMotors driverMotorType, int steeringID, @Nullable SupportedMotors steeringMotorType) {
        if (driverMotorType != null) {
            switch (driverMotorType) {
                case VICTOR:
                    driver = new VictorMotorController(driverID);
                    break;
                case TALON_FX:
                    driver = new TalonMotorController(driverID);
                    break;
                case CAN_SPARK_MAX:
                    driver = new SparkMotorController(driverID);
            }
        }
        if (steeringMotorType != null) {
            switch (steeringMotorType) {
                case VICTOR:
                    steering = new VictorMotorController(steeringID);
                    break;
                case TALON_FX:
                    steering = new TalonMotorController(steeringID);
                    break;
                case CAN_SPARK_MAX:
                    steering = new SparkMotorController(steeringID);
            }
        }
    }

    /**
     * The main reason to use this object.
     *
     * @return the module state of the two swerve motors
     */
    public SwerveModuleState getState() {
        return new SwerveModuleState(driver.getRotations(), Rotation2d.fromDegrees(steering.getRotations()));
    }
}
