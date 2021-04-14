package frc.motors;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;

public class SwerveMotorController {
    public final AbstractMotorController driver, steering;

    public SwerveMotorController(int driverID, SupportedMotors driverMotorType, int steeringID, SupportedMotors steeringMotorType){
        switch (driverMotorType){
            case VICTOR:
                driver = new VictorMotorController(driverID);
                break;
            case TALON_FX:
                driver = new TalonMotorController(driverID);
                break;
            case CAN_SPARK_MAX:
                driver = new SparkMotorController(driverID);
                break;
            default:
                throw new UnsupportedOperationException("Connot create a " + driverMotorType.name());
        }
        switch (steeringMotorType){
            case VICTOR:
                steering = new VictorMotorController(steeringID);
                break;
            case TALON_FX:
                steering = new TalonMotorController(steeringID);
                break;
            case CAN_SPARK_MAX:
                steering = new SparkMotorController(steeringID);
                break;
            default:
                throw new UnsupportedOperationException("Connot create a " + steeringMotorType.name());
        }
    }

    public SwerveModuleState getState(){
        return new SwerveModuleState(driver.getRotations(), Rotation2d.fromDegrees(steering.getRotations()));
    }
}
