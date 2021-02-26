package frc.motors;

import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.misc.PID;

import static com.revrobotics.CANSparkMax.IdleMode.kBrake;
import static com.revrobotics.CANSparkMax.IdleMode.kCoast;
import static com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless;
import static com.revrobotics.ControlType.kVelocity;

/**
 * This works to wrap Neo's and maybe some other motors
 */
public class SparkMotorController extends AbstractMotorController {
    public final CANSparkMax motor;
    private final CANPIDController myPid;

    public SparkMotorController(int channelID) {
        this(channelID, kBrushless);
    }

    public SparkMotorController(int channelID, CANSparkMaxLowLevel.MotorType type) {
        motor = new CANSparkMax(channelID, type);
        myPid = motor.getPIDController();
        //I dont know if talons do this or if we ever dont do this so here it is
        myPid.setOutputRange(-1, 1);
    }

    @Override
    public void moveAtRotations(double rpm) {
        moveAtVelocity(rpm * sensorToRealDistanceFactor);
    }

    @Override
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        return this;
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        if (leader instanceof SparkMotorController)
            motor.follow(((SparkMotorController) leader).motor);
        return this;
    }

    @Override
    public void resetEncoder() {
        if (motor.getEncoder().setPosition(0) != CANError.kOk)
            throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not reset its encoder");
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        if (myPid.setP(pid.getP()) != CANError.kOk)
            throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " P in PIDF couldnt be reset");
        if (myPid.setI(pid.getI()) != CANError.kOk)
            throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " I in PIDF couldnt be reset");
        if (myPid.setD(pid.getD()) != CANError.kOk)
            throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " D in PIDF couldnt be reset");
        if (myPid.setFF(pid.getF()) != CANError.kOk)
            throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " F in PIDF couldnt be reset");
        return this;
    }

    @Override
    public void moveAtVelocity(double realDistance) {
        myPid.setReference(realDistance / sensorToRealDistanceFactor, kVelocity);
    }

    @Override
    public AbstractMotorController setBrake(boolean brake) {
        motor.setIdleMode(brake ? kBrake : kCoast);
        return this;
    }

    @Override
    public double getRotations() {
        //why 9? i dunno
        return motor.getEncoder().getPosition() * sensorToRealDistanceFactor;
        //return motor.getEncoder().getVelocity() * sensorToRevolutionFactor;
    }

    @Override
    public double getSpeed() {
        return motor.getEncoder().getVelocity() * sensorToRealDistanceFactor;
    }

    @Override
    public AbstractMotorController setCurrentLimit(int limit) {
        if (motor.setSmartCurrentLimit(limit) != CANError.kOk)
            throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not set current limit");
        return this;
    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(percent);
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        if (motor.setOpenLoopRampRate(timeToMax) != CANError.kOk)
            throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not set open loop ramp");
        return this;
    }

    @Override
    public double getMotorTemperature() {
        return motor.getMotorTemperature();
    }
}
