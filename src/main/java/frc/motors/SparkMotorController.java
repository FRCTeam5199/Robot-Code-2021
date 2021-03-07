package frc.motors;

import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import frc.misc.PID;
import frc.robot.Robot;

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
        super();
        motor = new CANSparkMax(channelID, type);
        myPid = motor.getPIDController();
        //I dont know if talons do this or if we ever dont do this so here it is
        if (myPid.setOutputRange(-1, 1) != CANError.kOk)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not set its output range");
            else
                failureFlag = true;
    }

    @Override
    public AbstractMotorController setInverted(boolean invert) {
        motor.setInverted(invert);
        System.out.println("INVERTING MOTOR " + motor.getDeviceId() + "!" + invert);
        return this;
    }

    @Override
    public String getName() {
        return "Spark: " + motor.getDeviceId();
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader) {
        return follow(leader, false);
    }

    @Override
    public AbstractMotorController follow(AbstractMotorController leader, boolean invert){
        if (!(leader instanceof SparkMotorController))
            throw new IllegalArgumentException("I cant follow that!!");
        if (motor.follow(((SparkMotorController) leader).motor, invert) != CANError.kOk)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not follow the leader");
            else
                failureFlag = true;
        return this;
    }

    @Override
    public void resetEncoder() {
        if (motor.getEncoder().setPosition(0) != CANError.kOk)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not reset its encoder");
            else
                failureFlag = true;
    }

    @Override
    public AbstractMotorController setPid(PID pid) {
        if (myPid.setP(pid.getP()) != CANError.kOk || myPid.setI(pid.getI()) != CANError.kOk || myPid.setD(pid.getD()) != CANError.kOk || myPid.setFF(pid.getF()) != CANError.kOk)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " F in PIDF couldnt be reset");
            else
                failureFlag = true;
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
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not set current limit");
            else
                failureFlag = true;
        return this;
    }

    @Override
    public void moveAtPercent(double percent) {
        motor.set(percent);
    }

    @Override
    public AbstractMotorController setOpenLoopRampRate(double timeToMax) {
        if (motor.setOpenLoopRampRate(timeToMax) != CANError.kOk)
            if (!Robot.SECOND_TRY)
                throw new IllegalStateException("Spark motor controller with ID " + motor.getDeviceId() + " could not set open loop ramp");
            else
                failureFlag = true;
        return this;
    }

    @Override
    public double getMotorTemperature() {
        return motor.getMotorTemperature();
    }
}
